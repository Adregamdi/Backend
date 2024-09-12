package com.adregamdi.like.dto.request;

import com.adregamdi.like.domain.enumtype.ContentType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record DeleteLikeRequest(

        @Pattern(regexp = "(?i)PLACE|TRAVELOGUE|SHORTS", message = "장소 혹은 여행기, 쇼츠만 입력 가능합니다.")
        String contentType,
        @Positive(message = "식별 값은 자연수만 가능합니다.")
        Long contentId
) {

        public ContentType getContentType() {
                return ContentType.valueOf(contentType.toUpperCase());
        }
}
