
package com.decagosq022.qlockin.service;

import com.decagosq022.qlockin.exceptions.NotActiveExceptions;
import com.decagosq022.qlockin.payload.response.*;

import java.io.NotActiveException;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    AttendanceResponse clockIn (String email, String employeeId) throws NotActiveExceptions, NotActiveException;

    AttendanceResponse clockOut (String email, String employeeId);

    AttendanceDataDto getAllAttendanceStat(String email, LocalDate date);

    List<AbsenteeismReportResponseDto> getMonthlyAbsenteeismReport(int year, int month);

    List<AttendanceReportDto> getAttendanceReport(String email, LocalDate date);

    AttendanceOvertimeDto getOvertimeReport(String email);

    List<AttendanceOvertimeDto> getGeneralOverTimeReport(String email);

    List<LateComersReport> generalLateComersReport (String email, int year, int month );

}
