/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * User: shamil
 * Date: 01.12.14
 * Time: 14:09
 */
public class ClientGroupMigrationHistory {

    public static final String MODIFY_IN_WEBAPP = "Изменено в веб.приложении. Пользователь: ";
    public static final String MODIFY_IN_ARM = "Изменено в АРМ.";
    public static final String MODIFY_IN_REGISTRY = "Изменено в Реестрах.";
    public static final String MODIFY_IN_ISPP = "Изменено сервером.";
    public static final String MODIFY_AUTO_MODE = "Изменено на сервере автоматически.";
    public static final String MODIFY_IN_WEB_ARM = "Изменено в Веб-АРМ. Пользователь: ";

    private long idOfGroupClientMigration;
    private Date registrationDate;
    private Org org;
    private Client client;

    private Long oldGroupId;
    private String oldGroupName;
    private Long newGroupId;
    private String newGroupName;
    private String comment;



    public ClientGroupMigrationHistory() {
    }

    public ClientGroupMigrationHistory(Org org, Client client) {
        this.org = org;
        this.client = client;
        this.registrationDate = new Date();
    }

    public long getIdOfGroupClientMigration() {
        return idOfGroupClientMigration;
    }

    public void setIdOfGroupClientMigration(long idOfGroupClientMigration) {
        this.idOfGroupClientMigration = idOfGroupClientMigration;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getOldGroupId() {
        return oldGroupId;
    }

    public void setOldGroupId(Long oldGroupId) {
        this.oldGroupId = oldGroupId;
    }

    public String getOldGroupName() {
        return oldGroupName;
    }

    public void setOldGroupName(String oldGroupName) {
        this.oldGroupName = oldGroupName;
    }

    public Long getNewGroupId() {
        return newGroupId;
    }

    public void setNewGroupId(Long newGroupId) {
        this.newGroupId = newGroupId;
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
}
