/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto;

import java.util.Collection;

public class ClientsUpdateRequest {
    private Collection<ClientUpdateItem> updateClients;

    public Collection<ClientUpdateItem> getUpdateClients() {
        return updateClients;
    }

    public void setUpdateClients(Collection<ClientUpdateItem> updateClients) {
        this.updateClients = updateClients;
    }

}
