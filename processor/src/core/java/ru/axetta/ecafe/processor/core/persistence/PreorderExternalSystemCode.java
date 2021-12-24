/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

public enum PreorderExternalSystemCode {
    MOS_RU(1, "Портал mos.ru"),
    GOSUSLUGI(2, "МП Госуслуги"),
    MESH(3, "МП Дневник МЭШ"),
    OTHER_1(4, "Неизвестная ИС №1"), // *
    OTHER_2(5, "Неизвестная ИС №2"), // * TODO зарезервировано для будущих ИС
    OTHER_3(6, "Неизвестная ИС №3"); // *

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
