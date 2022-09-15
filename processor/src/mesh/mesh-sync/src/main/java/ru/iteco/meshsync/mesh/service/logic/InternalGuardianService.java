package ru.iteco.meshsync.mesh.service.logic;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.iteco.client.model.PersonAgent;
import ru.iteco.client.model.PersonInfo;
import ru.iteco.meshsync.mesh.service.logic.dto.ClientRestDTO;
import ru.iteco.meshsync.mesh.service.logic.dto.GuardianRelationDTO;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InternalGuardianService {
    private static final Logger log = LoggerFactory.getLogger(InternalGuardianService.class);

    private static final String X_API_KEY = "X-Api-Key";

    @Value(value = "${client.internal.targeturl}")
    private String targetUrl;

    @Value(value = "${client.internal.X-Api-Key}")
    private String apiKey;

    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;

    @PostConstruct
    public void init() throws Exception {
        this.restTemplate = getRestTemplate();
        this.httpHeaders = new HttpHeaders();
        this.httpHeaders.set(X_API_KEY, apiKey);
    }

    private RestTemplate getRestTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        return new RestTemplate(requestFactory);
    }

    public void deleteClient(String personGUID) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("guardianMeshGuid", personGUID);

        HttpEntity<Void> request = new HttpEntity<>(httpHeaders);
        ResponseEntity<Void> response = restTemplate.exchange(
                targetUrl + "/client?guardianMeshGuid={guardianMeshGuid}",
                HttpMethod.DELETE,
                request, Void.class, params);
        processStatusCode(response.getStatusCode(),"InternalGuardianService.deleteClient:", personGUID);
    }

    public void updateClient(PersonInfo info, Integer operation) throws Exception {
        ClientRestDTO dto = ClientRestDTO.build(info, operation);

        HttpEntity<ClientRestDTO> request = new HttpEntity<>(dto, httpHeaders);
        ResponseEntity<Void> response = restTemplate.exchange(targetUrl + "/client", HttpMethod.PUT,
                request, Void.class);
        processStatusCode(response.getStatusCode(),"InternalGuardianService.updateClient:", dto.getPersonGUID());
    }

    public void processGuardianRelations(String childPersonGUID, List<PersonAgent> agents) throws Exception {
        GuardianRelationDTO dto = GuardianRelationDTO.build(childPersonGUID, agents);

        HttpEntity<GuardianRelationDTO> request = new HttpEntity<>(dto, httpHeaders);
        ResponseEntity<Void> response = restTemplate.exchange(targetUrl + "/guardians", HttpMethod.POST,
                request, Void.class);
        processStatusCode(response.getStatusCode(),"InternalGuardianService.processGuardianRelations:", childPersonGUID);

    }

    public void processStatusCode(HttpStatus statusCode, String sourceMethod, String personGuid) throws Exception {
        if (statusCode.equals(HttpStatus.NOT_FOUND)) {
            throw new RestClientException(sourceMethod + " Unable to find client by MESH-GUID: " + personGuid);
        }
        else if (statusCode.equals(HttpStatus.BAD_REQUEST)) {
            throw new RestClientException(sourceMethod +
                    " Incorrect data was transmitted to process client with MESH-GUID: " + personGuid);
        } else if (!statusCode.equals(HttpStatus.OK)) {
            throw new RestClientException(sourceMethod +
                    " Error while processing client with MESH-GUID: "
                    + personGuid + " StatusCode: " + statusCode.getReasonPhrase());
        }
    }
}
