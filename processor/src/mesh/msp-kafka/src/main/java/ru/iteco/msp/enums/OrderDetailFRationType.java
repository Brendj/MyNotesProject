/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.enums;

public enum OrderDetailFRationType {
    NOT_SPECIFIED("Не указано"),
    BREAKFAST("Завтрак"),
    DINNER("Обед"),
    LUNCH("Полдник"),
    SUPPER("Ужин");

    private final String description;

    OrderDetailFRationType(String description) {
        this.description = description;
    }

    public static String getByCode(Integer code) {
        if(code == null || code >= OrderDetailFRationType.values().length || code < 0){
            return "";
        }
        return ";" + OrderDetailFRationType.values()[code].description;
    }

    public String getDescription() {
        return description;
    }
}
