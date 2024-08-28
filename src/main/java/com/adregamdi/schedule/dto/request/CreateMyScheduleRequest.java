package com.adregamdi.schedule.dto.request;

import com.adregamdi.schedule.dto.ScheduleListDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public record CreateMyScheduleRequest(
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate,
        @NotBlank
        String title,
        @Positive
        Integer day,
        String memo,
        @NotNull
        List<ScheduleListDTO> scheduleList
) {
}
