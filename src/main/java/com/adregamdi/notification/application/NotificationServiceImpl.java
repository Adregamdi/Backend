package com.adregamdi.notification.application;

import com.adregamdi.core.constant.ContentType;
import com.adregamdi.notification.domain.Notification;
import com.adregamdi.notification.dto.NotificationDTO;
import com.adregamdi.notification.dto.request.CreateNotificationRequest;
import com.adregamdi.notification.dto.request.UpdateNotificationRequest;
import com.adregamdi.notification.dto.response.GetNotificationResponse;
import com.adregamdi.notification.exception.NotificationException.NotificationNotFoundException;
import com.adregamdi.notification.infrastructure.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    /*
     * [알림 생성]
     */
    @Override
    @Transactional
    public void create(final CreateNotificationRequest request) {
        notificationRepository.save(Notification.builder()
                .memberId(request.memberId())
                .contentId(request.contentId())
                .opponentMemberProfile(request.opponentMemberProfile())
                .opponentMemberHandle(request.opponentMemberHandle())
                .contentType(request.contentType())
                .notificationType(request.notificationType())
                .build());
    }

    /*
     * [내 알림 조회]
     */
    @Override
    @Transactional(readOnly = true)
    public GetNotificationResponse getMyNotification(final String currentMemberId, final Long lastId) {
        List<Notification> notifications = notificationRepository.findByMemberId(currentMemberId, LocalDateTime.now().minusDays(31), lastId)
                .orElseThrow(() -> new NotificationNotFoundException(currentMemberId));

        return GetNotificationResponse.of(
                countNoReadNotification(notifications),
                notifications.stream().map(NotificationDTO::from).toList()
        );
    }

    /*
     * [안 읽은 알림 개수 세기]
     */
    private int countNoReadNotification(final List<Notification> notifications) {
        return (int) notifications.stream()
                .filter(notification -> !notification.isRead())
                .count();
    }

    /*
     * [알림 수정]
     * 사용자가 알림 읽으면 상태 true 로 변경
     */
    @Override
    @Transactional
    public void update(final List<UpdateNotificationRequest> requests) {
        requests.stream()
                .map(request -> notificationRepository.findById(request.notificationId())
                        .orElseThrow(() -> new NotificationNotFoundException(request.notificationId())))
                .forEach(notification -> notification.updateIsRead(true));
    }

    /*
     * [알림 삭제]
     * 좋아요 취소 시 해당 알림 데이터 삭제
     */
    @Override
    @Transactional
    public void delete(final String memberId, final Long contentId, final ContentType contentType) {
        Notification notification = notificationRepository.findByMemberIdAndContentIdAndContentType(memberId, contentId, contentType)
                .orElseThrow(NotificationNotFoundException::new);
        notificationRepository.deleteById(notification.getNotificationId());
    }

    /*
     * [주기적 알림 삭제]
     * 30일이 지난 알림들 매일 자정에 삭제
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    protected void deleteMonth() {
        LocalDateTime date = LocalDateTime.now().minusDays(30);
        notificationRepository.deleteByCreatedAt(date);
    }
}
