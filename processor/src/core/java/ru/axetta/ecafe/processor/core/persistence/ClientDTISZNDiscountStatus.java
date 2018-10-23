/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

public enum ClientDTISZNDiscountStatus {
    NONE(0,"Не указано"),
    CONFIRMED(1,"Подтверждена"),
    NOT_CONFIRMED(2,"Не подтверждена");

    private final int value;
    private final String description;

    private ClientDTISZNDiscountStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
