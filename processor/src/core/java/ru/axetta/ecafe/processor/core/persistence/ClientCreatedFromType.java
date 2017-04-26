/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by i.semenov on 19.04.2017.
 */
public enum ClientCreatedFromType {
    DEFAULT(0,"По умолчанию"),//Создание клиента без учета привязки источника
    MPGU(1,"МПГУ");//Создание клиента в МПГУ (создание представителей в методе сервиса информирования)

    private final int value;
    private final String description;

    private ClientCreatedFromType(int value, String description) {
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
