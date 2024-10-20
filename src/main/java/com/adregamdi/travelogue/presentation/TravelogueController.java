package com.adregamdi.travelogue.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.travelogue.application.TravelogueService;
import com.adregamdi.travelogue.dto.request.CreateMyTravelogueRequest;
import com.adregamdi.travelogue.dto.response.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/travelogue")
@RestController
public class TravelogueController {
    private final TravelogueService travelogueService;

    @PostMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<CreateMyTravelogueResponse>> createMyTravelogue(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestBody @Valid final CreateMyTravelogueRequest request
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<CreateMyTravelogueResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .data(travelogueService.createMyTravelogue(userDetails.getUsername(), request))
                        .build()
                );
    }

    @GetMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetTravelogueResponse>> get(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam("travelogue_id") @Positive final Long travelogueId
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetTravelogueResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(travelogueService.get(userDetails.getUsername(), travelogueId))
                        .build()
                );
    }

    @GetMapping("/list/me")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetMyTraveloguesResponse>> getMyTravelogues(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(defaultValue = "0") final int page
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetMyTraveloguesResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(travelogueService.getMyTravelogues(userDetails.getUsername(), page))
                        .build()
                );
    }

    @GetMapping("/list/member")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetMemberTraveloguesResponse>> getMemberTravelogues(
            @RequestParam(value = "member_id") final String memberId,
            @RequestParam(value = "travelogue_id", required = false)
            @PositiveOrZero final Long lastTravelogueId,
            @RequestParam(value = "size", required = false, defaultValue = "10")
            @Positive final int size
    ) {
        GetMemberTraveloguesResponse response = travelogueService.getMemberTravelogues(
                memberId,
                lastTravelogueId != null ? lastTravelogueId : Long.MAX_VALUE,
                size
        );
        return ResponseEntity.ok()
                .body(ApiResponse.<GetMemberTraveloguesResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(response)
                        .build());

    }

    @GetMapping("/list/recent")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetRecentTraveloguesResponse>> getRecentTravelogues(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(defaultValue = "0") final int page
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetRecentTraveloguesResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(travelogueService.getRecentTravelogues(userDetails.getUsername(), page))
                        .build()
                );
    }

    @GetMapping("/list/hot")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetHotTraveloguesResponse>> getHotTravelogues(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(value = "like_count", required = false) @PositiveOrZero final Integer lastLikeCount,
            @RequestParam(value = "size", defaultValue = "10") @Positive final int size
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetHotTraveloguesResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(travelogueService.getHotTravelogues(userDetails.getUsername(), lastLikeCount != null ? lastLikeCount : Integer.MAX_VALUE, size))
                        .build());
    }

    @DeleteMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> deleteMyTravelogue(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam("travelogue_id") @Positive final Long travelogueId
    ) {
        travelogueService.deleteMyTravelogue(userDetails.getUsername(), travelogueId);
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .build()
                );
    }
}
