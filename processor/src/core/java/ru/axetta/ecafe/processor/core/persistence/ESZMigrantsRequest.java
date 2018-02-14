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
    private String firstname;
    private String surname;
    private String secondname;
    private Long idOfESZ;

    public ESZMigrantsRequest(Long idOfServiceClass, String groupName, String clientGuid, String visitOrgInn,
            Integer visitOrgUnom, Date dateEnd, Date dateLearnStart, Date dateLearnEnd, String firstname,
            String surname, String secondname, Long idOfESZ) {
        this.idOfServiceClass = idOfServiceClass;
        this.groupName = groupName;
        this.clientGuid = clientGuid;
        this.visitOrgInn = visitOrgInn;
        this.visitOrgUnom = visitOrgUnom;
        this.dateEnd = dateEnd;
        this.dateLearnStart = dateLearnStart;
        this.dateLearnEnd = dateLearnEnd;
        this.firstname = firstname;
        this.surname = surname;
        this.secondname = secondname;
        this.idOfESZ = idOfESZ;
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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSecondname() {
        return secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    public Long getIdOfESZ() {
        return idOfESZ;
    }

    public void setIdOfESZ(Long idOfESZ) {
        this.idOfESZ = idOfESZ;
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
