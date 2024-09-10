package com.adregamdi.shorts.dto.response;

import com.adregamdi.shorts.domain.Shorts;

public record SaveVideoResponse (
        String videoUrl,
        String videoThumbnailUrl,
        Long shortsId
){

    public static SaveVideoResponse ofEntity(Shorts shorts) {
        return new SaveVideoResponse(
                shorts.getShortsVideoUrl(),
                shorts.getThumbnailUrl(),
                shorts.getShortsId()
        );
    }
}
