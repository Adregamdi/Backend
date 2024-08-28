package com.adregamdi.schedule.application;

import com.adregamdi.schedule.domain.Schedule;
import com.adregamdi.schedule.domain.SchedulePlace;
import com.adregamdi.schedule.dto.ScheduleListDTO;
import com.adregamdi.schedule.dto.request.CreateMyScheduleRequest;
import com.adregamdi.schedule.dto.request.GetMyScheduleRequest;
import com.adregamdi.schedule.dto.response.GetMyScheduleResponse;
import com.adregamdi.schedule.exception.ScheduleException.ScheduleNotFoundException;
import com.adregamdi.schedule.exception.ScheduleException.SchedulePlaceNotFoundException;
import com.adregamdi.schedule.infrastructure.SchedulePlaceRepository;
import com.adregamdi.schedule.infrastructure.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final SchedulePlaceRepository schedulePlaceRepository;

    /*
     * 일정 조회
     * */
    @Transactional(readOnly = true)
    public GetMyScheduleResponse getMySchedule(final List<GetMyScheduleRequest> requests, final String memberId) {
        List<Schedule> schedules = new ArrayList<>();
        List<List<SchedulePlace>> schedulePlaces = new ArrayList<>();

        for (GetMyScheduleRequest request : requests) {
            Schedule schedule = scheduleRepository.findByScheduleIdAndMemberId(request.scheduleId(), memberId)
                    .orElseThrow(ScheduleNotFoundException::new);
            schedules.add(schedule);
        }
        for (Schedule schedule : schedules) {
            List<SchedulePlace> schedulePlace = schedulePlaceRepository.findByScheduleId(schedule.getScheduleId())
                    .orElseThrow(() -> new SchedulePlaceNotFoundException(schedule.getScheduleId()));
            schedulePlaces.add(schedulePlace);
        }
        return GetMyScheduleResponse.from(schedules, schedulePlaces);
    }

    /*
     * 일정 하루 단위로 등록/수정
     * */
    @Transactional
    public void createMySchedule(final CreateMyScheduleRequest request, final String memberId) {
        Schedule schedule = scheduleRepository.findByMemberIdAndTitleAndDay(memberId, request.title(), request.day());

        if (schedule == null) {
            schedule = scheduleRepository.save(new Schedule(request, memberId));
        } else {
            schedule.updateSchedule(request);
        }

        for (ScheduleListDTO scheduleListDTO : request.scheduleList()) {
            SchedulePlace schedulePlace = schedulePlaceRepository.findByScheduleIdAndPlaceOrder(schedule.getScheduleId(), scheduleListDTO.getPlaceOrder())
                    .orElseThrow(SchedulePlaceNotFoundException::new);
            if (schedulePlace == null) {
                schedulePlaceRepository.save(new SchedulePlace(schedule.getScheduleId(), scheduleListDTO));
            } else {
                schedulePlace.updateSchedulePlace(schedule.getScheduleId(), scheduleListDTO);
            }
        }
    }
}
