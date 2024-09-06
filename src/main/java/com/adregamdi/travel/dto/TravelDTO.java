package com.adregamdi.travel.dto;

import java.time.LocalDate;

public record TravelDTO(
        Long travelId,
        LocalDate startDate,
        LocalDate endDate,
        String title
) {
}
