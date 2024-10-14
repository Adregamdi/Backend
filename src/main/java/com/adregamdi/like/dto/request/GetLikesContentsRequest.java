package com.adregamdi.like.dto.request;

import com.adregamdi.core.constant.ContentType;

public record GetLikesContentsRequest(
        ContentType selectedType,
        String memberId,
        Long lastLikeId,
        int size
) {

}