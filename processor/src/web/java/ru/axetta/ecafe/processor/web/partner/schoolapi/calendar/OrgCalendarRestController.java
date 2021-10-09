/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.calendar;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.dto.CreateOrUpdateOrgCalendarDateRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.dto.CreateOrUpdateOrgCalendarDateResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.dto.DeleteOrgCalendarDateResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.service.SchoolApiOrgCalendarService;
import ru.axetta.ecafe.processor.web.partner.schoolapi.service.BaseSchoolApiController;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrors;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;

import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Path(value = "/orgCalendar")
@Controller
@ApplicationPath("/school/api/v1")
public class OrgCalendarRestController extends BaseSchoolApiController {
    @DELETE
    @Path("/{idOfRecord}/{idOfOrgRequester}")
    public Response deleteOrgCalendarDate(@PathParam("idOfRecord") long idOfRecord, @PathParam("idOfOrgRequester") long idOfOrgRequester) {
        if (!isWebArmAnyRole()) throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        DeleteOrgCalendarDateResponse response = getService().deleteOrgCalendarDate(idOfRecord, idOfOrgRequester, getUser());
        return Response.ok().entity(response).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrUpdateOrgCalendarDate(CreateOrUpdateOrgCalendarDateRequest request) {
        if (!isWebArmAnyRole()) throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        CreateOrUpdateOrgCalendarDateResponse response = getService().createOrUpdateOrgCalendarDate(request, getUser());
        return Response.ok().entity(response).build();
    }

    private SchoolApiOrgCalendarService getService()
    {
        return RuntimeContext.getAppContext().getBean(SchoolApiOrgCalendarService.class);
    }
}
