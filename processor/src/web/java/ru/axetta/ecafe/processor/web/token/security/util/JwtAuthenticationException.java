/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.util;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {

    private Integer code;

    public JwtAuthenticationException(JwtAuthenticationErrorDTO jwtAuthenticationErrorDTO){
        super(jwtAuthenticationErrorDTO.getErrorMessage());
        this.code = jwtAuthenticationErrorDTO.getErrorCode();
    }

    public JwtAuthenticationException(JwtAuthenticationErrors error) {
        super(error.getErrorMessage());
        this.code = error.getErrorCode();
    }

    public Integer getCode() {
        return code;
    }
}
