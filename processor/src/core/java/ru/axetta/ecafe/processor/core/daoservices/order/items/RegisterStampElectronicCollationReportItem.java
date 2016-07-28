/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.order.items;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 28.07.16
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class RegisterStampElectronicCollationReportItem {
    private String caption = "Сведения о реализованных Рационах питания, шт.";
    private String level1;
    private String level2;
    private String level3;
    private String level4;
    private Long qty;
    private String date;
    private String number;
    private Date dateTime;
    private Integer orderType;

    public RegisterStampElectronicCollationReportItem(GoodItem goodItem, Long qty, String date, Date dateTime) {
        this(goodItem, qty, date, null, dateTime);
    }

    public RegisterStampElectronicCollationReportItem(GoodItem goodItem, Long qty, String date, String number, Date dateTime) {
        this.level1 = goodItem.getPathPart1();
        this.level2 = goodItem.getPathPart2();
        this.level3 = goodItem.getPathPart3();
        this.level4 = goodItem.getPathPart4();
        this.qty = qty;
        this.date = date;
        this.number = number;
        this.dateTime = dateTime;
        this.orderType = goodItem.getOrderType(); //.ordinal();
    }

    public String getLevel1() {
        return level1;
    }

    public void setLevel1(String level1) {
        this.level1 = level1;
    }

    public String getLevel2() {
        return level2;
    }

    public void setLevel2(String level2) {
        this.level2 = level2;
    }

    public String getLevel3() {
        return level3;
    }

    public void setLevel3(String level3) {
        this.level3 = level3;
    }

    public String getLevel4() {
        return level4;
    }

    public void setLevel4(String level4) {
        this.level4 = level4;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getCaption() {
        return caption;
    }
}
