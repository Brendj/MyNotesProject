/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response;

import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.DTO.SetOrderClientDTO;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class ResponseSetOrder extends Result {
    @JsonProperty("Clients")
    private List<SetOrderClientDTO> clients;

    public ResponseSetOrder(List<SetOrderClientDTO> clients){
        super(0, "OK");
        this.clients = clients;
    }

    public List<SetOrderClientDTO> getClients() {
        return clients;
    }

    public void setClients(List<SetOrderClientDTO> clients) {
        this.clients = clients;
    }
}
