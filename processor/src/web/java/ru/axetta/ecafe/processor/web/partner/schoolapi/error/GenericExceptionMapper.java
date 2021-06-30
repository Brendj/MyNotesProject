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


public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable e) {
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;


        WebApplicationErrorResponse errorResponse = new WebApplicationErrorResponse(status.getReasonPhrase(),
                status.getStatusCode(), e.getMessage(), ExceptionUtils.getStackTrace(e), getUriString());
        return Response.status(status).header(Header.CONTENT_TYPE, "application/json;charset=UTF-8")
                .entity(errorResponse).build();
    }

    private String getUriString() {
        try {
            return uriInfo.getRequestUri().toString();
        }
        catch(Exception ex){

        }
        return "";
    }

}
