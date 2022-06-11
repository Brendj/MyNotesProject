package ru.iteco.meshsync.mesh.service.logic;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.iteco.client.model.PersonAgent;
import ru.iteco.client.model.PersonInfo;
import ru.iteco.meshsync.mesh.service.logic.dto.ClientRestDTO;

import javax.annotation.PostConstruct;
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
    public void init(){
        this.restTemplate = new RestTemplate();
        this.httpHeaders = new HttpHeaders();
        this.httpHeaders.set(X_API_KEY, apiKey);
    }


    public Boolean clientExist(String personGUID) {
        Map<String, String> params = new HashMap<>();
        params.put("guardianMeshGuid", personGUID);
        HttpEntity<Void> request = new HttpEntity<>(httpHeaders);

        ResponseEntity<ClientRestDTO> response = restTemplate.exchange(targetUrl + "/client", HttpMethod.GET,
                request, ClientRestDTO.class, params);


        ClientRestDTO dto = response.getBody();

        return dto != null && StringUtils.isNotEmpty(dto.getPersonGUID());
    }

    public void deleteClient(String personGUID) {
        Map<String, String> params = new HashMap<>();
        params.put("guardianMeshGuid", personGUID);

        HttpEntity<Void> request = new HttpEntity<>(httpHeaders);
        ResponseEntity<Void> response = restTemplate.exchange(targetUrl + "/client", HttpMethod.DELETE,
                request, Void.class, params);

        if(!response.getStatusCode().equals(HttpStatus.OK)){
            throw new RestClientException("ИС ПП вернул ошибку!");
        }
    }

    public void updateClient(PersonInfo info) {


    }

    public void createClientGuardian(String childrenPersonId, PersonInfo guardInfo) {

    }

    public void processGuardianRelations(String personGUID, List<PersonAgent> agents) {
    }
}
