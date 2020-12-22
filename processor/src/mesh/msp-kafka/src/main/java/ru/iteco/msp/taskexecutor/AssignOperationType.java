/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.taskexecutor;

public enum AssignOperationType {
    ADD("0"),
    CHANGE("1"),
    DELETE("2");

    private String code;

    AssignOperationType(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
