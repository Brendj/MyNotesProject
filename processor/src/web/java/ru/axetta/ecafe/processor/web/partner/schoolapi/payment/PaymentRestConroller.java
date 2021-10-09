/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.payment;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.partner.schoolapi.payment.dto.PaymentDTO;
import ru.axetta.ecafe.processor.web.partner.schoolapi.payment.dto.ResPaymentDTO;
import ru.axetta.ecafe.processor.web.partner.schoolapi.payment.service.SchoolApiPaymentsService;
import ru.axetta.ecafe.processor.web.partner.schoolapi.service.BaseSchoolApiController;

import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Path(value = "/payments")
@Controller
@ApplicationPath("/school/api/v1")
public class PaymentRestConroller extends BaseSchoolApiController {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/orgs/{id}")
    public Response registerPayments(@PathParam("id") Long idOfOrg, List<PaymentDTO> payments) {
/*        if (!hasAnyRole(User.DefaultRole.ADMIN.name())) {
            throw new JwtAuthenticationException(JwtAuthenticationErrors.USER_ROLE_NOT_ALLOWED);
        }*/
        List<ResPaymentDTO> response = getService().registerPayments(idOfOrg, payments, getUser());
        return Response.ok().entity(response).build();
    }

    private SchoolApiPaymentsService getService() {
        return RuntimeContext.getAppContext().getBean(SchoolApiPaymentsService.class);
    }


}
