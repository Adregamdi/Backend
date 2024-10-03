package com.adregamdi.travelogue.infrastructure;

import com.adregamdi.travelogue.domain.Travelogue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TravelogueRepository extends JpaRepository<Travelogue, Long>, TravelogueCustomRepository {
    Travelogue findByTravelId(Long travelId);

    Optional<Travelogue> findByTravelogueIdAndMemberId(Long travelogueId, String memberId);
}
