/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 07.08.12
 * Time: 11:15
 * To change this template use File | Settings | File Templates.
 */
public class EMIAS {

    private Long idOfEMIAS;
    private String guid;
    private Long idEventEMIAS;
    private Long typeEventEMIAS;
    private Date dateLiberate;
    private Date startDateLiberate;
    private Date endDateLiberate;
    private Date createDate;
    private Date updateDate;
    private Boolean accepted;

   public EMIAS(){}


    public Long getIdOfEMIAS() {
        return idOfEMIAS;
    }

    public void setIdOfEMIAS(Long idOfEMIAS) {
        this.idOfEMIAS = idOfEMIAS;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getIdEventEMIAS() {
        return idEventEMIAS;
    }

    public void setIdEventEMIAS(Long idEventEMIAS) {
        this.idEventEMIAS = idEventEMIAS;
    }

    public Long getTypeEventEMIAS() {
        return typeEventEMIAS;
    }

    public void setTypeEventEMIAS(Long typeEventEMIAS) {
        this.typeEventEMIAS = typeEventEMIAS;
    }

    public Date getDateLiberate() {
        return dateLiberate;
    }

    public void setDateLiberate(Date dateLiberate) {
        this.dateLiberate = dateLiberate;
    }

    public Date getStartDateLiberate() {
        return startDateLiberate;
    }

    public void setStartDateLiberate(Date startDateLiberate) {
        this.startDateLiberate = startDateLiberate;
    }

    public Date getEndDateLiberate() {
        return endDateLiberate;
    }

    public void setEndDateLiberate(Date endDateLiberate) {
        this.endDateLiberate = endDateLiberate;
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

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
