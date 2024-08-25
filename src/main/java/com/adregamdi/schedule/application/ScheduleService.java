package com.adregamdi.schedule.application;

import com.adregamdi.place.infrastructure.PlaceRepository;
import com.adregamdi.schedule.domain.Schedule;
import com.adregamdi.schedule.domain.SchedulePlace;
import com.adregamdi.schedule.dto.request.CreateMyScheduleRequest;
import com.adregamdi.schedule.infrastructure.SchedulePlaceRepository;
import com.adregamdi.schedule.infrastructure.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduleService {
    private final PlaceRepository placeRepository;
    private final ScheduleRepository scheduleRepository;
    private final SchedulePlaceRepository schedulePlaceRepository;

    @Transactional
    public void createMySchedule(CreateMyScheduleRequest request, String memberId) {
        Schedule schedule = scheduleRepository.save(new Schedule(request, memberId));

        request.scheduleList().stream()
                .map(scheduleItem -> new SchedulePlace(
                        schedule.getScheduleId(),
                        scheduleItem.getPlaceId(),
                        scheduleItem.getDay()
                ))
                .forEach(schedulePlaceRepository::save);
    }
}
