/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch.dto;

public class JsonBalanceInfo {
    private Long totalBalance;
    private Long fallbackReserve;
    private Long preordersReserve;

    public Long getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(Long totalBalance) {
        this.totalBalance = totalBalance;
    }

    public Long getFallbackReserve() {
        return fallbackReserve;
    }

    public void setFallbackReserve(Long fallbackReserve) {
        this.fallbackReserve = fallbackReserve;
    }

    public Long getPreordersReserve() {
        return preordersReserve;
    }

    public void setPreordersReserve(Long preordersReserve) {
        this.preordersReserve = preordersReserve;
    }
}
