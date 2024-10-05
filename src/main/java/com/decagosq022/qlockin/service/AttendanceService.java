
package com.decagosq022.qlockin.service;

import com.decagosq022.qlockin.exceptions.NotActiveExceptions;
import com.decagosq022.qlockin.payload.response.*;

import java.io.NotActiveException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceService {
    AttendanceResponse clockIn (String email, String employeeId) throws NotActiveExceptions, NotActiveException;

    AttendanceResponse clockOut (String email, String employeeId);

    AttendanceDataDto getAllAttendanceStat(String email, LocalDate date);

    List<AbsenteeismReportResponseDto> getMonthlyAbsenteeismReport(int year, int month);

    List<AttendanceReportDto> getAttendanceReport(String email, LocalDate date);

    AttendanceOvertimeDto getOvertimeReport(String email);

    List<AttendanceOvertimeDto> getGeneralOverTimeReport(String email, LocalDate weekStart, LocalDate weekEnd);

    List<LateComersReport> generalLateComersReport (String email, int year, int month );

    AttendanceSummaryResponse getAttendanceSummary(String email);

    List<AttendanceReportDto> getAttendanceForToday(String email, LocalDate date);

    List<AttendanceHistoryDto> getUserAttendance(String email, int year, int month);

    List<AbsenteeismReportDto> getUserAbsenteeismReport(String email, int year);

    List<LateComersReportDto> getUserLateComersReport(String email, int year);

    List<OvertimeReportDto> getUserOvertimeReport(String email, int year, int month);

    List<AttendanceHistoryDto> getUserAttendanceById(Long userId, int year, int month);

    List<AbsenteeismReportDto> getUserAbsenteeismById(Long userId, int year);

    List<LateComersReportDto> getUserLateComersReportById(Long userId, int year);

    List<OvertimeReportDto> getUserOvertimeReportById(Long userId, int year, int month);

    Map<String, Object> getAttendanceForTheWeek(String email);


}
