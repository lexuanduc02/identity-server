package com.daniel.identity_service.service.impl;

import com.daniel.identity_service.dto.request.AuthenticationRequest;
import com.daniel.identity_service.dto.request.IntrospectRequest;
import com.daniel.identity_service.dto.request.LogoutRequest;
import com.daniel.identity_service.dto.request.RefreshTokenRequest;
import com.daniel.identity_service.dto.response.AuthenticationResponse;
import com.daniel.identity_service.dto.response.IntrospectResponse;
import com.daniel.identity_service.entity.InvalidatedToken;
import com.daniel.identity_service.entity.Permission;
import com.daniel.identity_service.entity.Role;
import com.daniel.identity_service.entity.User;
import com.daniel.identity_service.exception.AppException;
import com.daniel.identity_service.exception.ErrorCode;
import com.daniel.identity_service.repository.InvalidatedTokenRepository;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SECRET_KEY;

    @NonFinal
    @Value("${jwt.access-token-expiration-in-ms}")
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
                .accessToken(token)
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        boolean isValid = true;
        String token = request.getAccessToken();

        try {
            verifyToken(token);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .isValid(isValid)
                .build();
    }

    @Override
    public void logout(LogoutRequest request) throws JOSEException, ParseException {
        var signedJWT = verifyToken(request.getAccessToken());

        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

        String jti = claimsSet.getJWTID();
        Date expirationTime = claimsSet.getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiresAt(expirationTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws JOSEException, ParseException {
        var signedJWT = verifyToken(request.getAccessToken());

        var jwtClaimsSet = signedJWT.getJWTClaimsSet();

        var user = userRepository.findById(jwtClaimsSet.getSubject())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jwtClaimsSet.getJWTID())
                .expiresAt(jwtClaimsSet.getExpirationTime())
                .build();
        invalidatedTokenRepository.save(invalidatedToken);

        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .accessToken(token)
                .build();
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // Get the user's permissions
        List<String> permissions = new ArrayList<>();
        for (Role role : user.getRoles()) {
            permissions.add("ROLE_" + role.getName());
            permissions.addAll(role.getPermissions().stream().map(Permission::getName).toList());
        }

        // Add payload (claims) to the token
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .subject(user.getId())
                .issuer("daniel.com")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("scope", buildScopeString(permissions))
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

    private String buildScopeString(List<String> scopes) {
        StringBuilder scopeString = new StringBuilder();
        for (String scope : scopes) {
            scopeString.append(scope).append(" ");
        }
        return scopeString.toString().trim();
    }

    private SignedJWT verifyToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        if (!signedJWT.verify(verifier) || expirationTime.before(new Date())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return signedJWT;
    }
}
