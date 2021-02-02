/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.service;

import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientUpdateItem;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientsUpdateResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;

import java.util.Collection;

public interface SchoolApiClientsService {
    ClientsUpdateResponse moveClients(Collection<ClientUpdateItem> moveClientToGroups) throws WebApplicationException;

    ClientsUpdateResponse excludeClientsFromPlan(Collection<ClientUpdateItem> updateClients);
}
