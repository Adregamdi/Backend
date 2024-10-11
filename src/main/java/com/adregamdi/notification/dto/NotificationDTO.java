package com.adregamdi.notification.dto;

import com.adregamdi.notification.domain.ContentType;
import com.adregamdi.notification.domain.Notification;
import com.adregamdi.notification.domain.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long notificationId;
    private Long contentId;
    private String opponentMemberProfile;
    private String opponentMemberHandle;
    private ContentType contentType;
    private NotificationType notificationType;
    private boolean isRead;
    private LocalDate createdAt;

    public static NotificationDTO from(final Notification notification) {
        return NotificationDTO.builder()
                .notificationId(notification.getNotificationId())
                .contentId(notification.getContentId())
                .opponentMemberProfile(notification.getOpponentMemberProfile())
                .opponentMemberHandle(notification.getOpponentMemberHandle())
                .contentType(notification.getContentType())
                .notificationType(notification.getNotificationType())
                .isRead(notification.isRead())
                .createdAt(LocalDate.from(notification.getCreatedAt()))
                .build();
    }
}
