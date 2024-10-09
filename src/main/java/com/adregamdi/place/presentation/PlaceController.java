package com.adregamdi.place.presentation;

import com.adregamdi.core.annotation.AdminAuthorize;
import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.place.application.PlaceService;
import com.adregamdi.place.dto.PlaceReviewDTO;
import com.adregamdi.place.dto.request.CreatePlaceRequest;
import com.adregamdi.place.dto.request.CreatePlaceReviewRequest;
import com.adregamdi.place.dto.request.GetSortingPlacesRequest;
import com.adregamdi.place.dto.response.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
    public ResponseEntity<ApiResponse<CreatePlaceReviewResponse>> createReview(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestBody @Valid final CreatePlaceReviewRequest request
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<CreatePlaceReviewResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .data(placeService.createReview(userDetails.getUsername(), request))
                        .build()
                );
    }

    @PostMapping("/sort")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<List<GetSortingPlacesResponse>>> getSortingPlaces(@RequestBody @Valid final List<GetSortingPlacesRequest> requests) {
        return ResponseEntity.ok()
                .body(ApiResponse.<List<GetSortingPlacesResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(placeService.getSortingPlaces(requests))
                        .build()
                );
    }

    @GetMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetPlaceResponse>> get(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam("place_id") @Positive final Long placeId
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetPlaceResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(placeService.get(userDetails.getUsername(), placeId))
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

    @GetMapping("/popular")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetPopularPlacesResponse>> getPopularPlaces(
            @RequestParam(name = "last_id", required = false) final Long lastId,
            @RequestParam(name = "last_add_count", required = false) final Integer lastAddCount) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetPopularPlacesResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(placeService.getPopularPlaces(lastId, lastAddCount))
                        .build()
                );
    }

    @GetMapping("/review/me")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetMyPlaceReviewResponse>> getMyReview(@AuthenticationPrincipal final UserDetails userDetails) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetMyPlaceReviewResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(placeService.getMyReview(userDetails.getUsername()))
                        .build()
                );
    }

    @GetMapping("/reviews/member")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetMyPlaceReviewResponse>> getMemberReviews(@RequestParam(value = "member_id") final String memberId) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetMyPlaceReviewResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(placeService.getMyReview(memberId))
                        .build());
    }

    @GetMapping("/review")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetPlaceReviewResponse>> getReview(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam("place_review_id") final Long placeReviewId
    ) {
        PlaceReviewDTO placeReview = placeService.getReview(userDetails.getUsername(), placeReviewId);
        return ResponseEntity.ok()
                .body(ApiResponse.<GetPlaceReviewResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(GetPlaceReviewResponse.of(
                                placeReview.placeId(),
                                placeReview.placeReviewId(),
                                placeReview.title(),
                                placeReview.contentsLabel(),
                                placeReview.regionLabel(),
                                placeReview.visitDate(),
                                placeReview.content(),
                                placeReview.placeReviewImageList(),
                                placeReview.createdAt(),
                                placeReview.name(),
                                placeReview.profile(),
                                placeReview.handle()
                        ))
                        .build()
                );
    }

    @GetMapping("/reviews")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetPlaceReviewsResponse>> getReviews(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam("place_id") final Long placeId
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetPlaceReviewsResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(placeService.getReviews(userDetails.getUsername(), placeId))
                        .build()
                );
    }

    @GetMapping("/images")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetPlaceImagesResponse>> getPlaceImages(
            @RequestParam("place_id") final Long placeId
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetPlaceImagesResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(placeService.getPlaceImages(placeId))
                        .build()
                );
    }
}
