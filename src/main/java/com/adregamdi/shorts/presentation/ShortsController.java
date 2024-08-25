package com.adregamdi.shorts.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.core.jwt.service.JwtService;
import com.adregamdi.shorts.application.ShortsService;
import com.adregamdi.shorts.dto.request.CreateShortsRequest;
import com.adregamdi.shorts.dto.request.UpdateShortsRequest;
import com.adregamdi.shorts.dto.response.GetShortsResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shorts")
public class ShortsController {

    private final JwtService jwtService;
    private final ShortsService shortsService;

    @GetMapping("/list")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetShortsResponse>> getShortsList(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(value = "page_no", defaultValue = "0") @PositiveOrZero final int pageNo,
            @RequestParam(value = "size", defaultValue = "10") @Positive final int size
    ) {

//        String memberId = userDetails.getUsername();
        String memberId = "test";
        shortsService.getShortsList()

        return null;
    }

    @PostMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> createShorts(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateShortsRequest request
    ) {

//        UUID memberId = jwtService.extractMemberId(accessToken)
//                .orElseThrow(IllegalArgumentException::new);

        UUID memberId = UUID.randomUUID();
        shortsService.saveShorts(userDetails.getUsername(), request);

        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @PutMapping("/{shorts_id")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> updateShorts(@RequestHeader("Authorization") String accessToken,
                                                          @Valid @RequestBody UpdateShortsRequest request) {

//        UUID memberId = jwtService.extractMemberId(accessToken)
//                .orElseThrow(IllegalArgumentException::new);

        UUID memberId = UUID.randomUUID();
        shortsService.updateShorts(memberId, request);

        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .build());
    }

}
