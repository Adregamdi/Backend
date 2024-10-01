package com.adregamdi.core.jwt.filter;

import com.adregamdi.core.exception.GlobalException;
import com.adregamdi.core.handler.ErrorResponse;
import com.adregamdi.core.jwt.service.JwtService;
import com.adregamdi.core.utils.PasswordUtil;
import com.adregamdi.member.domain.Member;
import com.adregamdi.member.infrastructure.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final List<String> allowedUris;

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        log.info("들어온 요청 - URI: {}, Query: {}, Method: {}",
                requestUri,
                queryString != null ? queryString : "쿼리 스트링 없음",
                request.getMethod());

        if (isAllowedUri(requestUri)) {
            log.info("{} 허용 URI. 토큰 유효성 검사 스킵.", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtService.extractAccessToken(request).orElse(null);
        String refreshToken = jwtService.extractRefreshToken(request).orElse(null);

        log.info("토큰 추출 - Access: {}, Refresh: {}",
                accessToken != null ? "존재" : "존재하지 않음",
                refreshToken != null ? "존재" : "존재하지 않음");

        if (refreshToken != null && jwtService.isTokenValid(refreshToken) && Objects.equals("/api/auth/reissue", requestUri)) {
            log.info("유효한 리프레쉬 토큰 존재. 액세스 토큰을 재발급합니다.");
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return;
        }

        checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    private boolean isAllowedUri(String requestUri) {
        boolean allowed = false;
        for (String pattern : allowedUris) {
            if (pathMatcher.match(pattern, requestUri)) {
                allowed = true;
                break;
            }
        }
        log.info("URI {} is {}allowed", requestUri, allowed ? "" : "not ");
        return allowed;
    }

    public void checkRefreshTokenAndReIssueAccessToken(
            final HttpServletResponse response,
            final String refreshToken
    ) {
        try {
            memberRepository.findByRefreshToken(refreshToken)
                    .ifPresentOrElse(member -> {
                        jwtService.validateRefreshToken(refreshToken, member);
                        String newAccessToken = jwtService.createAccessToken(member.getMemberId(), member.getRole());
                        String newRefreshToken = reIssueRefreshToken(member);
                        jwtService.sendAccessAndRefreshToken(response, newAccessToken, newRefreshToken);
                    }, () -> {
                        throw new GlobalException.TokenValidationException("해당 리프레시 토큰을 가진 회원이 없습니다.");
                    });
        } catch (GlobalException.RefreshTokenMismatchException | GlobalException.TokenValidationException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            try {
                new ObjectMapper().writeValue(response.getWriter(), new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
            } catch (IOException ioException) {
                log.error("응답 쓰기 실패", ioException);
            }
        }
    }

    private String reIssueRefreshToken(final Member member) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        member.updateRefreshToken(reIssuedRefreshToken);
        memberRepository.saveAndFlush(member);
        return reIssuedRefreshToken;
    }

    public void checkAccessTokenAndAuthentication(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        log.info("checkAccessTokenAndAuthentication() 호출");
        try {
            jwtService.extractAccessToken(request)
                    .filter(jwtService::isTokenValid)
                    .flatMap(jwtService::extractMemberId)
                    .flatMap(memberId -> memberRepository.findByMemberIdAndMemberStatus(memberId, true))
                    .ifPresent(this::saveAuthentication);
        } catch (GlobalException.EmptyTokenException |
                 GlobalException.TokenExpiredException |
                 GlobalException.TokenValidationException |
                 GlobalException.MalformedTokenException |
                 GlobalException.UnsupportedTokenException |
                 GlobalException.TokenIssuedAtFutureException |
                 GlobalException.TokenClaimMissingException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
            return;
        } catch (GlobalException.LogoutMemberException e) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), new ErrorResponse(HttpStatus.FORBIDDEN.value(), e.getMessage()));
            return;
        }

        filterChain.doFilter(request, response);
    }

    public void saveAuthentication(final Member myMember) {
        String password = myMember.getPassword();
        if (password == null) {
            password = PasswordUtil.generateRandomPassword();
        }

        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(myMember.getMemberId())
                .password(password)
                .roles(myMember.getRole().name())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null, authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
