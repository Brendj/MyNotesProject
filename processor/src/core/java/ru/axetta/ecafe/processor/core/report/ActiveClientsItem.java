/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 25.10.13
 * Time: 14:13
 * To change this template use File | Settings | File Templates.
 */
public class ActiveClientsItem {
    public static final String DEFAULT_STYLE = "";
    public static final String REGION_STYLE = "region";
    public static final String OVERALL_STYLE = "overall";
    private String shortname;
    private long idOfOrg;
    private String num;
    private String region;
    private long value;
    private long totalCount;
    private long paymentCount;
    private long discountCount;
    private long employeesCount;
    private String active;
    private String style;
    private long realDiscountCount;
    private long entersCount;


    public ActiveClientsItem(long idOfOrg, String shortname, String num, String region, String style) {
        this.idOfOrg = idOfOrg;
        this.shortname = shortname;
        this.num = num;
        this.region = region;
        this.totalCount = 0;
        this.paymentCount = 0;
        this.discountCount = 0;
        this.employeesCount = 0;
        this.realDiscountCount = 0;
        this.entersCount = 0;
        this.style = style;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getPaymentCount() {
        return paymentCount;
    }

    public void setPaymentCount(long paymentCount) {
        this.paymentCount = paymentCount;
    }

    public long getDiscountCount() {
        return discountCount;
    }

    public void setDiscountCount(long discountCount) {
        this.discountCount = discountCount;
    }

    public long getEmployeesCount() {
        return employeesCount;
    }

    public void setEmployeesCount(long employeesCount) {
        this.employeesCount = employeesCount;
    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public void setActive(double value) {
        active = new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "%";
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public long getRealDiscountCount() {
        return realDiscountCount;
    }

    public void setRealDiscountCount(long realDiscountCount) {
        this.realDiscountCount = realDiscountCount;
    }

    public long getEntersCount() {
        return entersCount;
    }

    public void setEntersCount(long entersCount) {
        this.entersCount = entersCount;
    }
}