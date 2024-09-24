package com.decagosq022.qlockin.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailsResponse {

    private String fullName;

    private String phoneNumber;

    private String department;

    private String jobTitle;

    private String email;

    private String division;

    private String profilePicture;
}
