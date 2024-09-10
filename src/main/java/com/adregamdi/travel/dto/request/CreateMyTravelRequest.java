package com.adregamdi.travel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public record CreateMyTravelRequest(
        @Positive
        Long travelId,
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate,
        @NotBlank
        String title,
        List<DayInfo> dayList
) {
    public record DayInfo(
            LocalDate date,
            @Positive
            Integer day,
            String memo,
            List<PlaceInfo> placeList
    ) {
    }

    public record PlaceInfo(
            @Positive
            Long placeId,
            @Positive
            Integer placeOrder
    ) {
    }
}
