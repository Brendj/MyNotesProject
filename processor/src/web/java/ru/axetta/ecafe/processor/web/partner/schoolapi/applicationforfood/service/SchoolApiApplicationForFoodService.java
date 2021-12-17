/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.service;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.AplicationForFoodConfirmDocumentsResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.ApplicationForFoodConfirmResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.ApplicationForFoodDeclineResponse;

import java.util.Date;

public interface SchoolApiApplicationForFoodService {
    AplicationForFoodConfirmDocumentsResponse confirmDocuments(long id, User user);
    ApplicationForFoodDeclineResponse decline(long id, Date docOrderDate, String docOrderId, User user);
    ApplicationForFoodConfirmResponse confirm(long id, Date docOrderDate, String docOrderId, Date discountStartDate, Date discountEndDate, User user);
}
