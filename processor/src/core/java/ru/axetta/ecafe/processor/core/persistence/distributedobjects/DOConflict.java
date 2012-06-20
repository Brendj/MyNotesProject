/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.DateType;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 15.06.12
 * Time: 16:42
 * To change this template use File | Settings | File Templates.
 */
public class DOConflict {

    private long IdOfDoConflict;
    private String distributedObjectClassName;
    private Date createConflictDate;
    private long gVersionInc;
    private long gVersionCur;
    private String valueInc;
    private String valueCur;
    private long idOfOrg;

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getValueCur() {
        return valueCur;
    }

    public void setValueCur(String valueCur) {
        this.valueCur = valueCur;
    }

    public String getValueInc() {
        return valueInc;
    }

    public void setValueInc(String valueInc) {
        this.valueInc = valueInc;
    }

    public long getgVersionCur() {
        return gVersionCur;
    }

    public void setgVersionCur(long gVersionCur) {
        this.gVersionCur = gVersionCur;
    }

    public long getgVersionInc() {
        return gVersionInc;
    }

    public void setgVersionInc(long gVersionInc) {
        this.gVersionInc = gVersionInc;
    }

    public Date getCreateConflictDate() {
        return createConflictDate;
    }

    public void setCreateConflictDate(Date createConflictDate) {
        this.createConflictDate = createConflictDate;
    }

    public String getDistributedObjectClassName() {
        return distributedObjectClassName;
    }

    public void setDistributedObjectClassName(String distributedObjectClassName) {
        this.distributedObjectClassName = distributedObjectClassName;
    }

    public long getIdOfDoConflict() {
        return IdOfDoConflict;
    }

    public void setIdOfDoConflict(long idOfDoConflict) {
        IdOfDoConflict = idOfDoConflict;
    }
}
