/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.util;

public enum JwtAuthenticationErrors {
    TOKEN_CORRUPTED(100,"Token corrupted"),
    TOKEN_INVALID(101,"Token invalid"),
    TOKEN_EXPIRED(103,"Token expired"),
    USER_DISABLED(104,"User is disabled"),
    USERNAME_IS_NULL(105, "Username cannot be null"),
    INVALID_PASSWORD(106, "Invalid password"),
    ;

    private int errorCode;
    private String errorMessage;

    private JwtAuthenticationErrors(int errorCode, String errorMessage) {
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
