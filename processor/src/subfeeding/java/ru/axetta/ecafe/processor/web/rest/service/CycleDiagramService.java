/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.rest.service;

import ru.axetta.ecafe.processor.web.ClientAuthToken;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.IdResult;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomController;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomControllerWSService;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.04.14
 * Time: 10:40
 * To change this template use File | Settings | File Templates.
 */
@Path("/diagram")
public class CycleDiagramService {

    @PostConstruct
    public void init() throws Exception{

    }

    @GET
    @Path("/list.json")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> list(@Context HttpServletRequest req) throws MalformedURLException {
        ClientAuthToken token = ClientAuthToken.loadFrom(req.getSession());
        if(token==null){
            return Arrays.asList("");
        } else {
            URL wsdl = new URL("http://localhost:8080/processor/soap/client?wsdl");
            ClientRoomController clientRoomController = new ClientRoomControllerWSService(wsdl).getClientRoomControllerWSPort();
            IdResult result = clientRoomController.getIdOfClient(token.getContractId());
            if (result.resultCode==0){
                return Arrays.asList(Long.toString(result.id));
            } else {
                return Arrays.asList(result.description);
            }
        }
    }

}
