package com.adregamdi.place.dto.request;

public record CreatePlaceRequest(
        String name,
        String information,
        String image,
        Double latitude,
        Double longitude,
        Integer locationNo
) {
}
