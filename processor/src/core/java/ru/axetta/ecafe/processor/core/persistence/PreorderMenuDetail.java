/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by i.semenov on 12.03.2018.
 */
public class PreorderMenuDetail {
    private Long idOfPreorderMenuDetail;
    private ComplexInfo complexInfo;
    private ComplexInfoDetail complexInfoDetail;
    private Client client;
    private MenuDetail menuDetail;
    private Date preorderDate;
    private Integer amount;
    private Long version;
    private Boolean deletedState;

    public PreorderMenuDetail() {

    }

    public Long getIdOfPreorderMenuDetail() {
        return idOfPreorderMenuDetail;
    }

    public void setIdOfPreorderMenuDetail(Long idOfPreorderMenuDetail) {
        this.idOfPreorderMenuDetail = idOfPreorderMenuDetail;
    }

    public ComplexInfo getComplexInfo() {
        return complexInfo;
    }

    public void setComplexInfo(ComplexInfo complexInfo) {
        this.complexInfo = complexInfo;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public MenuDetail getMenuDetail() {
        return menuDetail;
    }

    public void setMenuDetail(MenuDetail menuDetail) {
        this.menuDetail = menuDetail;
    }

    public Date getPreorderDate() {
        return preorderDate;
    }

    public void setPreorderDate(Date preorderDate) {
        this.preorderDate = preorderDate;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public ComplexInfoDetail getComplexInfoDetail() {
        return complexInfoDetail;
    }

    public void setComplexInfoDetail(ComplexInfoDetail complexInfoDetail) {
        this.complexInfoDetail = complexInfoDetail;
    }
}
