/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.enums;

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

    public static AssignOperationType getAssignTypeByOperationType(OperationType type){
        switch (type){
            case ADD:
                return AssignOperationType.ADD;
            case CHANGE:
                return AssignOperationType.CHANGE;
            case DELETE:
                return AssignOperationType.DELETE;
            default:
                return null;
        }
    }
}
