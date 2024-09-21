package com.adregamdi.core.oauth2.presentation;

import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.core.jwt.service.JwtService;
import com.adregamdi.core.oauth2.application.OAuth2Service;
import com.adregamdi.core.oauth2.dto.LoginRequest;
import com.adregamdi.core.oauth2.dto.LoginResponse;
import com.adregamdi.member.domain.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/oauth2")
@RestController
public class OAuth2Controller {
    private final OAuth2Service oAuth2Service;
    private final JwtService jwtService;

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<LoginResponse>> getToken(@RequestParam("id") String memberId) {
        return ResponseEntity.ok()
                .body(ApiResponse.<LoginResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(new LoginResponse(jwtService.createNoExpiresAtAccessToken(memberId, Role.ADMIN), ""))
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid final LoginRequest request) {
        return ResponseEntity.ok()
                .body(ApiResponse.<LoginResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(oAuth2Service.login(request))
                        .build());
    }
}
