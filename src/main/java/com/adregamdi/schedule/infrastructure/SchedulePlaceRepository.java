package com.adregamdi.schedule.infrastructure;

import com.adregamdi.schedule.domain.SchedulePlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchedulePlaceRepository extends JpaRepository<SchedulePlace, Long>, ScheduleCustomRepository {
}
