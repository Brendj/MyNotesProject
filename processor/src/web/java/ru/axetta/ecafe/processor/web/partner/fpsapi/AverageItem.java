/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi;

public class AverageItem {
   private String date;
   private Integer range;
   private Long sum;
   private Float averagesum;
   private Integer daycount;
   private Integer accounttypeid;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getRange() {
        return range;
    }

    public void setRange(Integer range) {
        this.range = range;
    }

    public Long getSum() {
        return sum;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }

    public Float getAveragesum() {
        return averagesum;
    }

    public void setAveragesum(Float averagesum) {
        this.averagesum = averagesum;
    }

    public Integer getDaycount() {
        return daycount;
    }

    public void setDaycount(Integer daycount) {
        this.daycount = daycount;
    }

    public Integer getAccounttypeid() {
        return accounttypeid;
    }

    public void setAccounttypeid(Integer accounttypeid) {
        this.accounttypeid = accounttypeid;
    }
}
