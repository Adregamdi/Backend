package com.adregamdi.core.oauth2.presentation;

import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.core.oauth2.application.OAuth2Service;
import com.adregamdi.core.oauth2.dto.LoginRequest;
import com.adregamdi.core.oauth2.dto.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/oauth2")
@RestController
public class OAuth2Controller {
    private final OAuth2Service oAuth2Service;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid final LoginRequest request) {
        return ResponseEntity.ok()
                .body(ApiResponse.<LoginResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(oAuth2Service.login(request))
                        .build());
    }
}
