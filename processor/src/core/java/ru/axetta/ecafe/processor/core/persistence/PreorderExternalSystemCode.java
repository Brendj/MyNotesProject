/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

public enum PreorderExternalSystemCode {
    MOS_RU(1, "портал mos.ru"),
    GOSUSLUGI(2, "МП Госууслуги"),
    MESH(3, "МП Дневник МЭШ");

    private final Integer code;
    private final String description;

    PreorderExternalSystemCode(Integer code, String description){
        this.code = code;
        this.description = description;
    }

    public static PreorderExternalSystemCode getExternalSystemCode(Integer code){
        for(PreorderExternalSystemCode c : PreorderExternalSystemCode.values()){
            if (c.getCode().equals(code)){
                return c;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
