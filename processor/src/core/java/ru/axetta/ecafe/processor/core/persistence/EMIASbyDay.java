/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: a.voinov
 * Date: 2.05.21
 * To change this template use File | Settings | File Templates.
 */
public class EMIASbyDay {

    private Long idOfEmiasDay;
    private Long idOfClient;
    private Date date;
    private Boolean eat;
    private Long version;
    private Date createDate;
    private Date updateDate;
    private Long idOfOrg;

   public EMIASbyDay(){}

    public Long getIdOfEmiasDay() {
        return idOfEmiasDay;
    }

    public void setIdOfEmiasDay(Long idOfEmiasDay) {
        this.idOfEmiasDay = idOfEmiasDay;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getEat() {
        return eat;
    }

    public void setEat(Boolean eat) {
        this.eat = eat;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }
}
