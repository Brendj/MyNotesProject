/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 08.08.16
 * Time: 10:37
 */

public class ClientPhotoChangeResult {
    private long clientId;
    private int state;
    private int src;

    public ClientPhotoChangeResult() {
    }

    public ClientPhotoChangeResult(long clientId, int state, int src) {
        this.clientId = clientId;
        this.state = state;
        this.src = src;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }
}
