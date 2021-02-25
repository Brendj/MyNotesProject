/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.guardians;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto.CreateOrUpdateGuardianRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto.CreateOrUpdateGuardianResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto.DeleteGuardianResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.service.SchoolApiGuardiansService;
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

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Path(value = "/guardians")
@Controller
public class GuardiansRestController {

    @DELETE
    //@Consumes(MediaType.APPLICATION_JSON)
    @Path("/{idOfRecord}")
    public Response deleteGuardian(@PathParam("idOfRecord") Long idOfRecord)
    {
        if (hasAnyRole(User.DefaultRole.ADMIN.name()) == false) throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetailsImpl principal = (JwtUserDetailsImpl) authentication.getPrincipal();
        User user = null;
        DeleteGuardianResponse response = getService().deleteGuardian(idOfRecord, user);
        return Response.ok().entity(response).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createGuardian(CreateOrUpdateGuardianRequest request)
    {
        if (!hasAnyRole(User.DefaultRole.ADMIN.name(), User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.name())) throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetailsImpl principal = (JwtUserDetailsImpl) authentication.getPrincipal();
        User user = null;
        CreateOrUpdateGuardianResponse response = getService().createGuardian(request, user);
        return Response.ok().entity(response).build();
    }

    private SchoolApiGuardiansService getService()
    {
        return RuntimeContext.getAppContext().getBean(SchoolApiGuardiansService.class);
    }

    private boolean hasAnyRole(String... role)
    {
        AuthorityUtils authorityUtils = RuntimeContext.getAppContext().getBean(AuthorityUtils.class);
        return authorityUtils.hasAnyRole(role);
    }
}
