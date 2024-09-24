package com.decagosq022.qlockin.payload.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceSummaryResponse {
    private Long totalDaysAtWork;
    private Long daysEarly;
    private Long  daysLate;
    private Long dayAbsent;
    private Long earlyDepartures;
}
