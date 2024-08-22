package com.decagosq022.qlockin.service.Impl;

import com.decagosq022.qlockin.entity.ConfirmationToken;
import com.decagosq022.qlockin.entity.JToken;
import com.decagosq022.qlockin.entity.Role;
import com.decagosq022.qlockin.entity.User;
import com.decagosq022.qlockin.enums.RoleName;
import com.decagosq022.qlockin.enums.TokenType;
import com.decagosq022.qlockin.exceptions.AlreadyExistsException;
import com.decagosq022.qlockin.exceptions.NotEnabledException;
import com.decagosq022.qlockin.exceptions.NotFoundException;
import com.decagosq022.qlockin.infrastructure.config.JwtService;
import com.decagosq022.qlockin.payload.request.EmailDetails;
import com.decagosq022.qlockin.payload.request.LoginRequest;
import com.decagosq022.qlockin.payload.request.UserRegisterRequest;
import com.decagosq022.qlockin.payload.response.LoginInfo;
import com.decagosq022.qlockin.payload.response.LoginResponse;
import com.decagosq022.qlockin.payload.response.UserRegisterResponse;
import com.decagosq022.qlockin.repository.ConfirmationTokenRepository;
import com.decagosq022.qlockin.repository.JTokenRepository;
import com.decagosq022.qlockin.repository.RoleRepository;
import com.decagosq022.qlockin.repository.UserRepository;
import com.decagosq022.qlockin.service.EmailService;
import com.decagosq022.qlockin.service.UserService;
import com.decagosq022.qlockin.utils.AccountUtils;
import com.decagosq022.qlockin.utils.EmailUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                .build();

        User savedUser = userRepository.save(user);
        ConfirmationToken confirmationToken = new ConfirmationToken(savedUser);
        confirmationTokenRepository.save(confirmationToken);

        String confirmationUrl = EmailUtil.getVerificationUrl(confirmationToken.getToken());

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
                .responseMessage("Your have been login successfully")
                .loginInfo(LoginInfo.builder()
                        .email(person.getEmail())
                        .token(jwtToken)
                        .build())
                .build();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}
