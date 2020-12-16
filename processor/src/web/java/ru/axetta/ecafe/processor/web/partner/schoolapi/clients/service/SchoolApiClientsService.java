/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.service;

import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.MoveClientToGroup;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.MoveClientsResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;

import java.util.Collection;

public interface SchoolApiClientsService {
    MoveClientsResponse moveClients(Collection<MoveClientToGroup> moveClientToGroups) throws WebApplicationException;
}
