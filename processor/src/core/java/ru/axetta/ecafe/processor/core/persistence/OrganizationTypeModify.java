/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 12.02.15
 * Time: 17:31
 */

public enum OrganizationTypeModify {
    /*0*/ EMPTY(""),
    /*1*/ SCHOOL("Общеобразовательное ОУ"),
    /*2*/ KINDERGARTEN("Дошкольное ОУ"),
    /*3*/ PROFESSIONAL("Профессиональное ОУ"),
    /*4*/ ADDEDEDUCATION ("Доп.образование");

    private final String description;

    private OrganizationTypeModify(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

}

