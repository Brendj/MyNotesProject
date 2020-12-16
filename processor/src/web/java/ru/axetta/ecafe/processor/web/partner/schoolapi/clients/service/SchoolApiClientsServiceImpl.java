/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.service;

import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.MoveClientToGroup;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.MoveClientsResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SchoolApiClientsServiceImpl implements SchoolApiClientsService {
    @Autowired
    private MoveClientsCommand moveClientsCommand;

    @Override
    public MoveClientsResponse moveClients(Collection<MoveClientToGroup> moveClientToGroups) throws
            WebApplicationException {
        return moveClientsCommand.moveClients(moveClientToGroups);
    }

}
