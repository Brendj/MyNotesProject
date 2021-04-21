/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.employee.responsedto;

import ru.iteco.restservice.controller.base.BaseResponseDTO;

public class EmployeeResponseDTO  extends BaseResponseDTO {
    private Long balance;
    private String address;
    private Boolean isInside;
    private Boolean specialMenu;
    private String gender;
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

    public Boolean getInside() {
        return isInside;
    }

    public void setInside(Boolean inside) {
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
