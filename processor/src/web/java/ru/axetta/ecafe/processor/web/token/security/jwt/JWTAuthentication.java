/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class JWTAuthentication implements Authentication {
    private final String token;
    private Collection<GrantedAuthority> authorities;
    private boolean isAuthenticated;
    private UserDetails principal;

    public JWTAuthentication(String token) {
        this.token = token;
    }

    public JWTAuthentication(String token, Collection<GrantedAuthority> authorities, boolean isAuthenticated,
            UserDetails principal) {
        this(token);
        this.authorities = authorities;
        this.isAuthenticated = isAuthenticated;
        this.principal = principal;
    }


    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return principal;
    }

    @Override
    public String getName() {
        if (principal != null)
            return ((UserDetails) principal).getUsername();
        else
            return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {
        isAuthenticated = b;
    }

    public String getToken() {
        return token;
    }

}