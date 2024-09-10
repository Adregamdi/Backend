package com.adregamdi.like.dto.request;

import com.adregamdi.like.domain.enumtype.ContentType;

public record CreateLikesRequest (
        ContentType contentType,
        Long contentId
){
}