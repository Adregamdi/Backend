package com.adregamdi.schedule.infrastructure;

import com.adregamdi.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleCustomRepository {
    Optional<Schedule> findByMemberId(String memberId);

    Schedule findByMemberIdAndTitleAndDay(String memberId, String title, Integer day);
}
