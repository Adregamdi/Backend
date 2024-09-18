package com.adregamdi.travelogue.infrastructure;

import com.adregamdi.travelogue.domain.TravelogueDayPlaceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TravelogueDayPlaceReviewRepository extends JpaRepository<TravelogueDayPlaceReview, Long> {
    List<TravelogueDayPlaceReview> findByTravelogueDayId(Long travelogueDayId);

    Optional<TravelogueDayPlaceReview> findByPlaceReviewId(Long placeReviewId);
}