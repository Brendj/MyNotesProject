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
    private Client client;
    private Date preorderDate;
    private Integer amount;
    private Boolean deletedState;
    private String guid;
    private Long armIdOfMenu;
    //private Long idOfPreorderComplex;
    private PreorderComplex preorderComplex;

    public PreorderMenuDetail() {

    }

    public Long getIdOfPreorderMenuDetail() {
        return idOfPreorderMenuDetail;
    }

    public void setIdOfPreorderMenuDetail(Long idOfPreorderMenuDetail) {
        this.idOfPreorderMenuDetail = idOfPreorderMenuDetail;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getArmIdOfMenu() {
        return armIdOfMenu;
    }

    public void setArmIdOfMenu(Long armIdOfMenu) {
        this.armIdOfMenu = armIdOfMenu;
    }

    public PreorderComplex getPreorderComplex() {
        return preorderComplex;
    }

    public void setPreorderComplex(PreorderComplex preorderComplex) {
        this.preorderComplex = preorderComplex;
    }

    /*public Long getIdOfPreorderComplex() {
        return idOfPreorderComplex;
    }

    public void setIdOfPreorderComplex(Long idOfPreorderComplex) {
        this.idOfPreorderComplex = idOfPreorderComplex;
    }*/
}
