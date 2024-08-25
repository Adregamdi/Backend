package com.adregamdi.schedule.dto.request;

public record CreateMyScheduleRequest(
        String startDate,
        String endDate,
        String title
) {
}
