package com.decagosq022.qlockin.infrastructure.controller;


import com.decagosq022.qlockin.payload.request.ChangePasswordRequest;
import com.decagosq022.qlockin.payload.response.ChangePasswordResponse;
import com.decagosq022.qlockin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
