package com.adregamdi.notification.application;

import com.adregamdi.notification.dto.request.CreateNotificationRequest;
import com.adregamdi.notification.dto.request.UpdateNotificationRequest;
import com.adregamdi.notification.dto.response.GetNotificationResponse;

import java.util.List;

public interface NotificationService {
    void create(final CreateNotificationRequest request);

    GetNotificationResponse getMyNotification(final String currentMemberId, final Long lastId);

    void update(final List<UpdateNotificationRequest> requests);
}
