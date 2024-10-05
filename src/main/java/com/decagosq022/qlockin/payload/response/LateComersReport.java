package com.decagosq022.qlockin.payload.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LateComersReport {
    private String fullName;
    private String employeeId;
    private Long totalWorkDays;
    private Long lateArrivals;
    private Double lateComersRate;
    private Long id;
}
