package com.daniel.identity_service.configuration.security;

import com.daniel.identity_service.dto.request.IntrospectRequest;
import com.daniel.identity_service.dto.response.IntrospectResponse;
import com.daniel.identity_service.exception.AppException;
import com.daniel.identity_service.exception.ErrorCode;
import com.daniel.identity_service.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@NullMarked
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${jwt.signer-key}")
    private String signerKey;

    private final AuthenticationService authenticationService;

    @Nullable
    private NimbusJwtDecoder nimbusJwtDecoder;

    @Override
    public Jwt decode(String token) throws JwtException {
        IntrospectResponse response;
        try {
            response = authenticationService.introspect(
                    IntrospectRequest.builder()
                            .accessToken(token)
                            .build()
            );
        } catch (Exception e) {
            throw new JwtException(e.getMessage(), e);
        }

        if (!response.isValid()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }
}