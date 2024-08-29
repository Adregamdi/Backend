package com.adregamdi.notification.dto.request;

import com.adregamdi.notification.domain.NotificationType;
import lombok.Builder;

@Builder
public record CreateNotificationRequest(
        String memberId,
        String content,
        String uri,
        NotificationType type
) {
    // 여행기 좋아요 알림
    public static CreateNotificationRequest of(final NotificationType type) {
        return CreateNotificationRequest.builder()
                .type(type)
                .build();
    }

    // 쇼츠 좋아요 알림
    public static CreateNotificationRequest of() {
        return CreateNotificationRequest.builder()
                .build();
    }
}
