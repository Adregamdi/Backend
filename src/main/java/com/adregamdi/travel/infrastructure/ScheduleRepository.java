package com.adregamdi.travel.infrastructure;

import com.adregamdi.travel.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleCustomRepository {
    Optional<Schedule> findByScheduleIdAndMemberId(Long scheduleId, String memberId);

    Schedule findByMemberIdAndTitleAndDay(String memberId, String title, Integer day);
}
