package com.decagosq022.qlockin.payload.request;

import com.decagosq022.qlockin.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {


    @NotBlank(message = "Fullname is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email Format ")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Input your password again")
    private String confirmPassword;

    @NotBlank(message = "Input your position")
    private String position;


    private Gender gender;

}