package com.adregamdi.media.infrastructure;

import com.adregamdi.media.domain.Image;
import com.adregamdi.media.domain.ImageTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findImageByImageUrl(String imageUrl);

    Optional<Image> findImageByImageTargetAndTargetId(ImageTarget imageTarget, String targetId);

    List<Image> findByImageUrlIn(List<String> imageUrls);

    @Query("SELECT i.imageUrl FROM Image i WHERE i.targetId IS NULL AND i.createdAt < :date")
    List<String> findUnassignedImagesBeforeDate(@Param("date") LocalDateTime date);

    @Query("DELETE FROM Image i WHERE i.imageUrl IN :imageUrls")
    @Modifying
    int deleteAllByImageUrlIn(@Param("imageUrls") List<String> imageUrls);

    Optional<Image> findImageByTargetIdAndImageTarget(String targetId, ImageTarget imageTarget);
}
