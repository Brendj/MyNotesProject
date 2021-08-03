/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.enums;

public enum OrganizationType {
    SCHOOL("Общеобразовательное ОУ"),
    KINDERGARTEN("Дошкольное ОУ"),
    SUPPLIER("Поставщик питания"),
    PROFESSIONAL("Профессиональное ОУ"),
    ADDED_EDUCATION("Доп.образование");

    final String description;

    OrganizationType(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
