package com.roamandframe.gateway.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


@Configuration
class JwtKeys {

    @Bean
    JwtEncoder jwtEncoder(@Value("${app.jwt.private-key}") String privateKey,
                          @Value("${app.jwt.public-key}") String publicKey) {
        RSAKey jwk = new RSAKey.Builder(readPublicKey(publicKey))
                .privateKey(readPrivateKey(privateKey))
                .build();
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(jwk)));
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${app.jwt.public-key}") String publicKey) {
        return NimbusJwtDecoder.withPublicKey(readPublicKey(publicKey)).build();
    }

    private static RSAPublicKey readPublicKey(String base64) {
        try {
            return (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(base64)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("JWT_PUBLIC_KEY is not a valid base64 X.509 RSA public key", e);
        }
    }

    private static RSAPrivateKey readPrivateKey(String base64) {
        try {
            return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("JWT_PRIVATE_KEY is not a valid base64 PKCS#8 RSA private key", e);
        }
    }
}
