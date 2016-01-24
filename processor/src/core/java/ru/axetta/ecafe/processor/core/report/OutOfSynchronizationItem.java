/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: T800
 * Date: 22.01.16
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
public class OutOfSynchronizationItem {

    private String condition;
    private Long idOfOrg;
    private String orgShortName;
    private String address;

    private String tags;
    private Date lastSuccessfulBalanceSync;
    private String version;
    private String remoteAddr;

    public OutOfSynchronizationItem(String condition, Long idOfOrg, String orgShortName, String address, String tags,
            Date lastSuccessfulBalanceSync, String version, String remoteAddr) {
        this.condition = condition;
        this.idOfOrg = idOfOrg;
        this.orgShortName = orgShortName;
        this.address = address;
        this.tags = tags;
        this.lastSuccessfulBalanceSync = lastSuccessfulBalanceSync;
        this.version = version;
        this.remoteAddr = remoteAddr;
    }

    public OutOfSynchronizationItem() {
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getOrgShortName() {
        return orgShortName;
    }

    public void setOrgShortName(String orgShortName) {
        this.orgShortName = orgShortName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Date getLastSuccessfulBalanceSync() {
        return lastSuccessfulBalanceSync;
    }

    public void setLastSuccessfulBalanceSync(Date lastSuccessfulBalanceSync) {
        this.lastSuccessfulBalanceSync = lastSuccessfulBalanceSync;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getLastSuccessfulBalanceSyncShortName() {
        return CalendarUtils.dateTimeToString(lastSuccessfulBalanceSync);
    }
}
