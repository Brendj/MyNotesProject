/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;

import ru.axetta.ecafe.processor.web.token.security.service.JwtUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;

@Service
public class JWTAuthenticationManager implements AuthenticationManager {

    private final String key = "testKey";


    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            if (authentication instanceof JWTAuthentication) {
                JWTAuthentication readyJWTAuthentication = processAuthentication((JWTAuthentication) authentication);
                return readyJWTAuthentication;
            } else {
                authentication.setAuthenticated(false);
                return authentication;
            }
        } catch (Exception ex) {
            if(ex instanceof AuthenticationServiceException)
                throw ex;
        }
        return null;
    }

    private JWTAuthentication processAuthentication(JWTAuthentication authentication) throws AuthenticationException {
        String token = authentication.getToken();
        DefaultClaims claims;
        try {
            claims = (DefaultClaims) Jwts.parser().setSigningKey(key).parse(token).getBody();
        } catch (Exception ex) {
            throw new AuthenticationServiceException("Token corrupted");
        }
        if (claims.get("TOKEN_EXPIRATION_DATE", Long.class) == null)
            throw new AuthenticationServiceException("Invalid token");
        Date expiredDate = new Date(claims.get("TOKEN_EXPIRATION_DATE", Long.class));
        if (expiredDate.after(new Date()))
            return buildFullTokenAuthentication(authentication, claims);
        else
            throw new AuthenticationServiceException("Token expired date error");
    }

    private JWTAuthentication buildFullTokenAuthentication(JWTAuthentication authentication, DefaultClaims claims) {
        User user = (User) userDetailsService.loadUserByUsername(claims.get("USERNAME", String.class));
        if (user.isEnabled()) {
            Collection<GrantedAuthority> authorities = user.getAuthorities();
            JWTAuthentication fullJWTAuthentication =
                    new JWTAuthentication(authentication.getToken(), authorities, true, user);
            return fullJWTAuthentication;
        } else {
            throw new AuthenticationServiceException("User disabled");
        }
    }
}

