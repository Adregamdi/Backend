package com.adregamdi.like.dto.request;

import com.adregamdi.core.constant.ContentType;
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
