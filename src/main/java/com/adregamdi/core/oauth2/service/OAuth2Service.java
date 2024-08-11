package com.adregamdi.core.oauth2.service;

import com.adregamdi.core.jwt.service.JwtService;
import com.adregamdi.core.oauth2.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2Service {
    private final JwtService jwtService;

    public LoginResponse login(String oauthAccessToken) {
        return null;
    }
}
