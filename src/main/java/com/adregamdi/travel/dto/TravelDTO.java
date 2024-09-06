package com.adregamdi.travel.dto;

import java.time.LocalDate;

public record TravelDTO(
        LocalDate startDate,
        LocalDate endDate,
        String title
) {
}
