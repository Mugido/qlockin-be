package com.decagosq022.qlockin.payload.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AbsenteeismReportDto {
    private String month;
    private Long absentDays;
    private Long totalWorkDays;
    private Double absenteeismRate;

}
