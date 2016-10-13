/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.06.13
 * Time: 10:38
 * To change this template use File | Settings | File Templates.
 */
public enum ClientTypeEnum {
    CLIENT(0),VISITOR(1),EMPLOYEE(2),VISITORDOGM(3);
    private final int value;

    private ClientTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
