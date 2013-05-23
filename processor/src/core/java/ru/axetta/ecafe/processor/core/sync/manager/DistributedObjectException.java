/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.manager;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 10.07.12
 * Time: 13:11
 * To change this template use File | Settings | File Templates.
 */
public class DistributedObjectException extends Exception {

    private String data;

    public DistributedObjectException(String message) {
        super(message);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
