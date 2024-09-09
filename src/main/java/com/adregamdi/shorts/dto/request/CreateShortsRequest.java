package com.adregamdi.shorts.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public record CreateShortsRequest(
        @Positive(message = "쇼츠 ID 값을 추가해주세요.")
        Long shortsId,
        @NotBlank(message = "제목을 작성해주세요.")
        String title,
        @Positive
        Long placeId,
        @Positive
        Long travelogueId,
        @NotEmpty(message = "동영상을 업로드해주세요.")
        String videoUrl,
        @NotEmpty(message = "썸네일이 포함되지 않았습니다.")
        String thumbnailUrl
) {
}
