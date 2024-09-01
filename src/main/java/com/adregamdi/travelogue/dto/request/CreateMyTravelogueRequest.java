package com.adregamdi.travelogue.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public record CreateMyTravelogueRequest(
        @NotNull
        String title,
        String introduction,
        List<TravelogueImage> travelogueImageList,
        List<DayInfo> dayList
) {
    public record TravelogueImage(
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
            List<PlaceReviewImage> placeReviewImageList,
            List<PlaceReview> placeReviewList
    ) {
    }

    public record PlaceReviewImage(
            String url
    ) {
    }

    public record PlaceReview(
            @NotNull
            @Positive
            Long placeId,
            String content
    ) {
    }
}
