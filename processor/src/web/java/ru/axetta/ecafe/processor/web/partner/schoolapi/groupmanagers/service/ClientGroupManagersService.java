/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groupmanagers.service;

import ru.axetta.ecafe.processor.core.persistence.ClientGroupManager;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groupmanagers.dto.ClientGroupManagerDTO;

public interface ClientGroupManagersService {
    ClientGroupManager attachedGroup(ClientGroupManagerDTO groupManager);
    void dettachedGroup(Long idOfClientGroupManager);
}
