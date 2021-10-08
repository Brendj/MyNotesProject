/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.employee.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.iteco.restservice.controller.base.BaseResponseDTO;

@Schema(name = "EmployeeResponseDTO", description = "Данные по сотруднику")
public class EmployeeResponseDTO  extends BaseResponseDTO {
    @Schema(description = "Баланс сотрудника в копейках", example = "10000")
    private Long balance;

    @Schema(description = "Адрес организации, к которой привязан сотрудник", example = "Москва, Волжский бульвар, дом 16, корпус 2")
    private String address;

    @Schema(description = "Признак, что сотрудник сейчас находится на территории организации (прошел через турникет)", example = "true")
    private Boolean isInside;

    @Schema(description = "Признак наличия у сотрудника особенностей в питании", example = "true")
    private Boolean specialMenu;

    @Schema(description = "Пол сотрудника", example = "Ж")
    private String gender;

    @Schema(description = "Признак согласия сотрудника на получение услуги вариативного горячего питания",
            example = "true")
    private Boolean preorderAllowed;

    public EmployeeResponseDTO(Long contractId, Long balance, String firstName, String middleName, String lastname, String grade,
            String orgType, String orgName, String address, Boolean isInside, Boolean specialMenu,
            String gender, Boolean preorderAllowed) {
        super(contractId, firstName, middleName, lastname, grade, orgType, orgName);
        this.balance = balance;
        this.address = address;
        this.isInside = isInside;
        this.specialMenu = specialMenu;
        this.gender = gender;
        this.preorderAllowed = preorderAllowed;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getIsInside() {
        return isInside;
    }

    public void setIsInside(Boolean inside) {
        isInside = inside;
    }

    public Boolean getSpecialMenu() {
        return specialMenu;
    }

    public void setSpecialMenu(Boolean specialMenu) {
        this.specialMenu = specialMenu;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getPreorderAllowed() {
        return preorderAllowed;
    }

    public void setPreorderAllowed(Boolean preorderAllowed) {
        this.preorderAllowed = preorderAllowed;
    }
}
