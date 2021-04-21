/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.planorders.restrictions.service;

import ru.axetta.ecafe.processor.core.persistence.PlanOrdersRestriction;
import ru.axetta.ecafe.processor.web.partner.schoolapi.planorders.restrictions.dto.PlanOrderRestrictionDTO;

import java.util.List;

public interface SchoolApiPlanOrderRestrictionsService {
    List<PlanOrdersRestriction> updatePlanOrderRestrictions(Long idOfClient, List<PlanOrderRestrictionDTO> restrictions, boolean notified);
}
