package com.decagosq022.qlockin.service;

import com.decagosq022.qlockin.entity.User;
import com.decagosq022.qlockin.payload.request.ForgetPasswordRequestDto;
import com.decagosq022.qlockin.payload.request.LoginRequest;
import com.decagosq022.qlockin.payload.request.ResetPasswordDto;
import com.decagosq022.qlockin.payload.request.UserRegisterRequest;
import com.decagosq022.qlockin.payload.request.*;
import com.decagosq022.qlockin.payload.response.ChangePasswordResponse;
import com.decagosq022.qlockin.payload.response.LoginResponse;
import com.decagosq022.qlockin.payload.response.UserDetailsResponseDto;
import com.decagosq022.qlockin.payload.response.UserRegisterResponse;
import jakarta.mail.MessagingException;

import java.util.Optional;

public interface UserService {

    UserRegisterResponse registerUser(UserRegisterRequest registerRequest) throws MessagingException;

    LoginResponse loginUser(LoginRequest loginRequest);

    UserDetailsResponseDto getUserByEmail(String email);

    String forgetPassword(ForgetPasswordRequestDto requestDto);

    String resetPassword(ResetPasswordDto requestDto);

    ChangePasswordResponse changePassword(ChangePasswordRequest changePasswordRequest);

}
