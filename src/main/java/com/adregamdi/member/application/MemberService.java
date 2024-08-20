package com.adregamdi.member.application;

import com.adregamdi.member.domain.Member;
import com.adregamdi.member.domain.Role;
import com.adregamdi.member.domain.SocialType;
import com.adregamdi.member.dto.response.GetMyMemberResponse;
import com.adregamdi.member.exception.MemberException.MemberNotFoundException;
import com.adregamdi.member.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {
    private final WebClient webClient;
    private final MemberRepository memberRepository;

    /**
     * [내 정보 조회]
     */
    @Transactional
    public GetMyMemberResponse getMyMember(final String username) {
        Member member = memberRepository.findById(UUID.fromString(username))
                .orElseThrow(() -> new MemberNotFoundException(username));

        return GetMyMemberResponse.from(member);
    }

    /**
     * [로그아웃]
     */
    @Transactional
    public void logout(final String memberId) {
        Member member = memberRepository.findByIdAndMemberStatus(UUID.fromString(memberId), true)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        member.updateRefreshTokenStatus(false);
    }

    /**
     * [소프트 탈퇴]
     */
    @Transactional
    public void delete(final String memberId) {
        Member member = memberRepository.findByIdAndMemberStatus(UUID.fromString(memberId), true)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        member.updateAuthorization(Role.GUEST);
        member.updateMemberStatus(false);
        member.updateRefreshTokenStatus(false);
    }

    /**
     * [완전 탈퇴]
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    protected void leave() {
        LocalDateTime date = LocalDateTime.now().minusDays(30);
        List<Member> members = memberRepository.findByMemberStatusAndUpdatedAt(date)
                .orElseThrow(MemberNotFoundException::new);

        for (Member member : members) {
            // 회원과 관련된 데이터 삭제
            deleteData(member.getId());

            // 소셜 연결 끊기
            unlink(member.getSocialType(), member.getSocialId(), member.getSocialAccessToken());
        }

        // 회원 물리 삭제
        memberRepository.deleteByMemberStatusAndUpdatedAt(date);
    }

    private void deleteData(final UUID memberId) {

    }

    /**
     * [연결 끊기]
     */
    private void unlink(
            final SocialType socialType,
            final String memberId,
            final String socialAccessToken) {
        if (("KAKAO").equals(String.valueOf(socialType))) {
            unlinkKakao(memberId, socialAccessToken).block();
        }
        if (("GOOGLE").equals(String.valueOf(socialType))) {
            unlinkGoogle(socialAccessToken).block();
        }
        throw new RuntimeException();
    }

    /**
     * [카카오 연결 끊기]
     */
    private Mono<String> unlinkKakao(
            final String memberId,
            final String accessToken
    ) {
        String url = "https://kapi.kakao.com/v1/user/unlink";

        return webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("target_id_type=user_id&target_id=" + memberId)
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * [구글 연결 끊기]
     */
    private Mono<String> unlinkGoogle(final String accessToken) {
        String url = "https://accounts.google.com/o/oauth2/revoke";

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(url)
                        .queryParam("token", accessToken)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}
