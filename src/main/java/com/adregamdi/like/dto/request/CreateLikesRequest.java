package com.adregamdi.like.dto.request;

import com.adregamdi.like.domain.enumtype.ContentType;
import com.adregamdi.like.domain.enumtype.SelectedType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record CreateLikesRequest(
        @Pattern(regexp = "(?i)PLACE|TRAVELOGUE", message = "장소(PLACE) 혹은 여행기(TRAVELOGUE)만 입력 가능합니다.")
        String contentType,
        @Positive(message = "식별 값은 자연수만 가능합니다.")
        Long contentId
) {
    public ContentType getContentType() {
        return ContentType.valueOf(contentType.toUpperCase());
    }
}