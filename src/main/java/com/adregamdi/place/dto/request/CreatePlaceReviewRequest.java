package com.adregamdi.place.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public record CreatePlaceReviewRequest(
        @NotNull
        @Positive
        Long placeId,
        @NotNull
        @NotEmpty
        LocalDate visitDate,
        @NotNull
        @NotEmpty
        String content,
        List<PlaceReviewImageInfo> placeReviewImageList
) {
    public record PlaceReviewImageInfo(
            String url
    ) {
    }
}
