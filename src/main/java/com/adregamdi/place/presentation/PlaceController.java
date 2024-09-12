package com.adregamdi.place.presentation;

import com.adregamdi.core.annotation.AdminAuthorize;
import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.place.application.PlaceService;
import com.adregamdi.place.dto.request.CreatePlaceRequest;
import com.adregamdi.place.dto.request.CreatePlaceReviewRequest;
import com.adregamdi.place.dto.request.GetSortingPlacesRequest;
import com.adregamdi.place.dto.response.GetPlaceResponse;
import com.adregamdi.place.dto.response.GetPlacesResponse;
import com.adregamdi.place.dto.response.GetSelectionBasedRecommendationPlacesResponse;
import com.adregamdi.place.dto.response.GetSortingPlacesResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/place")
@RestController
public class PlaceController {
    private final PlaceService placeService;

    @PostMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> create(@RequestBody final CreatePlaceRequest request) {
        placeService.create(request);
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
                );
    }

    @PostMapping("/use-api")
    @AdminAuthorize
    public ResponseEntity<ApiResponse<Void>> createByAPI() {
        placeService.createByAPI();
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
                );
    }

    @PostMapping("/review")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> createReview(
            @RequestBody @Valid final CreatePlaceReviewRequest request,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        placeService.createReview(request, userDetails.getUsername());
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
                );
    }

    @GetMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetPlaceResponse>> get(@RequestParam("place_id") @PositiveOrZero final Long placeId) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetPlaceResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(placeService.get(placeId))
                        .build()
                );
    }

    @GetMapping("/list")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetPlacesResponse>> getPlaces(
            @RequestParam("page_no") @PositiveOrZero final int pageNo,
            @RequestParam("name") final String name
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetPlacesResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(placeService.getPlaces(pageNo, name))
                        .build()
                );
    }

    @GetMapping("/selection-based")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<List<GetSelectionBasedRecommendationPlacesResponse>>> getSelectionBasedRecommendationPlaces(
            @RequestParam("latitude") @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") final Double latitude,
            @RequestParam("longitude") @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") final Double longitude
    ) throws URISyntaxException {
        return ResponseEntity.ok()
                .body(ApiResponse.<List<GetSelectionBasedRecommendationPlacesResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(placeService.getSelectionBasedRecommendationPlaces(latitude, longitude))
                        .build()
                );
    }

    @GetMapping("/sort")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<List<GetSortingPlacesResponse>>> getSortingPlaces(@RequestBody @Valid final List<GetSortingPlacesRequest> requests) {
        return ResponseEntity.ok()
                .body(ApiResponse.<List<GetSortingPlacesResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(placeService.getSortingPlaces(requests))
                        .build()
                );
    }
}
