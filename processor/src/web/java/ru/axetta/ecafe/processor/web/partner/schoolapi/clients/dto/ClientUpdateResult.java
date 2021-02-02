/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto;

public class ClientUpdateResult {
    private long idOfClient;
    private int result;
    private String error;

    private ClientUpdateResult(long idOfClient){
        this.idOfClient = idOfClient;
        this.result = 0;
    }

    private ClientUpdateResult(long idOfClient, String error, int code){
        this.idOfClient = idOfClient;
        this.error = error;
        this.result = code;
    }

    public static ClientUpdateResult success(long idOfClient){
        return new ClientUpdateResult(idOfClient);
    }

    public static ClientUpdateResult error(long idOfClient, String error) {
        return new ClientUpdateResult(idOfClient, error, 1);
    }

    public long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
