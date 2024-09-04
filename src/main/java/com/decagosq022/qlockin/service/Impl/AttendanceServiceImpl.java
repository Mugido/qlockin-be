//package com.decagosq022.qlockin.service.Impl;
//
//import com.decagosq022.qlockin.payload.response.AbsenteeismReportResponseDto;
//import com.decagosq022.qlockin.repository.AttendanceRepository;
//import com.decagosq022.qlockin.service.AttendanceService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class AttendanceServiceImpl implements AttendanceService {
//
//    private final AttendanceRepository attendanceRepository;
//    @Autowired
//    public AttendanceServiceImpl(AttendanceRepository attendanceRepository){
//        this.attendanceRepository = attendanceRepository;
//    }
//
//
//
//    @Override
//    public List<AbsenteeismReportResponseDto> getAbsenteeismReport() {
//        return attendanceRepository.findTotalAbsenteesPerUser();
//    }
//}
package com.decagosq022.qlockin.service.Impl;

import com.decagosq022.qlockin.entity.Attendance;
import com.decagosq022.qlockin.entity.User;
import com.decagosq022.qlockin.exceptions.NotClockedInException;
import com.decagosq022.qlockin.exceptions.NotFoundException;
import com.decagosq022.qlockin.payload.response.AttendanceDataDto;
import com.decagosq022.qlockin.payload.response.AttendanceResponse;
import com.decagosq022.qlockin.repository.AttendanceRepository;
import com.decagosq022.qlockin.repository.UserRepository;
import com.decagosq022.qlockin.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.NotActiveException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
     private final AttendanceRepository attendanceRepository;
     private final UserRepository userRepository;
    @Override
    public AttendanceResponse clockIn(String email, String employeeId) throws NotActiveException {

        User user = userRepository.findByEmailAndEmployeeId(email,employeeId);

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
}
