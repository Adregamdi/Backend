package com.adregamdi.media.infrastructure;

import com.adregamdi.media.domain.Video;
import com.adregamdi.media.enumtype.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query("SELECT v.url" +
            " FROM Video v " +
            "WHERE v.targetId IS NULL " +
            "  AND v.createAt < :date")
    List<String> findUnassignedBeforeDate(@Param("date") LocalDateTime date);

    @Query("DELETE " +
            " FROM Video v " +
            "WHERE v.url " +
            "   IN :urlList")
    @Modifying
    int deleteAllByUrlIn(@Param("urlList") List<String> urlList);

    Optional<Video> findByUrlAndMediaType(String url, MediaType mediaType);
}
