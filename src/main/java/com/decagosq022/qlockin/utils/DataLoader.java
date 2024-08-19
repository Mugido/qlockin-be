package com.decagosq022.qlockin.utils;

import com.decagosq022.qlockin.enums.RoleName;
import com.decagosq022.qlockin.entity.Role;
import com.decagosq022.qlockin.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (roleRepository.findByRoleName(RoleName.ADMIN).isEmpty()) {
            roleRepository.save(new Role(RoleName.ADMIN));
        }

        if (roleRepository.findByRoleName(RoleName.USER).isEmpty()) {
            roleRepository.save(new Role(RoleName.USER));
        }
    }
}
