package com.adregamdi.shorts.presentation;

import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.core.jwt.service.JwtService;
import com.adregamdi.shorts.dto.request.CreateShortsRequest;
import com.adregamdi.shorts.dto.response.CreateShortsResponse;
import com.adregamdi.shorts.dto.response.GetShortsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shorts")
public class ShortsController {

    private final JwtService jwtService;
//    private final ShortsService shortsService;

    @PostMapping()
    public ResponseEntity<ApiResponse<CreateShortsResponse>> createShorts(
//            @RequestHeader("Authorization") String accessToken,
                                                          @Valid @RequestPart("body")CreateShortsRequest request) {

//        UUID memberId = jwtService.extractMemberId(accessToken)
//                .orElseThrow(IllegalArgumentException::new);

        UUID memberId = UUID.randomUUID();
//        CreateShortsResponse response = shortsService.uploadShorts(video, memberId, request);

//        return ResponseEntity.ok()
//                .body(ApiResponse.<CreateShortsResponse>builder()
//                .data(response)
//                .build());
        return null;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<GetShortsResponse>> getShortsList() {
        return null;
    }

}
