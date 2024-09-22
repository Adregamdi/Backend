package com.adregamdi.member.domain;

import com.adregamdi.core.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tbl_member")
public class Member extends BaseTime {
    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String memberId;
    @Column
    private String name; // 이름
    @Column
    private String profile; // 프로필 사진
    @Column
    private String handle; // 핸들
    @Column
    private String email; // 이메일
    @Column
    private String password; // 비밀번호 (사용 x)
    @Column
    private String age; // 연령대
    @Column
    private String gender; // 성별
    @Column
    private String socialId; // 소셜 id
    @Column
    private String socialAccessToken; // 소셜 액세스 토큰
    @Column
    private String refreshToken; // 리프레쉬 토큰
    @Column
    private Boolean refreshTokenStatus; // 리프레쉬 토큰 상태 (T: 로그인/F: 로그아웃)
    @Column
    private Boolean memberStatus; // 회원 상태 (T: 등록/F: 탈퇴)

    @Enumerated(EnumType.STRING)
    private SocialType socialType; // APPLE, GOOGLE, KAKAO

    @Enumerated(EnumType.STRING)
    private Role role; // 회원 권한

    @Builder
    public Member(String name, String profile, String handle, String email, String age, String gender, String socialId, SocialType socialType) {
        this.memberId = UUID.randomUUID().toString();
        this.name = name;
        this.profile = profile;
        this.handle = handle;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.socialId = socialId;
        this.socialType = socialType;
        this.role = Role.MEMBER;
        this.refreshTokenStatus = false;
        this.memberStatus = true;
    }

    public void updateMember(String name, String profile, String handle) {
        this.name = name;
        this.profile = profile;
        this.handle = handle;
    }

    public void updateSocialAccessToken(String socialAccessToken) {
        this.socialAccessToken = socialAccessToken;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateRefreshTokenStatus(Boolean status) {
        this.refreshTokenStatus = status;
    }

    public void updateAuthorization(Role role) {
        this.role = role;
    }

    public void updateMemberStatus(Boolean status) {
        this.memberStatus = status;
    }
}
