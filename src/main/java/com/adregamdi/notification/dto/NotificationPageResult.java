package com.adregamdi.notification.dto;

import com.adregamdi.notification.domain.Notification;

import java.util.List;

public record NotificationPageResult(
        boolean hasNext,
        List<Notification> notifications
) {
}
