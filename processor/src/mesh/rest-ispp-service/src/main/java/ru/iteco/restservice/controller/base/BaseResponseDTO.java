/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.base;

import java.io.Serializable;

public abstract class BaseResponseDTO implements Serializable {
    private Long contractId;
    private String firstName;
    private String middleName;
    private String lastname;
    private String grade;
    private String officialName;
    private String orgType;
    private String mobile;
    private String orgName;

    public BaseResponseDTO(Long contractId, String firstName, String middleName, String lastname, String grade,
            String officialName, String orgType, String mobile, String orgName) {
        this.contractId = contractId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastname = lastname;
        this.grade = grade;
        this.officialName = officialName;
        this.orgType = orgType;
        this.mobile = mobile;
        this.orgName = orgName;
    }

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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
