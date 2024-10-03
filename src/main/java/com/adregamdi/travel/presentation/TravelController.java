package com.adregamdi.travel.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.travel.application.TravelService;
import com.adregamdi.travel.dto.request.CreateMyTravelRequest;
import com.adregamdi.travel.dto.response.CreateMyTravelResponse;
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
    public ResponseEntity<ApiResponse<CreateMyTravelResponse>> createMyTravel(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestBody @Valid final CreateMyTravelRequest request
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<CreateMyTravelResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .data(travelService.createMyTravel(request, userDetails.getUsername()))
                        .build()
                );
    }

    @GetMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetMyTravelResponse>> getMyTravel(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam("travel_id") @Positive final Long travelId
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
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(defaultValue = "0") final int page
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetMyTravelsResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(travelService.getMyTravels(page, userDetails.getUsername()))
                        .build()
                );
    }

    @DeleteMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> deleteMyTravel(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam("travel_id") @Positive final Long travelId
    ) {
        travelService.deleteMyTravel(travelId, userDetails.getUsername());
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .build()
                );
    }
}
