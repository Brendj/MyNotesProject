/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 28.02.2010
 * Time: 12:23:20
 * To change this template use File | Settings | File Templates.
 */
public class SendResponse {

    public static final int AUTH_FAILED = -1;
    public static final int XML_ERROR = -2;
    public static final int NOT_ENOUGH_CREDITS = -3;
    public static final int NO_RECIPIENTS = -4;
    public static final int MIN_SUCCESS_STATUS = 1;
    public static final int INPUT_PARAMS_ERROR = -100;
    public static final int COMMON_FAILURE = -101;

    private final int statusCode;
    private final String error;
    private final String messageId;

    public SendResponse(int statusCode, String error, String messageId) {
        this.statusCode = statusCode;
        this.error = error;
        this.messageId = messageId;
    }

    public boolean isSuccess() {
        return statusCode >= MIN_SUCCESS_STATUS;
    }

    public int getStatusCode() {
        return statusCode;
    }
    public String getMessageId() {
        return messageId;
    }
    public String getError() {
        return error;
    }

    public String getStatusMessage() {
        if (isSuccess()) {
            return "";
        }
        switch (statusCode) {
            case AUTH_FAILED:
                return "Неправильный логин и/или пароль";
            case XML_ERROR:
                return "Неправильный формат XML";
            case NOT_ENOUGH_CREDITS:
                return "Недостаточно кредитов на аккаунте пользователя";
            case NO_RECIPIENTS:
                return "Нет верных номеров получателей";
        }
        return "Неизвестная ошибка";
    }
}
