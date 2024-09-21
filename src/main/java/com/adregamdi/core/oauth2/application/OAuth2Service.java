package com.adregamdi.core.oauth2.application;

import com.adregamdi.core.jwt.service.JwtService;
import com.adregamdi.core.oauth2.dto.LoginRequest;
import com.adregamdi.core.oauth2.dto.LoginResponse;
import com.adregamdi.core.oauth2.dto.OAuth2Attributes;
import com.adregamdi.media.application.ImageService;
import com.adregamdi.member.domain.Member;
import com.adregamdi.member.domain.SocialType;
import com.adregamdi.member.infrastructure.MemberRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.ECDSAKeyProvider;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

import static com.adregamdi.media.domain.ImageTarget.PROFILE;


@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2Service {
    private static final String APPLE = "apple";
    private static final String KAKAO = "kakao";
    private final JwtService jwtService;
    private final ImageService imageService;
    private final MemberRepository memberRepository;
    private final WebClient webClient;

    @Value("${social-login.provider.apple.team-id}")
    private String teamId; // Apple Developer에서 가져온 팀 ID
    @Value("${social-login.provider.apple.client-id}")
    private String clientId; // Apple Developer에서 가져온 클라이언트 ID
    @Value("${social-login.provider.apple.key-id}")
    private String keyId; // Apple Developer에서 가져온 키 ID
    @Value("${social-login.provider.apple.private-key}")
    private String privateKey; // .p8 파일의 내용

    @Transactional
    public LoginResponse login(final LoginRequest request) {
        Map<String, Object> userInfo = switch (request.socialType()) {
            case "kakao" -> {
                String kakaoUserInfoUrl = "https://kapi.kakao.com/v2/user/me";
                yield fetchUserInfo(kakaoUserInfoUrl, request.oauthAccessToken());
            }
            case "google" -> {
                String googleUserInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
                yield fetchUserInfo(googleUserInfoUrl, request.oauthAccessToken());
            }
            case "apple" -> handleAppleLogin(request);
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 서비스입니다.: " + request.socialType());
        };

        SocialType socialType = getSocialType(request.socialType());
        String userNameAttributeName = getUserNameAttributeName(request.socialType());
        OAuth2Attributes extractAttributes = OAuth2Attributes.of(socialType, userNameAttributeName, userInfo);

        Member findMember = getMember(extractAttributes, socialType);
        if (request.oauthAccessToken() != null) {
            findMember.updateSocialAccessToken(request.oauthAccessToken());
        } else if (request.idToken() != null) {
            findMember.updateSocialAccessToken(request.idToken());
        } else {
            findMember.updateSocialAccessToken(request.authorizationCode());
        }

        String accessToken = jwtService.createAccessToken(String.valueOf(findMember.getMemberId()), findMember.getRole());
        String refreshToken = jwtService.createRefreshToken();
        findMember.updateRefreshToken(refreshToken);
        findMember.updateRefreshTokenStatus(true);

        return new LoginResponse(accessToken, refreshToken);
    }

    private Map<String, Object> handleAppleLogin(final LoginRequest request) {
        String idToken;
        if (request.idToken() != null) { // Android case
            idToken = request.idToken();
        } else { // iOS case
            idToken = getAppleIdToken(request.authorizationCode());
        }

        return verifyAndExtractUserInfo(idToken);
    }

    private String getAppleIdToken(final String authorizationCode) {
        // Apple 서버에 authorizationCode를 사용하여 idToken을 요청
        String clientSecret = generateClientSecret(); // JWT 형식의 클라이언트 시크릿 생성

        return webClient.post()
                .uri("https://appleid.apple.com/auth/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("code", authorizationCode)
                        .with("grant_type", "authorization_code"))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Apple Auth Client Error: {}", body);
                                    return Mono.error(new RuntimeException("Apple authentication failed: " + body));
                                })
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Apple Auth Server Error: {}", body);
                                    return Mono.error(new RuntimeException("Apple server error: " + body));
                                })
                )
                .bodyToMono(Map.class)
                .doOnNext(response -> {
                    if (response.containsKey("error")) {
                        log.error("Apple Auth Error in response body: {}", response);
                        throw new RuntimeException("Apple authentication failed: " + response.get("error"));
                    }
                })
                .map(response -> {
                    log.info("Apple Auth Response: {}", response);
                    return (String) response.get("id_token");
                })
                .doOnError(error -> log.error("Error during Apple authentication", error))
                .block();
    }

    private String generateClientSecret() {
        try {
            // JWT 헤더 설정
            Map<String, Object> headerClaims = new HashMap<>();
            headerClaims.put("kid", keyId);

            // JWT 생성
            return JWT.create()
                    .withHeader(headerClaims)
                    .withIssuer(teamId)
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 15777000000L)) // 6개월
                    .withAudience("https://appleid.apple.com")
                    .withSubject(clientId)
                    .sign(Algorithm.ECDSA256(new ECDSAKeyProvider() {
                        @Override
                        public ECPublicKey getPublicKeyById(String keyId) {
                            return null; // 클라이언트 시크릿 생성시에는 필요 없음
                        }

                        @Override
                        public ECPrivateKey getPrivateKey() {
                            try {
                                byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(privateKey);
                                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
                                KeyFactory kf = KeyFactory.getInstance("EC");
                                return (ECPrivateKey) kf.generatePrivate(keySpec);
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to load private key", e);
                            }
                        }

                        @Override
                        public String getPrivateKeyId() {
                            return keyId;
                        }
                    }));
        } catch (Exception e) {
            log.error("애플 클라이언트 시크릿 생성 실패", e);
            throw new RuntimeException("애플 클라이언트 시크릿 생성 중 오류가 발생했습니다.", e);
        }
    }

    private Map<String, Object> verifyAndExtractUserInfo(final String idToken) {
        try {
            // Apple의 공개키 가져오기
            String jwksJson = webClient.get()
                    .uri("https://appleid.apple.com/auth/keys")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JSONObject jwks = new JSONObject(jwksJson);
            JSONArray keys = jwks.getJSONArray("keys");

            // ID 토큰 파싱
            String[] tokenParts = idToken.split("\\.");
            String header = new String(Base64.getUrlDecoder().decode(tokenParts[0]));
            String payload = new String(Base64.getUrlDecoder().decode(tokenParts[1]));

            JSONObject headerJson = new JSONObject(header);
            String kid = headerJson.getString("kid");

            // 매칭되는 키 찾기
            JSONObject key = findMatchingKey(keys, kid);
            if (key == null) {
                throw new RuntimeException("Matching key not found");
            }

            // 토큰 검증
            RSAPublicKey publicKey = getRSAPublicKey(key);
            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("https://appleid.apple.com")
                    .build();

            verifier.verify(idToken);

            // 페이로드에서 사용자 정보 추출
            JSONObject payloadJson = new JSONObject(payload);
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("sub", payloadJson.getString("sub"));
            if (payloadJson.has("email")) {
                userInfo.put("email", payloadJson.getString("email"));
            }

            return userInfo;
        } catch (Exception e) {
            log.error("애플 ID 토큰 검증 실패", e);
            throw new RuntimeException("애플 로그인 처리 중 오류가 발생했습니다.", e);
        }
    }

    private JSONObject findMatchingKey(final JSONArray keys, final String kid) {
        for (int i = 0; i < keys.length(); i++) {
            JSONObject key = keys.getJSONObject(i);
            if (kid.equals(key.getString("kid"))) {
                return key;
            }
        }
        return null;
    }

    private RSAPublicKey getRSAPublicKey(final JSONObject key) throws Exception {
        String nStr = key.getString("n");
        String eStr = key.getString("e");

        BigInteger n = new BigInteger(1, Base64.getUrlDecoder().decode(nStr));
        BigInteger e = new BigInteger(1, Base64.getUrlDecoder().decode(eStr));

        RSAPublicKeySpec spec = new RSAPublicKeySpec(n, e);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) factory.generatePublic(spec);
    }

    private Map fetchUserInfo(final String userInfoUrl, final String accessToken) {
        return webClient
                .get()
                .uri(userInfoUrl)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("Failed to fetch user info: " + body)))
                )
                .bodyToMono(Map.class)
                .block();
    }

    private SocialType getSocialType(final String registrationId) {
        if (Objects.equals(APPLE, registrationId)) {
            return SocialType.APPLE;
        }
        if (Objects.equals(KAKAO, registrationId)) {
            return SocialType.KAKAO;
        }
        return SocialType.GOOGLE;
    }

    private String getUserNameAttributeName(final String socialType) {
        return switch (socialType) {
            case "google", "apple" -> "sub";
            case "kakao" -> "id";
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 서비스입니다.: " + socialType);
        };
    }

    private Member getMember(final OAuth2Attributes attributes, final SocialType socialType) {
        Member findMember = memberRepository.findBySocialTypeAndSocialId(socialType, attributes.getOauth2UserInfo().getId())
                .orElse(null);

        if (findMember == null) {
            return saveMember(attributes, socialType);
        }
        return findMember;
    }

    private Member saveMember(final OAuth2Attributes attributes, final SocialType socialType) {
        Member createdMember = attributes.toEntity(socialType, attributes.getOauth2UserInfo());
        imageService.saveTargetId(createdMember.getProfile(), PROFILE, String.valueOf(createdMember.getMemberId()));
        return memberRepository.save(createdMember);
    }
}
