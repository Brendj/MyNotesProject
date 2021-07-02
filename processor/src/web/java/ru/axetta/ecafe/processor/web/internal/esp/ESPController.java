/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

/**
 * Created by a.voinov on 27.10.2020.
 */

package ru.axetta.ecafe.processor.web.internal.esp;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.file.FileUtils;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.internal.esp.service.ESPrequestsService;
import ru.axetta.ecafe.processor.web.internal.esp.service.InfoESPresponse;
import ru.axetta.ecafe.processor.web.internal.esp.service.NewESPresponse;
import ru.axetta.ecafe.processor.web.internal.esp.service.SendFileESPresponse;
import ru.axetta.ecafe.processor.web.partner.library.ResponseCodes;
import ru.axetta.ecafe.processor.web.partner.library.Result;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.cxf.common.util.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

@Path(value = "")
@Controller
public class ESPController {

    private Logger logger = LoggerFactory.getLogger(ESPController.class);
    public static final String KEY = "ecafe.processor.sendtoesp.key";
    private static final String SUCCESS = "success";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "newRequestESP")
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
            Client client = DAOUtils.findClient(persistenceSession, espRequest.getIdOfClient());
            if (client == null)
            {
                logger.error(String.format("Client with idOfClient=%s not found", espRequest.getIdOfClient()));
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
            esp.setCreateDate(new Date());
            esp.setEmail(espRequest.getEmail());
            esp.setOrg(org);
            esp.setTopic(espRequest.getTopic());
            esp.setMessage(espRequest.getMessage());
            persistenceSession.save(esp);
            /////
            ESPrequestsService esPrequestsService = new ESPrequestsService();
            Integer count = 1;
            List<String> files = new ArrayList<>();
            for (ESPRequestAttachedFile espRequestAttachedFile: espRequest.getAttached())
            {
                String path = FileUtils.saveFile(esp.getIdesprequest(), espRequestAttachedFile.getAttached_filedata(),
                        espRequestAttachedFile.getAttached_filename());
                ESPattached espattached = new ESPattached();
                espattached.setCreateDate(new Date());
                espattached.setEsp(esp);
                espattached.setNumber(count);
                espattached.setPath(path);
                //Отправка в FOS файлов
                SendFileESPresponse sendFileESPresponse = esPrequestsService.sendFileForESPRequest(path);
                if (sendFileESPresponse != null && sendFileESPresponse.getType().equals(SUCCESS))
                {
                    espattached.setLinkinfos(sendFileESPresponse.getUrl());
                    files.add(sendFileESPresponse.getUrl());
                }
                count++;
                persistenceSession.save(espattached);

            }
            /////////
            //Создание нового обращения
            NewESPresponse newESPresponse = esPrequestsService.sendNewESPRequst(espRequest, client, org, files);

            if (newESPresponse != null && newESPresponse.getType().equals(SUCCESS))
            {
                //Если обращение успешно создано
                esp.setNumberrequest(newESPresponse.getId());
                InfoESPresponse infoESPresponse = esPrequestsService.getInfoAboutESPReqeust(newESPresponse.getId());
                if (infoESPresponse.getClosed_at() != null)
                    esp.setCloseddate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(infoESPresponse.getClosed_at()));
                if (infoESPresponse.getSolution() != null)
                    esp.setSolution(infoESPresponse.getSolution());
                if (infoESPresponse.getStatus() != null)
                    esp.setStatus(infoESPresponse.getStatus());
                if (infoESPresponse.getSd() != null)
                    esp.setSd(infoESPresponse.getSd());
                esp.setUpdateDate(new Date());
                persistenceSession.save(esp);
            }

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

    public static class Org1 {
        @JsonProperty("idOfOrg")
        private Long idOfOrg;

        public Org1() {
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }
    }
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "getRequestsESP")
    public Response getESPRequests(@Context HttpServletRequest request, Org1 org1) {
        Result result = new Result();
        ResponseESPRequests responseESPRequests = new ResponseESPRequests();
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
            Org org = DAOUtils.findOrg(persistenceSession, org1.getIdOfOrg());
            if (org == null)
            {
                logger.error(String.format("Org with idOfOrg=%s not found", org1.idOfOrg));
                result.setErrorCode(ResponseCodes.RC_NOT_FOUND_ORG.getCode().toString());
                result.setErrorMessage(ResponseCodes.RC_NOT_FOUND_ORG.toString());
                return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
            }
            List<ESP> esps = DAOUtils.getESPForOrg(persistenceSession,org);
            ESPrequestsService esPrequestsService = new ESPrequestsService();
            for (ESP esp: esps)
            {
                InfoESPresponse infoESPresponse = esPrequestsService.getInfoAboutESPReqeust(esp.getNumberrequest());
                if (infoESPresponse.getClosed_at() != null)
                    esp.setCloseddate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(infoESPresponse.getClosed_at()));
                if (infoESPresponse.getSolution() != null)
                    esp.setSolution(infoESPresponse.getSolution());
                if (infoESPresponse.getStatus() != null)
                    esp.setStatus(infoESPresponse.getStatus());
                if (infoESPresponse.getSd() != null)
                    esp.setSd(infoESPresponse.getSd());
                esp.setUpdateDate(new Date());
                ResponseESPRequestsPOJO responseESPRequestsPOJO = new ResponseESPRequestsPOJO();
                responseESPRequestsPOJO.setDateRequest(esp.getCreateDate());
                responseESPRequestsPOJO.setEmail(esp.getEmail());
                responseESPRequestsPOJO.setIdOfOrg(esp.getOrg().getIdOfOrg());
                responseESPRequestsPOJO.setIdOfClient(esp.getClient().getIdOfClient());
                responseESPRequestsPOJO.setMessage(esp.getMessage());
                responseESPRequestsPOJO.setTopic(esp.getTopic());
                responseESPRequestsPOJO.setNumberrequest(esp.getNumberrequest());
                responseESPRequestsPOJO.setUpdateDate(esp.getUpdateDate());
                responseESPRequestsPOJO.setStatus(esp.getStatus());
                responseESPRequestsPOJO.setSolution(esp.getSolution());
                responseESPRequests.getEspRequests().add(responseESPRequestsPOJO);
                persistenceSession.save(esp);
            }
            responseESPRequests.setErrorCode(ResponseCodes.RC_OK.getCode().toString());
            responseESPRequests.setErrorMessage(ResponseCodes.RC_OK.toString());
        } catch (Exception e) {
            logger.error("Ошибка при получении списка обращений", e);
            result.setErrorCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        }
        result.setErrorCode(ResponseCodes.RC_OK.getCode().toString());
        result.setErrorMessage(ResponseCodes.RC_OK.toString());
        return Response.status(HttpURLConnection.HTTP_OK).entity(responseESPRequests).build();
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
