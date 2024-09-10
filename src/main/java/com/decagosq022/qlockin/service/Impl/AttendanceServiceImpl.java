
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
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
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

        //User user = userRepository.findByEmailAndEmployeeId(email,employeeId);
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
        User user = userRepository.findByEmailAndEmployeeId(email,employeeId);

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
            int totalWorkDays = endOfMonth.getDayOfMonth();
            int absentDays = totalWorkDays - userAttendances.size();

            double absenteeismRate = (double) absentDays / totalWorkDays * 100;

        return AbsenteeismReportResponseDto.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .absentDays(absentDays)
                .totalWorkDays(totalWorkDays)
                .absenteeismRate(absenteeismRate)
                .build();

        }).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceReportDto> getAttendanceReport(String email, LocalDate date) {
        userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        List<Attendance> attendances = attendanceRepository.findAllByDate(date);

        return attendances.stream().map(attendance -> {
            String status;
            LocalDateTime qlockIn = attendance.getQlockIn();
            LocalDateTime qlockOut = attendance.getQlockOut();
            long totalHours = 0;

            if (qlockIn == null) {
                status = "Absent";
            } else {
                totalHours = Duration.between(qlockIn, qlockOut != null ? qlockOut : LocalDateTime.now()).toHours();

                status = qlockIn.toLocalTime().isBefore(LocalTime.of(8, 0)) ? "Early" : "Late";
            }

            // No return keyword, directly creating the AttendanceReportDto object
            return new AttendanceReportDto(
                    attendance.getCreatedByUser().getEmployeeId(),
                    attendance.getCreatedByUser().getFullName(),
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

}
