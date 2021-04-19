/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.client.responsedto;

import ru.iteco.restservice.controller.base.BaseResponseDTO;

public class ClientResponseDTO extends BaseResponseDTO {
    private Long balance;
    private String address;
    private Boolean isInside;
    private String meshGUID;
    private Boolean specialMenu;
    private String gender;
    private String categoryDiscount;
    private Boolean preorderAllowed;
    private Long limit;

    public ClientResponseDTO(Long contractId, Long balance, String firstName, String lastname, String middleName,
            String grade, String orgName, String orgType,  String address, Boolean isInside, String meshGUID,
            Boolean specialMenu, String gender, String categoryDiscount, Boolean preorderAllowed, Long limit) {
        super(contractId, firstName, middleName, lastname, grade, orgType, orgName);
        this.balance = balance;
        this.address = address;
        this.isInside = isInside;
        this.meshGUID = meshGUID;
        this.specialMenu = specialMenu;
        this.gender = gender;
        this.categoryDiscount = categoryDiscount;
        this.preorderAllowed = preorderAllowed;
        this.limit = limit;
    }

    ClientResponseDTO(){

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

    public String getMeshGUID() {
        return meshGUID;
    }

    public void setMeshGUID(String meshGUID) {
        this.meshGUID = meshGUID;
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

    public String getCategoryDiscount() {
        return categoryDiscount;
    }

    public void setCategoryDiscount(String categoryDiscount) {
        this.categoryDiscount = categoryDiscount;
    }

    public Boolean getPreorderAllowed() {
        return preorderAllowed;
    }

    public void setPreorderAllowed(Boolean preorderAllowed) {
        this.preorderAllowed = preorderAllowed;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }
}
