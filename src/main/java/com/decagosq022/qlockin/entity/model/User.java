package com.decagosq022.qlockin.entity.model;


import com.decagosq022.qlockin.entity.enums.Gender;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "user_tbl")
public class User extends BaseEntity{

    private String fullName;

    private String email;

    private String phoneNumber;

    private String password;

    private LocalDateTime qLockIn;

    private LocalDateTime qLockOut;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String position;

    private boolean enabled;


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

}
