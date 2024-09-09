package com.adregamdi.place.dto.response;

import com.adregamdi.place.dto.PlaceCoordinate;

import java.util.List;

public record GetSortingPlacesResponse(
        Integer day,
        List<PlaceCoordinate> placeCoordinates
) {
}