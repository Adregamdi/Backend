package com.adregamdi.travel.dto.response;

import com.adregamdi.travel.domain.Travel;
import com.adregamdi.travel.domain.TravelDay;
import com.adregamdi.travel.domain.TravelPlace;
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
            final List<List<TravelPlace>> travelPlaces
    ) {
        Map<Long, List<TravelPlace>> placesByDayId = travelPlaces.stream()
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(TravelPlace::getTravelDayId));

        List<DayInfo> dayList = travelDays.stream()
                .map(travelDay -> {
                    List<PlaceInfo> places = placesByDayId.getOrDefault(travelDay.getTravelDayId(), Collections.emptyList())
                            .stream()
                            .map(travelPlace -> PlaceInfo.builder()
                                    .placeId(travelPlace.getPlaceId())
                                    .placeOrder(travelPlace.getPlaceOrder())
                                    .build())
                            .sorted(Comparator.comparing(PlaceInfo::placeOrder))
                            .collect(Collectors.toList());

                    return DayInfo.builder()
                            .date(travelDay.getDate())
                            .day(travelDay.getDay())
                            .memo(travelDay.getMemo())
                            .placeList(places)
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
            Long placeId,
            Integer placeOrder
    ) {
    }
}