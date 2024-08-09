package com.adregamdi.core.oauth2.presentation;

import com.adregamdi.core.handler.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/oauth2/kakao")
@RestController
public class OAuth2Controller {
    @GetMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(@RequestHeader("") String token) {



        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK)
                        .build());
    }
}
