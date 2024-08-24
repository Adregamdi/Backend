package com.adregamdi.schedule.dto.request;

import com.adregamdi.schedule.dto.ScheduleListDTO;

import java.time.LocalDate;
import java.util.List;

public record CreateMyScheduleRequest(
        LocalDate startDate,
        LocalDate endDate,
        String title,
        List<ScheduleListDTO> scheduleList
) {
}
