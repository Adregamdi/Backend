package com.adregamdi.like.dto.request;

import com.adregamdi.like.domain.enumtype.SelectedType;

public record GetLikesContentsRequest(
        SelectedType selectedType,
        String memberId,
        Long lastLikeId,
        int size
) {

}