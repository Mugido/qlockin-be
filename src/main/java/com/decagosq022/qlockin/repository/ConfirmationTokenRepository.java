package com.decagosq022.qlockin.repository;


import com.decagosq022.qlockin.entity.ConfirmationTokenModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationTokenModel, Long > {
    Optional<ConfirmationTokenModel> findByToken(String token);
}
