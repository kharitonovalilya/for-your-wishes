package com.dev.foryourwishes.security.jwt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collection;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    @Bean
    public RSAPublicKey rsaPublicKey(JwtProperties properties) throws IOException {
        try (InputStream inputStream = properties.publicKey().getInputStream()) {
            return RsaKeyConverters.x509().convert(inputStream);
        }
    }

    @Bean
    public RSAPrivateKey rsaPrivateKey(JwtProperties properties) throws IOException {
        try (InputStream inputStream = properties.privateKey().getInputStream()) {
            return RsaKeyConverters.pkcs8().convert(inputStream);
        }
    }

    @Bean
    public JwtEncoder jwtEncoder(
            RSAPublicKey publicKey,
            RSAPrivateKey privateKey
    ) {
        return NimbusJwtEncoder
                .withKeyPair(publicKey, privateKey)
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey, JwtProperties properties) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withPublicKey(publicKey)
                .build();
        OAuth2TokenValidator<Jwt> defaultValidators =
                JwtValidators.createDefaultWithIssuer(properties.issuer());
        OAuth2TokenValidator<Jwt> audienceValidator =
                new JwtClaimValidator<Collection<String>>(
                        "aud",
                        audience -> audience != null
                                && audience.contains(properties.audience())
                );

        OAuth2TokenValidator<Jwt> validator =
                new DelegatingOAuth2TokenValidator<>(
                        defaultValidators,
                        audienceValidator
                );
        jwtDecoder.setJwtValidator(validator);
        return jwtDecoder;
    }

}
