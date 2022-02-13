/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.foodbox;

import generated.etp.CitizenshipType;

public enum FoodBoxStateTypeEnum {
    NEW(0, "Новый"),
    ASSEMBLED(1, "Собран"),
    LOADED(2, "Загружен в ячейку"),
    EXECUTED(3, "Исполнен"),
    CANCELED(4, "Аннулирован");

    private final int value;
    private final String description;

    private FoodBoxStateTypeEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public static FoodBoxStateTypeEnum fromValue (Integer val)
    {
        for (FoodBoxStateTypeEnum f: FoodBoxStateTypeEnum.values()) {
            if (f.getValue() == val) {
                return f;
            }
        }
        throw new IllegalArgumentException(String.valueOf(val));
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

}
