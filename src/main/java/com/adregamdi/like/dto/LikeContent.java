package com.adregamdi.like.dto;

import com.adregamdi.like.domain.enumtype.ContentType;

public interface LikeContent {

    ContentType getContentType();
    Long getContentId();
    String getThumbnailUrl();
    String getTitle();
}