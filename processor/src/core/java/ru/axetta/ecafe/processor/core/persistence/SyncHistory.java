/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 */
public class SyncHistory {

    private Long idOfSync;
    private Org org;
    private Date syncStartTime;
    private Date syncEndTime;
    private Integer syncResult;
    private Long idOfPacket;
    private String clientVersion;
    private String remoteAddress;
    private Integer syncType;
    private Set<SyncHistoryException> syncHistoryExceptions = new HashSet<SyncHistoryException>();


    public String getRemoteAddress() {
        return remoteAddress;
    }

    private void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    private void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    protected SyncHistory() {
        // For Hibernate only
    }

    public SyncHistory(Org org, Date syncStartTime, long idOfPacket, String clientVersion, String remoteAddress) {
        this.org = org;
        this.syncStartTime = syncStartTime;
        this.idOfPacket = idOfPacket;
        this.clientVersion = clientVersion;
        this.remoteAddress = remoteAddress;
    }

    public SyncHistory(Org org, Date syncStartTime, long idOfPacket, String clientVersion, String remoteAddress,
            Integer syncType) {
        this.org = org;
        this.syncStartTime = syncStartTime;
        this.idOfPacket = idOfPacket;
        this.clientVersion = clientVersion;
        this.remoteAddress = remoteAddress;
        this.syncType = syncType;
    }

    public Long getIdOfSync() {
        return idOfSync;
    }

    private void setIdOfSync(Long idOfSync) {
        // For Hibernate only
        this.idOfSync = idOfSync;
    }

    public Org getOrg() {
        return org;
    }

    private void setOrg(Org org) {
        // For Hibernate only
        this.org = org;
    }

    public Date getSyncStartTime() {
        return syncStartTime;
    }

    private void setSyncStartTime(Date syncStartTime) {
        // For Hibernate only
        this.syncStartTime = syncStartTime;
    }

    public Date getSyncEndTime() {
        return syncEndTime;
    }

    public void setSyncEndTime(Date syncEndTime) {
        this.syncEndTime = syncEndTime;
    }

    public Integer getSyncResult() {
        return syncResult;
    }

    public void setSyncResult(Integer syncResult) {
        this.syncResult = syncResult;
    }

    public Long getIdOfPacket() {
        return idOfPacket;
    }

    private void setIdOfPacket(Long idOfPacket) {
        // For Hibernate only
        this.idOfPacket = idOfPacket;
    }

    public Integer getSyncType() {
        return syncType;
    }

    public void setSyncType(Integer syncType) {
        this.syncType = syncType;
    }

    public Set<SyncHistoryException> getSyncHistoryExceptions() {
        return syncHistoryExceptions;
    }

    public void setSyncHistoryExceptions(Set<SyncHistoryException> syncHistoryExceptions) {
        this.syncHistoryExceptions = syncHistoryExceptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SyncHistory)) {
            return false;
        }
        final SyncHistory that = (SyncHistory) o;
        return idOfSync.equals(that.getIdOfSync());
    }

    @Override
    public int hashCode() {
        return idOfSync.hashCode();
    }

    @Override
    public String toString() {
        return "SyncHistory{" + "idOfSync=" + idOfSync + ", org=" + org + ", syncStartTime=" + syncStartTime
                + ", syncEndTime=" + syncEndTime + ", syncResult=" + syncResult + ", idOfPacket='" + idOfPacket + '\''
                + '}';
    }
}