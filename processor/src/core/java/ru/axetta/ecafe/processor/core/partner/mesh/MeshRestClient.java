package ru.axetta.ecafe.processor.core.partner.mesh;

import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.http.params.BasicHttpParams;
import ru.axetta.ecafe.processor.core.utils.ssl.EasySSLProtocolSocketFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.*;
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
    private final Integer connectionTimeout;

    public MeshRestClient(String serviceAddress, String apiKey){
        this.serviceAddress = serviceAddress;
        this.apiKey = apiKey;
        this.connectionTimeout = Integer.valueOf(MeshPersonsSyncService.CONNECTION_TIMEOUT_DEFAULT);
    }

    public MeshRestClient(String serviceAddress, String apiKey, Integer connectionTimeout){
        this.serviceAddress = serviceAddress;
        this.apiKey = apiKey;
        this.connectionTimeout = connectionTimeout;
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

        return executeRequest(httpMethod, url);
    }

    private byte[] executeRequest(HttpMethodBase httpMethod, URL url) throws Exception {
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
                String errorMessage = "Mesh request has status " + statusCode;
                if (statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY) {
                    throw new MeshUnprocessableEntityException(errorMessage);
                } else {
                    throw new Exception(errorMessage);
                }
            }
        } finally {
            httpMethod.releaseConnection();
        }
    }

    private MeshResponseWithStatusCode executeRequestWithErrorProcess(HttpMethodBase httpMethod, URL url) throws Exception {
        try {
            HttpClient httpClient = getHttpClient(url);
            int statusCode = httpClient.executeMethod(httpMethod);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try {
                InputStream inputStream = httpMethod.getResponseBodyAsStream();
                int nRead;
                byte[] data = new byte[1024*1024];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
                return new MeshResponseWithStatusCode(buffer.toByteArray(), statusCode);
            } catch (Exception e) {
                return new MeshResponseWithStatusCode(null, statusCode);
            }
        } finally {
            httpMethod.releaseConnection();
        }
    }

    public byte[] executeCreateCategory(String meshGuid, String parameters) throws Exception {
        URL url = new URL(getServiceAddress() + "/persons/" + meshGuid + "/category");
        logger.info("Execute POST request to MESH REST: " + url);
        PostMethod httpMethod = new PostMethod(url.getPath());
        httpMethod.setRequestHeader("X-Api-Key", getApiKey());
        StringRequestEntity requestEntity = new StringRequestEntity(
                parameters,
                "application/json",
                "UTF-8");
        httpMethod.setRequestEntity(requestEntity);

        return executeRequest(httpMethod, url);
    }

    public MeshResponseWithStatusCode executeGetMethod(String relativeUrl, String parameters) throws Exception {
        URL url = new URL(getServiceAddress() + relativeUrl + parameters);
        logger.info("Execute GET request to MESH REST: " + url);
        GetMethod httpMethod = new GetMethod(url.getPath());
        httpMethod.setRequestHeader("X-Api-Key", getApiKey());
        httpMethod.setQueryString(parameters);
        return executeRequestWithErrorProcess(httpMethod, url);
    }

    public MeshResponseWithStatusCode executePostMethod(String relativeUrl, String parameters) throws Exception {
        URL url = new URL(getServiceAddress() + relativeUrl);
        logger.info("Execute POST request to MESH REST: " + url);
        PostMethod httpMethod = new PostMethod(url.getPath());
        httpMethod.setRequestHeader("X-Api-Key", getApiKey());
        StringRequestEntity requestEntity = new StringRequestEntity(
                parameters,
                "application/json",
                "UTF-8");
        httpMethod.setRequestEntity(requestEntity);

        return executeRequestWithErrorProcess(httpMethod, url);
    }

    public MeshResponseWithStatusCode executeDeleteMethod(String relativeUrl) throws Exception {
        URL url = new URL(getServiceAddress() + relativeUrl);
        logger.info("Execute DELETE request to MESH REST: " + url);
        DeleteMethod httpMethod = new DeleteMethod(url.getPath());
        httpMethod.setRequestHeader("X-Api-Key", getApiKey());

        return executeRequestWithErrorProcess(httpMethod, url);
    }

    public MeshResponseWithStatusCode executePutMethod(String relativeUrl, String parameters) throws Exception {
        URL url = new URL(getServiceAddress() + relativeUrl);
        logger.info("Execute PUT request to MESH REST: " + url);
        PutMethod httpMethod = new PutMethod(url.getPath());
        httpMethod.setRequestHeader("X-Api-Key", getApiKey());
        StringRequestEntity requestEntity = new StringRequestEntity(
                parameters,
                "application/json",
                "UTF-8");
        httpMethod.setRequestEntity(requestEntity);

        return executeRequestWithErrorProcess(httpMethod, url);
    }

    private HttpClient getHttpClient(URL url) {
        HttpClientParams httpParams = new HttpClientParams();
        httpParams.setParameter("http.connection.timeout", connectionTimeout);
        HttpClient httpClient = new HttpClient(httpParams);
        if (url.getProtocol().equals("https")) {
            int port = url.getPort() == -1 ? 443 : url.getPort();
            httpClient.getHostConfiguration().setHost(url.getHost(), port,
                    new Protocol("https", (ProtocolSocketFactory) new EasySSLProtocolSocketFactory(), port));
        } else {
            int port = url.getPort() == -1 ? 80 : url.getPort();
            httpClient.getHostConfiguration().setHost(url.getHost(), port);
        }
        return httpClient;
    }

    public byte[] executeUpdateCategory(String meshGUID, Integer idOfRefInExternalSystem, String json) throws Exception {
        URL url = new URL(getServiceAddress() + "/persons/" + meshGUID + "/category/" + idOfRefInExternalSystem);
        logger.info("Execute PUT request to MESH REST: " + url);
        PutMethod httpMethod = new PutMethod(url.getPath());
        httpMethod.setRequestHeader("X-Api-Key", getApiKey());
        StringRequestEntity requestEntity = new StringRequestEntity(
                json,
                "application/json",
                "UTF-8");
        httpMethod.setRequestEntity(requestEntity);

        return executeRequest(httpMethod, url);
    }

    public void executeDeleteCategory(String meshGUID, Integer idOfRefInExternalSystem) throws Exception {
        URL url = new URL(getServiceAddress() + "/persons/" + meshGUID + "/category/" + idOfRefInExternalSystem);
        logger.info("Execute DELETE request to MESH REST: " + url);
        DeleteMethod httpMethod = new DeleteMethod(url.getPath());
        httpMethod.setRequestHeader("X-Api-Key", getApiKey());
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