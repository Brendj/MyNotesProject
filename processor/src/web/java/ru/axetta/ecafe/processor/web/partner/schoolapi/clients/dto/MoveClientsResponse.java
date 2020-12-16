/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto;

import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.Result;

import java.util.ArrayList;
import java.util.List;

public class MoveClientsResponse extends Result {

    private List<MoveClientResult> clients;

    public MoveClientsResponse() {
        this.clients = new ArrayList<>();
    }

    public List<MoveClientResult> getClients() {
        return clients;
    }

    public void setClients(List<MoveClientResult> clients) {
        this.clients = clients;
    }
}
