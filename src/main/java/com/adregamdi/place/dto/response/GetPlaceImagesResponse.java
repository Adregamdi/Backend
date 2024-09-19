package com.adregamdi.place.dto.response;

import com.adregamdi.place.dto.PlaceImageDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record GetPlaceImagesResponse(
        Long placeId,
        List<PlaceImageDTO> imageList
) {
    public static GetPlaceImagesResponse of(
            final Long placeId,
            final List<PlaceImageDTO> placeImageDTOS
    ) {
        return GetPlaceImagesResponse.builder()
                .placeId(placeId)
                .imageList(placeImageDTOS)
                .build();
    }
}
