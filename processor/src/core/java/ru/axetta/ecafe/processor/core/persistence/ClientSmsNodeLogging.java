/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class ClientSmsNodeLogging {
    private Long id;
    private String idOfSms;
    private String params;
    private String nodeName;
    private Date createDate;


    protected ClientSmsNodeLogging() {
        // For Hibernate only
    }

    public ClientSmsNodeLogging(String idOfSms, String params, String nodeName, Date createDate) {
        this.idOfSms = idOfSms;
        this.params = params;
        this.nodeName = nodeName;
        this.createDate = createDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdOfSms() {
        return idOfSms;
    }

    public void setIdOfSms(String idOfSms) {
        this.idOfSms = idOfSms;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
}