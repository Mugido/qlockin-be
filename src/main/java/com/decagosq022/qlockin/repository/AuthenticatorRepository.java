package com.decagosq022.qlockin.repository;

import com.decagosq022.qlockin.entity.AuthUser;
import com.decagosq022.qlockin.entity.Authenticator;
import com.yubico.webauthn.data.ByteArray;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthenticatorRepository extends JpaRepository<Authenticator, Long> {

    List<Authenticator> findAllByUser(AuthUser user);

    Optional<Authenticator> findByName(String userName);

    List<Authenticator> findAllByCredentialId(ByteArray credentialId);
}
