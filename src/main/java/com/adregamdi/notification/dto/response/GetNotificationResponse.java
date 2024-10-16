package com.adregamdi.notification.dto.response;

import com.adregamdi.notification.dto.NotificationDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record GetNotificationResponse(
        int noReadElements,
        boolean hasNext,
        List<NotificationDTO> notificationList
) {
    public static GetNotificationResponse of(final int noReadElements, final boolean hasNext, final List<NotificationDTO> notificationList) {
        return GetNotificationResponse.builder()
                .noReadElements(noReadElements)
                .hasNext(hasNext)
                .notificationList(notificationList)
                .build();
    }
}
