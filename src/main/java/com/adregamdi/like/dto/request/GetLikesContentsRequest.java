package com.adregamdi.like.dto.request;

public record GetLikesContentsRequest(
        String memberId,
        Long lastLikeId,
        int size
) {

}