package com.daniel.identity_service.service.impl;

import com.daniel.identity_service.dto.request.AuthenticationRequest;
import com.daniel.identity_service.dto.request.IntrospectRequest;
import com.daniel.identity_service.dto.response.AuthenticationResponse;
import com.daniel.identity_service.dto.response.IntrospectResponse;
import com.daniel.identity_service.entity.User;
import com.daniel.identity_service.exception.AppException;
import com.daniel.identity_service.exception.ErrorCode;
import com.daniel.identity_service.repository.UserRepository;
import com.daniel.identity_service.service.AuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    UserRepository userRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SECRET_KEY;

    @NonFinal
    @Value("${jwt.expirationTime}")
    protected long EXPIRATION_TIME;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!isAuthenticated) {
            throw new AppException(ErrorCode.USER_AUTHENTICATION_FAILED);
        }

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        String token = request.getToken();

        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        boolean isValid = signedJWT.verify(verifier);

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        boolean isExpired = expirationTime.before(new Date());

        return IntrospectResponse.builder()
                .isValid(isValid && !isExpired)
                .build();
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // Add payload (claims) to the token
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("daniel.com")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Token valid for 1 hour
//                .claim("scope", buildScopeString(user.getRoles().toArray(new String[0])))
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        // Sign the token with a secret key
        try {
            jwsObject.sign(new MACSigner(SECRET_KEY));
            return jwsObject.serialize();
        } catch (Exception e) {
            log.error("Error signing the token", e);
            throw new RuntimeException("Error signing the token", e);
        }
    }

    private String buildScopeString(String[] scopes) {
        StringBuilder scopeString = new StringBuilder();
        for (String scope : scopes) {
            scopeString.append(scope).append(" ");
        }
        return scopeString.toString().trim();
    }
}
