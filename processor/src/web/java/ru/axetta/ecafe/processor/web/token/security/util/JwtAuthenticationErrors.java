/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.util;

public enum JwtAuthenticationErrors {
    TOKEN_CORRUPTED(300,"Token corrupted"),
    TOKEN_INVALID(301,"Token invalid"),
    TOKEN_EXPIRED(303,"Token expired"),
    USER_DISABLED(304,"User is disabled"),
    USERNAME_IS_NULL(305, "Username cannot be null"),
    USER_ROLE_NOT_ALLOWED(306, "User role does not match the allowed roles");

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
