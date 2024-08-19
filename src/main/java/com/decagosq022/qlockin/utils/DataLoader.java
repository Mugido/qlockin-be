package com.decagosq022.qlockin.utils;

import com.decagosq022.qlockin.entity.model.Role;
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
        if (roleRepository.findByName("USER").isEmpty()) {
            roleRepository.save(new Role("ADMIN"));
        }

        if (roleRepository.findByName("ADMIN").isEmpty()) {
            roleRepository.save(new Role( "USER"));
        }
    }
}
