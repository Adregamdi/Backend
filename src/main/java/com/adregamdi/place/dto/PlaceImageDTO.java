package com.adregamdi.place.dto;

import java.time.LocalDate;

public record PlaceImageDTO(
        LocalDate createdAt,
        String url
) {
}
