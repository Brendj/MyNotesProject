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
    private Integer posted;

    public FoodBoxPreorder()
    {
        updateDate = new Date();
    }

    public FoodBoxPreorder(Client client, Long version, String xrequestStr)
    {
        this.version = version;
        this.client = client;
        this.state = FoodBoxStateTypeEnum.NEW;
        this.org = client.getOrg();
        this.createDate = new Date();
        this.idFoodBoxExternal = xrequestStr;
        this.posted = 0;
    }

    public Long getIdFoodBoxPreorder() {
        return idFoodBoxPreorder;
    }

    public void setIdFoodBoxPreorder(Long idFoodBoxPreorder) {
        this.idFoodBoxPreorder = idFoodBoxPreorder;
        this.setUpdateDate(new Date());
    }

    public FoodBoxStateTypeEnum getState() {
        return state;
    }

    public void setState(FoodBoxStateTypeEnum state) {
        this.state = state;
        this.setUpdateDate(new Date());
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
        this.setUpdateDate(new Date());
    }

    public Date getInitialDateTime() {
        return initialDateTime;
    }

    public void setInitialDateTime(Date initialDateTime) {
        this.initialDateTime = initialDateTime;
        this.setUpdateDate(new Date());
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
        this.setUpdateDate(new Date());
    }

    public Long getIdOfFoodBox() {
        return idOfFoodBox;
    }

    public void setIdOfFoodBox(Long idOfFoodBox) {
        this.idOfFoodBox = idOfFoodBox;
        this.setUpdateDate(new Date());
    }

    public Integer getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(Integer cellNumber) {
        this.cellNumber = cellNumber;
        this.setUpdateDate(new Date());
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
        this.setUpdateDate(new Date());
    }

    public Integer getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(Integer cancelReason) {
        this.cancelReason = cancelReason;
        this.setUpdateDate(new Date());
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
        this.setUpdateDate(new Date());
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
        this.setUpdateDate(new Date());
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
        this.setUpdateDate(new Date());
    }

    public String getIdFoodBoxExternal() {
        return idFoodBoxExternal;
    }

    public void setIdFoodBoxExternal(String idFoodBoxExternal) {
        this.idFoodBoxExternal = idFoodBoxExternal;
        this.setUpdateDate(new Date());
    }

    public Long getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Long orderPrice) {
        this.orderPrice = orderPrice;
        this.setUpdateDate(new Date());
    }

    public Integer getPosted() {
        return posted;
    }

    public void setPosted(Integer posted) {
        this.posted = posted;
        this.setUpdateDate(new Date());
    }
}
