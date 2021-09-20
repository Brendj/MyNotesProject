/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.meshsync.enums;

public enum ServiceType {
    EARLY_DEVELOPMENT_GROUP(1, "По присмотру и уходу"),
    EDUCATION(2, "Образование"),
    ATTESTATION(3, "Аттестация"),
    ACADEMIC_LEAVE(4, "Академический отпуск"),
    ADDITIONAL_EDUCATION(5, "Доп.Образование");

    private final Integer code;
    private final String description;

    ServiceType(Integer code, String description){
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
