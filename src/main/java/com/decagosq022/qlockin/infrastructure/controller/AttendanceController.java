package com.decagosq022.qlockin.infrastructure.controller;

import com.decagosq022.qlockin.payload.response.*;
import com.decagosq022.qlockin.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.NotActiveException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping("/clockIn")
    public ResponseEntity<AttendanceResponse> clockIn(@RequestParam String employeeId) throws NotActiveException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        AttendanceResponse response = attendanceService.clockIn(currentUsername, employeeId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/clockout")
    public ResponseEntity<AttendanceResponse> clockOut(@RequestParam String employeeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        AttendanceResponse response = attendanceService.clockOut(currentUsername, employeeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getAllAttendanceData(@RequestParam LocalDate date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        AttendanceDataDto stats = attendanceService.getAllAttendanceStat(currentUsername, date);
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/absenteeism")
    public ResponseEntity<List<AbsenteeismReportResponseDto>> getMonthlyAbsenteeismReport(
            @RequestParam int year, @RequestParam int month){
        List<AbsenteeismReportResponseDto> report = attendanceService.getMonthlyAbsenteeismReport(year, month);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/attendancereport")
    public ResponseEntity<List<AttendanceReportDto>> getAttendanceReport(@RequestParam LocalDate date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
       List <AttendanceReportDto> report = attendanceService.getAttendanceReport(currentUsername, date);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/overtime")
    public ResponseEntity<AttendanceOvertimeDto> getOvertimeData(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        AttendanceOvertimeDto response = attendanceService.getOvertimeReport(currentUsername);
        return ResponseEntity.ok(response);
    }
}