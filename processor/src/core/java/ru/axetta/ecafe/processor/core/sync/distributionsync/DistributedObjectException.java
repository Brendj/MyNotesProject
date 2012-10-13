/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.distributionsync;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 10.07.12
 * Time: 13:11
 * To change this template use File | Settings | File Templates.
 */
public class DistributedObjectException extends Exception {

    public enum ErrorType{
        UNKNOWN_ERROR(1),
        DUPLICATE_VALUE(2),
        NOT_FOUND_VALUE(3),
        CONFIGURATION_PROVIDER_NOT_FOUND(4);

        Integer value;

        ErrorType(int value) {
            this.value = value;
        }

        public static ErrorType parse(Integer key) {
            ErrorType errorType = null; // Default
            for (ErrorType item: ErrorType.values()) {
                if (item.getValue().equals(key)) {
                    errorType = item;
                    break;
                }
            }
            return errorType;
        }

        public Integer getValue() {
            return value;
        }
    }

    private ErrorType type;

    public DistributedObjectException(Integer type) {
        this.type = ErrorType.parse(type);
    }

    public DistributedObjectException(ErrorType errorType) {
        this.type = errorType;
    }

    public Integer getType() {
        return type.getValue();
    }
}
