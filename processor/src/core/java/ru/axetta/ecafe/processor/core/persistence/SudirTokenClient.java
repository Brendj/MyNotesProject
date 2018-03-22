/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by i.semenov on 01.03.2018.
 */
public class SudirTokenClient {
    private Long idOfSudirTokenClient;
    private String access_token;
    private Long contractId;

    public SudirTokenClient() {

    }

    public SudirTokenClient(Long contractId, String access_token) {
        this.contractId = contractId;
        this.access_token = access_token;
    }

    public Long getIdOfSudirTokenClient() {
        return idOfSudirTokenClient;
    }

    public void setIdOfSudirTokenClient(Long idOfSudirTokenClient) {
        this.idOfSudirTokenClient = idOfSudirTokenClient;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }
}
