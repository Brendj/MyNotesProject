/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.util.Date;

public class FoodDaysCalendarReportItem {

    private String orgName;
    private String groupName;
    private String periodDate;
    private String date;
    private String idOfClientGroup;
    private String idOfOrg;
    private String shortAddress;
    private String isWeekend;
    private String comment;
    private String deleted;
    private Date armLastUpdate;
    private String idOfOrgOwner;
    private String name;
    private String address;
    private Integer deletedHistory;

    public FoodDaysCalendarReportItem(String orgName, String groupName, String periodDate, String date,
            String idOfClientGroup, String idOfOrg, String shortAddress, String isWeekend, String comment,
            String deleted, Date armLastUpdate, String idOfOrgOwner, String name, String address,
            Integer deletedHistory) {
        this.orgName = orgName;
        this.groupName = groupName;
        this.periodDate = periodDate;
        this.date = date;
        this.idOfClientGroup = idOfClientGroup;
        this.idOfOrg = idOfOrg;
        this.shortAddress = shortAddress;
        this.isWeekend = isWeekend;
        this.comment = comment;
        this.deleted = deleted;
        this.armLastUpdate = armLastUpdate;
        this.idOfOrgOwner = idOfOrgOwner;
        this.name = name;
        this.address = address;
        this.deletedHistory = deletedHistory;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(String periodDate) {
        this.periodDate = periodDate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(String idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }

    public String getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(String idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public String getIsWeekend() {
        return isWeekend;
    }

    public void setIsWeekend(String isWeekend) {
        this.isWeekend = isWeekend;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public Date getArmLastUpdate() {
        return armLastUpdate;
    }

    public void setArmLastUpdate(Date armLastUpdate) {
        this.armLastUpdate = armLastUpdate;
    }

    public String getIdOfOrgOwner() {
        return idOfOrgOwner;
    }

    public void setIdOfOrgOwner(String idOfOrgOwner) {
        this.idOfOrgOwner = idOfOrgOwner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getDeletedHistory() {
        return deletedHistory;
    }

    public void setDeletedHistory(Integer deletedHistory) {
        this.deletedHistory = deletedHistory;
    }
}