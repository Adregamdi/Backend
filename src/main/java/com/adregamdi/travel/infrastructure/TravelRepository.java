package com.adregamdi.travel.infrastructure;

import com.adregamdi.travel.domain.Travel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TravelRepository extends JpaRepository<Travel, Long>, TravelCustomRepository {
    Optional<Travel> findByTravelIdAndMemberId(Long travelId, UUID memberId);
}
