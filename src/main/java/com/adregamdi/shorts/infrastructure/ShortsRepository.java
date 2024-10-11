package com.adregamdi.shorts.infrastructure;

import com.adregamdi.shorts.domain.Shorts;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShortsRepository extends JpaRepository<Shorts, Long>, ShortsCustomRepository {
    Optional<Slice<Shorts>> findAllByMemberId(Pageable pageable, String memberId);

    @Query("""
                SELECT s.shortsId, s.title, s.memberId, s.placeId, s.travelogueId,
                       s.shortsVideoUrl, s.thumbnailUrl, s.assignedStatus, s.viewCount,
                       m.name, m.handle, m.profile
                FROM Shorts s
                JOIN Member m ON s.memberId = m.memberId
                WHERE s.shortsId = :shortsId
            """)
    Optional<Object[]> findShortsWithMemberByShortsId(@Param("shortsId") Long shortsId);

    @Query("""
            SELECT s
             FROM Shorts s
            WHERE s.assignedStatus IS false
              AND s.createdAt < :date""")
    List<Shorts> findUnassignedBeforeDate(@Param("date") LocalDateTime date);

    List<Shorts> findAllByMemberId(String memberId);
}
