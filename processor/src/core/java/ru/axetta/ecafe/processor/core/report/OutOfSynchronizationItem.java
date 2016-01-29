/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;

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
    private String officialName;
    private String address;

    private String tags;
    private String lastSuccessfulBalanceSync;
    private String version;
    private String remoteAddr;

    public OutOfSynchronizationItem(String condition, Long idOfOrg, String officialName, String address, String tags,
            String lastSuccessfulBalanceSync, String version, String remoteAddr) {
        this.condition = condition;
        this.idOfOrg = idOfOrg;
        this.officialName = officialName;
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

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
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

    public String getLastSuccessfulBalanceSync() {
        return lastSuccessfulBalanceSync;
    }

    public void setLastSuccessfulBalanceSync(String lastSuccessfulBalanceSync) {
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
}
