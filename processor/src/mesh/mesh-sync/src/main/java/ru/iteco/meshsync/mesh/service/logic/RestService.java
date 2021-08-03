package ru.iteco.meshsync.mesh.service.logic;

import ru.iteco.client.ApiClient;
import ru.iteco.client.ApiException;
import ru.iteco.client.api.ClassApi;
import ru.iteco.client.api.PersonApi;
import ru.iteco.client.model.ModelClass;
import ru.iteco.client.model.PersonInfo;

import org.springframework.stereotype.Service;
import org.threeten.bp.OffsetDateTime;

import java.util.UUID;

@Service
public class RestService {

    private final PersonApi personApi;
    private final ClassApi classApi;

    public RestService(ApiClient apiClient) {
        this.personApi = new PersonApi();
        this.personApi.setApiClient(apiClient);

        this.classApi = new ClassApi();
        this.classApi.setApiClient(apiClient);
    }

    public PersonInfo getPersonInfoByGUIDAndExpand(final String GUID, final String expand) throws ApiException {
        return personApi.personsIdGet(GUID, OffsetDateTime.now().plusHours(3L),  expand);
    }

    public ModelClass getClassById(UUID id) throws ApiException {
        return classApi.getClassById(id);
    }
}
