package com.decagosq022.qlockin.repository;

import com.decagosq022.qlockin.enums.RoleName;
import com.decagosq022.qlockin.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleName roleName);

}
