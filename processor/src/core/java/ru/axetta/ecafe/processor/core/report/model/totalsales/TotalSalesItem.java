/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.totalsales;

import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

/**
 * User: regal
 * Date: 25.01.15
 * Time: 0:03
 */
public class TotalSalesItem {
    private String name;
    private String date;
    private long summ;
    private String type;


    public TotalSalesItem(String name, String date, long summ, String type) {
        this.name = name;
        this.date = date;
        this.summ = summ;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getSumm() {
        return summ;
    }

    public String getSummToString() {
        return CurrencyStringUtils.copecksToRubles(summ);
    }

    public void setSumm(long summ) {
        this.summ = summ;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
