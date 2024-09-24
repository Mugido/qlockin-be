package com.decagosq022.qlockin.service.Impl;

import com.decagosq022.qlockin.entity.ConfirmationToken;
import com.decagosq022.qlockin.entity.JToken;
import com.decagosq022.qlockin.entity.Role;
import com.decagosq022.qlockin.entity.User;
import com.decagosq022.qlockin.enums.RoleName;
import com.decagosq022.qlockin.enums.TokenType;
import com.decagosq022.qlockin.exceptions.*;
import com.decagosq022.qlockin.infrastructure.config.JwtService;
import com.decagosq022.qlockin.payload.request.*;
import com.decagosq022.qlockin.payload.response.*;
import com.decagosq022.qlockin.repository.ConfirmationTokenRepository;
import com.decagosq022.qlockin.repository.JTokenRepository;
import com.decagosq022.qlockin.repository.RoleRepository;
import com.decagosq022.qlockin.repository.UserRepository;
import com.decagosq022.qlockin.service.EmailService;
import com.decagosq022.qlockin.service.FileUploadService;
import com.decagosq022.qlockin.service.UserService;
import com.decagosq022.qlockin.utils.AccountUtils;
import com.decagosq022.qlockin.utils.EmailBody;
import com.decagosq022.qlockin.utils.EmailUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final JTokenRepository jTokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AccountUtils accountUtils;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final FileUploadService fileUploadService;
    private final EmailUtil emailUtil;


    @Override
    public UserRegisterResponse registerUser(UserRegisterRequest registerRequest) throws MessagingException {
        // Validate email format
        String emailRegex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(registerRequest.getEmail());

        if (!matcher.matches()){
            throw new RuntimeException("Invalid email domain");
        }
        String[] emailParts = registerRequest.getEmail().split("\\.");
        if (emailParts.length < 2 || emailParts[emailParts.length - 1].length() < 2){
            throw new RuntimeException("Invalid email domain");
        }
        if(!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())){
            throw new RuntimeException("Passwords do not match");
        }
        Optional<User> existingUser = userRepository.findByEmail(registerRequest.getEmail());
        if (existingUser.isPresent()) {
            throw new AlreadyExistsException("User already exists, please Login");
        }

        Role userRole = roleRepository.findByRoleName(RoleName.USER).orElseThrow( () -> new NotFoundException("Role not found"));
        User user = User.builder()
                .fullName(registerRequest.getFullName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phoneNumber(registerRequest.getPhoneNumber())
                .position(registerRequest.getPosition())
                .employeeId(accountUtils.generateUniqueId())
                .roles(Set.of(userRole))
                .gender(registerRequest.getGender())
                .build();

        User savedUser = userRepository.save(user);
        ConfirmationToken confirmationToken = new ConfirmationToken(savedUser);

        confirmationTokenRepository.save(confirmationToken);

        String confirmationUrl = emailUtil.getVerificationUrl(confirmationToken.getToken());

        EmailDetails emailDetails = EmailDetails.builder()
                .fullName(savedUser.getFullName())
                .employeeId(savedUser.getEmployeeId())
                .recipient(savedUser.getEmail())
                .subject("QLOCKIN ACCOUNT CREATED SUCCESSFULLY")
                .link(confirmationUrl)
                .build();

        emailService.sendSimpleMailMessage(emailDetails,"email");


        return UserRegisterResponse.builder()
                .responseCode("001")
                .responseMessage("You have been registered successfully, Kindly check your email")
                .build();
    }


    private void saveUserToken(User user, String jwtToken){
        var token = JToken.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        jTokenRepository.save(token);
    }
    private void revokeAllUserTokens(User user){
        var validUserTokens = jTokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        jTokenRepository.saveAll(validUserTokens);
    }

    @Override
    public LoginResponse loginUser(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        User person = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()-> new NotFoundException("User is not found"));

        if (!person.isEnabled()){
            throw new NotEnabledException("User account is not enabled. Please check your email to confirm your account.");
        }

        var jwtToken = jwtService.generateToken(person);
        revokeAllUserTokens(person);
        saveUserToken(person, jwtToken);

        return LoginResponse.builder()
                .responseCode("002")
                .responseMessage("Your have been logged in successfully")
                .loginInfo(LoginInfo.builder()
                        .email(person.getEmail())
                        .token(jwtToken)
                        .build())
                .build();
    }

    @Override
    public UserDetailsResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if(user == null){
            throw new NotFoundException("User Not Found");
        }
        Set<Role> roles = user.getRoles();
        List<String> roleNames = roles.stream()
                .map(role -> role.getRoleName().name())
                .toList();

        StringBuilder roleStr = new StringBuilder();
        for(String roleNameStr : roleNames){
            roleStr.append(roleNameStr);
        }

        return UserDetailsResponseDto.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .employeeId(user.getEmployeeId())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .roleName(roleStr.toString())
                .position(user.getPosition())
                .profilePicture(user.getProfilePicture())
                .build();
    }

    @Override
    public String forgetPassword(ForgetPasswordRequestDto requestDto) {

        User user = userRepository.findByEmail(requestDto.getEmail()).orElse(null);

        if(user == null){
            throw new NotFoundException("User does not exist");
        }


        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenCreationTime(LocalDateTime.now());
        userRepository.save(user);

        String resetUrl = emailUtil.getResetUrl(token);

//        // click this link to reset password;
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("FORGET PASSWORD")
                .messageBody(EmailBody.buildEmail(user.getFullName(), resetUrl ))
                .build();

        //send the reset password link
        emailService.mimeMailMessage(emailDetails);

        return "A reset password link has been sent to your account email address";

    }


    // reset the password for real now

    @Override
    public String resetPassword(ResetPasswordDto requestDto) {

        User user = userRepository.findByResetToken(requestDto.getToken()).orElse(null);

        if(user == null){
            throw new NotFoundException("You cannot make this change!");
        }

        if (Duration.between(user.getResetTokenCreationTime(), LocalDateTime.now()).toMinutes() > 5) {
            user.setResetToken(null);
            userRepository.save(user);
            throw new NotEnabledException("Token has expired!");
        }

        if(!requestDto.getPassword().equals(requestDto.getConfirmPassword())){
            throw new NotEnabledException("Confirmation Password does not match!");
        }

        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));


        // set the reset token to null
        user.setResetToken(null);

        userRepository.save(user);

        return "Password Reset Successful";
    }

    @Override
    public ChangePasswordResponse changePassword(ChangePasswordRequest changePasswordRequest) {

        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Check if new passwords match
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())) {
            return ChangePasswordResponse.builder()
                    .responseCode("400")
                    .responseMessage("New passwords do not match")
                    .build();
        }

        // Check old password
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            return ChangePasswordResponse.builder()
                    .responseCode("401")
                    .responseMessage("Old password is incorrect")
                    .build();
        }

        // Update password
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);

        return ChangePasswordResponse.builder()
                .responseCode("200")
                .responseMessage("Password changed successfully")
                .build();
    }

    @Override
    public EmployeeRegistrationResponse addEmployee(EmployeeRegistrationRequest registerRequest) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Authenticated user not found"));

        boolean isUser = authenticatedUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals(RoleName.USER));

        if (!isUser) {
            throw new SecurityException("Only users with the USER role can add new employees.");
        }

        Optional<User> existingUser = userRepository.findByEmail(registerRequest.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("User already exists, please Login");
        }

        Role userRole = roleRepository.findByRoleName(RoleName.USER)
                .orElseThrow(() -> new NotFoundException("Role 'USER' not found in the database."));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        // Validate employeeId format
        String employeeIdPattern = "^[A-Z]{2}\\d{6}$"; // Two letters followed by six digits
        if (!registerRequest.getEmployeeId().matches(employeeIdPattern)) {
            throw new InvalidInputException("Employee ID must follow the pattern: Two letters followed by six digits (e.g., JQ959623).");
        }

        // Check if employeeId already exists
        Optional<User> existingEmployeeId = userRepository.findByEmployeeId(registerRequest.getEmployeeId());
        if (existingEmployeeId.isPresent()) {
            throw new UserAlreadyExistsException("Employee ID already exists, please use a different one.");
        }

        String generatedPassword = AccountUtils.generatePassword();

        // Upload profile picture if provided
        String profilePictureUrl = null;
        if (registerRequest.getProfilePicture() != null && !registerRequest.getProfilePicture().isEmpty()) {
            try {
                profilePictureUrl = fileUploadService.uploadFile(registerRequest.getProfilePicture());
            } catch (FileSizeLimitExceededException e) {
                throw new InvalidInputException("Profile picture file size exceeds the allowable limit.");
            } catch (IOException e) {
                throw new InvalidInputException("An error occurred while uploading the profile picture. Please try again.");
            }
        }

        User user = User.builder()
                .profilePicture(profilePictureUrl)
                .employeeId(registerRequest.getEmployeeId())
                .fullName(registerRequest.getFirstName() + " " + registerRequest.getLastName())
                .dateOfBirth(registerRequest.getDateOfBirth())
                .preferredName(registerRequest.getPreferredName())
                .email(registerRequest.getEmail())
                .department(registerRequest.getDepartment())
                .position(registerRequest.getJobTitle())
                .shiftTime(registerRequest.getShiftTime()) // the time format is 09:30:00
                .dateOfHire(registerRequest.getDateOfHire())
                .division(registerRequest.getDivision())
                .employeeStatus(registerRequest.getEmployeeStatus())
                .password(passwordEncoder.encode(generatedPassword))
                .roles(roles)
                .isActive(true)
                .enabled(true)
                .build();

        // Save the new user to the repository
        User savedUser = userRepository.save(user);

        // Send a welcome email
        String loginLink = emailUtil.getLoginUrl();
        String emailContent = EmailBody.addEmployeeEmailBody(savedUser.getFullName(), savedUser.getEmployeeId(), generatedPassword, loginLink);
        EmailDetails emailDetails = EmailDetails.builder()
                .fullName(savedUser.getFullName())
                .employeeId(savedUser.getEmployeeId())
                .recipient(savedUser.getEmail())
                .subject("Welcome to Qlock-in")
                .messageBody(emailContent)
                .build();

        emailService.mimeMailMessage(emailDetails);

        return EmployeeRegistrationResponse.builder()
                .responseCode("200")
                .responseMessage("Employee has been created successfully. Login details have been sent to their email.")
                .build();
    }


    @Override
    public String deleteEmployee(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (Objects.equals(user.getId(), userId))
        {

            throw new NotEnabledException("Cannot perform this action");
        }

        boolean existingUser= userRepository.existsById(userId);
        if (!existingUser) {
            throw new NotFoundException("User does not exist");
        }

        userRepository.deleteById(userId);
        return "User Deleted Successfully";
    }

    @Override
    public List<AllEmployeeProfileResponse> getAllEmployeeProfiles() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotFoundException("User is not authenticated");
        }

        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> AllEmployeeProfileResponse.builder()
                        .profilePicture(user.getProfilePicture())
                        .fullName(user.getFullName())
                        .position(user.getPosition())
                        .id(user.getId())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public UpdateEmployeeDetailsResponse updateUserDetails(UpdateUserDetailsRequest request, Long id) {
        String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // Fetch the user by email
        User user = userRepository.findByEmail(loggedInUserEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Update user details
        user.setFullName(request.getFirstName() + " " + request.getLastName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDepartment(request.getDepartment());
        user.setPosition(request.getJobTitle());
        user.setShiftTime(request.getShiftTime());
        user.setDateOfHire(request.getDateOfHire());
        user.setDivision(request.getDivision());
        user.setEmployeeStatus(request.getEmployeeStatus());

        if (request.getProfilePicture() != null) {
           try {
               String uploadedFilePath = fileUploadService.uploadFile(request.getProfilePicture());
               user.setProfilePicture(uploadedFilePath);
           } catch (FileSizeLimitExceededException e) {
               throw new InvalidInputException("Profile picture file size exceeds the allowable limit.");
           } catch (IOException e) {
               throw new InvalidInputException("An error occurred while uploading the profile picture. Please try again.");
           }
        }

        userRepository.save(user);

        return UpdateEmployeeDetailsResponse.builder()
                .responseCode("001")
                .responseMessage("Employee details has been updated successfully")
                .build();
    }

    @Override
    public UserDetailsResponse userDetails(Long id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotFoundException("User is not authenticated");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return UserDetailsResponse.builder()
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .department(user.getDepartment())
                .jobTitle(user.getPosition())
                .email(user.getEmail())
                .division(user.getDivision())
                .profilePicture(user.getProfilePicture())
                .build();

    }


    @Override
    public ResponseEntity<UploadResponse> uploadProfilePics(MultipartFile file, String email) {
        Optional<User> userEntityOptional =
                userRepository.findByEmail(email);

        String fileUrl = null;
        try{
            if (userEntityOptional.isPresent()){
                fileUrl = fileUploadService.uploadFile(file);
                User userEntity= userEntityOptional.get();
                userEntity.setProfilePicture(fileUrl);

                userRepository.save(userEntity);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(
                new UploadResponse(
                        "Upload Successfully",
                        fileUrl
                )
        );
    }

    public String activateUser(Long userId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (user.isActive()) {
            return "User is already active";
        }

        user.setActive(true);
        userRepository.save(user);

        return "User activated successfully";
    }

    public boolean deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
