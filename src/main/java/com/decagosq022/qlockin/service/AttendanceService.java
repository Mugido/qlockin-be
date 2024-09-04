//package com.decagosq022.qlockin.service;
//
//import com.decagosq022.qlockin.payload.response.AbsenteeismReportResponseDto;
//
//import java.util.List;
//
//public interface AttendanceService {
//    List<AbsenteeismReportResponseDto> getAbsenteeismReport();
//
//}
package com.decagosq022.qlockin.service;

import com.decagosq022.qlockin.exceptions.NotActiveExceptions;
import com.decagosq022.qlockin.payload.response.AttendanceDataDto;
import com.decagosq022.qlockin.payload.response.AttendanceResponse;

import java.io.NotActiveException;
import java.time.LocalDate;

public interface AttendanceService {
    AttendanceResponse clockIn (String email, String employeeId) throws NotActiveExceptions, NotActiveException;

    AttendanceResponse clockOut (String email, String employeeId);

    AttendanceDataDto getAllAttendanceStat(String email, LocalDate date);

}
