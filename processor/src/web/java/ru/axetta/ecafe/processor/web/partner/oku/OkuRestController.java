/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.oku;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.partner.oku.dataflow.*;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Path(value = "")
@Controller
@ApplicationPath("/assessment/")
public class OkuRestController extends Application {

    private Logger logger = LoggerFactory.getLogger(OkuRestController.class);

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy'T'HH:mm:ss.SSSZZZ");

    @Path(value = "presence")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkClient(@QueryParam(value = "surname") String surname,
            @QueryParam(value = "contract_id") Long contractId) throws Exception {
        try {
            checkParameter("surname", surname);
            checkParameter("contract_id", contractId);

            ClientData clientData = RuntimeContext.getAppContext().getBean(OkuDAOService.class)
                    .checkClient(contractId, surname);
            RuntimeContext.getAppContext().getBean(OkuDAOService.class).setClientAsUserOP(contractId);

            return generateResponse(HttpURLConnection.HTTP_OK, clientData);
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
            checkParameter("contract_id", contractId);
            checkParameter("ordered_from", orderedFromString);

            if (!RuntimeContext.getAppContext().getBean(OkuDAOService.class).checkClientByContractId(contractId)) {
                logger.error("Unable to check client");
                return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorResult.notFound());
            }

            Date orderedFromDate = dateFormat.parse(orderedFromString);

            Collection<Order> orderList = RuntimeContext.getAppContext().getBean(OkuDAOService.class)
                    .getOrdersByContractIdFromDate(contractId, orderedFromDate);

            return Response.status(HttpURLConnection.HTTP_OK).entity(orderList).build();
        } catch (IllegalArgumentException e) {
            logger.error("Couldn't find all parameters", e);
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorResult.badRequest());
        } catch (ParseException e) {
            logger.error("Unable to parse ordered_from", e);
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorResult.badRequest());
        } catch (NoResultException e) {
            logger.error("Unable to check client", e);
            return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorResult.notFound());
        }
    }

    @Path(value = "getPurchasesAll")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPurchasesAll(@QueryParam(value = "ordered_from") String orderedFromString,
            @QueryParam(value = "ordered_to") String orderedToString, @QueryParam(value = "limit") Integer limit,
            @QueryParam(value = "offset") Integer offset) {
        try {
            checkParameter("ordered_from", orderedFromString);
            checkParameter("ordered_to", orderedToString);
            checkParameter("limit", limit);
            checkParameter("offset", offset);

            Date orderedFromDate = dateFormat.parse(orderedFromString);
            Date orderedToDate = dateFormat.parse(orderedToString);

            Collection<Order> orderList = RuntimeContext.getAppContext().getBean(OkuDAOService.class)
                    .getOrders(orderedFromDate, orderedToDate, limit, offset);

            return Response.status(HttpURLConnection.HTTP_OK).entity(orderList).build();
        } catch (IllegalArgumentException e) {
            logger.error("Couldn't find all parameters", e);
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorResult.badRequest());
        } catch (ParseException e) {
            logger.error("Unable to parse dates", e);
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorResult.badRequest());
        } catch (NoResultException e) {
            logger.error("Unable to check client", e);
            return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorResult.notFound());
        }
    }

    @Path(value = "getOrganizationInfo")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrganizationInfo(@QueryParam(value = "organization_id") Long idOfOrg) {
        try {
            checkParameter("organization_id", idOfOrg);

            List<Organization> organizationList = RuntimeContext.getAppContext().getBean(OkuDAOService.class)
                    .getOrganizationInfo(idOfOrg);

            return Response.status(HttpURLConnection.HTTP_OK).entity(organizationList).build();
        } catch (IllegalArgumentException e) {
            logger.error("Couldn't find all parameters", e);
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorResult.badRequest());
        } catch (NoResultException e) {
            logger.error("Unable to find organization", e);
            return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorResult.notFound());
        }
    }

    @Path(value = "getOrganizations")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrganizations(@QueryParam(value = "updated_from") String updatedFromString, @QueryParam(value = "limit") Integer limit,
            @QueryParam(value = "offset") Integer offset) {
        try {
            checkParameter("updated_from", updatedFromString);
            checkParameter("limit", limit);
            checkParameter("offset", offset);

            Date updatedFromDate = dateFormat.parse(updatedFromString);

            List<Organization> organizationList = RuntimeContext.getAppContext().getBean(OkuDAOService.class)
                    .getOrganizationInfoList(updatedFromDate, limit, offset);

            return Response.status(HttpURLConnection.HTTP_OK).entity(organizationList).build();
        } catch (IllegalArgumentException e) {
            logger.error("Couldn't find all parameters", e);
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorResult.badRequest());
        } catch (ParseException e) {
            logger.error("Unable to parse date", e);
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorResult.badRequest());
        } catch (NoResultException e) {
            logger.error("Unable to find organizations", e);
            return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorResult.notFound());
        }
    }

    private void checkParameter(String parameterName, String parameter) throws IllegalArgumentException {
        if (StringUtils.isEmpty(parameter)) {
            throw new IllegalArgumentException(String.format("Parameter \"%s\" couldn't be null", parameterName));
        }
    }

    private <T extends Number> void checkParameter(String parameterName, T parameter) throws IllegalArgumentException {
        if (null == parameter) {
            throw new IllegalArgumentException(String.format("Parameter \"%s\" couldn't be null", parameterName));
        }
    }

    private Response generateResponse(Integer responseCode, IResponseEntity entity) {
        return Response.status(responseCode).entity(entity).build();
    }
}
