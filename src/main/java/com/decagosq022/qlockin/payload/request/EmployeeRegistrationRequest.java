package com.decagosq022.qlockin.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.Timestamp;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRegistrationRequest {
//    private String photoUrl;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    private String preferredName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email Format")
    private String email;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = " JOb Title is required")
    private String jobTitle;

//    private LocalTime shiftTime;

    @NotBlank(message = "Shift cannot be blank")
    private String shiftTime;

    @NotNull(message = "Date of hire is required")
    private LocalDate dateOfHire;

    @NotBlank(message = "Division is required")
    private String division;

    private String employeeStatus;

    private String employeeId;

    private MultipartFile profilePicture;

}
