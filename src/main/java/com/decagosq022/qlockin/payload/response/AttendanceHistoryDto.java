package com.decagosq022.qlockin.payload.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceHistoryDto {
    private LocalDate date;
    private LocalDateTime qlockIn;
    private LocalDateTime qlockOut;
    private Long totalHours;
    private String status;
}
