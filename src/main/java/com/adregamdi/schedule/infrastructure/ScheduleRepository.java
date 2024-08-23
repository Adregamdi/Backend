package com.adregamdi.schedule.infrastructure;

import com.adregamdi.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleCustomRepository {
}
