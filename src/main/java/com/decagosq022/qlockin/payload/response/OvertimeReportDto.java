package com.decagosq022.qlockin.payload.response;

import lombok.*;

import java.time.Duration;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OvertimeReportDto {
    private LocalDate date;
    private Duration regularHours;
    private Duration overtimeHours;
    private Duration totalHours;
}

