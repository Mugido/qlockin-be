package com.decagosq022.qlockin.infrastructure.controller;

import com.decagosq022.qlockin.entity.User;
import com.decagosq022.qlockin.payload.request.EmployeeRegistrationRequest;
import com.decagosq022.qlockin.payload.response.AllEmployeeProfileResponse;
import com.decagosq022.qlockin.payload.response.EmployeeRegistrationResponse;
import com.decagosq022.qlockin.payload.response.UserRegisterResponse;
import com.decagosq022.qlockin.payload.response.UploadResponse;
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
    public ResponseEntity<?> getUserByEmail() {
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

    @PostMapping(value = "/add-employee", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmployeeRegistrationResponse> addEmployee(
            @ModelAttribute EmployeeRegistrationRequest registrationRequest) throws IOException {

        EmployeeRegistrationResponse response = userService.addEmployee(registrationRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-employee")
    public ResponseEntity<String> deleteEmployee(@RequestParam Long userId) {
        String response = userService.deleteEmployee(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/activate")
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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        boolean isRemoved = userService.deleteUserById(id);
        if (isRemoved) {
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/all-employees")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<AllEmployeeProfileResponse>> getAllEmployeeProfiles() {
        List<AllEmployeeProfileResponse> profiles = userService.getAllEmployeeProfiles();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/employee-details/{id}")
    public ResponseEntity<?> getEmployeeDetails(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return ResponseEntity.ok("user details "+currentUsername+" "+id);
    }

    @PostMapping("/promote-to-admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> promoteToAdmin(@PathVariable Long userId) {
        String responseMessage = userService.promoteUserToAdmin(userId);
        return ResponseEntity.ok(responseMessage);
    }

}
