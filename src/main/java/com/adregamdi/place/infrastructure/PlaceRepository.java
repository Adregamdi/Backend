package com.adregamdi.place.infrastructure;

import com.adregamdi.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceCustomRepository {
    Optional<Place> findByTitleAndContentsLabel(String title, String ContentsLabel);

    Optional<Place> findByTitle(String title);

    @Query("SELECT COUNT(p) FROM Place p")
    long countTotalPlaces();

    @Query("""
            SELECT COUNT(DISTINCT pr.placeReviewId)
            FROM PlaceReview pr
            JOIN PlaceReviewImage pri
            ON pr.placeReviewId = pri.placeReviewId
            WHERE pr.placeId = :placeId
            """)
    int countPlaceReviewsWithImagesForPlace(@Param("placeId") Long placeId);

    @Query("""
            SELECT COUNT(s)
            FROM Shorts s
            WHERE s.placeId = :placeId
            """)
    int countShortsReviewsForPlace(@Param("placeId") Long placeId);
}
