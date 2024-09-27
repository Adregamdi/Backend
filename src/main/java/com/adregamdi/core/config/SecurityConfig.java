package com.adregamdi.core.config;

import com.adregamdi.core.jwt.filter.JwtAuthenticationFilter;
import com.adregamdi.core.jwt.service.JwtService;
import com.adregamdi.core.oauth2.application.CustomOAuth2UserService;
import com.adregamdi.core.oauth2.handler.OAuth2LoginFailureHandler;
import com.adregamdi.core.oauth2.handler.OAuth2LoginSuccessHandler;
import com.adregamdi.member.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import java.util.Arrays;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final String[] ALLOWED_URLS = {
            "/",
            "/index.html",
            "/api/oauth2/**",
            "/api/shorts/stream/**",
            "/video-streaming.html"
    };

    @Bean
    public SecurityFilterChain filterChain(
            final HttpSecurity http
    ) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
//                .headers(AbstractHttpConfigurer::disable)
                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(request ->
                        request.requestMatchers(ALLOWED_URLS).permitAll()
                                .anyRequest().authenticated())
                .oauth2Login(oauth2Login -> oauth2Login
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                )
                .addFilterAfter(jwtAuthenticationFilter(), LogoutFilter.class)
        ;

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, memberRepository, Arrays.asList(ALLOWED_URLS));
    }
}
