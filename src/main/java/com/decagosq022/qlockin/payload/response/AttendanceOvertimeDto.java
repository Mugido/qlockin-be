package com.decagosq022.qlockin.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

//public class AttendanceOvertimeDto {

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public class AttendanceOvertimeDto {
		private String fullName;
		private String employeeId;
		private Duration regularHours;
		private Duration overtimeHours;
		private Duration totalHours;
		private Long id;
	}

