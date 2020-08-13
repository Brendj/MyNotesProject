package ru.axetta.ecafe.processor.core.partner.mesh;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.ssl.EasySSLProtocolSocketFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;

@Component
public class MeshRestClient {
    public static final String MESH_REST_ADDRESS_PROPERTY = "ecafe.processing.mesh.rest.address";
    public static final String MESH_REST_API_KEY_PROPERTY = "ecafe.processing.mesh.rest.api.key";

    public byte[] executeRequest(String relativeUrl, String parameters) throws Exception {
        URL url = new URL(getServiceAddress() + relativeUrl + parameters);
        GetMethod httpMethod = new GetMethod(url.getPath());
        httpMethod.setRequestHeader("X-Api-Key", getApiKey());
        httpMethod.setQueryString(parameters);
        try {
            HttpClient httpClient = getHttpClient(url);
            int statusCode = httpClient.executeMethod(httpMethod);
            if (statusCode == HttpStatus.SC_OK) {
                InputStream inputStream = httpMethod.getResponseBodyAsStream();
                return IOUtils.toByteArray(inputStream);
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

    private String getServiceAddress() throws Exception{
        String address = RuntimeContext.getInstance().getConfigProperties().getProperty(MESH_REST_ADDRESS_PROPERTY, "");
        if (address.equals("")) throw new Exception("MESH REST address not specified");
        return address;
    }

    private String getApiKey() throws Exception{
        String key = RuntimeContext.getInstance().getConfigProperties().getProperty(MESH_REST_API_KEY_PROPERTY, "");
        if (key.equals("")) throw new Exception("MESH API key not specified");
        return key;
    }

}