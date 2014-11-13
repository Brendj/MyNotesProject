/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.requestsandorders;

/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 11.11.14
 * Time: 12:48
 * To change this template use File | Settings | File Templates.
 */
public enum State {

    Requested(0L), // Заказано
    Ordered(1L); // Оплачено

    private Long type;

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    State(Long type) {
        this.type = type;
    }
}