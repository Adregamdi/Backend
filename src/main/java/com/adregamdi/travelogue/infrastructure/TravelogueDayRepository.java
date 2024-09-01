package com.adregamdi.travelogue.infrastructure;

import com.adregamdi.travelogue.domain.TravelogueDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TravelogueDayRepository extends JpaRepository<TravelogueDay, Long> {
    Optional<List<TravelogueDay>> findByTravelogueIdOrderByDay(final Long travelogueId);
}
