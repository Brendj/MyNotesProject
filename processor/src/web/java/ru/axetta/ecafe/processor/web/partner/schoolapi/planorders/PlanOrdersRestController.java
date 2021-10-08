/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.planorders;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.PlanOrdersRestriction;
import ru.axetta.ecafe.processor.web.partner.schoolapi.planorders.restrictions.dto.PlanOrderRestrictionDTO;
import ru.axetta.ecafe.processor.web.partner.schoolapi.planorders.restrictions.service.SchoolApiPlanOrderRestrictionsService;
import ru.axetta.ecafe.processor.web.partner.schoolapi.service.BaseSchoolApiController;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrors;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;

import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Path(value = "/planorders")
@Controller
public class PlanOrdersRestController extends BaseSchoolApiController {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/restrictions/client/{id}")
    public Response setClientPlanOrderRestrictions(@PathParam("id") Long idOfClient,
            @QueryParam(value = "notified") @DefaultValue("false") Boolean notified,
            List<PlanOrderRestrictionDTO> restrictions) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        List<PlanOrdersRestriction> updatedItems = getService()
                .updatePlanOrderRestrictions(idOfClient, restrictions, notified);
        List<PlanOrderRestrictionDTO> response = PlanOrderRestrictionDTO.fromList(updatedItems);
        return Response.ok().entity(response).build();
    }

    private SchoolApiPlanOrderRestrictionsService getService() {
        return RuntimeContext.getAppContext().getBean(SchoolApiPlanOrderRestrictionsService.class);
    }


}
