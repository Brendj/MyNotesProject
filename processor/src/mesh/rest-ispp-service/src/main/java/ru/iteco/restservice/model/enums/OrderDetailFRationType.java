/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.enums;

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

    @Override
    public String toString() {
        return  description;
    }
}
