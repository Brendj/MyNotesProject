/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.nsi;

import generated.nsiws_delta.NSIDeltaService;
import generated.nsiws_delta.ReceiveNSIDeltaRequestType;
import generated.nsiws_delta.ReceiveNSIDeltaResponseType;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.servlet.http.HttpServlet;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 19.06.14
 * Time: 13:41
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
//@WebService(targetNamespace = "http://rstyle.com/nsi/delta/service", serviceName = "NSIDeltaSOAPService", portName = "NSIDeltaServicePort")
public class NSIDeltaController extends HttpServlet implements NSIDeltaService {



    @WebMethod
    @WebResult(name = "receiveNSIDeltaResponse", targetNamespace = "http://rstyle.com/nsi/delta/service", partName = "receiveNSIDeltaResponse")
    public ReceiveNSIDeltaResponseType receiveNSIDelta(
            @WebParam(name = "receiveNSIDeltaRequest", targetNamespace = "http://rstyle.com/nsi/delta/service", partName = "receiveNSIDeltaRequest") ReceiveNSIDeltaRequestType receiveNSIDeltaRequest) {

        return RuntimeContext.getAppContext().getBean(NSIDeltaProcessor.class).process(receiveNSIDeltaRequest);
    }
}