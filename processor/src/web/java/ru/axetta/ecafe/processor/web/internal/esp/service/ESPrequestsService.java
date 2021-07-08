/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.esp.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.file.FileUtils;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ssl.EasySSLProtocolSocketFactory;
import ru.axetta.ecafe.processor.web.internal.esp.ESPRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class ESPrequestsService {
    public static final String ESP_REST_ADDRESS_PROPERTY = "ecafe.processing.esp.rest.address";
    public static final String ESP_REST_API_KEY_PROPERTY = "ecafe.processing.esp.rest.api.key";
    private static final String ESP_REST_LOG = "ecafe.processing.esp.rest.log";
    private static final String ESP_REST_TEMPLATE = "ecafe.processing.esp.rest.template";

    private final ObjectMapper ob = new ObjectMapper();
    private Logger logger = LoggerFactory.getLogger(ESPrequestsService.class);

    public NewESPresponse sendNewESPRequst(ESPRequest espRequest, Client client, Org org, List<String> files)
            throws Exception {
        NewESPForService newESPForService = new NewESPForService();
        String description = "Название ОО=" + org.getShortNameInfoService() + "\nокруг=" + org.getDistrict() + "\nкраткий адрес=" + org.getShortAddress() + "\nтема=" +
                espRequest.getTopic() + "\nтело обращения=" + espRequest.getMessage();
        newESPForService.setDescription(description);
        newESPForService.setProblem(espRequest.getTopic());
        newESPForService.setTemplate(getTemplate());
        newESPForService.setAttachments(files);
        NewESPUserInfo newESPUserInfo = new NewESPUserInfo();
        newESPUserInfo.setEmail(espRequest.getEmail());
        newESPUserInfo.setFio(client.getPerson().getFullName());
        newESPForService.setUser(newESPUserInfo);
        NewESPresponse responseESP = null;
        try {
            String json = ob.writeValueAsString(newESPForService);
            logESP(json, 1);
            byte[] response = executeCreateESPRequest(json);
            responseESP = ob.readValue(response, NewESPresponse.class);
            logESP(ob.writeValueAsString(responseESP), 2);
        } catch (Exception e){
            logger.error("Exception, when send POST-request", e);
            return null;
        }
        return responseESP;
    }

    private void logESP(String message, Integer type) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(CalendarUtils.dateTimeToString(new Date()));
            sb.append(" ");
            if (type==1)
                sb.append("(new request) out: ");
            if (type==2)
                sb.append("in: ");
            if (type==3)
                sb.append("(get info request id) out: ");
            if (type==4)
                sb.append("(file) out: ");
            sb.append(message);
            sb.append("\r\n");
            FileWriter fw = new FileWriter(RuntimeContext.getInstance().getConfigProperties().getProperty(ESP_REST_LOG, ""), true);
            fw.write(sb.toString());
            fw.flush();
            fw.close();
        } catch (Exception e) {
            logger.error("Error writing ESP message to log: ", e);
        }
    }


    public InfoESPresponse getInfoAboutESPReqeust(String requestNumber) {
        InfoESPresponse infoESPresponse = null;
        try {
            logESP(requestNumber, 3);
            byte[] response = executeInfoESPRequest(requestNumber);
            infoESPresponse = ob.readValue(response, InfoESPresponse.class);
            logESP(ob.writeValueAsString(infoESPresponse), 2);
        } catch (Exception e){
            logger.error("Exception, when send GET-request", e);
            return null;
        }
        return infoESPresponse;
    }

    public SendFileESPresponse sendFileForESPRequest(String path) {
        logESP(path, 4);
        File file = new File(FileUtils.getBaseFilePathForESP() + path);
        SendFileESPresponse sendFileESPresponse = null;
        try {
            byte[] response = executeSendFileForESPRequest(file);
            sendFileESPresponse = ob.readValue(response, SendFileESPresponse.class);
            logESP(ob.writeValueAsString(sendFileESPresponse), 2);
        } catch (Exception e){
            logger.error("Exception, when send GET-request", e);
            return null;
        }
        return sendFileESPresponse;
    }

    public byte[] executeSendFileForESPRequest(File file) throws Exception {
        Part[] parts = {new FilePart( "file", file )};
        URL url = new URL(getServiceAddressAttach());
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

    private String getServiceAddressAttach() throws Exception{
        String address = RuntimeContext.getInstance().getConfigProperties().getProperty(ESP_REST_ADDRESS_PROPERTY, "");

        if (address.equals("")) throw new Exception("ESP REST address not specified");
        return address + "attach/";
    }

    private String getApiKey() throws Exception{
        String key = RuntimeContext.getInstance().getConfigProperties().getProperty(ESP_REST_API_KEY_PROPERTY, "");
        if (key.equals("")) throw new Exception("ESP API key not specified");
        return key;
    }

    private String getTemplate() throws Exception{
        String template = RuntimeContext.getInstance().getConfigProperties().getProperty(ESP_REST_TEMPLATE, "");
        if (template.equals("")) throw new Exception("ESP template not specified");
        return template;
    }
}
