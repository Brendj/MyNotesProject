/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 26.12.11
 * Time: 11:01
 * To change this template use File | Settings | File Templates.
 */
public class ClientsData {
    public Long resultCode;
    public String description;

    private ClientList clientList;

    public ClientList getClientList() {
        return clientList;
    }

    public void setClientList(ClientList clientList) {
        this.clientList = clientList;
    }
}
