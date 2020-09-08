/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.enums;

public enum CardState {
    UNKNOWN(100,"Неизвестная карта"),
    FREE(5,"Свободна к выдаче"),
    ISSUED(0,"Выдана (активна)"),
    TEMPISSUED(4,"Выдана временно(временно активна)"),
    TEMPBLOCKED(1,"Временно заблокирована"),
    BLOCKED(6,"Заблокирована");

    private final int value;
    private final String description;

    CardState(int value, String description) {
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
