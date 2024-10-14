package com.adregamdi.notification.infrastructure;

import com.adregamdi.notification.domain.ContentType;
import com.adregamdi.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationCustomRepository {
    Optional<Notification> findByContentIdAndContentType(Long contentId, ContentType contentType);

    @Modifying
    @Query("""
            DELETE FROM Notification n
            WHERE n.createdAt< :date
            """)
    void deleteByCreatedAt(@Param("date") LocalDateTime date);

    void deleteByMemberId(String memberId);
}
