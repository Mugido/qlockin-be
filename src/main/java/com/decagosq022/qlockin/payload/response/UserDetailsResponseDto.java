package com.decagosq022.qlockin.payload.response;

import com.decagosq022.qlockin.enums.Gender;
import com.decagosq022.qlockin.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsResponseDto {

    private String fullName;
    private String email;
    private String phoneNumber;
    private Gender gender;
    private String position;
    private String employeeId;
    private String roleName;
    private String photoUrl;



}
