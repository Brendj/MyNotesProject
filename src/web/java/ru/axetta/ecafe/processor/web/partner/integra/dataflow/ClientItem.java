/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 26.12.11
 * Time: 10:23
 * To change this template use File | Settings | File Templates.
 */
public class ClientItem {
    private Long contractId;
    private String san;

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
}
