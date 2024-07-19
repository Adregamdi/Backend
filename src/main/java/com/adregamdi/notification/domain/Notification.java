package com.adregamdi.notification.domain;

import com.adregamdi.core.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_notification")
public class Notification extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;
    @Column
    private UUID memberId; // 회원 id
    @Column
    private String content; // 내용
    @Column
    private String uri; // 필요 시 리다이렉트 시킬 uri
    @Column
    private boolean isRead; // 상태 (T: 읽음, F: 안 읽음)
}
