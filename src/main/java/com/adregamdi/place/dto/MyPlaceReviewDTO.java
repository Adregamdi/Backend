package com.adregamdi.place.dto;

import com.adregamdi.place.domain.PlaceReviewImage;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record MyPlaceReviewDTO(
        String title,
        String contentsLabel,
        String regionLabel,
        Integer imageReviewCount,
        Integer shortsReviewCount,
        String travelDate,
        String content,
        List<PlaceReviewImage> placeReviewImageList,
        LocalDate createdAt
) {
    public static MyPlaceReviewDTO of(
            final String title,
            final String contentsLabel,
            final String regionLabel,
            final Integer imageReviewCount,
            final Integer shortsReviewCount,
            final String travelDate,
            final String content,
            final List<PlaceReviewImage> placeReviewImageList,
            final LocalDate createdAt
    ) {
        return MyPlaceReviewDTO.builder()
                .title(title)
                .contentsLabel(contentsLabel)
                .regionLabel(regionLabel)
                .imageReviewCount(imageReviewCount)
                .shortsReviewCount(shortsReviewCount)
                .travelDate(travelDate)
                .content(content)
                .placeReviewImageList(placeReviewImageList)
                .createdAt(createdAt)
                .build();
    }
}
