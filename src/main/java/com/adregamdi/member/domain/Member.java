package com.adregamdi.member.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role role; // 권한

    @Enumerated(EnumType.STRING)
    private Boolean refreshTokenStatus; // 리프레쉬 토큰 상태 (T: 로그인/F: 로그아웃)
}
