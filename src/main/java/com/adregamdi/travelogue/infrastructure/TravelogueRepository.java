package com.adregamdi.travelogue.infrastructure;

import com.adregamdi.travelogue.domain.Travelogue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelogueRepository extends JpaRepository<Travelogue, Long>, TravelogueCustomRepository {
    Travelogue findByTravelId(Long travelId);
}
