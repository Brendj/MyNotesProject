/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.utils;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 04.04.16
 * Time: 12:09
 * To change this template use File | Settings | File Templates.
 */
public class HTTPData {
    private String idOfSystem;
    private String ssoId;
    private String operationType;

    public String getIdOfSystem() {
        return idOfSystem;
    }

    public void setIdOfSystem(String idOfSystem) {
        this.idOfSystem = idOfSystem;
    }

    public String getSsoId() {
        return ssoId;
    }

    public void setSsoId(String ssoId) {
        this.ssoId = ssoId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
}
