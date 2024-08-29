package com.adregamdi.travel.dto.response;

import com.adregamdi.travel.domain.Schedule;
import com.adregamdi.travel.domain.SchedulePlace;
import com.adregamdi.travel.dto.ScheduleDTO;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Builder
public record GetMyScheduleResponse(
        List<ScheduleDTO> schedules
) {
    public static GetMyScheduleResponse from(List<Schedule> schedules, List<List<SchedulePlace>> schedulePlaces) {
        List<ScheduleDTO> scheduleDTOs = IntStream.range(0, schedules.size())
                .mapToObj(i -> new ScheduleDTO(schedules.get(i), schedulePlaces.get(i)))
                .collect(Collectors.toList());

        return GetMyScheduleResponse.builder()
                .schedules(scheduleDTOs)
                .build();
    }
}

