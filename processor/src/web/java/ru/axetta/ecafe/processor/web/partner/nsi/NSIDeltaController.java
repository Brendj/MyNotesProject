/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.nsi;

import generated.nsiws_delta.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ImportRegisterClientsService;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServlet;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 19.06.14
 * Time: 13:41
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
@WebService(targetNamespace = "http://ru.axetta.ecafe")
public class NSIDeltaController extends HttpServlet implements NSIDeltaService {



    @WebMethod
    @WebResult(name = "receiveNSIDeltaResponse", targetNamespace = "http://rstyle.com/nsi/delta/service", partName = "receiveNSIDeltaResponse")
    public ReceiveNSIDeltaResponseType receiveNSIDelta(
            @WebParam(name = "receiveNSIDeltaRequest", targetNamespace = "http://rstyle.com/nsi/delta/service", partName = "receiveNSIDeltaRequest") ReceiveNSIDeltaRequestType receiveNSIDeltaRequest) {

        return RuntimeContext.getAppContext().getBean(NSIDeltaProcessor.class).process(receiveNSIDeltaRequest);
    }
}