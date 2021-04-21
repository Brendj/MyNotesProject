/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.client.responsedto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import ru.iteco.restservice.controller.base.BaseResponseDTO;

@ApiModel(value = "ClientResponseDTO", description = "Данные по клиенту")
public class ClientResponseDTO extends BaseResponseDTO {

    @ApiModelProperty(notes = "Баланс клиента в копейках", example = "10000")
    private Long balance;

    @ApiModelProperty(notes = "Адрес организации, к которой привязан клиент", example = "Москва, Волжский бульвар, дом 16, корпус 2")
    private String address;

    @ApiModelProperty(notes = "Признак, что клиент сейчас находится на территории организации (прошел через турникет)", example = "true")
    private Boolean isInside;

    @ApiModelProperty(notes = "Идентификатор в системе МЭШ", example = "00xx00x0-0000-0x00-x0xx-x00x000x0xx0")
    private String meshGUID;

    @ApiModelProperty(notes = "Признак наличия у клиента особенностей в питании", example = "true")
    private Boolean specialMenu;

    @ApiModelProperty(notes = "Пол клиента", example = "Ж")
    private String gender;

    @ApiModelProperty(notes = "Список льгот в ИС ПП через запятую", example = "Многодетные")
    private String categoryDiscount;

    @ApiModelProperty(notes = "Признак согласия представителя клиента на получение услуги вариативного горячего питания", example = "true")
    private Boolean preorderAllowed;

    @ApiModelProperty(notes = "Размер дневного ограничения на покупку в буфете школьной столовой", example = "10000")
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
