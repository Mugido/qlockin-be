package com.decagosq022.qlockin.entity;

import com.decagosq022.qlockin.enums.RoleName;
import lombok.*;
import jakarta.persistence.*;

import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "role_tbl")
public class Role extends BaseEntity{

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleName roleName;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public Role(RoleName roleName) {
        this.roleName = roleName;
    }
}
