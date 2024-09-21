package com.adregamdi.place.infrastructure;

import com.adregamdi.place.domain.PlaceReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Long> {
    Optional<List<PlaceReview>> findAllByMemberIdOrderByPlaceReviewIdDesc(String memberId);

    List<PlaceReview> findAllByPlaceIdOrderByPlaceReviewIdDesc(Long placeId);

    Optional<PlaceReview> findByMemberIdAndPlaceId(String memberId, Long placeId);

    Optional<PlaceReview> findByMemberIdAndPlaceIdAndVisitDate(String memberId, Long placeId, LocalDate localDate);

    int countByPlaceId(Long placeId);
}
