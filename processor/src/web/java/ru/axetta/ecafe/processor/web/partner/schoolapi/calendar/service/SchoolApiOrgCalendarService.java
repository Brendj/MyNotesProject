/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.service;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.dto.CreateOrUpdateOrgCalendarDateRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.dto.CreateOrUpdateOrgCalendarDateResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.dto.DeleteOrgCalendarDateResponse;

public interface SchoolApiOrgCalendarService {
    DeleteOrgCalendarDateResponse deleteOrgCalendarDate(long idOfRecord, long idOfOrgRequester, User user);
    CreateOrUpdateOrgCalendarDateResponse createOrUpdateOrgCalendarDate(CreateOrUpdateOrgCalendarDateRequest createOrUpdateOrgCalendarDateRequest, User user);
}
