package com.adregamdi.schedule.application;

import com.adregamdi.schedule.dto.request.CreateMyScheduleRequest;
import com.adregamdi.schedule.infrastructure.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public void createMySchedule(CreateMyScheduleRequest request, String username) {
    }

}
