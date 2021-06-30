/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.service;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.AplicationForFoodConfirmDocumentsResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.ApplicationForFoodConfirmResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.ApplicationForFoodDeclineResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SchoolApiApplicationForFoodServiceImpl implements SchoolApiApplicationForFoodService {

    @Autowired
    private ApplicationForFoodConfirmDocumentsCommand applicationForFoodConfirmDocumentsCommand;

    @Autowired
    private ApplicationForFoodDeclineCommand applicationForFoodDeclineCommand;

    @Autowired
    private ApplicationForFoodConfirmCommand applicationForFoodConfirmCommand;

    @Override
    public AplicationForFoodConfirmDocumentsResponse confirmDocuments(long id, User user)
    {
        return applicationForFoodConfirmDocumentsCommand.confirmDocuments(id, user);
    }

    @Override
    public ApplicationForFoodDeclineResponse decline(long id, Date docOrderDate, String docOrderId, User user) {
        return applicationForFoodDeclineCommand.decline(id, docOrderDate, docOrderId, user);
    }

    @Override
    public ApplicationForFoodConfirmResponse confirm(long id, Date docOrderDate, String docOrderId,
            Date discountStartDate, Date discountEndDate, User user) {
        return applicationForFoodConfirmCommand.confirm(id, docOrderDate, docOrderId, discountStartDate, discountEndDate, user);
    }
}
