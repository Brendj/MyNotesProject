/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.util.login;

import javax.security.auth.login.LoginException;

public class JwtLoginException extends LoginException {
    private final int errorCode;
    private final String errorMessage;

    public JwtLoginException(int errorCode, String errorMessage){
        super(String.format("%d: %s", errorCode, errorMessage));
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
