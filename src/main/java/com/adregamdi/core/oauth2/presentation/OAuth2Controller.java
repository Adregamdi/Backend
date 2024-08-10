package com.adregamdi.core.oauth2.presentation;

import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.core.jwt.service.JwtService;
import com.adregamdi.core.oauth2.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/oauth2/kakao")
@RestController
public class OAuth2Controller {
    private final OAuth2Service oAuth2Service;

    @GetMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(OAuth2AuthenticationToken token) {
        oAuth2Service.login(token);
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK)
                        .build());
    }
}
