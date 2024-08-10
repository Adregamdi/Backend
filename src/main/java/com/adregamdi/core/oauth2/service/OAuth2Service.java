package com.adregamdi.core.oauth2.service;

import com.adregamdi.core.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2Service {
    private final JwtService jwtService;

    public void login(OAuth2AuthenticationToken token) {
    }
}
