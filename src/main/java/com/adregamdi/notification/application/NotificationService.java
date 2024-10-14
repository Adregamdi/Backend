package com.adregamdi.notification.application;

import com.adregamdi.notification.domain.ContentType;
import com.adregamdi.notification.dto.request.CreateNotificationRequest;
import com.adregamdi.notification.dto.request.UpdateNotificationRequest;
import com.adregamdi.notification.dto.response.GetNotificationResponse;

import java.util.List;

public interface NotificationService {
    /*
     * [알림 생성]
     */
    void create(final CreateNotificationRequest request);

    /*
     * [내 알림 조회]
     */
    GetNotificationResponse getMyNotification(final String currentMemberId, final Long lastId);

    /*
     * [알림 수정]
     * 사용자가 알림 읽으면 상태 true 로 변경
     */
    void update(final List<UpdateNotificationRequest> requests);

    /*
     * [알림 삭제]
     * 좋아요 취소 시 해당 알림 데이터 삭제
     */
    void delete(final Long contentId, final ContentType contentType);
}
