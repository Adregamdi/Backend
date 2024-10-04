package com.adregamdi.notification.dto.request;

import com.adregamdi.notification.domain.ContentType;
import com.adregamdi.notification.domain.NotificationType;
import lombok.Builder;

@Builder
public record CreateNotificationRequest(
        String memberId,
        String opponentMemberProfile,
        String opponentMemberHandle,
        ContentType contentType,
        NotificationType notificationType
) {
    public static CreateNotificationRequest of(
            final String memberId,
            final String opponentMemberProfile,
            final String opponentMemberHandle,
            final ContentType contentType,
            final NotificationType notificationType
    ) {
        return CreateNotificationRequest.builder()
                .memberId(memberId)
                .opponentMemberProfile(opponentMemberProfile)
                .opponentMemberHandle(opponentMemberHandle)
                .contentType(contentType)
                .notificationType(notificationType)
                .build();
    }
}
