/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.client.items;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientMigration;
import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 16.01.13
 * Time: 16:17
 * To change this template use File | Settings | File Templates.
 */
public class ClientMigrationItemInfo {

    private Long idOfClient;
    private Long contractId;
    private Long idOfOrg;
    private String shortName;
    private String firstName;
    private String surname;
    private String secondName;
    private String guid;
    private Date registrationDate;
    private String fio;

    public ClientMigrationItemInfo() {}

    public ClientMigrationItemInfo(ClientMigration clientMigration) {
        Client client = clientMigration.getClient();
        Org org = clientMigration.getOrg();
        this.firstName = client.getPerson().getFirstName();
        this.idOfClient = client.getIdOfClient();
        this.contractId = client.getContractId();
        this.idOfOrg = org.getIdOfOrg();
        this.shortName = org.getShortName();
        this.surname = client.getPerson().getSurname();
        this.secondName = client.getPerson().getSecondName();
        this.guid = client.getClientGUID();
        this.registrationDate = clientMigration.getRegistrationDate();
        this.fio = secondName+" "+firstName+" "+surname;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }
}
