/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 05.09.13
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public enum RefectoryType {

    /*0*/ RAW_DINING("Сырьевая столовая"),
    /*1*/ DINING_DOGOTOVOCHNAYA("Столовая-доготовочная"),
    /*2*/ BUFFET_DISTRIBUTING("Буфет-раздаточная"),
    /*3*/ CANTEEN("Комбинат питания");

    private final String description;

    private RefectoryType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
