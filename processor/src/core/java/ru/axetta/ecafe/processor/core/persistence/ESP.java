/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class ESP {
    private Long idesprequest;
    private Client client;
    private String email;
    private Org org;
    private String status;
    private String numberrequest;
    private String topic;
    private String message;
    private Date createDate;
    private Date updateDate;
    private Date closeddate;
    private String solution;

   public ESP(){}


    public Long getIdesprequest() {
        return idesprequest;
    }

    public void setIdesprequest(Long idesprequest) {
        this.idesprequest = idesprequest;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNumberrequest() {
        return numberrequest;
    }

    public void setNumberrequest(String numberrequest) {
        this.numberrequest = numberrequest;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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

    public Date getCloseddate() {
        return closeddate;
    }

    public void setCloseddate(Date closeddate) {
        this.closeddate = closeddate;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
