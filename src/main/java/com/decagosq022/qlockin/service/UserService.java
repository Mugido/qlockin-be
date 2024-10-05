package com.decagosq022.qlockin.service;

import com.decagosq022.qlockin.entity.User;
import com.decagosq022.qlockin.payload.request.ForgetPasswordRequestDto;
import com.decagosq022.qlockin.payload.request.LoginRequest;
import com.decagosq022.qlockin.payload.request.ResetPasswordDto;
import com.decagosq022.qlockin.payload.request.UserRegisterRequest;
import com.decagosq022.qlockin.payload.request.*;
import com.decagosq022.qlockin.payload.response.*;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    UserRegisterResponse registerUser(UserRegisterRequest registerRequest) throws MessagingException;

    LoginResponse loginUser(LoginRequest loginRequest);

    UserDetailsResponseDto getUserByEmail(String email);

    String forgetPassword(ForgetPasswordRequestDto requestDto);

    String resetPassword(ResetPasswordDto requestDto);

    ChangePasswordResponse changePassword(ChangePasswordRequest changePasswordRequest, String email);

    ResponseEntity<UploadResponse> uploadProfilePics(MultipartFile file, String email);

    EmployeeRegistrationResponse addEmployee(EmployeeRegistrationRequest registerRequest, String email) throws IOException;

    String deleteEmployee (Long userId, String email);

    String activateUser(Long userId);

    boolean deleteUserById(String email, String id);

    List<AllEmployeeProfileResponse> getAllEmployeeProfiles(String email);

    UpdateEmployeeDetailsResponse updateUserDetails (String email, UpdateUserDetailsRequest request);

    UserDetailsResponse userDetails (String email, Long id);


    String promoteUserToAdmin(Long userId);

}
