/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.sync.items;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.06.13
 * Time: 13:55
 */
public class Sync {

    private long idOfOrg;
    private String officialName;
    private Date syncStartTime;
    private Date syncEndTime;

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public Date getSyncStartTime() {
        return syncStartTime;
    }

    public void setSyncStartTime(Date syncStartTime) {
        this.syncStartTime = syncStartTime;
    }

    public Date getSyncEndTime() {
        return syncEndTime;
    }

    public void setSyncEndTime(Date syncEndTime) {
        this.syncEndTime = syncEndTime;
    }

}
