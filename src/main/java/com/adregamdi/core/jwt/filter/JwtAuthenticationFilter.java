package com.adregamdi.core.jwt.filter;

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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.adregamdi.core.exception.GlobalException.LogoutMemberException;
import static com.adregamdi.core.exception.GlobalException.TokenValidationException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String NO_CHECK_URL = "/login"; // "/login"으로 들어오는 요청은 Filter 작동 X
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final List<String> allowedUrls;

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        log.info("Incoming request - URL: {}, Query: {}, Method: {}",
                requestUri,
                queryString != null ? queryString : "No query string",
                request.getMethod());

        if (isAllowedUrl(requestUri)) {
            log.info("URL {} is in allowed list. Skipping token validation.", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtService.extractAccessToken(request).orElse(null);
        String refreshToken = jwtService.extractRefreshToken(request).orElse(null);

        log.info("Extracted tokens - Access: {}, Refresh: {}",
                accessToken != null ? "Present" : "Absent",
                refreshToken != null ? "Present" : "Absent");

        if (refreshToken != null && jwtService.isTokenValid(refreshToken) && Objects.equals("/api/auth/reissue", requestUri)) {
            log.info("Valid refresh token present. Reissuing access token.");
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return;
        }

        checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    private boolean isAllowedUrl(String requestUri) {
        boolean allowed = allowedUrls.stream().anyMatch(requestUri::startsWith);
        log.info("URL {} is {}allowed", requestUri, allowed ? "" : "not ");
        return allowed;
    }

    public void checkRefreshTokenAndReIssueAccessToken(
            final HttpServletResponse response,
            final String refreshToken
    ) {
        memberRepository.findByRefreshToken(refreshToken)
                .ifPresent(user -> {
                    if (Objects.equals(user.getRefreshTokenStatus(), true)) {
                        jwtService.sendAccessAndRefreshToken(
                                response,
                                jwtService.createAccessToken(user.getMemberId(), user.getRole()),
                                reIssueRefreshToken(user)
                        );
                    }
                });
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
        } catch (TokenValidationException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
            return;
        } catch (LogoutMemberException e) {
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
