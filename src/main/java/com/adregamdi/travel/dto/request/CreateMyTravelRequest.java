package com.adregamdi.travel.dto.request;

import com.adregamdi.travel.dto.TravelListDTO;
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
        @Positive
        Integer day,
        String memo,
        @NotNull
        List<TravelListDTO> travelList
) {
}