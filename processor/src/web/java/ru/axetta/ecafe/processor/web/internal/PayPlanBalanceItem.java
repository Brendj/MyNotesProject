/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

public class PayPlanBalanceItem {
    private Long idOfClient;
    private Long summa;

    public PayPlanBalanceItem() {

    }

    public PayPlanBalanceItem(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getSumma() {
        return summa;
    }

    public void setSumma(Long summa) {
        this.summa = summa;
    }
}
