package com.adregamdi.notification.dto.request;

import com.adregamdi.core.constant.ContentType;
import com.adregamdi.notification.domain.NotificationType;
import lombok.Builder;

@Builder
public record CreateNotificationRequest(
        String memberId,
        Long contentId,
        String opponentMemberProfile,
        String opponentMemberHandle,
        ContentType contentType,
        NotificationType notificationType
) {
    public static CreateNotificationRequest of(
            final String memberId,
            final Long contentId,
            final String opponentMemberProfile,
            final String opponentMemberHandle,
            final ContentType contentType,
            final NotificationType notificationType
    ) {
        return CreateNotificationRequest.builder()
                .memberId(memberId)
                .contentId(contentId)
                .opponentMemberProfile(opponentMemberProfile)
                .opponentMemberHandle(opponentMemberHandle)
                .contentType(contentType)
                .notificationType(notificationType)
                .build();
    }
}
