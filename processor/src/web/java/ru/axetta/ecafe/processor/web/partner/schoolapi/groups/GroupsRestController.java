/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groups;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.GroupClientsUpdateRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.GroupClientsUpdateResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.MiddleGroupRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.MiddleGroupResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.service.SchoolApiClientGroupsService;
import ru.axetta.ecafe.processor.web.partner.schoolapi.util.AuthorityUtils;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrors;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;

import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
@Path(value = "/groups")
@Controller
public class GroupsRestController {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value = "/{id}/org/{orgId}/subgroups")
    public Response createMiddleGroup(@PathParam("id") Long id, @PathParam("orgId") Long orgId,
            MiddleGroupRequest request) {
        if (!hasAnyRole(User.DefaultRole.ADMIN.name(), User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.name())) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        MiddleGroupResponse response = getService().createMiddleGroup(id, orgId, request, getUser());
        return Response.ok().entity(response).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value = "/{id}/org/{orgId}/subgroups")
    public Response updateMiddleGroup(@PathParam("id") Long id, @PathParam("orgId") Long orgId,
            MiddleGroupRequest request) {
        if (!hasAnyRole(User.DefaultRole.ADMIN.name(), User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.name())) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        MiddleGroupResponse response = getService().updateMiddleGroup(id, orgId, request, getUser());
        return Response.ok().entity(response).build();
    }

    @DELETE
    @Path("/subgroups/{id}")
    public Response deleteMiddleGroup(@PathParam("id") Long id){
        if (!hasAnyRole(User.DefaultRole.ADMIN.name(), User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.name())) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        MiddleGroupResponse response = getService().deleteMiddleGroup(id);
        return Response.ok().entity(response).build();
    }


    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value = "/{id}/org/{orgId}")
    public Response updateGroup(@PathParam("id") Long id, @PathParam("orgId") Long orgId, GroupClientsUpdateRequest request) {
        if (!hasAnyRole(User.DefaultRole.ADMIN.name(), User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.name())) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }
        GroupClientsUpdateResponse response = getService().updateGroup(id, orgId, request, getUser());
        return Response.ok().entity(response).build();
    }

    private User getUser() {
        AuthorityUtils authorityUtils = RuntimeContext.getAppContext().getBean(AuthorityUtils.class);
        return authorityUtils.findCurrentUser();
    }

    private SchoolApiClientGroupsService getService() {
        return RuntimeContext.getAppContext().getBean(SchoolApiClientGroupsService.class);
    }

    private boolean hasAnyRole(String... role) {
        AuthorityUtils authorityUtils = RuntimeContext.getAppContext().getBean(AuthorityUtils.class);
        return authorityUtils.hasAnyRole(role);
    }

}
