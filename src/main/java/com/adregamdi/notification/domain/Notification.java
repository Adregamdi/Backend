package com.adregamdi.notification.domain;

import com.adregamdi.core.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tbl_notification")
public class Notification extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;
    @Column(name = "member_id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private String memberId;  // 회원 id
    @Column
    private Long contentId; // 콘텐츠 id
    @Column
    private String opponentMemberProfile; // 상대 회원 프로필
    @Column
    private String opponentMemberHandle; // 상대 회원 핸들
    @Column
    private boolean isRead; // 상태 (T: 읽음, F: 안 읽음)

    @Enumerated(EnumType.STRING)
    private ContentType contentType; // 콘텐츠 종류 (쇼츠, 여행기)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType; // 알림 종류 (좋아요, 일정)

    @Builder
    public Notification(String memberId, Long contentId, String opponentMemberProfile, String opponentMemberHandle, ContentType contentType, NotificationType notificationType) {
        this.memberId = memberId;
        this.contentId = contentId;
        this.opponentMemberProfile = opponentMemberProfile;
        this.opponentMemberHandle = opponentMemberHandle;
        this.contentType = contentType;
        this.notificationType = notificationType;
        this.isRead = false;
    }

    public void updateIsRead(final boolean isRead) {
        this.isRead = isRead;
    }
}
