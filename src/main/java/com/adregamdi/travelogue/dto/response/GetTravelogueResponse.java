package com.adregamdi.travelogue.dto.response;

import com.adregamdi.place.domain.PlaceReview;
import com.adregamdi.place.domain.PlaceReviewImage;
import com.adregamdi.travelogue.domain.Travelogue;
import com.adregamdi.travelogue.domain.TravelogueDay;
import com.adregamdi.travelogue.domain.TravelogueImage;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Builder
public record GetTravelogueResponse(
        Long travelogueId,
        Long travelId,
        String title,
        String introduction,
        List<TravelogueImageInfo> travelogueImageList,
        List<DayInfo> dayList
) {
    public static GetTravelogueResponse of(
            final Travelogue travelogue,
            final List<TravelogueImage> travelogueImages,
            final List<TravelogueDay> travelogueDays,
            final List<PlaceReview> placeReviews,
            final Function<Long, List<PlaceReviewImage>> placeReviewImagesFetcher
    ) {
        List<TravelogueImageInfo> travelogueImageInfos = travelogueImages.stream()
                .map(image -> new TravelogueImageInfo(image.getUrl()))
                .collect(Collectors.toList());

        List<DayInfo> dayInfos = travelogueDays.stream()
                .map(day -> {
                    List<PlaceReviewInfo> placeReviewInfos = placeReviews.stream()
                            .filter(review -> review.getTravelogueDayId().equals(day.getTravelogueDayId()))
                            .map(review -> {
                                List<PlaceReviewImage> reviewImages = placeReviewImagesFetcher.apply(review.getPlaceReviewId());
                                List<PlaceReviewImageInfo> placeReviewImageInfos = reviewImages.stream()
                                        .map(image -> new PlaceReviewImageInfo(image.getUrl()))
                                        .collect(Collectors.toList());

                                return PlaceReviewInfo.builder()
                                        .placeId(review.getPlaceId())
                                        .content(review.getContent())
                                        .placeReviewImageList(placeReviewImageInfos)
                                        .build();
                            })
                            .collect(Collectors.toList());

                    return DayInfo.builder()
                            .date(day.getDate())
                            .day(day.getDay())
                            .content(day.getContent())
                            .placeReviewList(placeReviewInfos)
                            .build();
                })
                .collect(Collectors.toList());

        return GetTravelogueResponse.builder()
                .travelogueId(travelogue.getTravelogueId())
                .travelId(travelogue.getTravelId())
                .title(travelogue.getTitle())
                .introduction(travelogue.getIntroduction())
                .travelogueImageList(travelogueImageInfos)
                .dayList(dayInfos)
                .build();
    }

    @Builder
    public record TravelogueImageInfo(
            String url
    ) {
    }

    @Builder
    public record DayInfo(
            LocalDate date,
            Integer day,
            String content,
            List<PlaceReviewInfo> placeReviewList
    ) {
    }

    @Builder
    public record PlaceReviewInfo(
            Long placeId,
            String content,
            List<PlaceReviewImageInfo> placeReviewImageList
    ) {
    }

    @Builder
    public record PlaceReviewImageInfo(
            String url
    ) {
    }
}
