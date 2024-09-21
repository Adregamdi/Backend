package com.adregamdi.like.dto.request;

import com.adregamdi.like.domain.enumtype.ContentType;
import lombok.Builder;

@Builder
public record DeleteLikeRequest(

        String contentType,
        Long contentId
) {

        public ContentType getContentType() {
                return ContentType.valueOf(contentType.toUpperCase());
        }
}
