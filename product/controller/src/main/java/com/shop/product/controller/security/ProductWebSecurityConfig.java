package com.shop.product.controller.security;

import com.shop.authorization.common.constant.UsersRoles;
import com.shop.common.utils.filter.CommonJwtTokenFilter;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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
public class ProductWebSecurityConfig {

    private final CommonJwtTokenFilter jwtTokenFilter;

    @Bean
    public FilterRegistrationBean<CommonJwtTokenFilter> commonJwtTokenFilterFilterRegistrationBean() {
        FilterRegistrationBean<CommonJwtTokenFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(jwtTokenFilter);
        filterFilterRegistrationBean.setEnabled(false);
        return filterFilterRegistrationBean;
    }

    @PostConstruct
    public void init() {
        jwtTokenFilter.setFilteredPaths(
                "/swagger-ui.html", "/swagger-ui/**",
                "/api/tokens", "/api/tokens/**",
                "/v3/**", "/error", "/favicon.ico"
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(policy -> policy.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenFilter, (Class<? extends Filter>) UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests( request -> request
                        .requestMatchers("/api/tokens", "/api/tokens/**",
                                "/swagger-ui.html", "/swagger-ui/**", "/v3/**", "/error", "/favicon.ico")
                        .permitAll()
                        .requestMatchers("/category/**", "/discount/**", "/products/**")
                        .hasAnyAuthority(UsersRoles.ADMIN, UsersRoles.MICROSERVICE)
                        .requestMatchers("/api/**")
                        .hasAnyAuthority(UsersRoles.ADMIN, UsersRoles.MICROSERVICE)
                ).build();
    }

}
