/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto;

import java.io.Serializable;

public class MoveClientResult implements Serializable {
    private long idOfClient;
    private int result;
    private String error;

    private MoveClientResult(long idOfClient){
        this.idOfClient = idOfClient;
        this.result = 0;
    }

    private MoveClientResult(long idOfClient, String error, int code){
        this.idOfClient = idOfClient;
        this.error = error;
        this.result = code;
    }

    public static MoveClientResult success(long idOfClient){
        return new MoveClientResult(idOfClient);
    }

    public static MoveClientResult error(long idOfClient, String error) {
        return new MoveClientResult(idOfClient, error, 1);
    }

    public long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public int isResult() {
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
