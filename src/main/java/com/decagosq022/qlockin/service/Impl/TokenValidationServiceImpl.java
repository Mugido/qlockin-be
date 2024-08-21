package com.decagosq022.qlockin.service.Impl;

import com.decagosq022.qlockin.entity.ConfirmationToken;
import com.decagosq022.qlockin.entity.User;
import com.decagosq022.qlockin.repository.ConfirmationTokenRepository;
import com.decagosq022.qlockin.repository.UserRepository;
import com.decagosq022.qlockin.service.TokenValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenValidationServiceImpl implements TokenValidationService {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;

    @Override
    public String validateToken(String token) {
        Optional<ConfirmationToken> confirmationTokenOptional = confirmationTokenRepository.findByToken(token);
        if (confirmationTokenOptional.isEmpty()) {
            return "Invalid token";
        }

        ConfirmationToken confirmationToken = confirmationTokenOptional.get();

        if (confirmationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "Token has expired";
        }
        User user = confirmationToken.getUsers();
        user.setEnabled(true);
        userRepository.save(user);

        confirmationTokenRepository.delete(confirmationToken);

        return "Email confirmation is successful";
    }
}
