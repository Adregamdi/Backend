package com.adregamdi.travelogue.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.travelogue.application.TravelogueService;
import com.adregamdi.travelogue.dto.request.CreateMyTravelogueRequest;
import com.adregamdi.travelogue.dto.response.CreateMyTravelogueResponse;
import com.adregamdi.travelogue.dto.response.GetMyTraveloguesResponse;
import com.adregamdi.travelogue.dto.response.GetRecentTraveloguesResponse;
import com.adregamdi.travelogue.dto.response.GetTravelogueResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
            @RequestBody @Valid final CreateMyTravelogueRequest request,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<CreateMyTravelogueResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .data(travelogueService.createMyTravelogue(request, userDetails.getUsername()))
                        .build()
                );
    }

    @GetMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetTravelogueResponse>> get(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam("travelogue_id") @Positive Long travelogueId
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
            @RequestParam(defaultValue = "0") final int page,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetMyTraveloguesResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(travelogueService.getMyTravelogues(page, userDetails.getUsername()))
                        .build()
                );
    }

    @GetMapping("/list/recent")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetRecentTraveloguesResponse>> getRecentTravelogues(@RequestParam(defaultValue = "0") final int page) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetRecentTraveloguesResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(travelogueService.getRecentTravelogues(page))
                        .build()
                );
    }
}
