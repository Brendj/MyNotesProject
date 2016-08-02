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

    public RegisterStampElectronicCollationReportItem(Long qty, String date, Date dateTime, String level1, String level2, String level3, String level4) {
        this(qty, date, null, dateTime, level1, level2, level3, level4);
    }

    public RegisterStampElectronicCollationReportItem(Long qty, String date, String number, Date dateTime, String level1, String level2, String level3, String level4) {
        this.level1 = level1;
        this.level2 = level2;
        this.level3 = level3;
        this.level4 = level4;
        this.qty = qty;
        this.date = date;
        this.number = number;
        this.dateTime = dateTime;
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

    public String getCaption() {
        return caption;
    }
}
