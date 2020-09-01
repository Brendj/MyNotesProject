package ru.axetta.ecafe.processor.core.partner.mesh;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.ssl.EasySSLProtocolSocketFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

@Component
public class MeshRestClient {
    public static final String MESH_REST_ADDRESS_PROPERTY = "ecafe.processing.mesh.rest.address";
    public static final String MESH_REST_API_KEY_PROPERTY = "ecafe.processing.mesh.rest.api.key";
    private static final Logger logger = LoggerFactory.getLogger(MeshRestClient.class);

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