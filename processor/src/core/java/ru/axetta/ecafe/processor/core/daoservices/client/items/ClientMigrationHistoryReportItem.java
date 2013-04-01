/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.client.items;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 07.02.13
 * Time: 12:11
 * To change this template use File | Settings | File Templates.
 */
public class ClientMigrationHistoryReportItem {

    private  Long idOfClient;
    private  Long contractId;
    private  Long idOfOrg;
    private  String shortName;
    private  String firstName;
    private  String surname;
    private  String secondName;
    private  String guid;
    private  Date registrationDate;

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public String toString() {
        return "ClientMigrationHistoryReportItem{" +
                "contractId=" + contractId +
                ", idOfClient=" + idOfClient +
                ", idOfOrg=" + idOfOrg +
                ", shortName='" + shortName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", surname='" + surname + '\'' +
                ", secondName='" + secondName + '\'' +
                ", guid='" + guid + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }
}
