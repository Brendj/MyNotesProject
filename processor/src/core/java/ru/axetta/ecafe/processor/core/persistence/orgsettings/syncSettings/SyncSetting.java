/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings;

import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.Date;

public class SyncSetting {
    public static final String SEPARATOR = ";";

    private Long idOfSyncSetting;
    private Org org;
    private ContentType contentType;
    private Integer everySecond;
    private Integer limitStartHour;
    private Integer limitEndHour;
    private Boolean monday = false;
    private Boolean tuesday = false;
    private Boolean wednesday = false;
    private Boolean thursday = false;
    private Boolean friday = false;
    private Boolean saturday = false;
    private Boolean sunday = false;
    private Long version;
    private Boolean deleteState = false;
    private String concreteTime;
    private Date createdDate;
    private Date lastUpdate;

    public SyncSetting(){
        //for hibernate
    }

    public SyncSetting(Boolean monday, Boolean tuesday, Boolean wednesday, Boolean thursday, Boolean friday, Boolean saturday,
            Boolean sunday, Integer everySecond, String buildTime, Integer limitStartHour, Integer limitEndHour){
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        this.everySecond = everySecond;
        this.concreteTime = buildTime;
        this.limitStartHour = limitStartHour;
        this.limitEndHour = limitEndHour;
    }

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
        this.monday = monday == null ? false : monday;
    }

    public Boolean getTuesday() {
        return tuesday;
    }

    public void setTuesday(Boolean tuesday) {
        this.tuesday = tuesday == null ? false : tuesday;
    }

    public Boolean getWednesday() {
        return wednesday;
    }

    public void setWednesday(Boolean wednesday) {
        this.wednesday = wednesday == null ? false : wednesday;
    }

    public Boolean getThursday() {
        return thursday;
    }

    public void setThursday(Boolean thursday) {
        this.thursday = thursday == null ? false : thursday;
    }

    public Boolean getFriday() {
        return friday;
    }

    public void setFriday(Boolean friday) {
        this.friday = friday == null ? false : friday;
    }

    public Boolean getSaturday() {
        return saturday;
    }

    public void setSaturday(Boolean saturday) {
        this.saturday = saturday == null ? false : saturday;
    }

    public Boolean getSunday() {
        return sunday;
    }

    public void setSunday(Boolean sunday) {
         this.sunday = sunday == null ? false : sunday;
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
        this.deleteState = deleteState == null ? false : deleteState;
    }

    public String getConcreteTime() {
        return concreteTime;
    }

    public void setConcreteTime(String concreteTime) {
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
