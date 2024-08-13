package com.adregamdi.shorts.presentation;

import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.core.jwt.service.JwtService;
import com.adregamdi.shorts.application.ShortsService;
import com.adregamdi.shorts.dto.request.CreateShortsRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shorts")
public class ShortsController {

    private final JwtService jwtService;
    private final ShortsService shortsService;

    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> createShorts(@RequestHeader("Authorization") String accessToken,
                                                          @RequestPart("shorts") MultipartFile video,
                                                          @Valid @RequestPart("body")CreateShortsRequest request) {

        UUID memberId = jwtService.extractMemberId(accessToken)
                .orElseThrow(IllegalArgumentException::new);

//        String shortsUrl = shortsService.uploadVideo(video, memberId);

        return null;

    }

}
