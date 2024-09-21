package com.adregamdi.place.dto.response;

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
        String visitDate,
        String content,
        List<PlaceReviewImage> placeReviewImageList,
        LocalDate createdAt,
        String name,
        String profile,
        String handle
) {
    public static GetPlaceReviewResponse of(
            final Long placeReviewId,
            final String title,
            final String contentsLabel,
            final String regionLabel,
            final String visitDate,
            final String content,
            final List<PlaceReviewImage> placeReviewImages,
            final LocalDate createdAt,
            final String name,
            final String profile,
            final String handle
    ) {
        return GetPlaceReviewResponse.builder()
                .placeReviewId(placeReviewId)
                .title(title)
                .contentsLabel(contentsLabel)
                .regionLabel(regionLabel)
                .visitDate(visitDate)
                .content(content)
                .placeReviewImageList(placeReviewImages)
                .createdAt(createdAt)
                .name(name)
                .profile(profile)
                .handle(handle)
                .build();
    }
}
