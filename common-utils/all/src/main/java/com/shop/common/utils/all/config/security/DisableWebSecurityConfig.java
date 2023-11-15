package com.shop.common.utils.all.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Slf4j
@Profile("debug")
@Configuration
public class DisableWebSecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("DEBUG PROFILE IS ACTIVE! WEB SECURITY DISABLE!");
        return (web) -> {
            web.ignoring()
                    .requestMatchers("/**")
                    .anyRequest();
        };
    }

}
