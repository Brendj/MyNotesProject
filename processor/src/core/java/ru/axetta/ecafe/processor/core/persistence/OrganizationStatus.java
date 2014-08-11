/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 11.08.14
 * Time: 17:47
 * To change this template use File | Settings | File Templates.
 */
public enum OrganizationStatus {
    /*0*/ ACTIVE("Обслуживается"),
    /*1*/ INACTIVE("Не обслуживается"),
    /*2*/ PLANNED("Не обслуживается - запланировано подключение"),
    /*3*/ REPAIR("Не обслуживается - на ремонте"),
    /*4*/ CLOSED("Не обслуживается - закрыто");

    private final String description;

    private OrganizationStatus(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
