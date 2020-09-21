package ru.axetta.ecafe.processor.core.partner.mesh;

import ru.axetta.ecafe.processor.core.utils.ssl.EasySSLProtocolSocketFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

public class MeshRestClient {
    private static final Logger logger = LoggerFactory.getLogger(MeshRestClient.class);
    private final String serviceAddress;
    private final String apiKey;

    public MeshRestClient(String serviceAddress, String apiKey){
        this.serviceAddress = serviceAddress;
        this.apiKey = apiKey;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public String getApiKey() {
        return apiKey;
    }

    public byte[] executeRequest(String relativeUrl, String parameters) throws Exception {
        URL url = new URL(getServiceAddress() + relativeUrl + parameters);
        logger.info("Execute request to MESH REST: " + url);
        GetMethod httpMethod = new GetMethod(url.getPath());
        httpMethod.setRequestHeader("X-Api-Key", getApiKey());
        httpMethod.setQueryString(parameters);
        try {
            HttpClient httpClient = getHttpClient(url);
            int statusCode = httpClient.executeMethod(httpMethod);
            if (statusCode == HttpStatus.SC_OK) {
                InputStream inputStream = httpMethod.getResponseBodyAsStream();
                //return IOUtils.toByteArray(inputStream);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024*1024];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
                return buffer.toByteArray();
            } else {
                throw new Exception("Mesh request has status " + statusCode);
            }
        } finally {
            httpMethod.releaseConnection();
        }
    }

    public byte[] executeCreateCategory(String meshGuid, String parameters) throws Exception {
        URL url = new URL(getServiceAddress() + "/persons/" + meshGuid + "/category");
        logger.info("Execute request to MESH REST: " + url);
        PostMethod httpMethod = new PostMethod(url.getPath());
        httpMethod.setRequestHeader("X-Api-Key", getApiKey());
        StringRequestEntity requestEntity = new StringRequestEntity(
                parameters,
                "application/json",
                "UTF-8");
        httpMethod.setRequestEntity(requestEntity);

        try {
            HttpClient httpClient = getHttpClient(url);
            int statusCode = httpClient.executeMethod(httpMethod);
            if (statusCode == HttpStatus.SC_OK) {
                InputStream inputStream = httpMethod.getResponseBodyAsStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024*1024];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
                return buffer.toByteArray();
            } else {
                throw new Exception("Mesh request has status " + statusCode);
            }
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

    public void executeUpdateCategory(String meshGUID, Integer idOfRefInExternalSystem, String json) throws Exception {
        URL url = new URL(getServiceAddress() + "/persons/" + meshGUID + "/category/" + idOfRefInExternalSystem);
        logger.info("Execute request to MESH REST: " + url);
        PutMethod httpMethod = new PutMethod(url.getPath());
        httpMethod.setRequestHeader("X-Api-Key", getApiKey());
        StringRequestEntity requestEntity = new StringRequestEntity(
                json,
                "application/json",
                "UTF-8");
        httpMethod.setRequestEntity(requestEntity);

        try {
            HttpClient httpClient = getHttpClient(url);
            int statusCode = httpClient.executeMethod(httpMethod);
            if (statusCode != HttpStatus.SC_OK) {
                throw new Exception("Mesh request has status " + statusCode);
            }
        } finally {
            httpMethod.releaseConnection();
        }
    }
}