/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.AplicationForFoodConfirmDocumentsResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.ApplicationForFoodConfirmResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto.ApplicationForFoodDeclineResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.service.SchoolApiApplicationForFoodService;
import ru.axetta.ecafe.processor.web.partner.schoolapi.util.AuthorityUtils;
import ru.axetta.ecafe.processor.web.token.security.service.JwtUserDetailsImpl;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrors;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Path(value = "/applicationForFood")
@Controller
public class ApplicationForFoodRestController {

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/confirmDocuments")
    public Response confirmDocuments(@PathParam("id") Long id)
    {
        if (!hasAnyRole(User.DefaultRole.ADMIN.name(), User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.name())) throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetailsImpl principal = (JwtUserDetailsImpl) authentication.getPrincipal();
        User user = null;
        AplicationForFoodConfirmDocumentsResponse response = getService().confirmDocuments(id, user);
        return Response.ok().entity(response).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/decline")
    public Response decline(@PathParam("id") Long id, @QueryParam("docOrderDate") Long docOrderDate, @QueryParam("docOrderId") String docOrderId)
    {
        if (!hasAnyRole(User.DefaultRole.ADMIN.name(), User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.name())) throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetailsImpl principal = (JwtUserDetailsImpl) authentication.getPrincipal();
        User user = null;
        ApplicationForFoodDeclineResponse response = getService().decline(id, new Date(docOrderDate), docOrderId, user);
        return Response.ok().entity(response).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/confirm")
    public Response confirm(@PathParam("id") Long id, @QueryParam("docOrderDate") Long docOrderDate, @QueryParam("docOrderId") String docOrderId, @QueryParam("discountStartDate") Long discountStartDate, @QueryParam("discountEndDate") Long discountEndDate)
    {
        if (!hasAnyRole(User.DefaultRole.ADMIN.name(), User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.name())) throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetailsImpl principal = (JwtUserDetailsImpl) authentication.getPrincipal();
        User user = null;
        ApplicationForFoodConfirmResponse response = getService().confirm(id, new Date(docOrderDate), docOrderId, new Date(discountStartDate), new Date(discountEndDate), user);
        return Response.ok().entity(response).build();
    }

    private SchoolApiApplicationForFoodService getService()
    {
        return RuntimeContext.getAppContext().getBean(SchoolApiApplicationForFoodService.class);
    }

    private boolean hasAnyRole(String... role)
    {
        AuthorityUtils authorityUtils = RuntimeContext.getAppContext().getBean(AuthorityUtils.class);
        return authorityUtils.hasAnyRole(role);
    }
}
