package com.decagosq022.qlockin.infrastructure.controller;

import com.decagosq022.qlockin.payload.response.AttendanceDataDto;
import com.decagosq022.qlockin.payload.response.AttendanceResponse;
import com.decagosq022.qlockin.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.NotActiveException;
import java.time.LocalDate;

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
    public ResponseEntity<AttendanceDataDto> getAllAttendanceData(@RequestParam LocalDate date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        AttendanceDataDto stats = attendanceService.getAllAttendanceStat(currentUsername, date);
        return ResponseEntity.ok(stats);
    }
}