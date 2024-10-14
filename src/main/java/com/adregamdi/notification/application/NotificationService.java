package com.adregamdi.notification.application;

import com.adregamdi.core.constant.ContentType;
import com.adregamdi.notification.dto.request.CreateNotificationRequest;
import com.adregamdi.notification.dto.request.UpdateNotificationRequest;
import com.adregamdi.notification.dto.response.GetNotificationResponse;

import java.util.List;

public interface NotificationService {
    /*
     * [알림 생성]
     */
    void create(CreateNotificationRequest request);

    /*
     * [내 알림 조회]
     */
    GetNotificationResponse getMyNotification(String currentMemberId, Long lastId);

    /*
     * [알림 수정]
     * 사용자가 알림 읽으면 상태 true 로 변경
     */
    void update(List<UpdateNotificationRequest> requests);

    /*
     * [알림 삭제]
     * 좋아요 취소 시 해당 알림 데이터 삭제
     */
    void delete(Long contentId, ContentType contentType);
}
