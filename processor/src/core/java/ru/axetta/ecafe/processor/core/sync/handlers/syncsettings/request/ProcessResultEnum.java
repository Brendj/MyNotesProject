/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request;

public enum ProcessResultEnum {
    OK(0, "OK");

    ProcessResultEnum(Integer code, String description){
        this.code = code;
        this.description = description;
    }

    private Integer code;
    private String description;

    public Integer getCode() {
        return code;
    }

    @Override
    public String toString(){
        return description;
    }
}
