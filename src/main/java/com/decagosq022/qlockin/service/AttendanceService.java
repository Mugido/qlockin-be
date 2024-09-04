
package com.decagosq022.qlockin.service;

import com.decagosq022.qlockin.exceptions.NotActiveExceptions;
import com.decagosq022.qlockin.payload.response.AbsenteeismReportResponseDto;
import com.decagosq022.qlockin.payload.response.AttendanceDataDto;
import com.decagosq022.qlockin.payload.response.AttendanceResponse;

import java.io.NotActiveException;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    AttendanceResponse clockIn (String email, String employeeId) throws NotActiveExceptions, NotActiveException;

    AttendanceResponse clockOut (String email, String employeeId);

    AttendanceDataDto getAllAttendanceStat(String email, LocalDate date);

    List<AbsenteeismReportResponseDto> getMonthlyAbsenteeismReport(int year, int month);

}
