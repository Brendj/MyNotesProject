/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.error;

import io.jsonwebtoken.Header;

import org.apache.commons.lang.exception.ExceptionUtils;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(WebApplicationException e) {
        WebApplicationErrorResponse errorResponse;
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        errorResponse = new WebApplicationErrorResponse(e.getErrorCode().toString(), status.getStatusCode(),
                e.getErrorMessage(), ExceptionUtils.getStackTrace(e), uriInfo.getRequestUri().toString());
        return Response.status(status).header(Header.CONTENT_TYPE, "application/json;charset=UTF-8")
                .entity(errorResponse).build();
    }

}
