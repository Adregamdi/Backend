package com.adregamdi.notification.domain;

import com.adregamdi.core.entity.BaseTime;
import com.adregamdi.notification.dto.request.CreateNotificationRequest;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_notification")
public class Notification extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;
    @Column(name = "member_id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private String memberId;  // 회원 id
    @Column
    private String content; // 내용
    @Column
    private String uri; // 필요 시 리다이렉트 시킬 uri
    @Column
    private boolean isRead; // 상태 (T: 읽음, F: 안 읽음)

    @Enumerated(EnumType.STRING)
    private NotificationType type; // 알림 종류 (좋아요, 일정)

    public Notification(CreateNotificationRequest request) {
        this.memberId = request.memberId();
        this.content = request.content();
        this.uri = request.uri();
        this.isRead = false;
        this.type = request.type();
    }

    public void updateIsRead(final boolean isRead) {
        this.isRead = isRead;
    }
}
