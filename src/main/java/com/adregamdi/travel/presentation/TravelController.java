package com.adregamdi.travel.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.travel.application.TravelService;
import com.adregamdi.travel.dto.request.CreateMyTravelRequest;
import com.adregamdi.travel.dto.response.GetMyTravelResponse;
import com.adregamdi.travel.dto.response.GetMyTravelsResponse;
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
@RequestMapping("/api/travel")
@RestController
public class TravelController {
    private final TravelService travelService;

    @PostMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> createMyTravel(
            @RequestBody @Valid final CreateMyTravelRequest request,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        travelService.createMyTravel(request, userDetails.getUsername());
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
                );
    }

    @GetMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetMyTravelResponse>> getMyTravel(
            @RequestParam("travel_id") @Positive final Long travelId,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetMyTravelResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(travelService.getMyTravel(travelId, userDetails.getUsername()))
                        .build()
                );
    }

    @GetMapping("/list")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetMyTravelsResponse>> getMyTravels(
            @RequestParam(defaultValue = "0") final int page,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetMyTravelsResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(travelService.getMyTravels(page, userDetails.getUsername()))
                        .build()
                );
    }
}
