package ru.axetta.ecafe.processor.core.proactive.service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.MeshUnprocessableEntityException;
import ru.axetta.ecafe.processor.core.utils.ssl.EasySSLProtocolSocketFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

@Service
public class AupdPersonService {
    private final Logger logger = LoggerFactory.getLogger(AupdPersonService.class);
    private final static String AUPD_ADDRESS_PROPERTY = "ecafe.processor.aupd.api.address";
    private final static String AUPD_ADDRESS_DEFAULT = "https://mes-api.mos.ru/aupd";
    private final static String AUPD_APIKEY_PROPERTY = "ecafe.processor.aupd.api.apikey";
    private final static String AUPD_APIKEY_DEFAULT = "229a2b81-c7af-4efa-80f7-33629fab3137";

    public String getSsoidByPersonId(String meshGuid) throws Exception {
        String parameters = String.format("person_id=%s", meshGuid);
        URL url = new URL(getAupdAddress() + "/aupd/person?" + parameters);
        logger.info("Execute AUPD GET request " + url.toString());
        GetMethod httpMethod = new GetMethod(url.getPath());
        httpMethod.setRequestHeader("X-Api-Key", getApiKey());
        httpMethod.setQueryString(parameters);
        byte[] response = executeRequest(httpMethod, url);
        ObjectMapper objectMapper = new ObjectMapper();
        AupdSsoResult aupdSsoResult = objectMapper.readValue(response, AupdSsoResult.class);
        return aupdSsoResult.getSso_id();
    }

    private byte[] executeRequest(HttpMethodBase httpMethod, URL url) throws Exception {
        try {
            HttpClient httpClient = getHttpClient(url);
            int statusCode = httpClient.executeMethod(httpMethod);
            if (statusCode == HttpStatus.SC_OK) {
                InputStream inputStream = httpMethod.getResponseBodyAsStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
                return buffer.toByteArray();
            } else {
                String errorMessage = "Aupd request has status " + statusCode;
                if (statusCode == HttpStatus.SC_NOT_FOUND) {
                    throw new AupdNotExistsException(errorMessage);
                } else {
                    throw new Exception(errorMessage);
                }
            }
        } finally {
            httpMethod.releaseConnection();
        }
    }

    private String getAupdAddress() {
        return RuntimeContext.getInstance().getConfigProperties().getProperty(AUPD_ADDRESS_PROPERTY, AUPD_ADDRESS_DEFAULT);
    }

    private String getApiKey() {
        return RuntimeContext.getInstance().getConfigProperties().getProperty(AUPD_APIKEY_PROPERTY, AUPD_APIKEY_DEFAULT);
    }

    private HttpClient getHttpClient(URL url) {
        HttpClient httpClient = new HttpClient();
        httpClient.getHostConfiguration().setHost(url.getHost(), url.getPort(),
                new Protocol("https", (ProtocolSocketFactory)new EasySSLProtocolSocketFactory(), 443));
        return httpClient;
    }
}
