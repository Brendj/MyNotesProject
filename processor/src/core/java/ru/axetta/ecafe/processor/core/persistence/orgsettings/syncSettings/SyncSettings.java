/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings;

import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class SyncSettings {
    private Long idOfSyncSetting;
    private Org org;
    private ContentType contentType;
    private Integer everySecond;
    private Integer limitStartHour;
    private Integer limitEndHour;
    private Boolean monday;
    private Boolean tuesday;
    private Boolean wednesday;
    private Boolean thursday;
    private Boolean friday;
    private Boolean saturday;
    private Boolean sunday;
    private Long version;
    private Boolean deleteState;
    private Set<ConcreteTime> concreteTime = new HashSet<>();
    private Date createdDate;
    private Date lastUpdate;

    public Long getIdOfSyncSetting() {
        return idOfSyncSetting;
    }

    public void setIdOfSyncSetting(Long idOfSyncSetting) {
        this.idOfSyncSetting = idOfSyncSetting;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public Integer getEverySecond() {
        return everySecond;
    }

    public void setEverySecond(Integer everySecond) {
        this.everySecond = everySecond;
    }

    public Integer getLimitStartHour() {
        return limitStartHour;
    }

    public void setLimitStartHour(Integer limitStartHour) {
        this.limitStartHour = limitStartHour;
    }

    public Integer getLimitEndHour() {
        return limitEndHour;
    }

    public void setLimitEndHour(Integer limitEndHour) {
        this.limitEndHour = limitEndHour;
    }

    public Boolean getMonday() {
        return monday;
    }

    public void setMonday(Boolean monday) {
        this.monday = monday;
    }

    public Boolean getTuesday() {
        return tuesday;
    }

    public void setTuesday(Boolean tuesday) {
        this.tuesday = tuesday;
    }

    public Boolean getWednesday() {
        return wednesday;
    }

    public void setWednesday(Boolean wednesday) {
        this.wednesday = wednesday;
    }

    public Boolean getThursday() {
        return thursday;
    }

    public void setThursday(Boolean thursday) {
        this.thursday = thursday;
    }

    public Boolean getFriday() {
        return friday;
    }

    public void setFriday(Boolean friday) {
        this.friday = friday;
    }

    public Boolean getSaturday() {
        return saturday;
    }

    public void setSaturday(Boolean saturday) {
        this.saturday = saturday;
    }

    public Boolean getSunday() {
        return sunday;
    }

    public void setSunday(Boolean sunday) {
        this.sunday = sunday;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getDeleteState() {
        return deleteState;
    }

    public void setDeleteState(Boolean deleteState) {
        this.deleteState = deleteState;
    }

    public Set<ConcreteTime> getConcreteTime() {
        return concreteTime;
    }

    public void setConcreteTime(Set<ConcreteTime> concreteTime) {
        this.concreteTime = concreteTime;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
