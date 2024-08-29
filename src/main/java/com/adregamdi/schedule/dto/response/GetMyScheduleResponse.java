package com.adregamdi.schedule.dto.response;

import com.adregamdi.schedule.domain.Schedule;
import com.adregamdi.schedule.domain.SchedulePlace;
import com.adregamdi.schedule.dto.ScheduleDTO;
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

