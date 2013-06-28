/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.06.13
 * Time: 13:01
 * To change this template use File | Settings | File Templates.
 */
public class RegisterClientResult {

    protected Long idOfClient;
    protected int recId;
    protected boolean success;
    protected String error;

    protected RegisterClientResult() {}

    public RegisterClientResult(Long idOfClient, int recId, boolean success) {
        this.idOfClient = idOfClient;
        this.recId = recId;
        this.success = success;
        this.error = null;
    }

    public RegisterClientResult(Long idOfClient, int recId, boolean success, String error) {
        this.idOfClient = idOfClient;
        this.recId = recId;
        this.success = success;
        this.error = error;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public int getRecId() {
        return recId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

}
