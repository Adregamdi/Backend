package com.adregamdi.notification.dto.request;

import jakarta.validation.constraints.Positive;

public record UpdateNotificationRequest(
        @Positive
        Long notificationId
) {
}
