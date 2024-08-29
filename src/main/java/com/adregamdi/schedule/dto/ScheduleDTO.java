package com.adregamdi.schedule.dto;

import com.adregamdi.schedule.domain.Schedule;
import com.adregamdi.schedule.domain.SchedulePlace;
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
