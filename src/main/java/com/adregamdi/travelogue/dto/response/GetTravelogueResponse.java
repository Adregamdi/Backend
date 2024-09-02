package com.adregamdi.travelogue.dto.response;

import com.adregamdi.place.domain.PlaceReview;
import com.adregamdi.place.domain.PlaceReviewImage;
import com.adregamdi.travelogue.domain.Travelogue;
import com.adregamdi.travelogue.domain.TravelogueDay;
import com.adregamdi.travelogue.domain.TravelogueImage;
import lombok.Builder;

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
                            .map(review -> {
                                List<PlaceReviewImage> reviewImages = placeReviewImagesFetcher.apply(review.getPlaceReviewId());
                                List<PlaceReviewImageInfo> placeReviewImageInfos = reviewImages.stream()
                                        .map(image -> new PlaceReviewImageInfo(image.getUrl()))
                                        .collect(Collectors.toList());

                                return new PlaceReviewInfo(
                                        review.getPlaceId(),
                                        review.getContent(),
                                        placeReviewImageInfos
                                );
                            })
                            .collect(Collectors.toList());

                    return new DayInfo(
                            day.getDay(),
                            day.getContent(),
                            placeReviewInfos
                    );
                })
                .collect(Collectors.toList());

        return new GetTravelogueResponse(
                travelogue.getTravelogueId(),
                travelogue.getTravelId(),
                travelogue.getTitle(),
                travelogue.getIntroduction(),
                travelogueImageInfos,
                dayInfos
        );
    }

    public record TravelogueImageInfo(
            String url
    ) {
    }

    public record DayInfo(
            Integer day,
            String content,
            List<PlaceReviewInfo> placeReviewList
    ) {
    }

    public record PlaceReviewInfo(
            Long placeId,
            String content,
            List<PlaceReviewImageInfo> placeReviewImageList
    ) {
    }

    public record PlaceReviewImageInfo(
            String url
    ) {
    }
}
