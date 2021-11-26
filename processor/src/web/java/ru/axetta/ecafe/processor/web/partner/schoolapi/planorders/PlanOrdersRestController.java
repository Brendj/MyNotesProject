/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.planorders;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.axetta.ecafe.processor.core.persistence.PlanOrdersRestriction;
import ru.axetta.ecafe.processor.web.partner.schoolapi.planorders.restrictions.dto.PlanOrderRestrictionDTO;
import ru.axetta.ecafe.processor.web.partner.schoolapi.planorders.restrictions.service.SchoolApiPlanOrderRestrictionsService;
import ru.axetta.ecafe.processor.web.partner.schoolapi.service.BaseSchoolApiController;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrors;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;

import java.util.List;

@RestController
@RequestMapping(value = "/school/api/v1/planorders", produces = "application/json")
public class PlanOrdersRestController extends BaseSchoolApiController {
    private final SchoolApiPlanOrderRestrictionsService service;

    public PlanOrdersRestController(SchoolApiPlanOrderRestrictionsService service) {this.service = service;}

    @PostMapping(value = "/restrictions/client/{id}", consumes = "application/json")
    public ResponseEntity<?> setClientPlanOrderRestrictions(@PathVariable("id") Long idOfClient,
                                                            @RequestParam(value = "notified", defaultValue = "false")
                                                                    Boolean notified,
                                                            List<PlanOrderRestrictionDTO> restrictions) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        List<PlanOrdersRestriction> updatedItems =
                service.updatePlanOrderRestrictions(idOfClient, restrictions, notified);
        List<PlanOrderRestrictionDTO> response = PlanOrderRestrictionDTO.fromList(updatedItems);
        return ResponseEntity.ok().body(response);
    }

}
