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
import org.codehaus.jackson.annotate.JsonProperty;
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
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

@Path(value = "")
@Controller
public class ESPController {

    private Logger logger = LoggerFactory.getLogger(ESPController.class);
    public static final String KEY = "ecafe.processor.sendtoesp.key";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "test")
    public Response test(@Context HttpServletRequest request, ESPRequest espRequest) {
        Result result = new Result();
                sendFile();
                return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
    }

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
            /////
            Integer count = 1;
            for (ESPRequestAttachedFile espRequestAttachedFile: espRequest.getAttached())
            {
                String path = FileUtils.saveFile(esp.getIdesprequest(), espRequestAttachedFile.getAttached_filedata(),
                        espRequestAttachedFile.getAttached_filename());
                ESPattached espattached = new ESPattached();
                espattached.setCreateDate(new Date());
                espattached.setEsp(esp);
                espattached.setNumber(count);
                espattached.setPath(path);
                count++;
                persistenceSession.save(espattached);
            }
            /////////
            ESPrequestsService esPrequestsService = new ESPrequestsService();
            NewESPresponse newESPresponse = esPrequestsService.sendNewESPRequst(espRequest, client);
            if (newESPresponse != null && newESPresponse.getType().equals("success"))
            {
                //Если обращение успешно создано
                esp.setNumberrequest(newESPresponse.getId());
                InfoESPresponse infoESPresponse = esPrequestsService.getInfoAboutESPReqeust(newESPresponse.getId());
                //esp.setCloseddate();
                persistenceSession.save(esp);
            }


            /////////
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
            for (ESP esp: esps)
            {
                ResponseESPRequestsPOJO responseESPRequestsPOJO = new ResponseESPRequestsPOJO();
                responseESPRequestsPOJO.setDateRequest(esp.getCreateDate());
                responseESPRequestsPOJO.setEmail(esp.getEmail());
                responseESPRequestsPOJO.setIdOfOrg(esp.getOrg().getIdOfOrg());
                responseESPRequestsPOJO.setMeshGuid(esp.getClient().getMeshGUID());
                responseESPRequestsPOJO.setMessage(esp.getMessage());
                responseESPRequestsPOJO.setTopic(esp.getTopic());
                responseESPRequestsPOJO.setNumberrequest(esp.getNumberrequest());
                responseESPRequestsPOJO.setUpdateDate(esp.getUpdateDate());
                responseESPRequestsPOJO.setStatus(esp.getStatus());
                responseESPRequestsPOJO.setSolution(esp.getSolution());
                responseESPRequests.getEspRequests().add(responseESPRequestsPOJO);
            }
            responseESPRequests.setErrorCode(ResponseCodes.RC_OK.getCode().toString());
            responseESPRequests.setErrorMessage(ResponseCodes.RC_OK.toString());
        } catch (Exception e) {
            logger.error("Ошибка при сохранении данных обращения", e);
            result.setErrorCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        }
        result.setErrorCode(ResponseCodes.RC_OK.getCode().toString());
        result.setErrorMessage(ResponseCodes.RC_OK.toString());
        return Response.status(HttpURLConnection.HTTP_OK).entity(responseESPRequests).build();
    }

    @POST
    @Path(value = "sendFile")
    public void sendFile() {
        HttpClient httpclient = new HttpClient();
        File file = new File( "C:\\JBosser\\7.1.1\\standalone\\files\\ESP\\10\\testdog1.jpg" );

        // DEBUG
        logger.debug( "FILE::" + file.exists() ); // IT IS NOT NULL
        try
        {
            ESPrequestsService esPrequestsService = new ESPrequestsService();
            SendFileESPresponse sendFileESPresponse = esPrequestsService.sendFileForESPRequest(file);
            System.out.println("ewtw");

        }
        catch( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
