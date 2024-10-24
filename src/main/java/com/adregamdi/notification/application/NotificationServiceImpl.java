package com.adregamdi.notification.application;

import com.adregamdi.core.constant.ContentType;
import com.adregamdi.member.domain.Member;
import com.adregamdi.member.exception.MemberException;
import com.adregamdi.member.infrastructure.MemberRepository;
import com.adregamdi.notification.domain.Notification;
import com.adregamdi.notification.dto.NotificationDTO;
import com.adregamdi.notification.dto.NotificationPageResult;
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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    /*
     * [알림 생성]
     */
    @Override
    @Transactional
    public void create(final CreateNotificationRequest request) {
        notificationRepository.save(createNotificationFromRequest(request));
    }

    /*
     * [내 알림 조회]
     */
    @Override
    @Transactional(readOnly = true)
    public GetNotificationResponse getMyNotification(final String currentMemberId, final Long lastId) {
        NotificationPageResult pageResult = fetchNotifications(currentMemberId, lastId);
        Map<String, Member> memberMap = fetchMemberMap(pageResult);
        List<NotificationDTO> notificationDTOs = createNotificationDTOs(pageResult, memberMap);

        return GetNotificationResponse.of(
                countNoReadNotification(pageResult.notifications()),
                pageResult.hasNext(),
                notificationDTOs
        );
    }

    /*
     * [알림 수정]
     * 사용자가 알림 읽으면 상태 true 로 변경
     */
    @Override
    @Transactional
    public void update(final List<UpdateNotificationRequest> requests) {
        requests.stream()
                .map(this::findNotificationById)
                .forEach(notification -> notification.updateIsRead(true));
    }

    /*
     * [알림 삭제]
     * 좋아요 취소 시 해당 알림 데이터 삭제
     */
    @Override
    @Transactional
    public void delete(final String opponentMemberId, final Long contentId, final ContentType contentType) {
        validateMemberExists(opponentMemberId);
        notificationRepository.findByOpponentMemberIdAndContentIdAndContentType(opponentMemberId, contentId, contentType)
                .ifPresent(notification -> notificationRepository.deleteById(notification.getNotificationId()));
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

    private Notification createNotificationFromRequest(final CreateNotificationRequest request) {
        return Notification.builder()
                .memberId(request.memberId())
                .contentId(request.contentId())
                .opponentMemberId(request.opponentMemberId())
                .contentType(request.contentType())
                .notificationType(request.notificationType())
                .build();
    }

    private NotificationPageResult fetchNotifications(final String currentMemberId, final Long lastId) {
        return notificationRepository.findByMemberId(
                currentMemberId,
                LocalDateTime.now().minusDays(31),
                lastId
        );
    }

    private Map<String, Member> fetchMemberMap(final NotificationPageResult pageResult) {
        List<String> opponentMemberIds = pageResult.notifications().stream()
                .map(Notification::getOpponentMemberId)
                .distinct()
                .toList();

        return memberRepository.findAllById(opponentMemberIds).stream()
                .collect(Collectors.toMap(Member::getMemberId, Function.identity()));
    }

    private List<NotificationDTO> createNotificationDTOs(final NotificationPageResult pageResult, final Map<String, Member> memberMap) {
        return pageResult.notifications().stream()
                .map(notification -> createNotificationDTO(notification, memberMap))
                .toList();
    }

    private NotificationDTO createNotificationDTO(final Notification notification, final Map<String, Member> memberMap) {
        Member opponentMember = memberMap.get(notification.getOpponentMemberId());
        if (opponentMember == null) {
            throw new MemberException.MemberNotFoundException(notification.getOpponentMemberId());
        }

        return NotificationDTO.from(notification, opponentMember);
    }

    /*
     * [안 읽은 알림 개수 세기]
     */
    private int countNoReadNotification(final List<Notification> notifications) {
        return (int) notifications.stream()
                .filter(notification -> !notification.isRead())
                .count();
    }

    private Notification findNotificationById(final UpdateNotificationRequest request) {
        return notificationRepository.findById(request.notificationId())
                .orElseThrow(() -> new NotificationNotFoundException(request.notificationId()));
    }

    private void validateMemberExists(final String memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException.MemberNotFoundException(memberId));
    }
}
