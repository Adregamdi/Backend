package com.adregamdi.place.infrastructure;

import com.adregamdi.place.domain.PlaceReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaceReviewImageRepository extends JpaRepository<PlaceReviewImage, Long> {
    Optional<List<PlaceReviewImage>> findAllByPlaceReviewId(Long placeReviewId);
}
