package com.decagosq022.qlockin.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceReportDto {
    private String employeeId;

    private String fullName;

    private LocalDateTime qlockIn;

    private LocalDateTime qlockOut;

    private Long totalHours;

    private String status;
}