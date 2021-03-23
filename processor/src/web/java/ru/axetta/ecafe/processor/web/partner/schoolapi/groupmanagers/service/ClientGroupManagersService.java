/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groupmanagers.service;

import ru.axetta.ecafe.processor.core.persistence.ClientGroupManager;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groupmanagers.dto.ClientGroupManagerDTO;

import java.util.List;

public interface ClientGroupManagersService {
    ClientGroupManager attachedGroup(ClientGroupManagerDTO groupManager);
    List<ClientGroupManager> attachedGroups(List<ClientGroupManagerDTO> groupManagers);
    void dettachedGroup(Long idOfClientGroupManager);
}
