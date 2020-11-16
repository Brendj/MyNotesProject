/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: voinov
 * Date: 11.11.20
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */
public class ClientGuardianHistory {
    private Long guardHistory;
    private ClientGuardian clientGuardian;
    private Date changeDate;
    private String action;
    private String webAdress;
    private ClientCreatedFromType  createdFrom;
    private Org org;
    private User user;
    private Long idOfPacket;
    private String changeParam;
    private String oldValue;
    private String newValue;
    private String reason;

    public ClientGuardianHistory getCopyClientGuardionHistory(ClientGuardianHistory clientGuardianHistory)
    {
        ClientGuardianHistory clientGuardianHistoryCopy = new ClientGuardianHistory();
        clientGuardianHistoryCopy.setClientGuardian(clientGuardianHistory.getClientGuardian());
        clientGuardianHistoryCopy.setChangeDate(clientGuardianHistory.getChangeDate());
        clientGuardianHistoryCopy.setAction(clientGuardianHistory.getAction());
        clientGuardianHistoryCopy.setWebAdress(clientGuardianHistory.getWebAdress());
        clientGuardianHistoryCopy.setCreatedFrom(clientGuardianHistory.getCreatedFrom());
        clientGuardianHistoryCopy.setOrg(clientGuardianHistory.getOrg());
        clientGuardianHistoryCopy.setUser(clientGuardianHistory.getUser());
        clientGuardianHistoryCopy.setIdOfPacket(clientGuardianHistory.getIdOfPacket());
        clientGuardianHistoryCopy.setChangeParam(clientGuardianHistory.getChangeParam());
        clientGuardianHistoryCopy.setOldValue(clientGuardianHistory.getOldValue());
        clientGuardianHistoryCopy.setNewValue(clientGuardianHistory.getNewValue());
        clientGuardianHistoryCopy.setReason(clientGuardianHistory.getReason());
        return clientGuardianHistoryCopy;
    }

    public Long getGuardHistory() {
        return guardHistory;
    }

    public void setGuardHistory(Long guardHistory) {
        this.guardHistory = guardHistory;
    }

    public ClientGuardian getClientGuardian() {
        return clientGuardian;
    }

    public void setClientGuardian(ClientGuardian clientGuardian) {
        this.clientGuardian = clientGuardian;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getWebAdress() {
        return webAdress;
    }

    public void setWebAdress(String webAdress) {
        this.webAdress = webAdress;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getChangeParam() {
        return changeParam;
    }

    public void setChangeParam(String changeParam) {
        this.changeParam = changeParam;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Long getIdOfPacket() {
        return idOfPacket;
    }

    public void setIdOfPacket(Long idOfPacket) {
        this.idOfPacket = idOfPacket;
    }

    public ClientCreatedFromType getCreatedFrom() {
        return createdFrom;
    }

    public void setCreatedFrom(ClientCreatedFromType createdFrom) {
        this.createdFrom = createdFrom;
    }
}
