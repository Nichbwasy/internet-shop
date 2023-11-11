package com.shop.authorization.controller.security.dto;

import io.jsonwebtoken.Claims;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Data
public class JwtAuthenticationDto implements Authentication {

    private boolean authenticated;
    private String username;
    private Collection<SimpleGrantedAuthority> authorities;

    public JwtAuthenticationDto(Claims claims) {
        if (!claims.isEmpty()) {
            this.authenticated = true;
            this.username = claims.getSubject();
            this.authorities = getAuthoritiesFromClaims(claims);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return authorities;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return username;
    }

    private Collection<SimpleGrantedAuthority> getAuthoritiesFromClaims(Claims claims) {
        HashSet<SimpleGrantedAuthority> authorities = new HashSet<>();
        List<String> roles = claims.get("authorities", List.class);
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
        return authorities;
    }

}