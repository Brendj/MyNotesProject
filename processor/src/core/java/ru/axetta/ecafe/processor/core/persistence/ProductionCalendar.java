/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by i.semenov on 05.02.2019.
 */
public class ProductionCalendar {
    private Long idOfProductionCalendar;
    private Date day;
    private Date createdDate;
    private Date lastUpdate;

    public ProductionCalendar() {

    }

    public ProductionCalendar(Date date) {
        this.day = date;
        this.createdDate = new Date();
    }

    public Long getIdOfProductionCalendar() {
        return idOfProductionCalendar;
    }

    public void setIdOfProductionCalendar(Long idOfProductionCalendar) {
        this.idOfProductionCalendar = idOfProductionCalendar;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
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
