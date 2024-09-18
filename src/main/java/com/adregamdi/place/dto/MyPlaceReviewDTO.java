package com.adregamdi.place.dto;

import com.adregamdi.place.domain.PlaceReviewImage;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record MyPlaceReviewDTO(
        Long placeReviewId,
        String title,
        String contentsLabel,
        String regionLabel,
        Integer imageReviewCount,
        Integer shortsReviewCount,
        String visitDate,
        String content,
        List<PlaceReviewImage> placeReviewImageList,
        LocalDate createdAt
) {
    public static MyPlaceReviewDTO of(
            final Long placeReviewId,
            final String title,
            final String contentsLabel,
            final String regionLabel,
            final Integer imageReviewCount,
            final Integer shortsReviewCount,
            final String visitDate,
            final String content,
            final List<PlaceReviewImage> placeReviewImageList,
            final LocalDate createdAt
    ) {
        return MyPlaceReviewDTO.builder()
                .placeReviewId(placeReviewId)
                .title(title)
                .contentsLabel(contentsLabel)
                .regionLabel(regionLabel)
                .imageReviewCount(imageReviewCount)
                .shortsReviewCount(shortsReviewCount)
                .visitDate(visitDate)
                .content(content)
                .placeReviewImageList(placeReviewImageList)
                .createdAt(createdAt)
                .build();
    }
}
