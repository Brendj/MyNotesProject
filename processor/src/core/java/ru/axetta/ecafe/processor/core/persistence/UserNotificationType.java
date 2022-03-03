/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: a.anvarov
 */

public enum UserNotificationType {

    /*0*/ GOOD_REQUEST_CHANGE_NOTIFY("Уведомление об изменении заказа"),
    /*1*/ ORDER_STATE_CHANGE_NOTIFY("Уведомление об отмене заказа"),
    /*2*/ ORG_SELECTED_FOR_USER("Организации выбранные для пользоваетля");

    private final String description;

    private UserNotificationType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
