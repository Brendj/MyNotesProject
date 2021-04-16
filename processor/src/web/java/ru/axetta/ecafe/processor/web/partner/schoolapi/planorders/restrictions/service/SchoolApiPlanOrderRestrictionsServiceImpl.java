/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.planorders.restrictions.service;

import ru.axetta.ecafe.processor.web.partner.schoolapi.planorders.restrictions.dto.PlanOrderRestrictionDTO;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class SchoolApiPlanOrderRestrictionsServiceImpl implements SchoolApiPlanOrderRestrictionsService {

    @Override
    public List<PlanOrderRestrictionDTO> updatePlanOrderRestrictions(Long idOfClient,
            List<PlanOrderRestrictionDTO> restrictions) {

        return new ArrayList<>();
    }

    @Override
    public List<PlanOrderRestrictionDTO> deletePlanOrderRestrictions(Long idOfClient,
            List<PlanOrderRestrictionDTO> restrictions) {
        return new ArrayList<>();
    }
}
