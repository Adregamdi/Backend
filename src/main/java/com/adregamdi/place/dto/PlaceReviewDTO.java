package com.adregamdi.place.dto;

import com.adregamdi.place.domain.PlaceReviewImage;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record PlaceReviewDTO(
        Long placeId,
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
    public static PlaceReviewDTO of(
            final Long placeId,
            final Long placeReviewId,
            final String title,
            final String contentsLabel,
            final String regionLabel,
            final String visitDate,
            final String content,
            final List<PlaceReviewImage> placeReviewImageList,
            final LocalDate createdAt,
            final String name,
            final String profile,
            final String handle
    ) {
        return PlaceReviewDTO.builder()
                .placeId(placeId)
                .placeReviewId(placeReviewId)
                .title(title)
                .contentsLabel(contentsLabel)
                .regionLabel(regionLabel)
                .visitDate(visitDate)
                .content(content)
                .placeReviewImageList(placeReviewImageList)
                .createdAt(createdAt)
                .name(name)
                .profile(profile)
                .handle(handle)
                .build();
    }
}
