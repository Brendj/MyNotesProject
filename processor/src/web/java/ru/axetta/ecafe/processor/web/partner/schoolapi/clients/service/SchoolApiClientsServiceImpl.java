/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.service;

import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientUpdateItem;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientsUpdateResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SchoolApiClientsServiceImpl implements SchoolApiClientsService {
    @Autowired
    private MoveClientsCommand moveClientsCommand;
    @Autowired
    private ExcludeFromPlanCommand excludeFromPlanCommand;

    @Override
    public ClientsUpdateResponse moveClients(Collection<ClientUpdateItem> moveClientToGroups) throws WebApplicationException {
        return moveClientsCommand.moveClients(moveClientToGroups);
    }

    @Override
    public ClientsUpdateResponse excludeClientsFromPlan(Collection<ClientUpdateItem> updateClients) throws WebApplicationException {
        return excludeFromPlanCommand.excludeClients(updateClients);
    }

}
