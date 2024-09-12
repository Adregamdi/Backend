package com.adregamdi.shorts.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
public record GetShortsByPlaceIdRequest(
        @JsonProperty("place_id")
        @Positive
        Long placeId,

        @JsonProperty("last_shorts_id")
        @PositiveOrZero
        Long lastShortsId,

        @Positive
        int size

) {

//    public GetShortsByPlaceIdRequest {
//        if (lastShortsId == null) {
//            lastShortsId = 0L;
//        }
//        if (size == 0) {
//            size = 10;
//        }
//    }
}
