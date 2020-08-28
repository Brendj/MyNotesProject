package ru.iteco.meshsync.mesh.service.logic;

import ru.iteco.client.ApiClient;
import ru.iteco.client.ApiException;
import ru.iteco.client.api.PersonApi;
import ru.iteco.client.model.PersonInfo;

import org.springframework.stereotype.Service;
import org.threeten.bp.OffsetDateTime;

@Service
public class RestService {

    private final PersonApi personApi;

    public RestService(ApiClient apiClient) {
        this.personApi = new PersonApi();
        this.personApi.setApiClient(apiClient);
    }

    PersonInfo getPersonInfoByGUIDAndExpand(final String GUID, final String expand) throws ApiException {
        return personApi.personsIdGet(GUID, OffsetDateTime.now().plusHours(3L),  expand);
    }
}
