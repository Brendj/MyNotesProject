/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

/**
 * Created by a.voinov on 27.10.2020.
 */

package ru.axetta.ecafe.processor.web.internal.esp;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ExternalEventNotificationService;
import ru.axetta.ecafe.processor.core.service.cardblock.CardBlockService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.library.LibraryRequest;
import ru.axetta.ecafe.processor.web.partner.library.ResponseCodes;
import ru.axetta.ecafe.processor.web.partner.library.Result;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.Enumeration;

@Path(value = "")
@Controller
public class ESPController {

    private Logger logger = LoggerFactory.getLogger(ESPController.class);
    public static final String KEY = "ecafe.processor.sendtoesp.key";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "requestESP")
    public Response requestESP(@Context HttpServletRequest request, ESPRequest espRequest) {
        Result result = new Result();
        //Контроль безопасности
        if (!validateAccess(request)) {
            logger.error("Неверный ключ доступа");
            result.setErrorCode(ResponseCodes.RC_WRONG_KEY.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_WRONG_KEY.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        }

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Client client = DAOUtils.findClientByMeshGuid(persistenceSession, espRequest.getMeshGuid());
            if (client == null)
            {
                logger.error(String.format("Client with meshGuid=%s not found", espRequest.getMeshGuid()));
                result.setErrorCode(ResponseCodes.RC_NOT_FOUND_CLIENT.getCode().toString());
                result.setErrorMessage(ResponseCodes.RC_NOT_FOUND_CLIENT.toString());
                return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
            }
            Org org = DAOUtils.findOrg(persistenceSession, espRequest.getIdOfOrg());
            if (org == null)
            {
                logger.error(String.format("Org with idOfOrg=%s not found", espRequest.getIdOfOrg()));
                result.setErrorCode(ResponseCodes.RC_NOT_FOUND_ORG.getCode().toString());
                result.setErrorMessage(ResponseCodes.RC_NOT_FOUND_ORG.toString());
                return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
            }
            ESP esp = new ESP();
            esp.setClient(client);
            esp.setCreateDate(espRequest.getDateRequest());
            esp.setEmail(espRequest.getEmail());
            esp.setOrg(org);
            esp.setTopic(espRequest.getTopic());
            esp.setMessage(espRequest.getMessage());
            persistenceSession.save(esp);
            persistenceTransaction.commit();
            persistenceTransaction = null;

        } catch (Exception e) {
            logger.error("Ошибка при сохранении данных обращения", e);
            result.setErrorCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        result.setErrorCode(ResponseCodes.RC_OK.getCode().toString());
        result.setErrorMessage(ResponseCodes.RC_OK.toString());
        return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
    }

    private boolean validateAccess(HttpServletRequest request) {
        String securityKey = "";
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                if (header.toLowerCase().equals("key"))
                {
                    securityKey = request.getHeader(header);
                    break;
                }
            }
        }
        String keyinternal = RuntimeContext.getInstance().getConfigProperties().getProperty(KEY, "");
        if (!securityKey.isEmpty() && securityKey.equals(keyinternal))
            return true;
        return false;
    }
}
