/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by i.semenov on 26.09.2018.
 */
public enum ClientBalanceHoldLastChangeStatus {
    ARM(0, "Арм администратора ОУ"),
    PROCESSING(-1, "Процессинг"),
    PORTAL(-2, "Портал");

    private final int value;
    private final String description;

    private ClientBalanceHoldLastChangeStatus(int value, String description) {
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
