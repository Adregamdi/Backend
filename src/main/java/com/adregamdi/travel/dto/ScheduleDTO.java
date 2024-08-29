package com.adregamdi.travel.dto;

import com.adregamdi.travel.domain.Schedule;
import com.adregamdi.travel.domain.SchedulePlace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private Schedule schedule;
    private List<SchedulePlace> schedulePlaces;
}
