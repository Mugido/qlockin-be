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
