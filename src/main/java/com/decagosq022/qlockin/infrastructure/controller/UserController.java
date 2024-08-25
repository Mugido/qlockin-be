package com.decagosq022.qlockin.infrastructure.controller;

import com.decagosq022.qlockin.entity.User;
import com.decagosq022.qlockin.payload.request.EmployeeRegistrationRequest;
import com.decagosq022.qlockin.payload.response.EmployeeRegistrationResponse;
import com.decagosq022.qlockin.payload.response.UserRegisterResponse;
import com.decagosq022.qlockin.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public ResponseEntity<?> getUserByEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(userService.getUserByEmail(currentUsername));
    }


    @PostMapping("/add-employee")
    public ResponseEntity<EmployeeRegistrationResponse> registerUser(@RequestBody EmployeeRegistrationRequest registrationRequest) throws MessagingException {;
        EmployeeRegistrationResponse response = userService.addEmployee(registrationRequest);
        return ResponseEntity.ok(response);
    }
}
