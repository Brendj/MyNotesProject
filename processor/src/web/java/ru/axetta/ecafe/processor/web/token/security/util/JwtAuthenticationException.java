/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.util;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {

    public JwtAuthenticationException(String msg) {
        super(msg);
    }

    public JwtAuthenticationException(JwtAuthenticationErrorDTO jwtAuthenticationErrorDTO){
        super(jwtAuthenticationErrorDTO.toString());
    }
}
