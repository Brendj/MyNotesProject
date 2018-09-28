/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by i.semenov on 26.09.2018.
 */
public enum ClientBalanceHoldCreateStatus {
    /*0*/ CHANGE_SUPPLIER("Смена поставщика"),
    /*1*/ MOS_RU("Портал"),
    /*2*/ ARM("АРМ администратора ОУ");

    private final String description;

    ClientBalanceHoldCreateStatus(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
