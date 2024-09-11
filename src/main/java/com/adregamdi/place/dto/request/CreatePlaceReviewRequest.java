package com.adregamdi.place.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreatePlaceReviewRequest(
        @NotNull
        @Positive
        Long placeId,
        @NotNull
        @NotEmpty
        String content,
        List<PlaceReviewImageInfo> placeReviewImageInfos
) {
    public record PlaceReviewImageInfo(
            String url
    ) {
    }
}
