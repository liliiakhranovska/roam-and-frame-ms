package com.roamandframe.customer.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
class JwtDecoderConfig {

    @Bean
    JwtDecoder jwtDecoder(@Value("${app.jwt.public-key}") String publicKey) {
        try {
            RSAPublicKey key = (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)));
            return NimbusJwtDecoder.withPublicKey(key).build();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("JWT_PUBLIC_KEY is not a valid base64 X.509 RSA public key", e);
        }
    }
}
