/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.util.Date;

/**
 * Created by i.semenov on 11.04.2018.
 */
public class PreorderReportItem implements Comparable<PreorderReportItem> {
    protected Date preorderDate;
    protected Integer amount;
    protected String preorderName;
    protected Long preorderPrice;
    protected Long preorderSum;
    protected Boolean isRegularPreorder;
    protected Boolean isPayed;

    public PreorderReportItem(Date preorderDate, Integer amount, String preorderName,
            Long preorderPrice, Boolean isRegularPreorder, Boolean isPayed) {
        this.preorderDate = preorderDate;
        this.amount = amount;
        this.preorderName = preorderName;
        this.preorderPrice = preorderPrice;
        this.isRegularPreorder = isRegularPreorder;
        this.isPayed = isPayed;

        calculateTotalPrice();
    }

    public PreorderReportItem(String preorderName) {
        this.amount = 0;
        this.preorderName = preorderName;
        this.preorderPrice = 0L;
        this.preorderSum = 0L;
        this.isPayed = false;
    }

    public PreorderReportItem() {
        this.amount = 0;
        this.preorderName = "";
        this.preorderPrice = 0L;
        this.preorderSum = 0L;
        this.isPayed = false;
    }

    public void calculateTotalPrice() {
        this.preorderSum = this.preorderPrice * this.amount;
    }

    public Date getPreorderDate() {
        return preorderDate;
    }

    public void setPreorderDate(Date preorderDate) {
        this.preorderDate = preorderDate;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getPreorderName() {
        return preorderName;
    }

    public void setPreorderName(String preorderName) {
        this.preorderName = preorderName;
    }

    public Boolean getIsRegularPreorder() {
        return isRegularPreorder;
    }

    public void setIsRegularPreorder(Boolean regularPreorder) {
        isRegularPreorder = regularPreorder;
    }

    public Long getPreorderPrice() {
        return preorderPrice;
    }

    public void setPreorderPrice(Long preorderPrice) {
        this.preorderPrice = preorderPrice;
    }

    public Long getPreorderSum() {
        return preorderSum;
    }

    public void setPreorderSum(Long preorderSum) {
        this.preorderSum = preorderSum;
    }

    public Boolean getIsPayed() {
        return isPayed;
    }

    public void setIsPayed(Boolean payed) {
        isPayed = payed;
    }

    @Override
    public int compareTo(PreorderReportItem item) {
        return this.preorderName.compareTo(item.getPreorderName());
    }
}
