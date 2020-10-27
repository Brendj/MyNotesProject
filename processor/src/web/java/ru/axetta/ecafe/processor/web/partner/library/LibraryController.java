/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.library;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.CardBlockService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Path(value = "")
@Controller
public class LibraryController {

    private Logger logger = LoggerFactory.getLogger(LibraryController.class);
    public static final String KEY_FOR_LIBRARY = "ecafe.processor.library.key";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "enterlibrary")
    public Response setEntryLibrary(@Context HttpServletRequest request) {
        Result result = new Result();
        String securityKey = "";
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                if (header.toLowerCase().equals("authorization bearer token") || header.toLowerCase().equals("key"))
                {
                    securityKey = request.getHeader(header);
                    break;
                }
            }
        }
        //Контроль безопасности
        if (!validateAccess(securityKey)) {
            logger.error("Неверный ключ доступа");
            result.setErrorCode(ResponseCodes.RC_WRONG_KEY.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_WRONG_KEY.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        }
        String guid = "";
        String libraryCode = "";
        String libraryName = "";
        String libraryAdress = "";
        Date accessTime = new Date(0);
        for (String key : request.getParameterMap().keySet()) {
            if (key.toLowerCase().equals("guid")) {
                guid = request.getParameterMap().get(key)[0];
            }
            if (key.toLowerCase().equals("librarycode")) {
                libraryCode = request.getParameterMap().get(key)[0];
            }
            if (key.toLowerCase().equals("libraryname")) {
                libraryName = request.getParameterMap().get(key)[0];
            }
            if (key.toLowerCase().equals("libraryadress")) {
                libraryAdress = request.getParameterMap().get(key)[0];
            }
            if (key.toLowerCase().equals("accesstime")) {
                String date = request.getParameterMap().get(key)[0];
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    accessTime = format.parse(date);
                } catch (ParseException e) {
                    logger.error("Ошибка при чтении даты для Библиотеки", e);
                    result.setErrorCode(ResponseCodes.RC_WRONG_DATE.getCode().toString());
                    result.setErrorMessage(ResponseCodes.RC_WRONG_DATE.toString());
                    return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
                }
            }

        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Client client = DAOUtils.findClientByMeshGuid(persistenceSession, guid);
            if (client == null)
            {
                logger.error(String.format("Client with guid=%s not found", guid));
                result.setErrorCode(ResponseCodes.RC_NOT_FOUND_CLIENT.getCode().toString());
                result.setErrorMessage(ResponseCodes.RC_NOT_FOUND_CLIENT.toString());
                return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
            }
            Card card = client.findActiveCard(persistenceSession, null);
            if (card != null) {
                RuntimeContext.getAppContext().getBean(CardBlockService.class)
                        .saveLastCardActivity(persistenceSession, card.getIdOfCard(), CardActivityType.ENTER_LIBRARY);
            }
            ExternalEventVersionHandler handler = new ExternalEventVersionHandler(persistenceSession);
            ExternalEvent event = new ExternalEvent(client, libraryCode, libraryName, libraryAdress,
                    ExternalEventType.LIBRARY, accessTime, card == null ? null : card.getCardNo(),
                    card == null ? null : card.getCardType(), handler);
            persistenceSession.save(event);
            //DAOUtils.saveLiblary(persistenceSession, guid, libraryCode, libraryName, libraryAdress, accessTime);
            persistenceTransaction.commit();
            persistenceTransaction = null;

        } catch (Exception e) {
            logger.error("Ошибка при сохранении данных для Библиотеки", e);
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

    private boolean validateAccess(String key) {
        //Узнаем, нужно ли использовать проверку по ip
        String keyinternal = RuntimeContext.getInstance().getConfigProperties().getProperty(KEY_FOR_LIBRARY, "");
        if (key != "" && key.equals(keyinternal))
            return true;
        return false;
    }
}
