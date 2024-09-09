package com.adregamdi.place.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PlaceCoordinate(
        @Positive
        Long placeId,
        @Positive
        Integer order,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0")
        Double latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0")
        Double longitude
) {
}