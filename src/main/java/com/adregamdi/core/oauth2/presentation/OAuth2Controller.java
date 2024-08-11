package com.adregamdi.core.oauth2.presentation;

import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.core.oauth2.dto.LoginResponse;
import com.adregamdi.core.oauth2.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/oauth2")
@RestController
public class OAuth2Controller {
    private final OAuth2Service oAuth2Service;

    @GetMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(String oauthAccessToken) {
        return ResponseEntity.ok()
                .body(ApiResponse.<LoginResponse>builder()
                        .statusCode(HttpStatus.OK)
                        .data(oAuth2Service.login(oauthAccessToken))
                        .build());
    }
}
