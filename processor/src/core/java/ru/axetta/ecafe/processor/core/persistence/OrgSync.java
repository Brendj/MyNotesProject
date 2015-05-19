/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;
import java.util.Date;

/**
 * Данные модифицируемые при синхронизации вынесены
 * User: Shamil
 * Date: 16.01.2015
 * Time: 10:39:31
 */
public class OrgSync implements Serializable {

    private Long idOfOrg;
    private long version;

    private Org org;

    private Long idOfPacket;
    private Date lastSuccessfulBalanceSync;
    private Date lastUnSuccessfulBalanceSync;
    private String clientVersion;
    private String remoteAddress;
    private Date lastAccRegistrySync;


    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
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

    public Date getLastSuccessfulBalanceSync() {
        return lastSuccessfulBalanceSync;
    }

    public void setLastSuccessfulBalanceSync(Date lastSuccessfulBalanceSync) {
        this.lastSuccessfulBalanceSync = lastSuccessfulBalanceSync;
    }

    public Date getLastUnSuccessfulBalanceSync() {
        return lastUnSuccessfulBalanceSync;
    }

    public void setLastUnSuccessfulBalanceSync(Date lastUnSuccessfulBalanceSync) {
        this.lastUnSuccessfulBalanceSync = lastUnSuccessfulBalanceSync;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public Date getLastAccRegistrySync() {
        return lastAccRegistrySync;
    }

    public void setLastAccRegistrySync(Date lastAccRegistrySync) {
        this.lastAccRegistrySync = lastAccRegistrySync;
    }

    @Override
    public String toString() {
        return "OrgSync{" +
                "idOfOrg=" + idOfOrg +
                ", version=" + version +
                ", idOfPacket=" + idOfPacket +
                ", lastSuccessfulBalanceSync=" + lastSuccessfulBalanceSync +
                ", lastUnSuccessfulBalanceSync=" + lastUnSuccessfulBalanceSync +
                ", clientVersion='" + clientVersion + '\'' +
                ", remoteAddress='" + remoteAddress + '\'' +
                '}';
    }
}
