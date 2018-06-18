/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

public enum PreorderState {
    /*0*/ OK("ОК"),
    /*1*/ DELETED("Удалено поставщиком"),
    /*2*/ CHANGED_PRICE("Изменение цены у поставщика");

    private final String description;

    private PreorderState(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
