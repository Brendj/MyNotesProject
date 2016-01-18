/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.balance.leaving;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: T800
 * Date: 17.01.16
 * Time: 1:02
 * To change this template use File | Settings | File Templates.
 */
public class BalanceLeaving {

    /**
     *
     */
    public Long idOfBalanceLeaving;

    /**
     * Ид клиента
     */
    public Client client;

    /**
     * Дата время обработки пакета (текущее)
     */
    public Date datePacketProcessing;

    /**
     * Сумма до уменьшения (до)
     */
    public Long beforeReduceBalance;

    /**
     * Сумма баланса финальная (после)
     */
    public Long finalBalance;

    public BalanceLeaving() {
    }

    public BalanceLeaving(Long idOfBalanceLeaving, Client client, Date datePacketProcessing, Long beforeReduceBalance,
            Long finalBalance) {
        this.idOfBalanceLeaving = idOfBalanceLeaving;
        this.client = client;
        this.datePacketProcessing = datePacketProcessing;
        this.beforeReduceBalance = beforeReduceBalance;
        this.finalBalance = finalBalance;
    }

    public Long getIdOfBalanceLeaving() {
        return idOfBalanceLeaving;
    }

    public void setIdOfBalanceLeaving(Long idOfBalanceLeaving) {
        this.idOfBalanceLeaving = idOfBalanceLeaving;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getDatePacketProcessing() {
        return datePacketProcessing;
    }

    public void setDatePacketProcessing(Date datePacketProcessing) {
        this.datePacketProcessing = datePacketProcessing;
    }

    public Long getBeforeReduceBalance() {
        return beforeReduceBalance;
    }

    public void setBeforeReduceBalance(Long beforeReduceBalance) {
        this.beforeReduceBalance = beforeReduceBalance;
    }

    public Long getFinalBalance() {
        return finalBalance;
    }

    public void setFinalBalance(Long finalBalance) {
        this.finalBalance = finalBalance;
    }
}
