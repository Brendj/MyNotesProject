/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by i.semenov on 05.02.2019.
 */
public class ProductionCalendar {
    public static final Integer HOLIDAY = 2;
    public static final Integer REGULAR = 1;

    private Long idOfProductionCalendar;
    private Date day;
    private Date createdDate;
    private Date lastUpdate;
    private Integer flag;
    private Long version;

    public ProductionCalendar() {

    }

    public ProductionCalendar(Date date, Integer flag, Long version) {
        this.day = date;
        this.createdDate = new Date();
        this.flag = flag;
        this.version = version;
    }

    public void modify(Integer flag, Long version) {
        this.lastUpdate = new Date();
        this.flag = flag;
        this.version = version;
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

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
