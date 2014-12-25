/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 24.12.14
 * Time: 16:39
 */

public class GroupControlSubscriptionsItem {

    public String result;
    public Long contractId;

    public GroupControlSubscriptionsItem() {
    }

    public GroupControlSubscriptionsItem(Long contractId, String result) {
        this.contractId = contractId;
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }
}
