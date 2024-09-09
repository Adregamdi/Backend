package com.adregamdi.place.dto.request;

import com.adregamdi.place.dto.PlaceCoordinate;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record GetSortingPlacesRequest(
        @Positive
        Integer day,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0")
        Double startLatitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0")
        Double startLongitude,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0")
        Double endLatitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0")
        Double endLongitude,
        List<PlaceCoordinate> placeCoordinates
) {
}
