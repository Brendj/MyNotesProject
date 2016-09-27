/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by i.semenov on 23.09.2016.
 */
@Entity
public class ItemListByGuardMobile {
    private Long idOfClient;
    private Long contractId;
    private String san;
    private Integer disabled;

    public ItemListByGuardMobile() {

    }
        /*public ItemListByGuardMobile(Long idOfClient, Long contractId, String san, Boolean disabled) {
            this.idOfClient = idOfClient;
            this.contractId = contractId;
            this.san = san;
            this.disabled = disabled;
        }*/
    @Id
    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getSan() {
        return san;
    }

    public void setSan(String san) {
        this.san = san;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    @Override
    public int hashCode() {
        return idOfClient.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        return this.idOfClient.equals(((ItemListByGuardMobile)o).getIdOfClient());
    }
}
