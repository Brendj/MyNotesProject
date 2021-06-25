/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.base;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

public abstract class BaseResponseDTO implements Serializable {
    @Schema(description = "Номер лицевого счета клиента", example = "1868344")
    private Long contractId;

    @Schema(description = "Имя", example = "Пётр")
    private String firstName;

    @Schema(description = "Отчество", example = "Петрович")
    private String middleName;

    @Schema(description = "Фамилия", example = "Петров")
    private String lastName;

    @Schema(description = "Группа", example = "5-А")
    private String grade;

    @Schema(description = "Тип организации, к которой привязан клиент", example = "Общеобразовательное ОУ")
    private String orgType;

    @Schema(description = "Название организации, к которой привязан клиент", example = "ГБОУ СОШ № 1367 (13)")
    private String orgName;

    public BaseResponseDTO(Long contractId, String firstName, String middleName, String lastname, String grade,
            String orgType, String orgName) {
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
