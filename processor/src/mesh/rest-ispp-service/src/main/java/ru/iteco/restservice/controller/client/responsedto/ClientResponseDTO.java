/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.client.responsedto;

import ru.iteco.restservice.controller.base.BaseResponseDTO;

import java.util.List;

public class ClientResponseDTO extends BaseResponseDTO {
    private Long balance;
    private String address;
    private Boolean isInside;
    private String meshGUID;
    private Boolean specialMenu;
    private String gender;
    private List<String> categoryDiscount;
    private Boolean preorderAllowed;

    public ClientResponseDTO(Long contractId, String firstName, String middleName, String lastname, String grade,
            String officialName, String orgType, String mobile, String orgName, Long balance, String address,
            Boolean isInside, String meshGUID, Boolean specialMenu, String gender, List<String> categoryDiscount,
            Boolean preorderAllowed) {
        super(contractId, firstName, middleName, lastname, grade, officialName, orgType, mobile, orgName);
        this.balance = balance;
        this.address = address;
        this.isInside = isInside;
        this.meshGUID = meshGUID;
        this.specialMenu = specialMenu;
        this.gender = gender;
        this.categoryDiscount = categoryDiscount;
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

    public List<String> getCategoryDiscount() {
        return categoryDiscount;
    }

    public void setCategoryDiscount(List<String> categoryDiscount) {
        this.categoryDiscount = categoryDiscount;
    }

    public Boolean getPreorderAllowed() {
        return preorderAllowed;
    }

    public void setPreorderAllowed(Boolean preorderAllowed) {
        this.preorderAllowed = preorderAllowed;
    }
}
