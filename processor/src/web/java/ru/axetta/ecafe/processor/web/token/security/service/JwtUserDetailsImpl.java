/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.service;

import ru.axetta.ecafe.processor.core.persistence.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class JwtUserDetailsImpl implements UserDetails {

    private final User user;
    private final String refreshToken;
    private final String refreshTokenHash;
    private Collection<GrantedAuthority> grantedAuthorities;

    public JwtUserDetailsImpl(final User user, final String refreshToken, final String refreshTokenHash ) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.refreshTokenHash = refreshTokenHash;
        this.grantedAuthorities = new ArrayList<>();
        this.grantedAuthorities.add(new GrantedAuthorityImpl(user.getRoleName()));
    }


    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !user.isBlocked();
    }

    public User getUser() { return user; }
}
