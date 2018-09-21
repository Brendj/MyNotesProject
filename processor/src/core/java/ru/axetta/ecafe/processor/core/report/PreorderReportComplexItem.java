/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PreorderReportComplexItem extends PreorderReportItem {
    private List<PreorderReportItem> dishes;

    public PreorderReportComplexItem(Date preorderDate, Integer amount, String preorderName,
            Long preorderPrice, Boolean isRegularPreorder, Boolean isPayed) {
        this.preorderDate = preorderDate;
        this.amount = amount;
        this.preorderName = preorderName;
        this.preorderPrice = preorderPrice;
        this.isRegularPreorder = isRegularPreorder;
        this.isPayed = isPayed;

        this.dishes = new ArrayList<PreorderReportItem>();
    }

    public PreorderReportComplexItem(String preorderName) {
        this.amount = 0;
        this.preorderName = preorderName;
        this.preorderPrice = null;
        this.preorderSum = 0L;
        this.isPayed = false;

        this.dishes = new ArrayList<PreorderReportItem>();
    }

    public List<PreorderReportItem> getDishes() {
        return dishes;
    }

    public void setDishes(List<PreorderReportItem> dishes) {
        this.dishes = dishes;
    }

    public void appendToTotalDishes(PreorderReportItem dish) {
        boolean isExisted = false;
        for (PreorderReportItem item : dishes) {
            if (item.getPreorderName().equals(dish.getPreorderName())) {
                item.setAmount(item.getAmount() + dish.getAmount());
                item.setPreorderSum(item.getPreorderSum() + dish.getAmount() * dish.getPreorderPrice());
                isExisted = true;
                break;
            }
        }
        if (!isExisted) {
            dishes.add(dish);
        }
    }

    @Override
    public void calculateTotalPrice() {
        if (null == this.preorderPrice) {
            for (PreorderReportItem item : dishes) {
                this.preorderSum += item.getPreorderPrice() * item.getAmount();
            }
            if (0 == this.amount)
                this.amount = 1;
        } else {
            this.preorderSum = this.preorderPrice * this.amount;
        }
    }
}
