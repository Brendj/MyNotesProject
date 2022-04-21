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
import ru.axetta.ecafe.processor.core.partner.mesh.MeshRestClient;
import ru.axetta.ecafe.processor.core.partner.mesh.json.*;
import ru.axetta.ecafe.processor.core.persistence.Client;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@DependsOn("runtimeContext")
@Component("meshGuardiansService")
public class MeshGuardiansService extends MeshPersonsSyncService {
    private static final Logger logger = LoggerFactory.getLogger(MeshGuardiansService.class);
    private static final String PERSONS_LIKE_URL = "/persons/like";
    private static final String DOCUMENT_CREATE_URL = "/persons/%s/documents";
    public static final String PERSONS_LIKE_EXPAND = "children";
    public static final Integer PERSONS_LIKE_LIMIT = 5;
    private static final String PERSON_ID_STUB = "00000000-0000-0000-0000-000000000000";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final Integer PASSPORT_TYPE_ID = 15;
    public static final String MK_ERROR = "Ошибка в МЭШ Контингент: %s";


    private MeshGuardianConverter getMeshGuardianConverter() {
        return RuntimeContext.getAppContext().getBean(MeshGuardianConverter.class);
    }

    public PersonResponse searchPerson(Client client) {
        try {
            checkClientData(client);
            String parameters = String.format("?person=%s&expand=%s&limit=%s", URLEncoder
                    .encode(buildSearchPersonParameters(client), "UTF-8"), PERSONS_LIKE_EXPAND, PERSONS_LIKE_LIMIT);
            byte[] result = meshRestClient.executeRequest(PERSONS_LIKE_URL, parameters);
            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            CollectionType collectionType = typeFactory.constructCollectionType(List.class, SimilarPerson.class);
            List<SimilarPerson> similarPersons = objectMapper.readValue(result, collectionType);
            return new PersonResponse(getMeshGuardianConverter().toDTO(similarPersons)).okResponse();
        } catch (MeshGuardianNotEnoughClientDataException e) {
            return new PersonResponse().notEnoughClientDataResponse(e.getMessage());
        } catch (Exception e) {
            logger.error("Error in searchPerson: ", e);
            return new PersonResponse().internalErrorResponse();
        }
    }

    public DocumentResponse createPersonDocument(Client client) {
        try {
//            checkClientData(client);
            ObjectMapper objectMapper = new ObjectMapper();
            PersonDocument parameter = buildPersonDocument();
            String json = objectMapper.writeValueAsString(parameter);
            MeshResponseWithStatusCode result = meshRestClient.executePostMethod(buildCreateDocumentUrl(client), json);

            if (result.getCode() == HttpStatus.SC_OK) {
                PersonDocument personDocument = objectMapper.readValue(result.getResponse(), PersonDocument.class);
                return getMeshGuardianConverter().toDTO(personDocument);
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toDTO(errorResponse);
                //return new DocumentResponse(result.getCode(), String.format(MK_ERROR, new String(result.getResponse(), StandardCharsets.UTF_8)));
            }
        } catch (Exception e) {
            logger.error("Error in createPersonDocument: ", e);
            return new DocumentResponse().internalErrorResponse();
        }
    }

    PersonDocument buildPersonDocument() {
        PersonDocument personDocument = new PersonDocument();
        personDocument.setId(0);
        personDocument.setPersonId(PERSON_ID_STUB);
        personDocument.setDocumentTypeId(15);
        personDocument.setNumber("123456");
        personDocument.setSeries("9922");
        return personDocument;
    }

    private String buildCreateDocumentUrl(Client client) {
        return String.format(DOCUMENT_CREATE_URL, client.getMeshGUID());
    }

    private String buildSearchPersonParameters(Client client) throws Exception {
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

    //todo брать документ из новой таблицы документов клиента
    private List<PersonDocument> getClientDocument(Client client) {
        if (StringUtils.isEmpty(client.getPassportNumber())) return null;
        PersonDocument document = new PersonDocument();
        document.setId(0);
        document.setPersonId(PERSON_ID_STUB);
        document.setDocumentTypeId(PASSPORT_TYPE_ID);
        document.setNumber(client.getPassportNumber());
        document.setSeries(client.getPassportSeries());
        return Arrays.asList(document);
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
