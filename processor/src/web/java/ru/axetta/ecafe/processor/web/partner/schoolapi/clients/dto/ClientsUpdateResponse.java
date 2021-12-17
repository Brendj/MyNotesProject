/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto;

import java.util.ArrayList;
import java.util.List;

public class ClientsUpdateResponse {
    private List<ClientUpdateResult> clients = new ArrayList<>();

    public List<ClientUpdateResult> getClients() {
        return clients;
    }

    public void setClients(List<ClientUpdateResult> clients) {
        this.clients = clients;
    }
}
