/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by i.semenov on 07.02.2019.
 */
public class OrgGoodRequest {
    private Long idOfOrgGoodRequest;
    private Long idOfOrg;
    private Date day;
    private Date createdDate;
    private Boolean isSent;
    private Date sendDate;

    public OrgGoodRequest() {

    }

    public OrgGoodRequest(Long idOfOrg, Date day) {
        this.idOfOrg = idOfOrg;
        this.day = day;
        this.createdDate = new Date();
        this.isSent = false;
    }

    public void requestIsSent() {
        this.isSent = true;
        this.sendDate = new Date();
    }

    public Long getIdOfOrgGoodRequest() {
        return idOfOrgGoodRequest;
    }

    public void setIdOfOrgGoodRequest(Long idOfOrgGoodRequest) {
        this.idOfOrgGoodRequest = idOfOrgGoodRequest;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
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

    public Boolean getIsSent() {
        return isSent;
    }

    public void setIsSent(Boolean sent) {
        isSent = sent;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }
}
