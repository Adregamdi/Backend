package com.adregamdi.place.infrastructure;

import com.adregamdi.place.domain.PlaceReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Long> {
}
