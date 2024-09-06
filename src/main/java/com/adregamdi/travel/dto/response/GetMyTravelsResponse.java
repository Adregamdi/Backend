package com.adregamdi.travel.dto.response;

import java.time.LocalDate;

public record GetMyTravelsResponse(
        LocalDate startDate,
        LocalDate endDate,
        String title
) {
}
