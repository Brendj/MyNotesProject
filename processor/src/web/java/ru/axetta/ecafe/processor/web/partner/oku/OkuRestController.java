/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.oku;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.partner.oku.dataflow.ClientData;
import ru.axetta.ecafe.processor.web.partner.oku.dataflow.ErrorResult;
import ru.axetta.ecafe.processor.web.partner.oku.dataflow.IResponseEntity;
import ru.axetta.ecafe.processor.web.partner.oku.dataflow.Order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

@Path(value = "")
@Controller
public class OkuRestController {

    private Logger logger = LoggerFactory.getLogger(OkuRestController.class);

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy'T'HH:mm:ss.SSSZZZ");

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
            RuntimeContext.getAppContext().getBean(OkuDAOService.class).setClientAsUserOP(contractId);

            return Response.status(HttpURLConnection.HTTP_OK).entity(clientData).build();
        } catch (IllegalArgumentException e) {
            logger.error("Unable to check client", e);
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorResult.badRequest());
        } catch (Exception e) {
            logger.error("Unable to check client", e);
            return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorResult.notFound());
        }
    }

    @Path(value = "getPurchases")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPurchases(@QueryParam(value = "contract_id") Long contractId,
            @QueryParam(value = "ordered_from") String orderedFromString) {
        try {
            if (!RuntimeContext.getAppContext().getBean(OkuDAOService.class).checkClientByContractId(contractId)) {
                logger.error("Unable to check client");
                return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorResult.notFound());
            }

            Date orderedFromDate = dateFormat.parse(orderedFromString);

            Collection<Order> orderList = RuntimeContext.getAppContext().getBean(OkuDAOService.class)
                    .getOrdersByContractIdFromDate(contractId, orderedFromDate);

            return Response.status(HttpURLConnection.HTTP_OK).entity(orderList).build();
        } catch (ParseException e) {
            logger.error("Unable to parse ordered_from", e);
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorResult.badRequest());
        } catch (NoResultException e) {
            logger.error("Unable to check client", e);
            return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorResult.notFound());
        }
    }

    private Response generateResponse(Integer responseCode, IResponseEntity entity) {
        return Response.status(responseCode).entity(entity).build();
    }
}
