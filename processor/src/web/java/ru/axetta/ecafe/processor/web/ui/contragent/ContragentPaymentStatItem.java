/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.contragent;

import ru.axetta.ecafe.processor.core.persistence.Contragent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by i.semenov on 23.11.2017.
 */
public class ContragentPaymentStatItem implements Comparable {
    private static final AtomicLong id = new AtomicLong();
    private String contragentName;
    private String way;
    private String amountTotal;
    private String amountNotSuccessful;
    private String percent;
    private String difference;
    private Long idOfItem;
    private Long rownum;

    public ContragentPaymentStatItem(Contragent contragent) {
        this.contragentName = contragent.getContragentName();
        this.idOfItem = id.addAndGet(1);
    }

    public int compareTo(Object o) {
        return this.contragentName.compareTo(((ContragentPaymentStatItem)o).getContragentName());
    }

    public String getPercentStr() {
        try {
            Integer amTotal = Integer.parseInt(amountTotal);
            Integer amNotSuccessful = Integer.parseInt(amountNotSuccessful);
            Float res = (amTotal.floatValue() - amNotSuccessful.floatValue()) / amTotal.floatValue() * 100f;
            return new Integer(Math.round(res)).toString() + "%";
        } catch (Exception ignore) {
            return "";
        }
    }

    public String getContragentName() {
        return contragentName;
    }

    public void setContragentName(String contragentName) {
        this.contragentName = contragentName;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public String getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(String amountTotal) {
        this.amountTotal = amountTotal;
    }

    public String getAmountNotSuccessful() {
        return amountNotSuccessful;
    }

    public void setAmountNotSuccessful(String amountNotSuccessful) {
        this.amountNotSuccessful = amountNotSuccessful;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getDifference() {
        return difference;
    }

    public void setDifference(String difference) {
        this.difference = difference;
    }

    public Long getIdOfItem() {
        return idOfItem;
    }

    public void setIdOfItem(Long idOfItem) {
        this.idOfItem = idOfItem;
    }

    public Long getRownum() {
        return rownum;
    }

    public void setRownum(Long rownum) {
        this.rownum = rownum;
    }
}
