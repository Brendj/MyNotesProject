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
    private String orgType;
    private String orgName;

    public BaseResponseDTO(Long contractId, String firstName, String middleName, String lastname, String grade
            , String orgType, String orgName) {
        this.contractId = contractId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastname = lastname;
        this.grade = grade;
        this.orgType = orgType;
        this.orgName = orgName;
    }

    public BaseResponseDTO() {
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

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
