package com.adregamdi.member.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String password; // 비밀번호 (사용 x)
    @Column
    private String refreshToken; // 리프레쉬 토큰
    @Column
    private Boolean refreshTokenStatus; // 리프레쉬 토큰 상태 (T: 로그인/F: 로그아웃)
    @Column
    private Boolean memberStatus; // 회원 상태 (T: 등록/F: 탈퇴)

    @Enumerated(EnumType.STRING)
    private Role role; // 회원 권한

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
