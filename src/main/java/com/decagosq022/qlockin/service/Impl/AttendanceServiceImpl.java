
package com.decagosq022.qlockin.service.Impl;

import com.decagosq022.qlockin.entity.Attendance;
import com.decagosq022.qlockin.entity.User;
import com.decagosq022.qlockin.exceptions.NotClockedInException;
import com.decagosq022.qlockin.exceptions.NotFoundException;
import com.decagosq022.qlockin.payload.response.*;
import com.decagosq022.qlockin.repository.AttendanceRepository;
import com.decagosq022.qlockin.repository.UserRepository;
import com.decagosq022.qlockin.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.NotActiveException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
     private final AttendanceRepository attendanceRepository;
     private final UserRepository userRepository;
    @Override
    public AttendanceResponse clockIn(String email, String employeeId) throws NotActiveException {


         User user = userRepository.findByEmployeeId(employeeId).orElse(null);

        if(user == null) {
            throw new NotFoundException("User not found");
        }

        if (!user.isActive()){
            throw new NotActiveException("You have not been activated, you cant clock in ");
        }


        LocalDate today = LocalDate.now();
        boolean hasClockedInToday = attendanceRepository.existsByCreatedByUserAndQlockInBetween(
                user, today.atStartOfDay(), today.plusDays(1).atStartOfDay());

        if (hasClockedInToday) {
            return AttendanceResponse.builder()
                    .responseCode("006")
                    .responseMessage("You have already clocked in today.")
                    .build();
        }

        LocalDateTime defaultClockIn = LocalDateTime.now().withHour(8).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime defaultClockOut = LocalDateTime.now().withHour(16).withMinute(0).withSecond(0).withNano(0);

        Attendance attendance = Attendance.builder()
                .default_Qlock_In(defaultClockIn)
                .default_Qlock_Out(defaultClockOut)
                .date(LocalDate.now())
                .qlockIn(LocalDateTime.now())
                .createdByUser(user)
                .build();

        attendanceRepository.save(attendance);

        return AttendanceResponse.builder()
                .responseCode("005")
                .responseMessage(" Your Attendance has been ticked")
                .build();
    }

    @Override
    public AttendanceResponse clockOut(String email, String employeeId) {

         User user = userRepository.findByEmployeeId(employeeId).orElse(null);

        if(user == null) {
            throw new NotFoundException("User not found");
        }

        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);

        Attendance attendance = attendanceRepository.findByCreatedByUserAndQlockInBetween(user,startOfDay,endOfDay)
                .orElseThrow(() -> new NotClockedInException("You did not clock in, you can't clock out "));

        if (attendance.getQlockOut() != null) {
            return AttendanceResponse.builder()
                    .responseCode("008")
                    .responseMessage("You have already clocked out today.")
                    .build();
        }

        LocalDateTime defaultClockIn = LocalDateTime.now().withHour(8).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime defaultClockOut = LocalDateTime.now().withHour(16).withMinute(0).withSecond(0).withNano(0);

        attendance.setCreatedByUser(user);
        attendance.setQlockOut(LocalDateTime.now());
        attendance.setDefault_Qlock_In(defaultClockIn);
        attendance.setDefault_Qlock_Out(defaultClockOut);

        attendanceRepository.save(attendance);

        return AttendanceResponse.builder()
                .responseCode("007")
                .responseMessage("You have clocked out of work today ")
                .build();
    }

    @Override
    public AttendanceDataDto getAllAttendanceStat(String email, LocalDate date) {
        userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        List<Attendance> attendances = attendanceRepository.findAllByDate(date);

        Long earlyArrivals = attendances.stream()
                .filter(a ->a.getQlockIn() != null && a.getQlockIn().isBefore(a.getDefault_Qlock_In()))
                .count();

        Long lateArrivals = attendances.stream()
                .filter(a -> a.getQlockIn() != null && a.getQlockIn().isAfter(a.getDefault_Qlock_In()))
                .count();

        Long earlyDepartures = attendances.stream()
                .filter(a -> a.getQlockOut() != null && a.getQlockOut().isBefore(a.getDefault_Qlock_Out()))
                .count();

        Long absentEmployees = attendances.stream()
                .filter(a -> a.getQlockIn() == null)
                .count();

        Long activeEmployees = userRepository.countByIsActiveTrue();


        return AttendanceDataDto.builder()
                .responseCode("009")
                .responseMessage("These are your Attendance Statistics ")
                .earlyArrivals(earlyArrivals)
                .lateArrivals(lateArrivals)
                .earlyDepartures(earlyDepartures)
                .absentEmployees(absentEmployees)
                .activeEmployees(activeEmployees)
                .build();
    }

    @Override
    public List<AbsenteeismReportResponseDto> getMonthlyAbsenteeismReport(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        List<User> users = userRepository.findAll();
        List<Attendance> attendances = attendanceRepository.findAllByDateBetween(startOfMonth, endOfMonth);

        Map<Long, List<Attendance>> userAttendanceMap = attendances.stream()
                .collect(Collectors.groupingBy(attendance -> attendance.getCreatedByUser().getId()));


        return users.stream().map(user -> {
            List<Attendance> userAttendances = userAttendanceMap.getOrDefault(user.getId(), List.of());
            long totalWorkDays = calculateTotalWorkdays(year, month);
            long absentDays = totalWorkDays - userAttendances.size();

            double absenteeismRate = (double) absentDays / totalWorkDays * 100;

        return AbsenteeismReportResponseDto.builder()
                .userId(user.getEmployeeId())
                .fullName(user.getFullName())
                .absentDays(absentDays)
                .totalWorkDays(totalWorkDays)
                .absenteeismRate(Math.ceil(absenteeismRate))
                .build();

        }).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceReportDto> getAttendanceReport(String email, LocalDate date) {
        userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        List<User> employees = userRepository.findAll();

        List<Attendance> attendances = attendanceRepository.findAllByDate(date);

        // Create a map of users and their attendance for easier lookup
        Map<User, Attendance> attendanceMap = attendances.stream()
                .collect(Collectors.toMap(Attendance::getCreatedByUser, attendance -> attendance));

        // For each employee, create an attendance report
        return employees.stream().map(employee -> {
            Attendance attendance = attendanceMap.get(employee);

            String status;
            LocalDateTime qlockIn = null;
            LocalDateTime qlockOut = null;
            long totalHours = 0;

            if (attendance == null || attendance.getQlockIn() == null) {
                // If no attendance record or no clock-in time, mark as Absent
                status = "Absent";
            } else {
                qlockIn = attendance.getQlockIn();
                qlockOut = attendance.getQlockOut();

                // Calculate total hours worked if both clock-in and clock-out are available
                totalHours = Duration.between(qlockIn, qlockOut != null ? qlockOut : LocalDateTime.now()).toHours();

                // Determine status based on the clock-in time (before 8 AM is "Early", after 8 AM is "Late")
                status = qlockIn.toLocalTime().isBefore(LocalTime.of(8, 0)) ? "Early" : "Late";
            }

            // Return the DTO with attendance details
            return new AttendanceReportDto(
                    employee.getEmployeeId(),
                    employee.getFullName(),
                    qlockIn,
                    qlockOut,
                    totalHours,
                    status
            );
        }).collect(Collectors.toList());
    }

    @Override
    public AttendanceOvertimeDto getOvertimeReport(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));

        List<Attendance> attendanceList = attendanceRepository.findByCreatedByUserAndDateBetween(user, weekStart, weekEnd);

        Duration totalRegularHours = Duration.ZERO;
        Duration totalOvertimeHours = Duration.ZERO;

        for (Attendance attendance : attendanceList) {
            if (attendance.getQlockIn() != null && attendance.getQlockOut() != null) {
                Duration workedHours = Duration.between(attendance.getQlockIn(), attendance.getQlockOut());

                // Assuming 8 hours as the regular workday
                Duration regularHours = Duration.ofHours(8);

                if (workedHours.compareTo(regularHours) > 0) {
                    totalRegularHours = totalRegularHours.plus(regularHours);
                    totalOvertimeHours = totalOvertimeHours.plus(workedHours.minus(regularHours));
                } else {
                    totalRegularHours = totalRegularHours.plus(workedHours);
                }
            }
        }
        AttendanceOvertimeDto report = AttendanceOvertimeDto.builder()
                .fullName(user.getFullName())
                .employeeId(user.getEmployeeId())
                .regularHours(totalRegularHours)
                .overtimeHours(totalOvertimeHours)
                .totalHours(totalRegularHours.plus(totalOvertimeHours))
                .build();

        return report;
    }
    @Override
    public List<AttendanceOvertimeDto> getGeneralOverTimeReport(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        List<Attendance> attendanceList = attendanceRepository.findAll();

        List<AttendanceOvertimeDto> generalList = new ArrayList<>();

        for(Attendance attendance : attendanceList){

            AttendanceOvertimeDto singletOvertime = getSingleOvertime(attendance.getCreatedByUser());

            generalList.add(singletOvertime);


        }

        return generalList;


    }

    private AttendanceOvertimeDto getSingleOvertime(User user){
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));

        List<Attendance> attendanceList = attendanceRepository.findByCreatedByUserAndDateBetween(user, weekStart, weekEnd);

        Duration totalRegularHours = Duration.ZERO;
        Duration totalOvertimeHours = Duration.ZERO;

        for (Attendance attendance : attendanceList) {
            if (attendance.getQlockIn() != null && attendance.getQlockOut() != null) {
                Duration workedHours = Duration.between(attendance.getQlockIn(), attendance.getQlockOut());

                // Assuming 8 hours as the regular workday
                Duration regularHours = Duration.ofHours(8);

                if (workedHours.compareTo(regularHours) > 0) {
                    totalRegularHours = totalRegularHours.plus(regularHours);
                    totalOvertimeHours = totalOvertimeHours.plus(workedHours.minus(regularHours));
                } else {
                    totalRegularHours = totalRegularHours.plus(workedHours);
                }
            }
        }

        AttendanceOvertimeDto report = AttendanceOvertimeDto.builder()
                .fullName(user.getFullName())
                .employeeId(user.getEmployeeId())
                .regularHours(totalRegularHours)
                .overtimeHours(totalOvertimeHours)
                .totalHours(totalRegularHours.plus(totalOvertimeHours))
                .build();

        return report;
    }


    @Override
    public List<LateComersReport> generalLateComersReport(String email, int year, int month) {
        userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        List<Attendance> attendances = attendanceRepository.findAll();

        List<LateComersReport> reports = new ArrayList<>();

        HashSet<User> user = new HashSet<>();
        for(Attendance attendance : attendances) {
            user.add(attendance.getCreatedByUser());
        }

        for(User user1 : user) {
            LateComersReport report = getLateComersReport(user1,year, month);

            reports.add(report);
        }

        return reports;
    }

    @Override
    public AttendanceSummaryResponse getAttendanceSummary(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        List<Attendance> attendances = attendanceRepository.findByCreatedByUser(user);

        // Calculate the counts
        Long totalDaysAtWork = (long) attendances.size();

        Long earlyArrivals = attendances.stream()
                .filter(a -> a.getQlockIn() != null && a.getQlockIn().isBefore(a.getDefault_Qlock_In()))
                .count();

        // Calculate late arrivals
        Long lateArrivals = attendances.stream()
                .filter(a -> a.getQlockIn() != null && a.getQlockIn().isAfter(a.getDefault_Qlock_In()))
                .count();

        // Calculate early departures
        Long earlyDepartures = attendances.stream()
                .filter(a -> a.getQlockOut() != null && a.getQlockOut().isBefore(a.getDefault_Qlock_Out()))
                .count();

        // Calculate absent employees
        Long absentDays = attendances.stream()
                .filter(a -> a.getQlockIn() == null)
                .count();

        return AttendanceSummaryResponse.builder()
                .totalDaysAtWork(totalDaysAtWork)
                .dayAbsent(absentDays)
                .daysEarly(earlyArrivals)
                .daysLate(lateArrivals)
                .earlyDepartures(earlyDepartures)
                .build();
    }

    @Override
    public List<AttendanceReportDto> getAttendanceForToday(String email, LocalDate date) {
        userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        List<User> employees = userRepository.findAll();

        List<Attendance> attendances = attendanceRepository.findAllByDate(date);

        // Create a map of users and their attendance for easier lookup
        Map<User, Attendance> attendanceMap = attendances.stream()
                .collect(Collectors.toMap(Attendance::getCreatedByUser, attendance -> attendance));

        // For each employee, create an attendance report
        return employees.stream().map(employee -> {
            Attendance attendance = attendanceMap.get(employee);

            String status;
            LocalDateTime qlockIn = null;
            LocalDateTime qlockOut = null;
            long totalHours = 0;

            if (attendance == null || attendance.getQlockIn() == null) {
                // If no attendance record or no clock-in time, mark as Absent
                status = "Absent";
            } else {
                qlockIn = attendance.getQlockIn();
                qlockOut = attendance.getQlockOut();

                // Calculate total hours worked if both clock-in and clock-out are available
                totalHours = Duration.between(qlockIn, qlockOut != null ? qlockOut : LocalDateTime.now()).toHours();

                // Determine status based on the clock-in time (before 8 AM is "Early", after 8 AM is "Late")
                status = qlockIn.toLocalTime().isBefore(LocalTime.of(8, 0)) ? "Early" : "Late";
            }

            // Return the DTO with attendance details
            return new AttendanceReportDto(
                    employee.getFullName(),
                    qlockIn,
                    qlockOut,
                    totalHours,
                    status
            );
        }).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceHistoryDto> getUserAttendance(String email, int year, int month) {
        userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        List<Attendance> attendances = attendanceRepository.findAttendanceByCreatedByUserEmailAndMonth(email, year, month);
        List<AttendanceHistoryDto> attendanceHistoryDtos = new ArrayList<>();

        for(Attendance attendance : attendances) {

            if (attendance.getQlockIn() == null || attendance.getQlockOut() == null) {
                continue; // Skip if either qlockIn or qlockOut is null
            }
            // Calculate total hours worked
            long hoursWorked = ChronoUnit.HOURS.between(attendance.getQlockIn(), attendance.getQlockOut());

            // Determine status
            LocalTime earlyTime = LocalTime.of(8, 0);
            LocalTime clockInTime = attendance.getQlockIn().toLocalTime();
            String status = clockInTime.isBefore(earlyTime) ? "Early" : "Late";

            AttendanceHistoryDto attendanceHistoryDto = AttendanceHistoryDto.builder()
                    .date(attendance.getDate())
                    .qlockIn(attendance.getQlockIn())
                    .qlockOut(attendance.getQlockOut())
                    .totalHours(hoursWorked)
                    .status(status)
                    .build();

            attendanceHistoryDtos.add(attendanceHistoryDto);
        }
        return attendanceHistoryDtos;

    }

    @Override
    public List<AbsenteeismReportDto> getUserAbsenteeismReport(String email, int year) {
        List<AbsenteeismReportDto> report = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);
            long totalWorkDays = calculateTotalWorkdays(year, month);

            List<Attendance> attendances = attendanceRepository.findAttendanceByCreatedByUserEmailAndMonth(email, year, month);
            long attendedDays = attendances.size();
            long absentDays = totalWorkDays - attendedDays;

            // Calculate absenteeism rate (absent days / total workdays) * 100
            double absenteeismRate = (double) absentDays / totalWorkDays * 100;

            AbsenteeismReportDto absenteeismReportDto = AbsenteeismReportDto.builder()
                    .month(yearMonth.getMonth().name())
                    .absentDays(absentDays)
                    .totalWorkDays(totalWorkDays)
                    .absenteeismRate(absenteeismRate)
                    .build();
            report.add(absenteeismReportDto);
        }
        return report;
    }

    @Override
    public List<LateComersReportDto> getUserLateComersReport(String email, int year) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        List<LateComersReportDto> reportList = new ArrayList<>();
        // Iterate over each month of the year
        for (int month =1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);
            long totalWorkDays = calculateTotalWorkdays(year, month);

            // Fetch attendance records for the user for the given month
            List<Attendance> attendances = attendanceRepository.findAttendanceByCreatedByUserEmailAndMonth(email, year, month);

            long lateDays = 0;

            // Define the threshold for being late (e.g., 9:00 AM)
            LocalTime lateThreshold = LocalTime.of(8, 0);

            // Calculate late days
            for (Attendance attendance : attendances) {
                if (attendance.getQlockIn() != null && attendance.getQlockIn().toLocalTime().isAfter(lateThreshold)) {
                    lateDays++;
                }
            }
            // Calculate latecomer rate (late days / total workdays) * 100
            double latecomerRate = (double) lateDays / totalWorkDays * 100;

            // Build the report for the month
            LateComersReportDto lateComersReportDto = LateComersReportDto.builder()
                    .month(yearMonth.getMonth().name())
                    .totalWorkDays(totalWorkDays)
                    .lateArrivals(lateDays)
                    .lateComerRate(latecomerRate)
                    .build();
            // Add the monthly report to the list
            reportList.add(lateComersReportDto);

        }
        return reportList;
    }

    @Override
    public List<OvertimeReportDto> getUserOvertimeReport(String email, int year, int month) {
        List<OvertimeReportDto> report = new ArrayList<>();

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Fetch attendance records for the current user within the current month
        List<Attendance> attendances = attendanceRepository.findAttendanceByCreatedByUserEmailAndDateBetween(email, startDate, endDate);

        // Regular work hours (8:00 AM - 5:00 PM)
        LocalTime regularStartTime = LocalTime.of(8, 0);
        LocalTime regularEndTime = LocalTime.of(16, 0);
        Duration regularWorkHours = Duration.between(regularStartTime, regularEndTime);
        for( Attendance attendance : attendances) {
            if (attendance.getQlockIn() != null && attendance.getQlockOut() != null) {
                LocalTime clockIn = attendance.getQlockIn().toLocalTime();
                LocalTime clockOut = attendance.getQlockOut().toLocalTime();

                Duration workedHours = Duration.between(clockIn, clockOut);
                Duration overtimeHours = Duration.ZERO;


                if (clockOut.isAfter(regularEndTime)) {
                    overtimeHours = Duration.between(regularEndTime, clockOut);
                }

                // Calculate total hours as regular work hours + overtime hours
                Duration totalHours = regularWorkHours.plus(overtimeHours);

                // Build the report for each day
                OvertimeReportDto overtimeReportDto = OvertimeReportDto.builder()
                        .date(attendance.getDate()) // Assuming Attendance entity has a 'date' field
                        .regularHours(regularWorkHours)
                        .overtimeHours(overtimeHours)
                        .totalHours(totalHours)
                        .build();

                report.add(overtimeReportDto);
            }
        }

        return report;
    }

    @Override
    public List<AttendanceHistoryDto> getUserAttendanceById(Long userId, int year, int month) {
        // Fetch user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Fetch attendance records for the user in the specified year and month
        List<Attendance> attendances = attendanceRepository.findAttendanceByCreatedByUserAndMonth(userId, year, month);
        List<AttendanceHistoryDto> attendanceHistoryDtos = new ArrayList<>();

        for (Attendance attendance : attendances) {

            // Skip if either qlockIn or qlockOut is null
            if (attendance.getQlockIn() == null || attendance.getQlockOut() == null) {
                continue;
            }

            // Calculate total hours worked
            long hoursWorked = ChronoUnit.HOURS.between(attendance.getQlockIn(), attendance.getQlockOut());

            // Determine status (Early or Late)
            LocalTime earlyTime = LocalTime.of(8, 0);
            LocalTime clockInTime = attendance.getQlockIn().toLocalTime();
            String status = clockInTime.isBefore(earlyTime) ? "Early" : "Late";

            // Build the DTO for each attendance record
            AttendanceHistoryDto attendanceHistoryDto = AttendanceHistoryDto.builder()
                    .date(attendance.getDate())
                    .qlockIn(attendance.getQlockIn())
                    .qlockOut(attendance.getQlockOut())
                    .totalHours(hoursWorked)
                    .status(status)
                    .build();

            // Add to the list of attendance history DTOs
            attendanceHistoryDtos.add(attendanceHistoryDto);
        }

        return attendanceHistoryDtos;
    }

    @Override
    public List<AbsenteeismReportDto> getUserAbsenteeismById(Long userId, int year) {
        // Fetch user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<AbsenteeismReportDto> report = new ArrayList<>();

        // Loop through each month of the year
        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);

            // Calculate the total number of workdays for the current month
            long totalWorkDays = calculateTotalWorkdays(year, month);

            // Retrieve attendance records for the user for the specified month and year
            List<Attendance> attendances = attendanceRepository.findAttendanceByCreatedByUserAndMonth(userId, year, month);
            long attendedDays = attendances.size();
            long absentDays = totalWorkDays - attendedDays;

            // Calculate absenteeism rate (absent days / total workdays) * 100
            double absenteeismRate = (double) absentDays / totalWorkDays * 100;

            // Build the absenteeism report for the current month
            AbsenteeismReportDto absenteeismReportDto = AbsenteeismReportDto.builder()
                    .month(yearMonth.getMonth().name())
                    .absentDays(absentDays)
                    .totalWorkDays(totalWorkDays)
                    .absenteeismRate(absenteeismRate)
                    .build();

            // Add the report to the list
            report.add(absenteeismReportDto);
        }

        return report;
    }

    @Override
    public List<LateComersReportDto> getUserLateComersReportById(Long userId, int year) {
        // Fetch user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<LateComersReportDto> reportList = new ArrayList<>();
        // Iterate over each month of the year
        for (int month =1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);
            long totalWorkDays = calculateTotalWorkdays(year, month);

            // Fetch attendance records for the user for the given month
            List<Attendance> attendances = attendanceRepository.findAttendanceByCreatedByUserAndMonth(userId, year, month);

            long lateDays = 0;

            // Define the threshold for being late (e.g., 9:00 AM)
            LocalTime lateThreshold = LocalTime.of(8, 0);

            // Calculate late days
            for (Attendance attendance : attendances) {
                if (attendance.getQlockIn() != null && attendance.getQlockIn().toLocalTime().isAfter(lateThreshold)) {
                    lateDays++;
                }
            }
            // Calculate latecomer rate (late days / total workdays) * 100
            double latecomerRate = (double) lateDays / totalWorkDays * 100;

            // Build the report for the month
            LateComersReportDto lateComersReportDto = LateComersReportDto.builder()
                    .month(yearMonth.getMonth().name())
                    .totalWorkDays(totalWorkDays)
                    .lateArrivals(lateDays)
                    .lateComerRate(latecomerRate)
                    .build();
            // Add the monthly report to the list
            reportList.add(lateComersReportDto);

        }
        return reportList;
    }

    @Override
    public List<OvertimeReportDto> getUserOvertimeReportById(Long userId, int year, int month) {
        // Fetch user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<OvertimeReportDto> report = new ArrayList<>();

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Fetch attendance records for the current user within the current month
        List<Attendance> attendances = attendanceRepository.findByCreatedByUserAndDateBetween(user, startDate, endDate);

        // Regular work hours (8:00 AM - 5:00 PM)
        LocalTime regularStartTime = LocalTime.of(8, 0);
        LocalTime regularEndTime = LocalTime.of(16, 0);
        Duration regularWorkHours = Duration.between(regularStartTime, regularEndTime);
        for( Attendance attendance : attendances) {
            if (attendance.getQlockIn() != null && attendance.getQlockOut() != null) {
                LocalTime clockIn = attendance.getQlockIn().toLocalTime();
                LocalTime clockOut = attendance.getQlockOut().toLocalTime();

                Duration workedHours = Duration.between(clockIn, clockOut);
                Duration overtimeHours = Duration.ZERO;


                if (clockOut.isAfter(regularEndTime)) {
                    overtimeHours = Duration.between(regularEndTime, clockOut);
                }

                // Calculate total hours as regular work hours + overtime hours
                Duration totalHours = regularWorkHours.plus(overtimeHours);

                // Build the report for each day
                OvertimeReportDto overtimeReportDto = OvertimeReportDto.builder()
                        .date(attendance.getDate()) // Assuming Attendance entity has a 'date' field
                        .regularHours(regularWorkHours)
                        .overtimeHours(overtimeHours)
                        .totalHours(totalHours)
                        .build();

                report.add(overtimeReportDto);
            }
        }

        return report;
    }

    private long calculateTotalWorkdays(int year, int month) {
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());

        return firstDayOfMonth.datesUntil(lastDayOfMonth.plusDays(1))
                .filter(date -> !(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY))
                .count();

    }


    private LateComersReport getLateComersReport(User user, int year, int month) {


        List<Attendance> attendances = attendanceRepository.findByCreatedByUserAndYearAndMonth(user.getId(), year, month);

        long totalWorkDays = calculateTotalWorkdays(year, month);

        Map<User, List<Attendance>> attendanceByUser = attendances.stream()
                .collect(Collectors.groupingBy(Attendance::getCreatedByUser));

        List<LateComersReport> userReports = attendanceByUser.entrySet().stream()
                .map(entry -> {
                    User users = entry.getKey();
                    List<Attendance> attendance = entry.getValue();

                    Long lateArrivals = attendance.stream()
                            .filter(att -> att.getQlockIn() != null && att.getQlockIn().toLocalTime().isAfter(LocalTime.of(8,0)))
                            .count();

                    Double latePercentage = (double) lateArrivals / totalWorkDays * 100;

                    return LateComersReport.builder()
                            .employeeId(users.getEmployeeId())
                            .fullName(users.getFullName())
                            .lateArrivals(lateArrivals)
                            .lateComersRate(Math.ceil(latePercentage))
                            .totalWorkDays(totalWorkDays)
                            .build();
                }).collect(Collectors.toList());

        LateComersReport report = userReports.getFirst();

        return report;
    }

}
