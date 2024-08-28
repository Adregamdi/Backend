package com.adregamdi.schedule.dto.request;

import com.adregamdi.schedule.dto.ScheduleListDTO;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public record CreateMyScheduleRequest(
        @NotBlank
        LocalDate startDate,
        @NotBlank
        LocalDate endDate,
        @NotBlank
        String title,
        @NotBlank
        Integer day,
        String memo,
        @NotBlank
        List<ScheduleListDTO> scheduleList
) {
}
