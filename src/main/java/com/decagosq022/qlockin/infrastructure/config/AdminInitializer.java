package com.decagosq022.qlockin.infrastructure.config;

import com.decagosq022.qlockin.entity.Role;
import com.decagosq022.qlockin.entity.User;
import com.decagosq022.qlockin.enums.RoleName;
import com.decagosq022.qlockin.exceptions.NotFoundException;
import com.decagosq022.qlockin.repository.RoleRepository;
import com.decagosq022.qlockin.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Component
public class AdminInitializer {
    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Value("${qlockin.admin.email}")
    private String adminEmail;

    @Value("${qlockin.admin.password}")
    private String adminPassword;

    @Value("${qlockin.admin.fullname}")
    private String adminFullname;

    @Value("${qlockin.admin.position}")
    private String adminPosition;

    @Value("${qlockin.admin.phoneNumber}")
    private String adminPhoneNumber;

    @Value("${qlockin.admin.employeeId}")
    private String adminEmployeeId;

    // Constructor injection
    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }


    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            Optional<Role> adminRole = roleRepository.findByRoleName(RoleName.ADMIN);
            if (adminRole.isEmpty()) {
                throw new NotFoundException("Default role ADMIN not found in the database.");
            }
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole.get());

            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User adminUser = new User();
                adminUser.setEmail(adminEmail);
                adminUser.setPassword(passwordEncoder.encode(adminPassword));
                adminUser.setRoles(roles);
                adminUser.setEnabled(true);
                adminUser.setFullName(adminFullname);
                adminUser.setPosition(adminPosition);
                adminUser.setPhoneNumber(adminPhoneNumber);
                adminUser.setEmployeeId(adminEmployeeId);

                userRepository.save(adminUser);

                logger.info("Admin user seeded into the database.");
            } else {
                logger.info("Admin user already exists.");
            }

        };
    }
}
