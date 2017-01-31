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

    public static final String MODIFY_IN_REGISTRY = "Изменено в Реестрах.";


    private Long idOfClientMigration;
    private Date registrationDate;
    private Org org;
    private Org oldOrg;
    private Contragent oldContragent;
    private Contragent newContragent;
    private Client client;
    private Long balance;

    private String oldGroupName;
    private String newGroupName;
    private String comment;

    protected ClientMigration(){}

    public ClientMigration(Org oldOrg) {
        this.oldOrg = oldOrg;
        this.oldContragent = oldOrg.getDefaultSupplier();
        this.registrationDate = new Date();
    }

    public ClientMigration(Client client, Org org) {
        this.client = client;
        this.balance = client.getBalance();
        this.org = org;
        this.setNewContragent(org.getDefaultSupplier());
        this.registrationDate = new Date();
    }


    public ClientMigration(Client client, Org org, Org oldOrg) {
        this.client = client;
        this.balance = client.getBalance();
        this.org = org;
        this.setNewContragent(org.getDefaultSupplier());
        this.oldOrg = oldOrg;
        this.setOldContragent(oldOrg.getDefaultSupplier());
        this.registrationDate = new Date();
    }

    public ClientMigration(Client client, Org org, Date registrationDate) {
        this.client = client;
        this.balance = client.getBalance();
        this.org = org;
        this.setNewContragent(org.getDefaultSupplier());
        this.registrationDate = registrationDate;
    }

    public ClientMigration(Client client, Org org, Date registrationDate, String newGroupName) {
        this.client = client;
        this.balance = client.getBalance();
        this.org = org;
        this.setNewContragent(org.getDefaultSupplier());
        this.registrationDate = registrationDate;
        this.newGroupName = newGroupName;
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

    public Org getOldOrg() {
        return oldOrg;
    }

    public void setOldOrg(Org oldOrg) {
        this.oldOrg = oldOrg;
    }

    public Contragent getOldContragent() {
        return oldContragent;
    }

    public void setOldContragent(Contragent oldContragent) {
        this.oldContragent = oldContragent;
    }

    public Contragent getNewContragent() {
        return newContragent;
    }

    public void setNewContragent(Contragent newContragent) {
        this.newContragent = newContragent;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public String getOldGroupName() {
        return oldGroupName;
    }

    public void setOldGroupName(String oldGroupName) {
        this.oldGroupName = oldGroupName;
    }

    public String getNewGroupName() {
        return newGroupName;
    }

    public void setNewGroupName(String newGroupName) {
        this.newGroupName = newGroupName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (oldOrg != null ? oldOrg.hashCode() : 0);
        result = 31 * result + (client != null ? client.hashCode() : 0);
        return result;
    }
}
