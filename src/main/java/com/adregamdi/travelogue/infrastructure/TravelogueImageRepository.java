package com.adregamdi.travelogue.infrastructure;

import com.adregamdi.travelogue.domain.TravelogueImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TravelogueImageRepository extends JpaRepository<TravelogueImage, Long> {
    Optional<List<TravelogueImage>> findByTravelogueId(Long travelogueId);

    List<TravelogueImage> findAllByTravelogueId(Long travelogueId);
}
