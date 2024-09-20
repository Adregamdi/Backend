package com.adregamdi.travelogue.dto.response;

import com.adregamdi.travelogue.domain.Travelogue;
import com.adregamdi.travelogue.domain.TravelogueDay;
import com.adregamdi.travelogue.domain.TravelogueImage;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
public record GetTravelogueResponse(
        boolean isLiked,
        Long travelogueId,
        Long travelId,
        String title,
        String introduction,
        List<TravelogueImageInfo> travelogueImageList,
        List<DayInfo> dayList
) {
    public static GetTravelogueResponse of(
            final boolean isLiked,
            final Travelogue travelogue,
            final List<TravelogueImage> travelogueImages,
            final List<TravelogueDay> travelogueDays,
            final Map<Long, List<PlaceReviewInfo>> placeReviewsMap
    ) {
        List<TravelogueImageInfo> travelogueImageInfos = travelogueImages.stream()
                .map(image -> new TravelogueImageInfo(image.getUrl()))
                .collect(Collectors.toList());

        List<DayInfo> dayInfoList = travelogueDays.stream()
                .map(day -> new DayInfo(
                        day.getDate(),
                        day.getDay(),
                        day.getContent(),
                        placeReviewsMap.getOrDefault(day.getTravelogueDayId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());

        return GetTravelogueResponse.builder()
                .isLiked(isLiked)
                .travelogueId(travelogue.getTravelogueId())
                .travelId(travelogue.getTravelId())
                .title(travelogue.getTitle())
                .introduction(travelogue.getIntroduction())
                .travelogueImageList(travelogueImageInfos)
                .dayList(dayInfoList)
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
            String title,
            String contentsLabel,
            String regionLabel,
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
