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
    private String disctrict;
    private String date;
    private long sum;
    private String type;


    public TotalSalesItem(String name,String disctrict , String date, long sum, String type) {
        this.name = name;
        this.disctrict = disctrict;
        this.date = date;
        this.sum = sum;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisctrict() {
        return disctrict;
    }

    public void setDisctrict(String disctrict) {
        this.disctrict = disctrict;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getSum() {
        return sum;
    }

    public String getSummToString() {
        return CurrencyStringUtils.copecksToRubles(sum);
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
