package com.adregamdi.place.infrastructure;

import com.adregamdi.place.domain.PlaceReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Long> {
    Optional<List<PlaceReview>> findByTravelogueId(final Long travelogueId);

    int countByPlaceId(Long placeId);
}
