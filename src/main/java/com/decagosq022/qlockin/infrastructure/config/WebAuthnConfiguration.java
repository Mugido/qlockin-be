package com.decagosq022.qlockin.infrastructure.config;

import com.decagosq022.qlockin.service.RegistrationService;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class WebAuthnConfiguration {

    @Bean
    @Autowired
    public RelyingParty relyingParty(RegistrationService registrationService, WebAuthnConfig webAuthnConfig){

        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
                .id(webAuthnConfig.getHostname())
                .name(webAuthnConfig.getDisplay())
                .build();

        return RelyingParty.builder()
                .identity(rpIdentity)
                .credentialRepository(registrationService)
                .origins(Collections.singleton(webAuthnConfig.getOrigin()))
                .build();
    }
}
