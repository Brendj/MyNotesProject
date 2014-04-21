/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.rest.items;

import ru.axetta.ecafe.processor.web.ClientAuthToken;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.04.14
 * Time: 10:12
 * To change this template use File | Settings | File Templates.
 */
@Path("/test")
public class Test {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String test(@Context HttpServletRequest req){
        ClientAuthToken token = ClientAuthToken.loadFrom(req.getSession());
        return "Hello "+(token==null?"null":token);
    }

}
