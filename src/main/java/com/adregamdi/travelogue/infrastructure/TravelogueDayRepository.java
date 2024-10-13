package com.adregamdi.travelogue.infrastructure;

import com.adregamdi.travelogue.domain.TravelogueDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelogueDayRepository extends JpaRepository<TravelogueDay, Long> {
    List<TravelogueDay> findByTravelogueIdOrderByDay(Long travelogueId);
}
