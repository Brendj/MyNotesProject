/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.foodbox;

public enum FoodBoxStateTypeEnum {
    NEW(0, "new"), //новый заказ
    ASSEMBLED(1, "collected"), //заказ собран
    LOADED(2, "loaded"), //заказ загружен
    EXECUTED(3, "received"), //заказ получен
    CANCELED(4, "canceled"); //заказ аннулирован

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
