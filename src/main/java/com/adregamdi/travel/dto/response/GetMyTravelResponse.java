package com.adregamdi.travel.dto.response;

import com.adregamdi.place.dto.PlaceReviewDTO;
import com.adregamdi.travel.domain.Travel;
import com.adregamdi.travel.domain.TravelDay;
import com.adregamdi.travel.dto.TravelPlaceDTO;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
public record GetMyTravelResponse(
        TravelInfo travel
) {
    public static GetMyTravelResponse of(
            final Travel travel,
            final List<TravelDay> travelDays,
            final List<List<TravelPlaceDTO>> travelPlaces
    ) {
        Map<Long, List<TravelPlaceDTO>> placesByDayId = travelPlaces.stream()
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(dto -> dto.travelPlace().getTravelDayId()));

        List<DayInfo> dayList = travelDays.stream()
                .map(travelDay -> {
                    List<PlaceInfo> placeInfos = placesByDayId.getOrDefault(travelDay.getTravelDayId(), Collections.emptyList())
                            .stream()
                            .map(travelPlace -> PlaceInfo.builder()
                                    .placeReview(travelPlace.placeReview())
                                    .placeId(travelPlace.place().getPlaceId())
                                    .title(travelPlace.place().getTitle())
                                    .contentsLabel(travelPlace.place().getContentsLabel())
                                    .regionLabel(travelPlace.place().getRegionLabel())
                                    .latitude(travelPlace.place().getLatitude())
                                    .longitude(travelPlace.place().getLongitude())
                                    .thumbnailPath(travelPlace.place().getThumbnailPath())
                                    .placeOrder(travelPlace.travelPlace().getPlaceOrder())
                                    .build())
                            .sorted(Comparator.comparing(PlaceInfo::placeOrder))
                            .collect(Collectors.toList());

                    return DayInfo.builder()
                            .date(travelDay.getDate())
                            .day(travelDay.getDay())
                            .memo(travelDay.getMemo())
                            .placeList(placeInfos)
                            .build();
                })
                .sorted(Comparator.comparing(DayInfo::day))
                .collect(Collectors.toList());

        TravelInfo travelData = TravelInfo.builder()
                .travelId(travel.getTravelId())
                .startDate(travel.getStartDate())
                .endDate(travel.getEndDate())
                .title(travel.getTitle())
                .dayList(dayList)
                .build();

        return GetMyTravelResponse.builder()
                .travel(travelData)
                .build();
    }

    @Builder
    public record TravelInfo(
            Long travelId,
            LocalDate startDate,
            LocalDate endDate,
            String title,
            List<DayInfo> dayList
    ) {
    }

    @Builder
    public record DayInfo(
            LocalDate date,
            Integer day,
            String memo,
            List<PlaceInfo> placeList
    ) {
    }

    @Builder
    public record PlaceInfo(
            PlaceReviewDTO placeReview,
            Long placeId,
            String title,
            String contentsLabel,
            String regionLabel,
            Double latitude,
            Double longitude,
            String thumbnailPath,
            Integer placeOrder
    ) {
    }
}