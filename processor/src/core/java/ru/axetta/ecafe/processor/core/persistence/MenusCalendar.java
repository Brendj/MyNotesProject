/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by i.semenov on 14.06.2018.
 */
public class MenusCalendar {
    private Long idOfMenusCalendar;
    private String guid;
    private Org org;
    private String guidOfMenu;
    private Date startDate;
    private Date endDate;
    private Boolean sixWorkDays;
    private Long version;
    private Boolean deletedState;
    private Date createdDate;
    private Date lastUpdate;

    public MenusCalendar() {

    }

    public MenusCalendar(String guid, Org org, String guidOfMenu, Date startDate, Date endDate, Boolean sixWorkDays,
            Long version, Boolean deletedState) {
        this.guid = guid;
        this.org = org;
        this.guidOfMenu = guidOfMenu;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sixWorkDays = sixWorkDays;
        this.version = version;
        this.deletedState = deletedState;
        this.createdDate = new Date();
    }

    public Long getIdOfMenusCalendar() {
        return idOfMenusCalendar;
    }

    public void setIdOfMenusCalendar(Long idOfMenusCalendar) {
        this.idOfMenusCalendar = idOfMenusCalendar;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public String getGuidOfMenu() {
        return guidOfMenu;
    }

    public void setGuidOfMenu(String guidOfMenu) {
        this.guidOfMenu = guidOfMenu;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getSixWorkDays() {
        return sixWorkDays;
    }

    public void setSixWorkDays(Boolean sixWorkDays) {
        this.sixWorkDays = sixWorkDays;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
