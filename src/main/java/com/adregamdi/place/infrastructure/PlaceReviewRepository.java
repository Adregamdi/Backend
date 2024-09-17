package com.adregamdi.place.infrastructure;

import com.adregamdi.place.domain.PlaceReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Long> {
    Optional<List<PlaceReview>> findAllByTravelogueId(Long travelogueId);

    Optional<List<PlaceReview>> findAllByMemberIdOrderByPlaceReviewIdDesc(UUID memberId);

    int countByPlaceId(Long placeId);
}
