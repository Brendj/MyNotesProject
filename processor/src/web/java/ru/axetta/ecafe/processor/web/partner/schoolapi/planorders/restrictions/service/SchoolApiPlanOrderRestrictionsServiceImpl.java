/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.planorders.restrictions.service;

import ru.axetta.ecafe.processor.core.persistence.PlanOrdersRestriction;
import ru.axetta.ecafe.processor.web.partner.schoolapi.planorders.restrictions.dto.PlanOrderRestrictionDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class SchoolApiPlanOrderRestrictionsServiceImpl implements SchoolApiPlanOrderRestrictionsService {
    @Autowired
    private UpdatePlanOrderRestrictionsCommand updatePlanOrderRestrictionsCommand;

    @Override
    public List<PlanOrdersRestriction> updatePlanOrderRestrictions(Long idOfClient,
            List<PlanOrderRestrictionDTO> restrictions, boolean notified) {
        return updatePlanOrderRestrictionsCommand.updateClientPlanOrderRestrictions(idOfClient, restrictions, notified);
    }

}
