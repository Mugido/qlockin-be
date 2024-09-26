package com.decagosq022.qlockin.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailsResponse {

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String department;

    private String jobTitle;

    private String email;

    private String division;

    private String profilePicture;

    private String employeeId;

    private LocalDate dateOfHire;

    private LocalDate dateOfBirth;

    private String shiftTime;

    private String employeeStatus;
}
