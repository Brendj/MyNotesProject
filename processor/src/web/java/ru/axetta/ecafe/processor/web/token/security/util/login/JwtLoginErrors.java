/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.util.login;

public enum JwtLoginErrors {
    USERNAME_IS_NULL(200, "Username cannot be null"),
    INVALID_PASSWORD(201, "Invalid password"),
    USER_NOT_FOUND(203,"User not found"),
    USER_IS_BLOCKED(204,"User is blocked"),
    UNSUCCESSFUL_AUTHORIZATION(205, "Unsuccessful authorization"),
    INVALID_REFRESH_TOKEN(206, "Invalid refresh token"),
    INVALID_CHANGE_PASSWORD_DATA(207, "Invalid change password data"),
    CANNOT_SEND_SMS(208, "Can not send sms code");

    private int errorCode;
    private String errorMessage;

    private JwtLoginErrors(int errorCode, String errorMessage) {
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
