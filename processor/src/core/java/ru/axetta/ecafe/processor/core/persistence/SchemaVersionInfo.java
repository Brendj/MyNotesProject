/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class SchemaVersionInfo {
    private long schemaVersionInfoId;
    private int majorVersionNum;
    private int middleVersionNum;
    private int minorVersionNum;
    private int buildVersionNum;
    private Date updateTime;

    public SchemaVersionInfo() {

    }

    public SchemaVersionInfo(int[] version,
            Date updateTime) {
        this.majorVersionNum = version[0];
        this.middleVersionNum = version[1];
        this.minorVersionNum = version[2];
        this.buildVersionNum = version[3];
        this.updateTime = updateTime;
    }

    public long getSchemaVersionInfoId() {
        return schemaVersionInfoId;
    }

    public void setSchemaVersionInfoId(long schemaVersionInfoId) {
        this.schemaVersionInfoId = schemaVersionInfoId;
    }

    public int getMajorVersionNum() {
        return majorVersionNum;
    }

    public void setMajorVersionNum(int majorVersionNum) {
        this.majorVersionNum = majorVersionNum;
    }

    public int getMiddleVersionNum() {
        return middleVersionNum;
    }

    public void setMiddleVersionNum(int middleVersionNum) {
        this.middleVersionNum = middleVersionNum;
    }

    public int getMinorVersionNum() {
        return minorVersionNum;
    }

    public void setMinorVersionNum(int minorVersionNum) {
        this.minorVersionNum = minorVersionNum;
    }

    public int getBuildVersionNum() {
        return buildVersionNum;
    }

    public void setBuildVersionNum(int buildVersionNum) {
        this.buildVersionNum = buildVersionNum;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SchemaVersionInfo that = (SchemaVersionInfo) o;

        if (schemaVersionInfoId != that.schemaVersionInfoId) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (schemaVersionInfoId ^ (schemaVersionInfoId >>> 32));
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d.%d", majorVersionNum, middleVersionNum, minorVersionNum,
                buildVersionNum, updateTime==null?"null":updateTime.toString());
    }
}
