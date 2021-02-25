/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.service;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto.CreateOrUpdateGuardianRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto.CreateOrUpdateGuardianResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto.DeleteGuardianResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SchoolApiGuardianServiceImpl implements SchoolApiGuardiansService
{
    @Autowired
    private DeleteGuardianCommand deleteGuardianCommand;

    @Autowired
    private CreateOrUpdateGuardianCommand createGuardianCommand;

    @Override
    public DeleteGuardianResponse deleteGuardian(long recordId, User user)
    {
        return deleteGuardianCommand.deleteGuardian(recordId, user);
    }

    @Override
    public CreateOrUpdateGuardianResponse createGuardian(CreateOrUpdateGuardianRequest createGuardianRequest, User user)
    {
        return createGuardianCommand.createGuardian(createGuardianRequest, user);
    }
}
