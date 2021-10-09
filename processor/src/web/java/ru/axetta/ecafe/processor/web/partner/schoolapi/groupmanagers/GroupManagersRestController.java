/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groupmanagers;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroupManager;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groupmanagers.dto.ClientGroupManagerDTO;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groupmanagers.service.ClientGroupManagersService;
import ru.axetta.ecafe.processor.web.partner.schoolapi.service.BaseSchoolApiController;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrors;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;

import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Path(value = "/groupmanagers")
@Controller
@ApplicationPath("/school/api/v1")
public class GroupManagersRestController extends BaseSchoolApiController {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response attachedGroups(List<ClientGroupManagerDTO> groupClientManagers) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        List<ClientGroupManager> clientGroupManagers = getService().attachedGroups(groupClientManagers);
        List<ClientGroupManagerDTO> response = ClientGroupManagerDTO.fromCollection(clientGroupManagers);
        return Response.ok().entity(response).build();
    }

    @DELETE
    @Path("/{id}")
    public Response dettachedGroup(@PathParam("id") Long idOfClientGroupManager) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        getService().dettachedGroup(idOfClientGroupManager);
        return Response.ok().build();
    }

    @DELETE
    @Path("")
    public Response dettachedGroups(@QueryParam("id") final List<Long> ids) {
        if (!isWebArmAnyRole()) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        getService().dettachedGroups(ids);
        return Response.ok().build();
    }

    private ClientGroupManagersService getService() {
        return RuntimeContext.getAppContext().getBean(ClientGroupManagersService.class);
    }

}
