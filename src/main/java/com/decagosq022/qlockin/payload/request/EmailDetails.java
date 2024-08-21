package com.decagosq022.qlockin.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDetails {

    private String fullName;

    private String employeeId;

    private String recipient;

    private String messageBody;

    private String attachment;

    private String subject;

    private String link;
}
