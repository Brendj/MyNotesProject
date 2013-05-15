package ru.axetta.ecafe.processor.core.daoservices.order.items;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 30.04.13
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
public class RegisterStampReportItem {
    private String level1;
    private String level2;
    private Long qty;
    private String date;

    public RegisterStampReportItem(String level1, String level2, Long qty, String date) {
        this.level1 = level1;
        this.level2 = level2;
        this.qty = qty;
        this.date = date;
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
}
