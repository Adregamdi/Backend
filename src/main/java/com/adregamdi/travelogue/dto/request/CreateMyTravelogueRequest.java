package com.adregamdi.travelogue.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public record CreateMyTravelogueRequest(
        @Positive
        Long travelogueId,
        @NotNull
        @Positive
        Long travelId,
        @NotNull
        String title,
        String introduction,
        List<TravelogueImageInfo> travelogueImageList,
        List<DayInfo> dayList
) {
    public record TravelogueImageInfo(
            String url
    ) {
    }

    public record DayInfo(
            @NotNull
            LocalDate date,
            @NotNull
            @Positive
            Integer day,
            String content,
            List<PlaceReviewInfo> placeReviewList
    ) {
    }

    public record PlaceReviewInfo(
            @NotNull
            @Positive
            Long placeReviewId
    ) {
    }
}
