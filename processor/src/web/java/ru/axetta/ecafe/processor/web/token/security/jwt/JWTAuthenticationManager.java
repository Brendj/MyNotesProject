/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.jwt;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.impl.DefaultClaims;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.token.security.service.JwtUserDetailsImpl;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrorDTO;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrors;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;


@Service
public class JWTAuthenticationManager implements AuthenticationManager {
    

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
            if(ex instanceof AuthenticationException)
                throw ex;
        }
        return authentication;
    }

    private JWTAuthentication processAuthentication(JWTAuthentication authentication) throws AuthenticationException {
        String token = authentication.getToken();
        Jwt jwtToken;
        JwtTokenProvider jwtTokenProvider = RuntimeContext.getAppContext().getBean(JwtTokenProvider.class);
        try {
            jwtToken = jwtTokenProvider.validateToken(token);
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) jwtTokenProvider.getUserDetailsFromToken((DefaultClaims) jwtToken.getBody());
            JWTAuthentication jwtAuthentication = new JWTAuthentication(token,jwtUserDetails.getAuthorities(),true, jwtUserDetails);
            if(!jwtUserDetails.isEnabled())
                throw new JwtAuthenticationException(new JwtAuthenticationErrorDTO(JwtAuthenticationErrors.USER_DISABLED.getErrorCode(),
                        JwtAuthenticationErrors.USER_DISABLED.getErrorMessage()));
            return jwtAuthentication;
        } catch (AuthenticationException ex) {
            throw ex;
        }
    }
}

