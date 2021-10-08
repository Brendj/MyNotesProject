/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.AplicationForFoodConfirmDocumentsResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.ApplicationForFoodConfirmResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.ApplicationForFoodDeclineResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.service.SchoolApiApplicationForFoodService;
import ru.axetta.ecafe.processor.web.partner.schoolapi.service.BaseSchoolApiController;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrors;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;

import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Path(value = "/applicationForFood")
@Controller
public class ApplicationForFoodRestController extends BaseSchoolApiController{

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/confirmDocuments")
    public Response confirmDocuments(@PathParam("id") Long id)
    {
        if (!isWebArmAnyRole()) throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        AplicationForFoodConfirmDocumentsResponse response = getService().confirmDocuments(id, getUser());
        return Response.ok().entity(response).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/decline")
    public Response decline(@PathParam("id") Long id, @QueryParam("docOrderDate") Long docOrderDate, @QueryParam("docOrderId") String docOrderId)
    {
        if (!isWebArmAnyRole()) throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        ApplicationForFoodDeclineResponse response = getService().decline(id, new Date(docOrderDate), docOrderId, getUser());
        return Response.ok().entity(response).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/confirm")
    public Response confirm(@PathParam("id") Long id, @QueryParam("docOrderDate") Long docOrderDate, @QueryParam("docOrderId") String docOrderId, @QueryParam("discountStartDate") Long discountStartDate, @QueryParam("discountEndDate") Long discountEndDate)
    {
        if (!isWebArmAnyRole()) throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        ApplicationForFoodConfirmResponse response = getService().confirm(id, new Date(docOrderDate), docOrderId, new Date(discountStartDate), new Date(discountEndDate), getUser());
        return Response.ok().entity(response).build();
    }

    private SchoolApiApplicationForFoodService getService()
    {
        return RuntimeContext.getAppContext().getBean(SchoolApiApplicationForFoodService.class);
    }
}
