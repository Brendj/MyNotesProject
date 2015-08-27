/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 27.08.15
 * Time: 16:40
 */

public enum LatePaymentDaysCountType {

    /*0*/ EMPTY(""),
    /*1*/ MORE_TEN("Более 10");

    private final String description;

    private LatePaymentDaysCountType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
