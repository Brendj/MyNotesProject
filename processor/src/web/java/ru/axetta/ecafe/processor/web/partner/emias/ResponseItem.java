/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.emias;

import java.io.Serializable;

/**
 * User: shamil
 * Date: 13.05.15
 * Time: 11:17
 */
public class ResponseItem implements Serializable {

    public static final int OK = 0;
    public static final int ERROR_INTERNAL_EMIAS = 100;
    public static final int ERROR_WRONG_KEY_EMIAS = 120;//????????
    public static final int ERROR_ORG_NOT_FOUND = 110;
    public static final int ERROR_CLIENT_NOT_FOUND_EMIAS = 110;
    public static final int ERROR_ARGUMENT_NOT_FOUND = 150;
    public static final int ERROR_EVENT_NOT_FOUND = 160;
    public static final int ERROR_DUPLICATE = 160;
    public static final int ERROR_DUPLICATE_FOR_SECOND_REGISTER = 161;
    public static final int ERROR_INTERNAL = 170;
    public static final int ERROR_SIGN_VERIFY = 180;
    public static final int ERROR_INVALID_TYPE = 190;
    public static final int ERROR_SPECIAL_CARD_NOT_FOUND = 200;
    public static final int ERROR_CARD_NOT_FOUND = 210;
    public static final int ERROR_CARD_WRONG_STATE = 220;
    public static final int ERROR_CARD_UID_GIVEN_AWAY = 230;
    public static final int ERROR_AUTHENTICATOIN_FAILED = 400;
    public static final int ERROR_INCORRECT_FORMAT_OF_MOBILE = 410;
    public static final int ERROR_CLIENT_NOT_FOUND = 420;
    public static final int ERROR_REQUIRED_FIELDS_NOT_FILLED = 430;
    public static final int ERROR_ORGANIZATION_NOT_FOUND = 440;
    public static final int ERROR_CLIENT_ALREADY_EXIST = 450;
    public static final int ERROR_GUARDIAN = 460;
    public static final int ERROR_INCORRECT_DATE = 470;
    public static final String OK_MESSAGE = "Ok.";
    public static final String OK_MESSAGE_2 = "Успешно";
    public static final String ERROR_WRONG_KEY_MESSAGE_EMIAS = "Доступ запрещен";
    public static final String ERROR_ORG_NOT_FOUND_MESSAGE = "Организация не найдена";
    public static final String ERROR_EVENT_NOT_FOUND_MESSAGE = "Некорректный тип события";
    public static final String ERROR_CANCEL_EVENT_NOT_FOUND_MESSAGE = "Некорректный идентификатор отменяемого события";
    public static final String ERROR_ARGUMENT_NOT_FOUND_MESSAGE = "Не заполнены обязательные поля";
    public static final String ERROR_CLIENT_NOT_FOUND_MESSAGE_EMIAS = "Клиент не найден";
    public static final String ERROR_ID_EVENT_EMIAS = "Некорректные данные";
    public static final String ERROR_DUPLICATE_CARD_MESSAGE = "Данная карта уже зарегистрирована.";
    public static final String ERROR_DUPLICATE_CARD_SECOND_REGISTER_MESSAGE = "Карта прикреплена к организации ";
    public static final String ERROR_INTERNAL_MESSAGE = "Внутренняя ошибка приложения.";
    public static final String ERROR_INTERNAL_MESSAGE_EMIAS = "Внутренняя ошибка";
    public static final String ERROR_SIGN_VERIFY_MESSAGE = "Не пройдена проверка цифровой подписи";
    public static final String ERROR_INVALID_TYPE_MESSAGE = "Неизвестный тип карты";
    public static final String ERROR_AUTHENTICATION_FAILED_MESSAGE = "Неверные учетные данные пользователя";
    public static final String ERROR_SPECIAL_CARD_NOT_FOUND_MESSAGE = "Карта не найдена";
    public static final String ERROR_CARD_ALREADY_EXIST_MESSAGE = "Карта уже занята и не может быть зарегистрирована";
    public static final String ERROR_CARD_ALREADY_EXIST_IN_YOUR_ORG_MESSAGE = "Карта уже зарегистрирована в вашей организации";
    public static final String ERROR_CARD_WRONG_STATE_MESSAGE = "Невозможно вернуть/разблокировать карту с текущим статусом";
    public static final String ERROR_CARD_UID_GIVEN_AWAY_MESSAGE = "УИД карты передан в пользование другой ОО";
    public static final String ERROR_INCORRECT_FORMAT_OF_MOBILE_MESSAGE = "Некорректный формат мобилного телефона";
    public static final String ERROR_CLIENT_NOT_FOUND_MESSAGE = "Клиент не найден";
    public static final String ERROR_REQUIRED_FIELDS_NOT_FILLED_MESSAGE = "Не заполнены обязательные поля";
    public static final String ERROR_ORGANIZATION_NOT_FOUND_MESSAGE = "Организация не найдена";
    public static final String ERROR_CLIENT_ALREADY_EXIST_MESSAGE = "Клиент уже существует";
    public static final String ERROR_INCORRECT_FORMAT = "Некорректный формат";
    public static final String USED_IN_ANOTHER_ORG = "Используется в другой ОО";
    public static final String ERROR_GUARDIAN_MESSAGE = "Связка опекуна с опекаемым не найдена";
    public static final String ERROR_INCORRECT_DATE_MESSAGE = "Некорректная дата";
    public int resultCode;
    public String description;

    public ResponseItem() {
        resultCode = OK;
        description = OK_MESSAGE;
    }

    public ResponseItem(int code, String message) {
        this.resultCode = code;
        this.description = message;
    }
}
