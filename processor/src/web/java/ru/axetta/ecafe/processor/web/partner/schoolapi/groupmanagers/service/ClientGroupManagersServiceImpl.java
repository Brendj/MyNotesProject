/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groupmanagers.service;

import ru.axetta.ecafe.processor.core.persistence.ClientGroupManager;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groupmanagers.dto.ClientGroupManagerDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class ClientGroupManagersServiceImpl implements ClientGroupManagersService {

    @Autowired
    private AttachedGroupCommand attachedGroupCommand;

    @Override
    public ClientGroupManager attachedGroup(ClientGroupManagerDTO groupManager) {
        return attachedGroupCommand.attachedGroup(groupManager);
    }

    @Override
    public void dettachedGroup(Long idOfClientGroupManager) {
        attachedGroupCommand.dettachedGroup(idOfClientGroupManager);
    }
}
