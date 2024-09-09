package com.adregamdi.member.domain;

import com.adregamdi.core.entity.BaseTime;
import com.adregamdi.core.oauth2.dto.SignUpDTO;
import com.adregamdi.member.dto.request.UpdateMyMemberRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_member")
public class Member extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID memberId;
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

    public Member(SignUpDTO signUpDTO) {
        this.name = signUpDTO.getName();
        this.profile = "https://adregamdi-dev2.s3.ap-northeast-2.amazonaws.com/profile/default_profile_image.png";
        this.handle = signUpDTO.getHandle();
        this.email = signUpDTO.getEmail();
        this.age = signUpDTO.getAge();
        this.gender = signUpDTO.getGender();
        this.socialId = signUpDTO.getSocialId();
        this.socialType = signUpDTO.getSocialType();
        this.role = Role.MEMBER;
        this.refreshTokenStatus = false;
        this.memberStatus = true;
    }

    public void updateMember(UpdateMyMemberRequest request) {
        this.name = request.name();
        this.profile = request.profile();
        this.handle = request.handle();
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
