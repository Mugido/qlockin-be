package com.decagosq022.qlockin.entity;


import com.decagosq022.qlockin.enums.Gender;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "user_tbl")
public class User extends BaseEntity implements UserDetails {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email Format")
    private String email;

    @NotBlank(message = " PhoneNumber is required")
    private String phoneNumber;

    @NotBlank(message = " Password is required")
    private String password;

    @Transient
    private  String confirmPassword;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotBlank(message = " Position is required")
    private String position;

    private String employeeId;

    private String photoUrl;

    private boolean enabled;

    // this token is to handle reset password
    private String resetToken;

    // monitor token creation time and expiration time
    private LocalDateTime resetTokenCreationTime;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @OneToMany(mappedBy = "createdByUser")
    @JsonManagedReference
    private List<Attendance> createdAttendance;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JToken> jtokens;

    @OneToMany(mappedBy = "users",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfirmationToken> confirmationTokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }
}
