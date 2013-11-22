/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.06.13
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public class SyncHistoryException {

    private Long idOfSyncHistoryException;
    private Org org;
    private String message;
    private SyncHistory syncHistory;

    protected SyncHistoryException() {}

    public SyncHistoryException(Org org, SyncHistory syncHistory, String message) {
        this.org = org;
        this.message = message;
        this.syncHistory = syncHistory;
    }

    public SyncHistory getSyncHistory() {
        return syncHistory;
    }

    void setSyncHistory(SyncHistory syncHistory) {
        this.syncHistory = syncHistory;
    }

    public String getMessage() {
        return message;
    }

    void setMessage(String message) {
        this.message = message;
    }

    public Org getOrg() {
        return org;
    }

    void setOrg(Org org) {
        this.org = org;
    }

    public Long getIdOfSyncHistoryException() {
        return idOfSyncHistoryException;
    }

    void setIdOfSyncHistoryException(Long idOfSyncHistoryException) {
        this.idOfSyncHistoryException = idOfSyncHistoryException;
    }
}
