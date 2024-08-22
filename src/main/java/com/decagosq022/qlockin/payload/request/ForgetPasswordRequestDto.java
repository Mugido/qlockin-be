package com.decagosq022.qlockin.payload.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgetPasswordRequestDto {

    private String email;
}
