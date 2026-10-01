package com.daniel.identity_service.service;

import com.daniel.identity_service.dto.request.AuthenticationRequest;
import com.daniel.identity_service.dto.request.IntrospectRequest;
import com.daniel.identity_service.dto.request.LogoutRequest;
import com.daniel.identity_service.dto.request.RefreshTokenRequest;
import com.daniel.identity_service.dto.response.AuthenticationResponse;
import com.daniel.identity_service.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);

    IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;

    void logout(LogoutRequest request) throws JOSEException, ParseException;

    AuthenticationResponse refreshToken(RefreshTokenRequest request) throws JOSEException, ParseException;
}
