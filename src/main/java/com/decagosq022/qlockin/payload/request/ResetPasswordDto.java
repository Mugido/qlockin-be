package com.decagosq022.qlockin.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto {

    private String token;

    private String password;

    private String confirmPassword;
}
