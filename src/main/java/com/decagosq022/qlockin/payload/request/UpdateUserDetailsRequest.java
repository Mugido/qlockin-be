package com.decagosq022.qlockin.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserDetailsRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    private String phoneNumber;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = " JOb Title is required")
    private String jobTitle;

    @NotBlank(message = "Shift cannot be blank")
    private String shiftTime;

    @NotNull(message = "Date of hire is required")
    private LocalDate dateOfHire;

    @NotBlank(message = "Division is required")
    private String division;

    @NotBlank(message = "Employee Status is required")
    private String employeeStatus;

    private MultipartFile profilePicture;
}
