package com.decagosq022.qlockin.infrastructure.controller;

import com.decagosq022.qlockin.payload.request.*;
import com.decagosq022.qlockin.payload.response.ChangePasswordResponse;
import com.decagosq022.qlockin.payload.response.LoginResponse;
import com.decagosq022.qlockin.payload.response.UserRegisterResponse;
import com.decagosq022.qlockin.service.TokenValidationService;
import com.decagosq022.qlockin.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")

public class AuthController {

    private final UserService userService;
    private final TokenValidationService tokenValidationService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> registerUser(@Valid @RequestBody UserRegisterRequest registerRequest) {

        try {
            UserRegisterResponse registerUser = userService.registerUser(registerRequest);
            if(!registerUser.equals("Invalid Email domain")){
                return ResponseEntity.ok(registerUser);
            }else {
                return ResponseEntity.badRequest().body(registerUser);
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest)  {
        return ResponseEntity.ok(userService.loginUser(loginRequest));

    }
    @GetMapping("/confirm")
    public ResponseEntity<?> confirmEmail(@RequestParam("token") String token){

        String result = tokenValidationService.validateToken(token);
        if ("Email confirmed successfully".equals(result)) {
            return ResponseEntity.ok(Collections.singletonMap("message", result));
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", result));
        }

    }

    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestBody ForgetPasswordRequestDto requestDto){

        String response = userService.forgetPassword(requestDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto requestDto){

        String response = userService.resetPassword(requestDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        ChangePasswordResponse response = userService.changePassword(changePasswordRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyToken() {
        // If the request reaches this point, it means the token is valid
        return ResponseEntity.ok(Collections.singletonMap("message", "Token is valid"));
    }
}