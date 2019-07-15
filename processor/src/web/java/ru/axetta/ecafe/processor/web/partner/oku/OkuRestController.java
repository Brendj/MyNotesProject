/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.oku;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.partner.oku.dataflow.ClientData;
import ru.axetta.ecafe.processor.web.partner.oku.dataflow.ErrorResult;
import ru.axetta.ecafe.processor.web.partner.oku.dataflow.IResponseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;

@Path(value = "")
@Controller
public class OkuRestController {

    private Logger logger = LoggerFactory.getLogger(OkuRestController.class);

    @Path(value = "presence")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkClient(@QueryParam(value = "surname") String surname,
            @QueryParam(value = "contract_id") Long contractId) throws Exception {
        try {
            if (surname == null || surname.isEmpty()) {
                throw new IllegalArgumentException("Empty surname field");
            }

            ClientData clientData = RuntimeContext.getAppContext().getBean(OkuDAOService.class)
                    .checkClient(contractId, surname);

            return Response.status(HttpURLConnection.HTTP_OK).entity(clientData).build();
        } catch (IllegalArgumentException e) {
            logger.error("Unable to check client", e);
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorResult.badRequest());
        } catch (Exception e) {
            logger.error("Unable to check client", e);
            return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorResult.notFound());
        }
    }

    private Response generateResponse(Integer responseCode, IResponseEntity entity) {
        return Response.status(responseCode).entity(entity).build();
    }
}
