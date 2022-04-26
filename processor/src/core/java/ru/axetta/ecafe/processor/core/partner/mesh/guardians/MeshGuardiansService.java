package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.MeshPersonsSyncService;
import ru.axetta.ecafe.processor.core.partner.mesh.MeshResponseWithStatusCode;
import ru.axetta.ecafe.processor.core.partner.mesh.json.*;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.DulDetail;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DependsOn("runtimeContext")
@Component("meshGuardiansService")
public class MeshGuardiansService extends MeshPersonsSyncService {
    private static final Logger logger = LoggerFactory.getLogger(MeshGuardiansService.class);
    private static final String PERSONS_LIKE_URL = "/persons/like";
    private static final String PERSONS_CREATE_URL = "/persons/%s/agents";
    private static final String DOCUMENT_CREATE_URL = "/persons/%s/documents";
    private static final String DOCUMENT_DELETE_URL = "/persons/%s/documents/%s";
    public static final String PERSONS_LIKE_EXPAND = "children,documents";
    public static final Integer PERSONS_LIKE_LIMIT = 5;
    public static final Integer GUARDIAN_DEFAULT_TYPE = 1;
    private static final String PERSON_ID_STUB = "00000000-0000-0000-0000-000000000000";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final Integer PASSPORT_TYPE_ID = 15;
    public static final String MK_ERROR = "Ошибка в МЭШ Контингент: %s";


    private MeshGuardianConverter getMeshGuardianConverter() {
        return RuntimeContext.getAppContext().getBean(MeshGuardianConverter.class);
    }

    public PersonListResponse searchPerson(Client client) {
        try {
            checkClientData(client);
            String parameters = String.format("?person=%s&expand=%s&limit=%s", URLEncoder
                    .encode(buildSearchPersonParameters(client), "UTF-8"), PERSONS_LIKE_EXPAND, PERSONS_LIKE_LIMIT);
            byte[] result = meshRestClient.executeRequest(PERSONS_LIKE_URL, parameters);
            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            CollectionType collectionType = typeFactory.constructCollectionType(List.class, SimilarPerson.class);
            List<SimilarPerson> similarPersons = objectMapper.readValue(result, collectionType);
            return new PersonListResponse(getMeshGuardianConverter().toDTO(similarPersons)).okResponse();
        } catch (MeshGuardianNotEnoughClientDataException e) {
            return new PersonListResponse().notEnoughClientDataResponse(e.getMessage());
        } catch (Exception e) {
            logger.error("Error in searchPerson: ", e);
            return new PersonListResponse().internalErrorResponse();
        }
    }

    public PersonResponse createPerson(Client child, Client guardian) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PersonAgent personAgent = buildPersonAgent(guardian);
            String json = objectMapper.writeValueAsString(personAgent);
            MeshResponseWithStatusCode result = meshRestClient.executePostMethod(buildCreatePersonUrl(child.getMeshGUID()), json);
            if (result.getCode() == HttpStatus.SC_OK) {
                PersonAgent personResult = objectMapper.readValue(result.getResponse(), PersonAgent.class);
                return new PersonResponse(getMeshGuardianConverter().toDTO(personResult)).okResponse();
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toPersonDTO(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Error in createPerson: ", e);
            return new PersonResponse().internalErrorResponse();
        }
    }

    public DocumentResponse createPersonDocument(String meshGuid, DulDetail dulDetail) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PersonDocument parameter = buildPersonDocument(dulDetail);
            String json = objectMapper.writeValueAsString(parameter);
            MeshResponseWithStatusCode result = meshRestClient.executePostMethod(buildCreateDocumentUrl(meshGuid), json);

            if (result.getCode() == HttpStatus.SC_OK) {
                PersonDocument personDocument = objectMapper.readValue(result.getResponse(), PersonDocument.class);
                return getMeshGuardianConverter().toDTO(personDocument);
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toDTO(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Error in createPersonDocument: ", e);
            return new DocumentResponse().internalErrorResponse();
        }
    }

    public DocumentResponse deletePersonDocument(String meshGuid, DulDetail dulDetail) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MeshResponseWithStatusCode result = meshRestClient.executeDeleteMethod(buildDeleteAndModifyDocumentUrl(meshGuid, dulDetail.getIdMkDocument()));

            if (result.getCode() == HttpStatus.SC_OK) {
                return new DocumentResponse().okResponse();
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toDTO(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Error in deletePersonDocument: ", e);
            return new DocumentResponse().internalErrorResponse();
        }
    }

    public DocumentResponse modifyPersonDocument(String meshGuid, DulDetail dulDetail) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PersonDocument parameter = buildPersonDocument(dulDetail);
            String json = objectMapper.writeValueAsString(parameter);
            MeshResponseWithStatusCode result = meshRestClient.executePutMethod(buildDeleteAndModifyDocumentUrl(meshGuid, dulDetail.getIdMkDocument()), json);

            if (result.getCode() == HttpStatus.SC_OK) {
                PersonDocument personDocument = objectMapper.readValue(result.getResponse(), PersonDocument.class);
                return getMeshGuardianConverter().toDTO(personDocument);
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toDTO(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Error in modifyPersonDocument: ", e);
            return new DocumentResponse().internalErrorResponse();
        }
    }

    PersonDocument buildPersonDocument(DulDetail dulDetail) {
        PersonDocument personDocument = new PersonDocument();
        personDocument.setId(0);
        personDocument.setPersonId(PERSON_ID_STUB);
        personDocument.setDocumentTypeId(dulDetail.getDocumentTypeId().intValue());
        personDocument.setNumber(dulDetail.getNumber());
        personDocument.setSeries(dulDetail.getSeries());
        personDocument.setSubdivisionCode(dulDetail.getSubdivisionCode());
        personDocument.setIssued(dulDetail.getIssued());
        personDocument.setIssuer(dulDetail.getIssuer());
        personDocument.setExpiration(dulDetail.getExpiration());
        return personDocument;
    }

    private PersonAgent buildPersonAgent(Client guardian) throws Exception {
        PersonAgent personAgent = new PersonAgent();
        personAgent.setAgentTypeId(GUARDIAN_DEFAULT_TYPE);
        if (StringUtils.isEmpty(guardian.getMeshGUID())) {
            personAgent.setAgentPerson(buildResponsePerson(guardian));
        } else {
            personAgent.setAgentPersonId(guardian.getMeshGUID());
        }

        return personAgent;
    }


    private String buildCreatePersonUrl(String meshGuid) {
        return String.format(PERSONS_CREATE_URL, meshGuid);
    }

    private String buildCreateDocumentUrl(String meshGuid) {
        return String.format(DOCUMENT_CREATE_URL, meshGuid);
    }

    private String buildDeleteAndModifyDocumentUrl(String meshGuid, Long id) {
        return String.format(DOCUMENT_DELETE_URL, meshGuid, id);
    }

    private ResponsePersons buildResponsePerson(Client client) throws Exception {
        ResponsePersons person = new ResponsePersons();
        person.setId(0);
        person.setPersonId(PERSON_ID_STUB);
        person.setLastname(client.getPerson().getSurname());
        person.setFirstname(client.getPerson().getFirstName());
        person.setPatronymic(client.getPerson().getSecondName());
        person.setBirthdate(convertDate(client.getBirthDate()));
        person.setGenderId(getGender(client.getGender()));
        person.setSnils(client.getSan());
        person.setDocuments(getClientDocument(client));
        return person;
    }

    private String buildSearchPersonParameters(Client client) throws Exception {
        ResponsePersons person = buildResponsePerson(client);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(person);
    }

    private void checkClientData(Client client) throws MeshGuardianNotEnoughClientDataException {
        if (StringUtils.isEmpty(client.getPerson().getFirstName()) || StringUtils.isEmpty(client.getPerson().getSurname())
            || client.getBirthDate() == null || client.getGender() == null
            || (StringUtils.isEmpty(client.getSan()) && getClientDocument(client) == null)) {
            throw new MeshGuardianNotEnoughClientDataException("не заполнены обязательные параметры: фамилия, имя, " +
                    "дата рождения, пол, и одно из: СНИЛС либо паспортные данные");
        }
    }

    private List<PersonDocument> getClientDocument(Client client) {
        if (client.getDulDetail().isEmpty()) return null;
        List<PersonDocument> result = new ArrayList<>();
        for (DulDetail dulDetail : client.getDulDetail()) {
            result.add(buildPersonDocument(dulDetail));
        }
        return result;
    }

    private String convertDate(Date date) throws MeshGuardianNotEnoughClientDataException {
        if (date == null) throw new MeshGuardianNotEnoughClientDataException("Client birth date is null");
        DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        return dateFormat.format(date);
    }

    private Integer getGender(Integer isppGender) throws MeshGuardianNotEnoughClientDataException {
        if (isppGender == null) throw new MeshGuardianNotEnoughClientDataException("Client gender is null");
        if (isppGender == 0) return 2; else return 1;
    }

    public void createPerson() {

    }

    public void createGuardianship() {

    }
}
