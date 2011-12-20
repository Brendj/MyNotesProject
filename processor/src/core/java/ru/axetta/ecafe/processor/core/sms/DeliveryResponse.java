/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 28.02.2010
 * Time: 12:23:20
 * To change this template use File | Settings | File Templates.
 */
public class DeliveryResponse {

    public static final int UNKNOWN = 0;
    public static final int SENT = 1;
    public static final int NOT_DELIVERED = 2;
    public static final int DELIVERED = 3;
    public static final int NOT_ALLOWED = 4;
    public static final int INVALID_DESTINATION_ADDRESS = 5;
    public static final int INVALID_SOURCE_ADDRESS = 6;
    public static final int NOT_ENOUGH_CREDITS = 7;
    public static final String[] STATUS_TEXT = {
            "", "SENT", "NOT_DELIVERED", "DELIVERED", "NOT_ALLOWED", "INVALID_DESTINATION_ADDRESS",
            "INVALID_SOURCE_ADDRESS", "NOT_ENOUGH_CREDITS", "NOT DELIVERED"};
    public static final int[] STATUS_CODE = {
            0, 1, 2, 3, 4, 5, 6, 7, 2};
    private static final String[] STATUS_MESSAGES = {
            "Статус неизвестен", "Отослано", "Не доставлено", "Доставлено", "Оператор не обслуживается",
            "Неверный адрес для доставки", "Неправильное имя «От кого»", "Недостаточно кредитов"};

    private final int statusCode;
    private final Date sentDate;
    private final Date doneDate;

    public DeliveryResponse(int statusCode, Date sentDate, Date doneDate) {
        this.statusCode = statusCode;
        this.sentDate = sentDate;
        this.doneDate = doneDate;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public Date getDoneDate() {
        return doneDate;
    }

    public String getStatusMessage() {
        if (0 <= statusCode && STATUS_MESSAGES.length > statusCode) {
            return STATUS_MESSAGES[statusCode];
        }
        return null;
    }

    public boolean isDelivered() {
        return DELIVERED == statusCode;
    }
}