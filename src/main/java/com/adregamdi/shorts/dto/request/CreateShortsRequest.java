package com.adregamdi.shorts.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateShortsRequest(
        @NotBlank(message = "제목을 작성해주세요.")
        String title,
        @NotBlank
        @Positive
        Long travelReviewNo
) {
}
