package com.adregamdi.shorts.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.core.jwt.service.JwtService;
import com.adregamdi.shorts.application.ShortsService;
import com.adregamdi.shorts.dto.request.CreateShortsRequest;
import com.adregamdi.shorts.dto.request.UpdateShortsRequest;
import com.adregamdi.shorts.dto.response.GetShortsResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shorts")
public class ShortsController {

    private final JwtService jwtService;
    private final ShortsService shortsService;

    private static String MEMBER_ID_FOR_TEST = "memberId";

    @GetMapping("/list")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetShortsResponse>> getShorts(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(value = "shorts_id", required = false, defaultValue = "0") @PositiveOrZero final Long lastShortsId
    ) {

//        String memberId = userDetails.getUsername();
        GetShortsResponse response = shortsService.getShorts(MEMBER_ID_FOR_TEST, lastShortsId);

        return ResponseEntity
                .ok()
                .body(ApiResponse.<GetShortsResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(response)
                        .build());
    }

    @PostMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> createShorts(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateShortsRequest request
    ) {

//        UUID memberId = jwtService.extractMemberId(accessToken)
//                .orElseThrow(IllegalArgumentException::new);

        shortsService.saveShorts(userDetails.getUsername(), request);

        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @PutMapping("/{shorts_id}")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> updateShorts(@RequestHeader("Authorization") String accessToken,
                                                          @Valid @RequestBody UpdateShortsRequest request) {

//        UUID memberId = jwtService.extractMemberId(accessToken)
//                .orElseThrow(IllegalArgumentException::new);

        shortsService.updateShorts(MEMBER_ID_FOR_TEST, request);

        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .build());
    }

}
