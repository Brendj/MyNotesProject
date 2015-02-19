/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 18.02.15
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */
public class GroupControlBenefitsItems {

    private String orgName;
    private String groupName;
    private String firstName;
    private String surname;
    private String secondName;
    private String contractId;
    private String benefits;
    public String result;

    public GroupControlBenefitsItems() {
    }

    public GroupControlBenefitsItems(String orgName, String groupName, String firstName, String surname,
            String secondName, String contractId, String benefits, String result) {
        this.orgName = orgName;
        this.groupName = groupName;
        this.firstName = firstName;
        this.surname = surname;
        this.secondName = secondName;
        this.contractId = contractId;
        this.benefits = benefits;
        this.result = result;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
