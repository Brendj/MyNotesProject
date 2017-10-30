/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import java.io.Serializable;

/**
 * User: shamil
 * Date: 13.05.15
 * Time: 11:17
 */
public class ResponseItem implements Serializable {

    public static final int OK = 0;
    public static final int ERROR_DUPLICATE = 160;
    public static final int ERROR_INTERNAL = 170;
    public static final int ERROR_SIGN_VERIFY = 180;
    public static final int ERROR_INVALID_TYPE = 190;
    public static final String OK_MESSAGE = "Ok.";
    public static final String ERROR_DUPLICATE_CARD_MESSAGE = "Данная карта уже зарегистрирована.";
    public static final String ERROR_INTERNAL_MESSAGE = "Внутренняя ошибка приложения.";
    public static final String ERROR_SIGN_VERIFY_MESSAGE = "Не пройдена проверка цифровой подписи";
    public static final String ERROR_INVALID_TYPE_MESSAGE = "Неизвестный тип карты";
    public int code;
    public String message;

    public ResponseItem() {
        code = OK;
        message = OK_MESSAGE;
    }

    public ResponseItem(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
