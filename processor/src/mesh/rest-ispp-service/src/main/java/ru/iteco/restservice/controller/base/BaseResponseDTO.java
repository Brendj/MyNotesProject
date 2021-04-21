/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.base;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public abstract class BaseResponseDTO implements Serializable {
    @ApiModelProperty(name = "Номер лицевого счета клиента", example = "1868344")
    private Long contractId;

    @ApiModelProperty(name = "Имя", example = "Пётр")
    private String firstName;

    @ApiModelProperty(name = "Отчество", example = "Петрович")
    private String middleName;

    @ApiModelProperty(name = "Фамилия", example = "Петров")
    private String lastName;

    @ApiModelProperty(name = "Группа", example = "5-А")
    private String grade;

    @ApiModelProperty(name = "Тип организации, к которой привязан клиент", example = "Общеобразовательное ОУ")
    private String orgType;

    @ApiModelProperty(name = "Название организации, к которой привязан клиент", example = "ГБОУ СОШ № 1367 (13)")
    private String orgName;

    public BaseResponseDTO(Long contractId, String firstName, String middleName, String lastname, String grade
            , String orgType, String orgName) {
        this.contractId = contractId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastname;
        this.grade = grade;
        this.orgType = orgType;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastname) {
        this.lastName = lastname;
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
