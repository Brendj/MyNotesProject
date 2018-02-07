/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class ESZMigrantsRequest {
    private Long idOfESZMigrantsRequest;
    private Long idOfServiceClass;
    private String groupName;
    private String clientGuid;
    private String visitOrgInn;
    private Integer visitOrgUnom;
    private Date dateEnd;
    private Date dateLearnStart;
    private Date dateLearnEnd;

    public ESZMigrantsRequest(Long idOfServiceClass, String groupName, String clientGuid, String visitOrgInn,
            Integer visitOrgUnom, Date dateEnd, Date dateLearnStart, Date dateLearnEnd) {
        this.idOfServiceClass = idOfServiceClass;
        this.groupName = groupName;
        this.clientGuid = clientGuid;
        this.visitOrgInn = visitOrgInn;
        this.visitOrgUnom = visitOrgUnom;
        this.dateEnd = dateEnd;
        this.dateLearnStart = dateLearnStart;
        this.dateLearnEnd = dateLearnEnd;
    }

    protected ESZMigrantsRequest() {
        // For Hibernate only
    }

    public Long getIdOfESZMigrantsRequest() {
        return idOfESZMigrantsRequest;
    }

    public void setIdOfESZMigrantsRequest(Long idOfESZMigrantsRequest) {
        this.idOfESZMigrantsRequest = idOfESZMigrantsRequest;
    }

    public Long getIdOfServiceClass() {
        return idOfServiceClass;
    }

    public void setIdOfServiceClass(Long idOfServideClass) {
        this.idOfServiceClass = idOfServideClass;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getClientGuid() {
        return clientGuid;
    }

    public void setClientGuid(String clientGuid) {
        this.clientGuid = clientGuid;
    }

    public String getVisitOrgInn() {
        return visitOrgInn;
    }

    public void setVisitOrgInn(String visitOrgInn) {
        this.visitOrgInn = visitOrgInn;
    }

    public Integer getVisitOrgUnom() {
        return visitOrgUnom;
    }

    public void setVisitOrgUnom(Integer visitOrgUnom) {
        this.visitOrgUnom = visitOrgUnom;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Date getDateLearnStart() {
        return dateLearnStart;
    }

    public void setDateLearnStart(Date dateLearnStart) {
        this.dateLearnStart = dateLearnStart;
    }

    public Date getDateLearnEnd() {
        return dateLearnEnd;
    }

    public void setDateLearnEnd(Date dateLearnEnd) {
        this.dateLearnEnd = dateLearnEnd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ESZMigrantsRequest)) {
            return false;
        }
        final ESZMigrantsRequest eszMigrantsRequest = (ESZMigrantsRequest) o;
        return clientGuid.equals(eszMigrantsRequest.getClientGuid()) &&
                idOfServiceClass.equals(eszMigrantsRequest.getIdOfServiceClass());
    }

    @Override
    public int hashCode() {
        return idOfESZMigrantsRequest.hashCode();
    }

    @Override
    public String toString() {
        return "ESZMigrantsRequest{" + "idOfESZMigrantsRequest=" + idOfESZMigrantsRequest + ", idOfServiceClass=" + idOfServiceClass
                + ", groupName=" + groupName + ", clientGuid='" + clientGuid + ", visitOrgInn='" + visitOrgInn
                + ", visitOrgUnom='" + visitOrgUnom + ", dateEnd='" + dateEnd + ", dateLearnStart='" + dateLearnStart
                + ", dateLearnEnd='" + dateLearnEnd  + '}';
    }
}
