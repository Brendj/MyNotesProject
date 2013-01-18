/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.client;

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
public final class ClientMigrationItemInfo {

    private final Long idOfClient;
    private final Long contractId;
    private final Long idOfOrg;
    private final String shortName;
    private final String firstName;
    private final String surname;
    private final String secondName;
    private final String guid;
    private final Date registrationDate;
    private final String fio;

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

    public Long getContractId() {
        return contractId;
    }

    public String getFio() {
        return fio;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getGuid() {
        return guid;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getSurname() {
        return surname;
    }
}
