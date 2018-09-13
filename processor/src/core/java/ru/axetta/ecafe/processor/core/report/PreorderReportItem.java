/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.util.Date;

/**
 * Created by i.semenov on 11.04.2018.
 */
public class PreorderReportItem implements Comparable<PreorderReportItem> {
    private Date preorderDate;
    private Integer amount;
    private String preorderName;
    private Long preorderPrice;
    private Long discount;
    private Long preorderSum;
    private Long totalPrice;
    private Boolean isRegularPreorder;

    public PreorderReportItem(Date preorderDate, Integer amount, String preorderName,
            Long preorderPrice, Long discount, Boolean isRegularPreorder) {
        this.preorderDate = preorderDate;
        this.amount = amount;
        this.preorderName = preorderName;
        this.preorderPrice = preorderPrice;
        this.discount = discount;
        this.isRegularPreorder = isRegularPreorder;

        calculateTotalPrice();
    }

    public PreorderReportItem(String preorderName) {
        this.amount = 0;
        this.preorderName = preorderName;
        this.preorderPrice = 0L;
        this.preorderSum = 0L;
        this.totalPrice = 0L;
        this.discount = 0L;
    }

    public void calculateTotalPrice() {
        this.preorderSum = this.preorderPrice * this.amount;
        this.totalPrice = this.preorderSum - this.discount;
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

    public Long getDiscount() {
        return discount;
    }

    public void setDiscount(Long discount) {
        this.discount = discount;
    }

    public Long getPreorderSum() {
        return preorderSum;
    }

    public void setPreorderSum(Long preorderSum) {
        this.preorderSum = preorderSum;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public int compareTo(PreorderReportItem item) {
        return this.preorderName.compareTo(item.getPreorderName());
    }
}
