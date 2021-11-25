/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import ru.axetta.ecafe.processor.web.token.security.service.JwtUserDetailsImpl;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrorDTO;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrors;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;


@Service
public class JWTAuthenticationManager implements AuthenticationManager {
    private final JwtTokenProvider jwtTokenProvider;

    public JWTAuthenticationManager(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            if (authentication instanceof JWTAuthentication) {
                return processAuthentication((JWTAuthentication) authentication);
            } else {
                authentication.setAuthenticated(false);
                return authentication;
            }
        } catch (Exception ex) {
            authentication.setAuthenticated(false);
            if(ex instanceof AuthenticationException)
                throw ex;
        }
        return authentication;
    }

    private JWTAuthentication processAuthentication(JWTAuthentication authentication) throws AuthenticationException {
        String token = authentication.getToken();
        try {
            if (!jwtTokenProvider.validateToken(token)){
                throw new JwtAuthenticationException(new JwtAuthenticationErrorDTO(JwtAuthenticationErrors.TOKEN_INVALID.getErrorCode(),
                                                                                   JwtAuthenticationErrors.TOKEN_INVALID.getErrorMessage()));
            }
            JWTAuthentication jwtAuthentication = jwtTokenProvider.getAuthentication(authentication.getToken());
            if (!jwtAuthentication.isAuthenticated()){
                throw new JwtAuthenticationException(new JwtAuthenticationErrorDTO(JwtAuthenticationErrors.TOKEN_INVALID.getErrorCode(),
                                                                                   JwtAuthenticationErrors.TOKEN_INVALID.getErrorMessage()));
            }
            if(!((JwtUserDetailsImpl)jwtAuthentication.getPrincipal()).isEnabled())
                throw new JwtAuthenticationException(new JwtAuthenticationErrorDTO(JwtAuthenticationErrors.USER_DISABLED.getErrorCode(),
                        JwtAuthenticationErrors.USER_DISABLED.getErrorMessage()));
            return jwtAuthentication;
        } catch (AuthenticationException ex) {
            throw ex;
        }
    }
}

