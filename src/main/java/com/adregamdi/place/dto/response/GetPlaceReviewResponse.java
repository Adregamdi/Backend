package com.adregamdi.place.dto.response;

import com.adregamdi.place.domain.Place;
import com.adregamdi.place.domain.PlaceReview;
import com.adregamdi.place.domain.PlaceReviewImage;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record GetPlaceReviewResponse(
        Long placeReviewId,
        String title,
        String contentsLabel,
        String regionLabel,
        LocalDate visitDate,
        String content,
        List<PlaceReviewImage> placeReviewImageList,
        LocalDate createdAt
) {
    public static GetPlaceReviewResponse of(
            final Place place,
            final PlaceReview placeReview,
            final List<PlaceReviewImage> placeReviewImages
    ) {
        return GetPlaceReviewResponse.builder()
                .placeReviewId(placeReview.getPlaceReviewId())
                .title(place.getTitle())
                .contentsLabel(place.getContentsLabel())
                .regionLabel(place.getRegionLabel())
                .visitDate(placeReview.getVisitDate())
                .content(placeReview.getContent())
                .placeReviewImageList(placeReviewImages)
                .createdAt(LocalDate.from(placeReview.getCreatedAt()))
                .build();
    }
}
