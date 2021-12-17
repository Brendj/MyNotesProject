/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.service;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.dto.CreateOrUpdateOrgCalendarDateRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.dto.CreateOrUpdateOrgCalendarDateResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.dto.DeleteOrgCalendarDateResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SchoolApiOrgCalendarServiceImpl implements SchoolApiOrgCalendarService {

    @Autowired
    private DeleteOrgCalendarDateCommand deleteOrgCalendarDateCommand;

    @Autowired
    private CreateOrUpdateOrgCalendarDateCommand createOrUpdateOrgCalendarDateCommand;

    @Override
    public DeleteOrgCalendarDateResponse deleteOrgCalendarDate(long idOfRecord, long idOfOrgRequester, User user)
    {
        return deleteOrgCalendarDateCommand.deleteOrgCalendarDate(idOfRecord, idOfOrgRequester, user);
    }

    @Override
    public CreateOrUpdateOrgCalendarDateResponse createOrUpdateOrgCalendarDate(CreateOrUpdateOrgCalendarDateRequest createOrUpdateOrgCalendarDateRequest, User user) {
        return createOrUpdateOrgCalendarDateCommand.createorUpdateOrgCalendarDate(createOrUpdateOrgCalendarDateRequest, user);
    }
}
