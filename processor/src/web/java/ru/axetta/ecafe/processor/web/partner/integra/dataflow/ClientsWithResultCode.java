/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 26.12.11
 * Time: 11:01
 * To change this template use File | Settings | File Templates.
 */
public class ClientsWithResultCode {
    private Map<Client, ClientWithAddInfo> clients;
    public Long resultCode;
    public String description;

    public Map<Client, ClientWithAddInfo> getClients() {
        return clients;
    }

    public void setClients(Map<Client, ClientWithAddInfo> clients) {
        this.clients = clients;
    }
}
