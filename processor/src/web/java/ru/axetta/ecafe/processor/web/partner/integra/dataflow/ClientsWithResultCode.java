/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientCreatedFromType;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 26.12.11
 * Time: 11:01
 * To change this template use File | Settings | File Templates.
 */
public class ClientsWithResultCode {
    private Map<Client, ClientCreatedFromType> clients;
    public Long resultCode;
    public String description;

    public Map<Client, ClientCreatedFromType> getClients() {
        return clients;
    }

    public void setClients(Map<Client, ClientCreatedFromType> clients) {
        this.clients = clients;
    }
}
