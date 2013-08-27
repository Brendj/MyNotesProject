/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 16.01.13
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */
public class ClientMigration {

    private Long idOfClientMigration;
    private Date registrationDate;
    private Org org;
    private Client client;

    protected ClientMigration(){}

    public ClientMigration(Client client, Org org) {
        this.client = client;
        this.org = org;
        this.registrationDate = new Date();
    }

    public ClientMigration(Client client, Org org, Date registrationDate) {
        this.client = client;
        this.org = org;
        this.registrationDate = registrationDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }


    public Date getRegistrationDate() {
        return registrationDate;
    }

    protected void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Long getIdOfClientMigration() {
        return idOfClientMigration;
    }

    protected void setIdOfClientMigration(Long idOfClientMigration) {
        this.idOfClientMigration = idOfClientMigration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ClientMigration that = (ClientMigration) o;

        if (idOfClientMigration != null ? !idOfClientMigration.equals(that.idOfClientMigration)
                : that.idOfClientMigration != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = idOfClientMigration != null ? idOfClientMigration.hashCode() : 0;
        result = 31 * result + (registrationDate != null ? registrationDate.hashCode() : 0);
        result = 31 * result + (org != null ? org.hashCode() : 0);
        result = 31 * result + (client != null ? client.hashCode() : 0);
        return result;
    }
}
