package com.decagosq022.qlockin.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceDataDto {
    private Long earlyArrivals;
    private Long lateArrivals;
    private Long absentEmployees;
    private Long activeEmployees;
    public Long earlyDepartures;
    public String responseCode;
    public String responseMessage;
}
