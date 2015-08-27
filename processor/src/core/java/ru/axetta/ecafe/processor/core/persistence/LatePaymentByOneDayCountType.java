/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 27.08.15
 * Time: 15:43
 */

public enum LatePaymentByOneDayCountType {

    /*0*/ EMPTY(""),
    /*1*/ MORE_FIVE("Более 5");

    private final String description;

    private LatePaymentByOneDayCountType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
