/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.util.ArrayList;
import java.util.List;

public class PreorderReportTotalItem {
    private List<PreorderReportClientItem> preorderReportClientItems;
    private List<PreorderReportItem> preorderReportItems = new ArrayList<PreorderReportItem>();
    private Integer allAmount;
    private Long allPreorderSum;
    private Long allDiscount;
    private Long allTotalPrice;

    public PreorderReportTotalItem() {

    }

    public void calculateTotalItem() {
        for (PreorderReportClientItem item : preorderReportClientItems) {
            item.calculateTotalValues();
        }

        allAmount = 0;
        allPreorderSum = 0L;
        allDiscount = 0L;
        allTotalPrice = 0L;
        for (PreorderReportItem item : preorderReportItems) {
            item.calculateTotalPrice();
            allAmount += item.getAmount();
            allPreorderSum += item.getPreorderSum();
            allDiscount += item.getDiscount();
            allTotalPrice += item.getTotalPrice();
        }
    }

    public List<PreorderReportClientItem> getPreorderReportClientItems() {
        return preorderReportClientItems;
    }

    public void setPreorderReportClientItems(List<PreorderReportClientItem> preorderReportClientItems) {
        this.preorderReportClientItems = preorderReportClientItems;
    }

    public List<PreorderReportItem> getPreorderReportItems() {
        return preorderReportItems;
    }

    public void setPreorderReportItems(List<PreorderReportItem> preorderReportItems) {
        this.preorderReportItems = preorderReportItems;
    }

    public Integer getAllAmount() {
        return allAmount;
    }

    public void setAllAmount(Integer allAmount) {
        this.allAmount = allAmount;
    }

    public Long getAllPreorderSum() {
        return allPreorderSum;
    }

    public void setAllPreorderSum(Long allPreorderSum) {
        this.allPreorderSum = allPreorderSum;
    }

    public Long getAllDiscount() {
        return allDiscount;
    }

    public void setAllDiscount(Long allDiscount) {
        this.allDiscount = allDiscount;
    }

    public Long getAllTotalPrice() {
        return allTotalPrice;
    }

    public void setAllTotalPrice(Long allTotalPrice) {
        this.allTotalPrice = allTotalPrice;
    }
}
