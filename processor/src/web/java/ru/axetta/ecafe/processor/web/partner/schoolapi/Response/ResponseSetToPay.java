/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response;

import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.DTO.SetToPayClientDTO;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class ResponseSetToPay extends Result {
    @JsonProperty("Clients")
    private List<SetToPayClientDTO> clients;

    public ResponseSetToPay(List<SetToPayClientDTO> clients){
        super(0,"OK");
        this.clients = clients;
    }

    public List<SetToPayClientDTO> getClients() {
        return clients;
    }

    public void setClients(List<SetToPayClientDTO> clients) {
        this.clients = clients;
    }
}
