/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 17.04.16
 * Time: 11:20
 */
public class SpecialDatesReportItem {
    private Long idOfOrg;
    private String date;
    private String orgShortName;
    private Boolean isWeekend;

    public SpecialDatesReportItem(Long idOfOrg, String date, String orgShortName, Boolean isWeekend) {
        this.idOfOrg = idOfOrg;
        this.date = date;
        this.orgShortName = orgShortName;
        this.isWeekend = isWeekend;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrgShortName() {
        return orgShortName;
    }

    public void setOrgShortName(String orgShortName) {
        this.orgShortName = orgShortName;
    }

    public Boolean getIsWeekend() {
        return isWeekend;
    }

    public void setIsWeekend(Boolean isWeekend) {
        this.isWeekend = isWeekend;
    }
}
