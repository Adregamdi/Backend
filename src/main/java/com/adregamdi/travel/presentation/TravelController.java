package com.adregamdi.travel.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.travel.application.TravelService;
import com.adregamdi.travel.dto.request.CreateMyTravelRequest;
import com.adregamdi.travel.dto.request.GetMyTravelRequest;
import com.adregamdi.travel.dto.response.GetMyTravelResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/travel")
@RestController
public class TravelController {
    private final TravelService travelService;

    @GetMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetMyTravelResponse>> getMyTravel(
            @RequestBody @Valid List<GetMyTravelRequest> requests,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetMyTravelResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(travelService.getMyTravel(requests, userDetails.getUsername()))
                        .build()
                );
    }

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
}
