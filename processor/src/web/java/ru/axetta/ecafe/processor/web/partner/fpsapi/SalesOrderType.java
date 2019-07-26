/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi;

public enum SalesOrderType {

    HOT_FOOD(1, "Горячее питание"),
    BUFFET(2, "Буфет");

    private final int code;
    private final String description;

    private SalesOrderType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
