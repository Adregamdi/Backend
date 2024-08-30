package com.adregamdi.travel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public record CreateMyTravelRequest(
        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate,
        @NotBlank
        String title,

        @NotNull
        List<DayInfo> dayList
) {
    public record DayInfo(
            @NotNull
            @Positive
            Integer day,

            String memo,

            @NotNull
            List<PlaceInfo> placeList
    ) {
    }

    public record PlaceInfo(
            @NotNull
            @Positive
            Long placeId,

            @NotNull
            @Positive
            Integer placeOrder
    ) {
    }
}
