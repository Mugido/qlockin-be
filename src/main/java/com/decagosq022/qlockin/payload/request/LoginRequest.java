package com.decagosq022.qlockin.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email format")
    private String email;

    @NotBlank( message = "Password is required")
    private String password;
}
