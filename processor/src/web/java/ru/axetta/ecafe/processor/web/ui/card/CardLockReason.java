/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

/**
 * Created by anvarov on 24.07.2017.
 */
public enum CardLockReason {
    EMPTY(0, ""),
    NEW(1, "новая"),
    REISSUE_BROKEN(2, "перевыпуск (сломана)"),
    REISSUE_LOSS(3, "перевыпуск (утеря)"),
    DEMAGNETIZED(4, "размагничена"),
    OTHER(5, "другое");

    private final int value;
    private final String description;

    CardLockReason(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
