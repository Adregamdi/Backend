package com.adregamdi.member.application;

import com.adregamdi.core.redis.application.TokenRedisService;
import com.adregamdi.like.application.LikesService;
import com.adregamdi.media.application.ImageService;
import com.adregamdi.member.domain.Member;
import com.adregamdi.member.domain.Role;
import com.adregamdi.member.domain.SocialType;
import com.adregamdi.member.dto.request.GetMemberContentsRequest;
import com.adregamdi.member.dto.request.UpdateMyMemberRequest;
import com.adregamdi.member.dto.response.GetMemberContentsResponse;
import com.adregamdi.member.dto.response.GetMyMemberResponse;
import com.adregamdi.member.exception.MemberException.HandleExistException;
import com.adregamdi.member.exception.MemberException.MemberNotFoundException;
import com.adregamdi.member.infrastructure.MemberRepository;
import com.adregamdi.place.application.PlaceService;
import com.adregamdi.shorts.application.ShortsService;
import com.adregamdi.travelogue.application.TravelogueService;
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
import java.util.Objects;

import static com.adregamdi.media.domain.ImageTarget.PROFILE;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {
    private final WebClient webClient;
    private final ImageService imageService;
    private final TokenRedisService tokenRedisService;
    private final MemberRepository memberRepository;

    private final LikesService likesService;
    private final PlaceService placeService;
    private final TravelogueService travelogueService;
    private final ShortsService shortsService;

    /*
     * [마지막 접속 시간 체크]
     */
    @Override
    @Transactional
    public void connectedAt(final Member member) {
        member.updateConnectedAt();
    }

    /*
     * [내 정보 조회]
     */
    @Override
    @Transactional(readOnly = true)
    public GetMyMemberResponse getMyMember(final String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        return GetMyMemberResponse.from(member);
    }

    /*
     * [내 정보 수정]
     */
    @Override
    @Transactional
    public void update(final UpdateMyMemberRequest request, final String memberId) {
        Member another = memberRepository.findByHandle(request.handle());
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        if (another != null && !Objects.equals(another.getHandle(), member.getHandle())) {
            throw new HandleExistException(request.handle());
        }

        member.updateMember(request.name(), request.profile(), request.handle());
        imageService.updateImage(request.profile(), PROFILE, memberId);
    }

    /*
     * [로그아웃]
     */
    @Override
    @Transactional
    public void logout(final String memberId, final String accessToken) {
//        Member member = memberRepository.findByMemberIdAndMemberStatus(memberId, true)
//                .orElseThrow(() -> new MemberNotFoundException(memberId));
//
//        member.updateRefreshTokenStatus(false);

        // Redis에서 리프레시 토큰 삭제 및 액세스 토큰 로그아웃 처리
        tokenRedisService.logoutUser(memberId, accessToken);
    }

    /*
     * [소프트 탈퇴]
     */
    @Override
    @Transactional
    public void delete(final String memberId) {
        Member member = memberRepository.findByMemberIdAndMemberStatus(memberId, true)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        member.updateAuthorization(Role.GUEST);
        member.updateMemberStatus(false);
    }

    /*
     * [완전 탈퇴]
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    protected void leave() {
        LocalDateTime date = LocalDateTime.now().minusDays(30);
        List<Member> members = memberRepository.findByMemberStatusAndUpdatedAt(date);
        if (members.isEmpty()) {
            return;
        }

        for (Member member : members) {
            // 회원과 관련된 데이터 삭제
            deleteData(member.getMemberId());

            // 소셜 연결 끊기
            unlink(member.getSocialType(), member.getSocialId(), member.getSocialAccessToken());
        }

        // 회원 물리 삭제
        memberRepository.deleteByMemberStatusAndUpdatedAt(date);
    }

    private void deleteData(final String memberId) {
        likesService.deleteMyLike(memberId);
        placeService.deleteMyReview(memberId);
        shortsService.deleteMyShorts(memberId);
        travelogueService.deleteMyTravelogue(memberId);
    }

    /*
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

    /*
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

    /*
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

    /*
     * [특정 멤버 컨텐츠 조회]
     */
    @Override
    @Transactional(readOnly = true)
    public GetMemberContentsResponse<?> getMemberContentsOfAll(GetMemberContentsRequest request) {
        return memberRepository.getMemberContentsOfAll(request);
    }
}
