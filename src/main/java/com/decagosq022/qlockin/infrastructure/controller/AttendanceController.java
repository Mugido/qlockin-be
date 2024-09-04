//package com.decagosq022.qlockin.infrastructure.controller;
//
//import com.decagosq022.qlockin.payload.response.AbsenteeismReportResponseDto;
//import com.decagosq022.qlockin.service.AttendanceService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/attendance")
//public class AttendanceController {
//
//    private final AttendanceService attendanceService;
//    @Autowired
//    public AttendanceController(AttendanceService attendanceService){
//        this.attendanceService = attendanceService;
//    }
//    @GetMapping("/absenteeism-report")
//    public ResponseEntity<List<AbsenteeismReportResponseDto>>getAbsenteeismReport(){
//        List<AbsenteeismReportResponseDto> report = attendanceService.getAbsenteeismReport();
//        return ResponseEntity.ok(report);
//    }
//}
