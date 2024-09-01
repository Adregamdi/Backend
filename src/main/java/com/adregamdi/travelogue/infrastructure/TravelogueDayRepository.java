package com.adregamdi.travelogue.infrastructure;

import com.adregamdi.travelogue.domain.TravelogueDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelogueDayRepository extends JpaRepository<TravelogueDay, Long> {
}
