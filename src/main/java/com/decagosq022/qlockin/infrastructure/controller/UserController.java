package com.decagosq022.qlockin.infrastructure.controller;

import com.decagosq022.qlockin.entity.User;
import com.decagosq022.qlockin.payload.request.ChangePasswordRequest;
import com.decagosq022.qlockin.payload.request.EmployeeRegistrationRequest;
import com.decagosq022.qlockin.payload.request.UpdateUserDetailsRequest;
import com.decagosq022.qlockin.payload.response.*;
import com.decagosq022.qlockin.service.ReverseAuthService;
import com.decagosq022.qlockin.service.UserService;
import jakarta.mail.MessagingException;
import com.decagosq022.qlockin.utils.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;



    @GetMapping("/")
    @PreAuthorize(" hasRole('USER')")
    public ResponseEntity<?> getUserByEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(userService.getUserByEmail(currentUsername));
    }
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminByEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(userService.getUserByEmail(currentUsername));
    }

    @PutMapping("/profile-picture")
    public ResponseEntity<UploadResponse> profileUpload(@RequestParam("file") MultipartFile profilePic) {
        //Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
// Get the email of the user
        String currentUser = authentication.getName();
        if (profilePic.getSize() > AppConstants.MAX_FILE_SIZE) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new UploadResponse("File size exceed the normal limit"));
        }
        return userService.uploadProfilePics(profilePic, currentUser);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        ChangePasswordResponse response = userService.changePassword(changePasswordRequest, email);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/add-employee", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addEmployee(
            @ModelAttribute EmployeeRegistrationRequest registrationRequest) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        EmployeeRegistrationResponse response = userService.addEmployee(registrationRequest, email);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-employee")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEmployee(@RequestParam Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        String response = userService.deleteEmployee(userId, email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> activateUser(@PathVariable Long id) {
        String result = userService.activateUser(id);

        if ("User activated successfully".equals(result)) {
            return ResponseEntity.ok(result);
        } else if ("User is already active".equals(result)) {
            return ResponseEntity.status(409).body(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @DeleteMapping("/delete-by-employeeId")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUserById(@RequestParam String employeeId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        boolean isRemoved = userService.deleteUserById(email, employeeId);
        if (isRemoved) {
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/all-employees")
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<List<AllEmployeeProfileResponse>> getAllEmployeeProfiles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        List<AllEmployeeProfileResponse> profiles = userService.getAllEmployeeProfiles(email);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/employee-details/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> getEmployeeDetails(@PathVariable Long id ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return ResponseEntity.ok(userService.userDetails(email, id));
    }

    @PutMapping(value = "/update-employee-details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUserDetails(
            @ModelAttribute UpdateUserDetailsRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        UpdateEmployeeDetailsResponse response = userService.updateUserDetails(email, request);

        return ResponseEntity.ok(response);
    }




}
