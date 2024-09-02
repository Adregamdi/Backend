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

        List<Day> dayList = travelDays.stream()
                .map(travelDay -> {
                    List<Place> places = placesByDayId.getOrDefault(travelDay.getTravelDayId(), Collections.emptyList())
                            .stream()
                            .map(travelPlace -> Place.builder()
                                    .placeId(travelPlace.getPlaceId())
                                    .placeOrder(travelPlace.getPlaceOrder())
                                    .build())
                            .sorted(Comparator.comparing(Place::placeOrder))
                            .collect(Collectors.toList());

                    return Day.builder()
                            .day(travelDay.getDay())
                            .memo(travelDay.getMemo())
                            .placeList(places)
                            .build();
                })
                .sorted(Comparator.comparing(Day::day))
                .collect(Collectors.toList());

        TravelInfo travelData = TravelInfo.builder()
                .travelId(travel.getTravelId())
                .startDate(travel.getStartDate())
                .endDate(travel.getEndDate())
                .title(travel.getTitle())
                .dayList(dayList)
                .build();

        return new GetMyTravelResponse(travelData);
    }

    @Builder
    public record TravelInfo(
            Long travelId,
            LocalDate startDate,
            LocalDate endDate,
            String title,
            List<Day> dayList
    ) {
    }

    @Builder
    public record Day(
            Integer day,
            String memo,
            List<Place> placeList
    ) {
    }

    @Builder
    public record Place(
            Long placeId,
            Integer placeOrder
    ) {
    }
}