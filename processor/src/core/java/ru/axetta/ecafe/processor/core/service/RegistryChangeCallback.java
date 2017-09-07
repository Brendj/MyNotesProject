/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 25.03.14
 * Time: 19:04
 * To change this template use File | Settings | File Templates.
 */
public class RegistryChangeCallback {

    protected Long idOfRegistryChange;
    protected String error;

    public RegistryChangeCallback() {
        idOfRegistryChange = null;
        error = null;
    }

    public RegistryChangeCallback(Long idOfRegistryChange, String error) {
        this.idOfRegistryChange = idOfRegistryChange;
        this.error = error;
    }

    public Long getIdOfRegistryChange() {
        return idOfRegistryChange;
    }

    public void setIdOfRegistryChange(Long idOfRegistryChange) {
        this.idOfRegistryChange = idOfRegistryChange;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
