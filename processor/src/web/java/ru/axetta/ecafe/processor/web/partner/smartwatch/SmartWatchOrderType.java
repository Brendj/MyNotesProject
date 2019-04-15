/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch;

public enum SmartWatchOrderType {
    BUFFET("Буфет"),
    REDUCED_PRICE_PLAN_FOOD("Льготное питание"),
    PAY_PLAN_FOOD("Горячее питание");

    private String description;

    SmartWatchOrderType(String description){
        this.description = description;
    }

    @Override
    public String toString(){
        return this.description;
    }
}
