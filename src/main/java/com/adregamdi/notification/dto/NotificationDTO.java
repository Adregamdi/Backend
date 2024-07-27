package com.adregamdi.notification.dto;

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
    private Long id;
    private String content;
    private String uri;
    private NotificationType type;
    private boolean isRead;
    private LocalDate createdAt;

    public static NotificationDTO from(final Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .uri(notification.getUri())
                .type(notification.getType())
                .isRead(notification.isRead())
                .createdAt(LocalDate.from(notification.getCreatedAt()))
                .build();
    }
}
