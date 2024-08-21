package com.decagosq022.qlockin.service;

import com.decagosq022.qlockin.payload.request.LoginRequest;
import com.decagosq022.qlockin.payload.request.UserRegisterRequest;
import com.decagosq022.qlockin.payload.response.LoginResponse;
import com.decagosq022.qlockin.payload.response.UserRegisterResponse;
import jakarta.mail.MessagingException;

public interface UserService {

    UserRegisterResponse registerUser(UserRegisterRequest registerRequest) throws MessagingException;

    LoginResponse loginUser(LoginRequest loginRequest);
}
