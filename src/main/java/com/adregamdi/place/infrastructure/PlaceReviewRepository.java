package com.adregamdi.place.infrastructure;

import com.adregamdi.place.domain.PlaceReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Long> {
    Optional<List<PlaceReview>> findAllByMemberIdOrderByPlaceReviewIdDesc(UUID memberId);

    List<PlaceReview> findAllByPlaceIdOrderByPlaceReviewIdDesc(Long placeId);

    Optional<PlaceReview> findByMemberIdAndPlaceId(UUID memberId, Long placeId);

    Optional<PlaceReview> findByMemberIdAndPlaceIdAndVisitDate(UUID memberId, Long placeId, LocalDate localDate);

    int countByPlaceId(Long placeId);
}
