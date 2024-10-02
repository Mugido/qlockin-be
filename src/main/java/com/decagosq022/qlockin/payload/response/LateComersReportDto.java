package com.decagosq022.qlockin.payload.response;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LateComersReportDto {
    private String month;
    private Long lateArrivals;
    private Long totalWorkDays;
    private Double lateComerRate;
}
