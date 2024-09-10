package com.decagosq022.qlockin.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AbsenteeismReportResponseDto {
    private String userId;
    private String fullName;
    private int absentDays;
    private int totalWorkDays;
    private double absenteeismRate;
}
