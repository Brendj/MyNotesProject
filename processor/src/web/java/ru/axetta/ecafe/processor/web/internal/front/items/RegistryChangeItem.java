/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 30.09.13
 * Time: 18:57
 * To change this template use File | Settings | File Templates.
 */
public class RegistryChangeItem {
    public static final int APPLY_REGISTRY_CHANGE = 1;
    protected Long idOfOrg;
    protected Long idOfMigrateOrgTo;
    protected Long idOfMigrateOrgFrom;
    protected Long createDate;
    protected Long idOfRegistryChange;
    protected String clientGUID;
    protected String firstName;
    protected String secondName;
    protected String surname;
    protected String groupName;
    protected String firstNameFrom;
    protected String secondNameFrom;
    protected String surnameFrom;
    protected String groupNameFrom;
    protected Long idOfClient;
    protected Integer operation;
    protected Boolean applied;
    protected String error;

    public RegistryChangeItem() {
    }

    public RegistryChangeItem(Long idOfOrg, Long idOfMigrateOrgTo, Long idOfMigrateOrgFrom, Long createDate,
            Long idOfRegistryChange, String clientGUID, String firstName, String secondName, String surname,
            String groupName, String firstNameFrom, String secondNameFrom, String surnameFrom, String groupNameFrom,
            Long idOfClient, Integer operation, Boolean applied, String error) {
        this.idOfOrg = idOfOrg;
        this.idOfMigrateOrgTo = idOfMigrateOrgTo;
        this.idOfMigrateOrgFrom = idOfMigrateOrgFrom;
        this.createDate = createDate;
        this.idOfRegistryChange = idOfRegistryChange;
        this.clientGUID = clientGUID;
        this.firstName = firstName;
        this.secondName = secondName;
        this.surname = surname;
        this.groupName = groupName;
        this.firstNameFrom = firstNameFrom;
        this.secondNameFrom = secondNameFrom;
        this.surnameFrom = surnameFrom;
        this.groupNameFrom = groupNameFrom;
        this.idOfClient = idOfClient;
        this.operation = operation;
        this.applied = applied;
        this.error = error;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfMigrateOrgTo() {
        return idOfMigrateOrgTo;
    }

    public void setIdOfMigrateOrgTo(Long idOfMigrateOrgTo) {
        this.idOfMigrateOrgTo = idOfMigrateOrgTo;
    }

    public Long getIdOfMigrateOrgFrom() {
        return idOfMigrateOrgFrom;
    }

    public void setIdOfMigrateOrgFrom(Long idOfMigrateOrgFrom) {
        this.idOfMigrateOrgFrom = idOfMigrateOrgFrom;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getIdOfRegistryChange() {
        return idOfRegistryChange;
    }

    public void setIdOfRegistryChange(Long idOfRegistryChange) {
        this.idOfRegistryChange = idOfRegistryChange;
    }

    public String getClientGUID() {
        return clientGUID;
    }

    public void setClientGUID(String clientGUID) {
        this.clientGUID = clientGUID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getFirstNameFrom() {
        return firstNameFrom;
    }

    public void setFirstNameFrom(String firstNameFrom) {
        this.firstNameFrom = firstNameFrom;
    }

    public String getSecondNameFrom() {
        return secondNameFrom;
    }

    public void setSecondNameFrom(String secondNameFrom) {
        this.secondNameFrom = secondNameFrom;
    }

    public String getSurnameFrom() {
        return surnameFrom;
    }

    public void setSurnameFrom(String surnameFrom) {
        this.surnameFrom = surnameFrom;
    }

    public String getGroupNameFrom() {
        return groupNameFrom;
    }

    public void setGroupNameFrom(String groupNameFrom) {
        this.groupNameFrom = groupNameFrom;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public Boolean getApplied() {
        return applied;
    }

    public void setApplied(Boolean applied) {
        this.applied = applied;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
