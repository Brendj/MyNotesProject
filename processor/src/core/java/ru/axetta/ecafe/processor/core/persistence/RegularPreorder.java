/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by baloun on 27.06.2018.
 */
public class RegularPreorder {
    private Long idOfRegularPreorder;
    private Client client;
    private Date startDate;
    private Date endDate;
    private String itemCode;
    private Integer idOfComplex;
    private Integer amount;
    private String itemName;
    private Boolean monday;
    private Boolean tuesday;
    private Boolean wednesday;
    private Boolean thursday;
    private Boolean friday;
    private Boolean saturday;
    private Long price;
    private Date createdDate;
    private Date lastUpdate;
    private Boolean deletedState;

    public RegularPreorder() {

    }

    public RegularPreorder(Client client, Date startDate, Date endDate, String itemCode, Integer idOfComplex,
            Integer amount, String itemName, Boolean monday, Boolean tuesday, Boolean wednesday, Boolean thursday,
            Boolean friday, Boolean saturday, Long price) {
        this.client = client;
        this.startDate = startDate;
        this.endDate = endDate;
        this.itemCode = itemCode;
        this.idOfComplex = idOfComplex;
        this.amount = amount;
        this.itemName = itemName;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.price = price;
        this.createdDate = new Date();
        this.lastUpdate = new Date();
        this.deletedState = false;
    }

    public Long getIdOfRegularPreorder() {
        return idOfRegularPreorder;
    }

    public void setIdOfRegularPreorder(Long idOfRegularPreorder) {
        this.idOfRegularPreorder = idOfRegularPreorder;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public Integer getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(Integer idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public Boolean getMonday() {
        return monday;
    }

    public void setMonday(Boolean monday) {
        this.monday = monday;
    }

    public Boolean getTuesday() {
        return tuesday;
    }

    public void setTuesday(Boolean tuesday) {
        this.tuesday = tuesday;
    }

    public Boolean getWednesday() {
        return wednesday;
    }

    public void setWednesday(Boolean wednesday) {
        this.wednesday = wednesday;
    }

    public Boolean getThursday() {
        return thursday;
    }

    public void setThursday(Boolean thursday) {
        this.thursday = thursday;
    }

    public Boolean getFriday() {
        return friday;
    }

    public void setFriday(Boolean friday) {
        this.friday = friday;
    }

    public Boolean getSaturday() {
        return saturday;
    }

    public void setSaturday(Boolean saturday) {
        this.saturday = saturday;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}