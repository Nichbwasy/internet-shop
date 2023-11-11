package com.shop.authorization.controller.security;

import com.shop.authorization.common.constant.UsersRoles;
import com.shop.authorization.controller.security.filter.JwtTokenFilter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class AuthorizationWebSecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    @PostConstruct
    public void init() {
        jwtTokenFilter.setFilteredPaths(
                "/authorization", "/registration",
                "/swagger-ui.html", "/swagger-ui/**",
                "/v3/**", "/error", "/favicon.ico"
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(policy -> policy.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests( request -> request
                        .requestMatchers("/authorization", "/registration",
                                "/swagger-ui.html", "/swagger-ui/**", "/v3/**", "/error", "/favicon.ico")
                        .permitAll()
                        .requestMatchers("/api/**")
                        .hasAnyAuthority(UsersRoles.ADMIN, UsersRoles.MICROSERVICE)
                ).build();
    }

}
