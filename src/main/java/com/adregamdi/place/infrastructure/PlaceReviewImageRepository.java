package com.adregamdi.place.infrastructure;

import com.adregamdi.place.domain.PlaceReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceReviewImageRepository extends JpaRepository<PlaceReviewImage, Long> {
    List<PlaceReviewImage> findByPlaceReviewIdOrderByPlaceReviewImageIdDesc(Long placeReviewId);
}
