package com.adregamdi.schedule.infrastructure;

import com.adregamdi.schedule.domain.SchedulePlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchedulePlaceRepository extends JpaRepository<SchedulePlace, Long>, ScheduleCustomRepository {
    Optional<SchedulePlace> findByScheduleId(Long scheduleId);

    Optional<SchedulePlace> findByScheduleIdAndOrder(Long scheduleId, Integer order);
}
