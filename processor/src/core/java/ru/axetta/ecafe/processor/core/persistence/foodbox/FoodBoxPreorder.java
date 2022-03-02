/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.foodbox;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.Date;

public class FoodBoxPreorder  {
    private Long idFoodBoxPreorder;
    private String idFoodBoxExternal;
    private FoodBoxStateTypeEnum state;
    private Client client;
    private Date initialDateTime;
    private String error;
    private Long idOfFoodBox;
    private Integer cellNumber;
    private Long idOfOrder;
    private Integer cancelReason;
    private Long version;
    private Date createDate;
    private Date updateDate;
    private Org org;
    private Long orderPrice;
    private Boolean located;

    public FoodBoxPreorder()
    {
        updateDate = new Date();
    }

    public Long getIdFoodBoxPreorder() {
        return idFoodBoxPreorder;
    }

    public void setIdFoodBoxPreorder(Long idFoodBoxPreorder) {
        this.idFoodBoxPreorder = idFoodBoxPreorder;
    }

    public FoodBoxStateTypeEnum getState() {
        return state;
    }

    public void setState(FoodBoxStateTypeEnum state) {
        this.state = state;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getInitialDateTime() {
        return initialDateTime;
    }

    public void setInitialDateTime(Date initialDateTime) {
        this.initialDateTime = initialDateTime;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Long getIdOfFoodBox() {
        return idOfFoodBox;
    }

    public void setIdOfFoodBox(Long idOfFoodBox) {
        this.idOfFoodBox = idOfFoodBox;
    }

    public Integer getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(Integer cellNumber) {
        this.cellNumber = cellNumber;
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
    }

    public Integer getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(Integer cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public String getIdFoodBoxExternal() {
        return idFoodBoxExternal;
    }

    public void setIdFoodBoxExternal(String idFoodBoxExternal) {
        this.idFoodBoxExternal = idFoodBoxExternal;
    }

    public Long getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Long orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Boolean getLocated() {
        return located;
    }

    public void setLocated(Boolean located) {
        this.located = located;
    }
}
