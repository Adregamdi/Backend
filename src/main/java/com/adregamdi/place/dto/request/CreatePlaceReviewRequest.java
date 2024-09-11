package com.adregamdi.place.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePlaceReviewRequest(
        @NotNull
        @Positive
        Long placeId,
        @NotNull
        @NotEmpty
        String content
) {
}
