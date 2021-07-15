/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientUpdateItem;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientUpdateResult;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientsUpdateRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientsUpdateResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.service.SchoolApiClientsService;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;
import ru.axetta.ecafe.processor.web.partner.schoolapi.service.BaseSchoolApiController;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrors;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;

import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Path(value = "/clients")
@Controller
public class ClientsRestController extends BaseSchoolApiController {

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response updateClient(@PathParam("id") Long idOfClient, ClientUpdateItem request) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        ClientUpdateResult response = getService().updateClient(idOfClient, request, getUser());
        return Response.ok().entity(response).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value = "/move")
    public Response moveClients(ClientsUpdateRequest moveClientsRequest) throws WebApplicationException {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        ClientsUpdateResponse response = getService().moveClients(moveClientsRequest.getUpdateClients(), getUser());
        return Response.ok().entity(response).build();
    }


    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value = "/plan/exclude")
    public Response planExclude(ClientsUpdateRequest request) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        ClientsUpdateResponse response = getService().excludeClientsFromPlan(request.getUpdateClients());
        return Response.ok().entity(response).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value = "/discounts")
    public Response updateDiscounts(ClientsUpdateRequest request) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        ClientsUpdateResponse response = getService().updateClientsDiscounts(request.getUpdateClients(), getUser());
        return Response.ok().entity(response).build();
    }

    private SchoolApiClientsService getService() {
        return RuntimeContext.getAppContext().getBean(SchoolApiClientsService.class);
    }

}
