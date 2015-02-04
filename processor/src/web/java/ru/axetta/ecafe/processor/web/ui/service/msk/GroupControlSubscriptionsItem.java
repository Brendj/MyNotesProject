/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 24.12.14
 * Time: 16:39
 */

public class GroupControlSubscriptionsItem {

    private String orgNameWithAddress;
    private String firstName;
    private String surname;
    private String secondName;
    public Long contractId;
    public String result;

    public GroupControlSubscriptionsItem() {
    }

    public GroupControlSubscriptionsItem(String orgNameWithAddress, String surname, String firstName, String secondName,
            Long contractId, String result) {
        this.orgNameWithAddress = orgNameWithAddress;
        this.surname = surname;
        this.firstName = firstName;
        this.secondName = secondName;
        this.contractId = contractId;
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getOrgNameWithAddress() {
        return orgNameWithAddress;
    }

    public void setOrgNameWithAddress(String orgNameWithAddress) {
        this.orgNameWithAddress = orgNameWithAddress;
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
}
