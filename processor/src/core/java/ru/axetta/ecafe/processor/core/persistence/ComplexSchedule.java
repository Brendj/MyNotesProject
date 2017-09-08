/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by i.semenov on 30.08.2017.
 */
public class ComplexSchedule {
    private Long idOfOrg;
    private Long idOfComplex;
    private String guid;
    private Integer intervalFrom;
    private Integer intervalTo;
    private Long version;
    private Long idOfOrgCreated;
    private String groupsIds;
    private Boolean deletedState;

    public ComplexSchedule() {

    }

    public ComplexSchedule(String guid) {
        this.guid = guid;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(Long idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getIntervalFrom() {
        return intervalFrom;
    }

    public void setIntervalFrom(Integer intervalFrom) {
        this.intervalFrom = intervalFrom;
    }

    public Integer getIntervalTo() {
        return intervalTo;
    }

    public void setIntervalTo(Integer intervalTo) {
        this.intervalTo = intervalTo;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getIdOfOrgCreated() {
        return idOfOrgCreated;
    }

    public void setIdOfOrgCreated(Long idOfOrgCreated) {
        this.idOfOrgCreated = idOfOrgCreated;
    }

    public String getGroupsIds() {
        return groupsIds;
    }

    public void setGroupsIds(String groupsIds) {
        this.groupsIds = groupsIds;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }
}
