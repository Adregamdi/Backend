package com.adregamdi.travelogue.infrastructure;

import com.adregamdi.travelogue.domain.TravelogueImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelogueImageRepository extends JpaRepository<TravelogueImage, Long> {
}
