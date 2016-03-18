/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: T800
 * Date: 18.03.16
 * Time: 13:04
 * To change this template use File | Settings | File Templates.
 */
public class CompositeIdOfInteractiveReportData implements Serializable {

    private Long idOfOrg;
    private Long idOfRecord;

    public CompositeIdOfInteractiveReportData() {
        // For Hibernate only
    }

    public CompositeIdOfInteractiveReportData(Long idOfOrg, Long idOfRecord) {
        this.idOfOrg = idOfOrg;
        this.idOfRecord = idOfRecord;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfRecord() {
        return idOfRecord;
    }

    public void setIdOfRecord(Long idOfRecord) {
        this.idOfRecord = idOfRecord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeIdOfInteractiveReportData)) {
            return false;
        }
        final CompositeIdOfInteractiveReportData that = (CompositeIdOfInteractiveReportData) o;
        return idOfRecord.equals(that.getIdOfRecord()) && idOfOrg.equals(that.getIdOfOrg());
    }

    @Override
    public int hashCode() {
        return idOfRecord.hashCode();
    }

    @Override
    public String toString() {
        return "CompositeIdOfOrder{" + "idOfOrg=" + idOfOrg + ", idOfRecord=" + idOfRecord + '}';
    }
}
