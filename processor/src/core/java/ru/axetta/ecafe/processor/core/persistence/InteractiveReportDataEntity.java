/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: Anvarov
 * Date: 18.03.16
 * Time: 09:48
 * To change this template use File | Settings | File Templates.
 */
public class InteractiveReportDataEntity {

    private Long idOfRecord;
    private Org org;
    private String value;

    private CompositeIdOfInteractiveReportData compositeIdOfInteractiveReportData;

    public InteractiveReportDataEntity() {
    }

    public InteractiveReportDataEntity(CompositeIdOfInteractiveReportData compositeIdOfInteractiveReportData, Long idOfRecord, Org org, String value) {
        this.compositeIdOfInteractiveReportData = compositeIdOfInteractiveReportData;
        this.idOfRecord = idOfRecord;
        this.org = org;
        this.value = value;
    }

    public Long getIdOfRecord() {
        return idOfRecord;
    }

    public void setIdOfRecord(Long idOfRecord) {
        this.idOfRecord = idOfRecord;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public CompositeIdOfInteractiveReportData getCompositeIdOfInteractiveReportData() {
        return compositeIdOfInteractiveReportData;
    }

    public void setCompositeIdOfInteractiveReportData(
            CompositeIdOfInteractiveReportData compositeIdOfInteractiveReportData) {
        this.compositeIdOfInteractiveReportData = compositeIdOfInteractiveReportData;
    }
}
