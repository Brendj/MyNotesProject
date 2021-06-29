/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.esp.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.MeshUnprocessableEntityException;
import ru.axetta.ecafe.processor.core.partner.mesh.json.Category;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.MeshClientCardRef;
import ru.axetta.ecafe.processor.core.utils.ssl.EasySSLProtocolSocketFactory;
import ru.axetta.ecafe.processor.web.internal.esp.ESPController;
import ru.axetta.ecafe.processor.web.internal.esp.ESPRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class ESPrequestsService {
    public static final String ESP_REST_ADDRESS_PROPERTY = "ecafe.processing.esp.rest.address";
    public static final String ESP_REST_API_KEY_PROPERTY = "ecafe.processing.esp.rest.api.key";

    private final ObjectMapper ob = new ObjectMapper();
    private Logger logger = LoggerFactory.getLogger(ESPrequestsService.class);

    public NewESPresponse sendNewESPRequst(ESPRequest espRequest, Client client) {
        NewESPForService newESPForService = new NewESPForService();
        newESPForService.setDescription(espRequest.getMessage());
        newESPForService.setProblem(espRequest.getTopic());
        newESPForService.setTemplate("ИСПП Прочее");
        NewESPUserInfo newESPUserInfo = new NewESPUserInfo();
        newESPUserInfo.setEmail(espRequest.getEmail());
        newESPUserInfo.setFio(client.getPerson().getFullName());
        newESPForService.setUser(newESPUserInfo);
        NewESPresponse responseESP = null;
        try {
            String json = ob.writeValueAsString(newESPForService);
            byte[] response = executeCreateESPRequest(json);
            responseESP = ob.readValue(response, NewESPresponse.class);
        } catch (Exception e){
            logger.error("Exception, when send POST-request", e);
            return null;
        }
        return responseESP;
    }

    public InfoESPresponse getInfoAboutESPReqeust(String requestNumber) {
        InfoESPresponse infoESPresponse = null;
        try {
            byte[] response = executeInfoESPRequest(requestNumber);
            infoESPresponse = ob.readValue(response, InfoESPresponse.class);
        } catch (Exception e){
            logger.error("Exception, when send GET-request", e);
            return null;
        }
        return infoESPresponse;
    }

    public SendFileESPresponse sendFileForESPRequest(File file) {
        SendFileESPresponse sendFileESPresponse = null;
        try {
            byte[] response = executeSendFileForESPRequest(file);
            sendFileESPresponse = ob.readValue(response, SendFileESPresponse.class);
        } catch (Exception e){
            logger.error("Exception, when send GET-request", e);
            return null;
        }
        return sendFileESPresponse;
    }

    public byte[] executeSendFileForESPRequest(File file) throws Exception {
        Part[] parts = {new FilePart( "file", file )};
        URL url = new URL(getServiceAddress());
        logger.info("Execute POST sendFile to ESP REST: " + url);
        PostMethod httpMethod = new PostMethod(url.getPath());
        httpMethod.setRequestHeader("X-Api-Key", getApiKey());
        httpMethod.setRequestEntity(
                new MultipartRequestEntity(parts, httpMethod.getParams())
        );
        return executeRequest(httpMethod, url);
    }

    public byte[] executeCreateESPRequest(String json) throws Exception {
        URL url = new URL(getServiceAddress());
        logger.info("Execute POST request to ESP REST: " + url);
        PostMethod httpMethod = new PostMethod(url.getPath());
        httpMethod.setRequestHeader("X-Api-Key", getApiKey());
        StringRequestEntity requestEntity = new StringRequestEntity(
                json,
                "application/json",
                "UTF-8");
        httpMethod.setRequestEntity(requestEntity);

        return executeRequest(httpMethod, url);
    }

    public byte[] executeInfoESPRequest(String requestNumber) throws Exception {
        URL url = new URL(getServiceAddress() + "?id=" + requestNumber);
        logger.info("Execute GET request to ESP REST: " + url);
        GetMethod httpMethod = new GetMethod(url.getPath());
        httpMethod.setRequestHeader("X-Api-Key", getApiKey());
        httpMethod.setQueryString("?id=" + requestNumber);

        return executeRequest(httpMethod, url);
    }

    private byte[] executeRequest(HttpMethodBase httpMethod, URL url) throws Exception {
        try {
            HttpClient httpClient = getHttpClient(url);
            int statusCode = httpClient.executeMethod(httpMethod);
            //if (statusCode == HttpStatus.SC_OK) {
                InputStream inputStream = httpMethod.getResponseBodyAsStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024*1024];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
                return buffer.toByteArray();
            //} else {
            //    String errorMessage = "ESP request has status " + statusCode;
            //    throw new Exception(errorMessage);
            //}
        } finally {
            httpMethod.releaseConnection();
        }
    }

    private HttpClient getHttpClient(URL url) {
        HttpClient httpClient = new HttpClient();
        httpClient.getHostConfiguration().setHost(url.getHost(), url.getPort(),
                new Protocol("https", (ProtocolSocketFactory)new EasySSLProtocolSocketFactory(), 443));
        return httpClient;
    }

    private String getServiceAddress() throws Exception{
        String address = RuntimeContext.getInstance().getConfigProperties().getProperty(ESP_REST_ADDRESS_PROPERTY, "");
        if (address.equals("")) throw new Exception("ESP REST address not specified");
        return address;
    }

    private String getApiKey() throws Exception{
        String key = RuntimeContext.getInstance().getConfigProperties().getProperty(ESP_REST_API_KEY_PROPERTY, "");
        if (key.equals("")) throw new Exception("ESP API key not specified");
        return key;
    }
}
