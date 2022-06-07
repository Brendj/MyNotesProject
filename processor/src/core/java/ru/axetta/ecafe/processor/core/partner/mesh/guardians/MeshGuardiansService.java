package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.items.ClientGuardianItem;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.partner.mesh.MeshPersonsSyncService;
import ru.axetta.ecafe.processor.core.partner.mesh.MeshResponseWithStatusCode;
import ru.axetta.ecafe.processor.core.partner.mesh.json.*;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

@DependsOn("runtimeContext")
@Component("meshGuardiansService")
public class MeshGuardiansService extends MeshPersonsSyncService {
    private static final Logger logger = LoggerFactory.getLogger(MeshGuardiansService.class);
    private static final String PERSONS_LIKE_URL = "/persons/like";
    private static final String PERSONS_CREATE_URL = "/persons/%s/agents";
    private static final String PERSONS_CHANGE_URL = "/persons/%s";
    private static final String PERSONS_DELETE_URL = "/persons/%s/agents/%s";
    private static final String DOCUMENT_CREATE_URL = "/persons/%s/documents";
    private static final String DOCUMENT_DELETE_URL = "/persons/%s/documents/%s";
    private static final String CONTACT_CREATE_URL = "/persons/%s/contacts";
    private static final String CONTACT_DELETE_URL = "/persons/%s/contacts/%s";
    public static final String PERSONS_LIKE_EXPAND = "documents,contacts";
    public static final Integer PERSONS_LIKE_LIMIT = 5;
    public static final Integer GUARDIAN_DEFAULT_TYPE = 1;
    private static final String PERSON_ID_STUB = "00000000-0000-0000-0000-000000000000";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final Integer PASSPORT_TYPE_ID = 15;
    public static final String MK_ERROR = "Ошибка в МЭШ Контингент: %s";
    public static final Integer CONTACT_MOBILE_TYPE_ID = 1;
    //todo проверить что CONTACT_EMAIL_TYPE_ID = 2
    public static final Integer CONTACT_EMAIL_TYPE_ID = 2;


    private MeshGuardianConverter getMeshGuardianConverter() {
        return RuntimeContext.getAppContext().getBean(MeshGuardianConverter.class);
    }

    public PersonListResponse searchPerson(String firstName,
                                           String patronymic,
                                           String lastName,
                                           Integer genderId,
                                           Date birthDate,
                                           String snils,
                                           String mobile,
                                           String email) {
        try {
            //checkClientData(client);
            String parameters = String.format("?person=%s&expand=%s&limit=%s", URLEncoder
                    .encode(buildSearchPersonParameters(
                            firstName, patronymic, lastName, genderId, birthDate, snils, mobile, email), "UTF-8"), PERSONS_LIKE_EXPAND, PERSONS_LIKE_LIMIT);
            ObjectMapper objectMapper = new ObjectMapper();
            MeshResponseWithStatusCode result = meshRestClient.executeGetMethod(PERSONS_LIKE_URL, parameters);
            if (result.getCode() == HttpStatus.SC_OK) {
                TypeFactory typeFactory = objectMapper.getTypeFactory();
                CollectionType collectionType = typeFactory.constructCollectionType(List.class, SimilarPerson.class);
                List<SimilarPerson> similarPersons = objectMapper.readValue(result.getResponse(), collectionType);
                return new PersonListResponse(getMeshGuardianConverter().toDTO(similarPersons)).okResponse();
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toPersonListDTO(errorResponse);
            }
        } catch (MeshGuardianNotEnoughClientDataException e) {
            return new PersonListResponse().notEnoughClientDataResponse(e.getMessage());
        } catch (Exception e) {
            logger.error("Error in searchPerson: ", e);
            return new PersonListResponse().internalErrorResponse();
        }
    }

    public PersonResponse createPerson(Long idOfOrg,
                                       String firstName,
                                       String patronymic,
                                       String lastName,
                                       Integer genderId,
                                       Date birthDate,
                                       String snils,
                                       String mobile,
                                       String email,
                                       String childMeshGuid,
                                       List<DulDetail> dulDetails,
                                       Integer agentTypeId,
                                       Integer relation,
                                       Integer typeOfLegalRepresent) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PersonAgent personAgent = buildPersonAgent(firstName, patronymic, lastName, genderId, birthDate, snils, mobile,
                    email, dulDetails, agentTypeId);
            String json = objectMapper.writeValueAsString(personAgent);
            MeshResponseWithStatusCode result = meshRestClient.executePostMethod(buildCreatePersonUrl(childMeshGuid), json);
            if (result.getCode() == HttpStatus.SC_OK) {
                PersonAgent personResult = objectMapper.readValue(result.getResponse(), PersonAgent.class);
                createGuardianInternal(idOfOrg, personResult.getAgentPersonId(), firstName, patronymic, lastName,
                        genderId, birthDate, snils, mobile, childMeshGuid, dulDetails, relation, typeOfLegalRepresent, agentTypeId);
                return new PersonResponse(personResult.getAgentPersonId()).okResponse();
            } else if (result.getCode() >= 500) {
                return new PersonResponse().internalErrorResponse("" + result.getCode());
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toPersonDTO(errorResponse);
            }
        } catch (ConnectTimeoutException te) {
            logger.error("Connection timeout in createPerson: ", te);
            return new PersonResponse().internalErrorResponse("Mesh service connection timeout");
        } catch (Exception e) {
            logger.error("Error in createPerson: ", e);
            return new PersonResponse().internalErrorResponse();
        }
    }

    public PersonResponse changePerson(String personId, String firstName,
                                       String patronymic, String lastName,
                                       Integer genderId, Date birthDate, String snils) {
        try {
            PersonListResponse personListResponse = searchPersonByMeshGuid(personId);
            if (!personListResponse.getCode().equals(PersonListResponse.OK_CODE))
                return new PersonResponse(personListResponse.getCode(), personListResponse.message);
            Integer id = personListResponse.getResponse().get(0).getId();
            ObjectMapper objectMapper = new ObjectMapper();
            ResponsePersons request = buildResponsePerson(personId, firstName, patronymic, lastName, genderId,
                    birthDate, snils, null, null, null);
            String json = objectMapper.writeValueAsString(request);
            MeshResponseWithStatusCode result = meshRestClient.executePutMethod(buildChangePersonUrl(id.longValue()), json);
            if (result.getCode() == HttpStatus.SC_OK) {
                ResponsePersons response = objectMapper.readValue(result.getResponse(), ResponsePersons.class);
                return new PersonResponse().okResponse();
            } else if (result.getCode() >= 500) {
                return new PersonResponse().internalErrorResponse("" + result.getCode());
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toPersonDTO(errorResponse);
            }
        } catch (ConnectTimeoutException te) {
            logger.error("Connection timeout in changePerson: ", te);
            return new PersonResponse().internalErrorResponse("Mesh service connection timeout");
        } catch (Exception e) {
            logger.error("Error in changePerson: ", e);
            return new PersonResponse().internalErrorResponse();
        }
    }

    private void createGuardianInternal(Long idOfOrg, String personId, String firstName,
                                        String patronymic, String lastName,
                                        Integer genderId, Date birthDate, String snils,
                                        String mobile, String сhildMeshGuid,
                                        List<DulDetail> dulDetails, Integer relation,
                                        Integer typeOfLegalRepresent, Integer agentTypeId) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Org org = session.load(Org.class, idOfOrg);
            ClientsMobileHistory clientsMobileHistory =
                    new ClientsMobileHistory("soap метод createMeshPerson");
            clientsMobileHistory.setShowing("АРМ");
            String remark = String.format("Создано в АРМ %s", new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
            Client guardian = ClientManager
                    .createGuardianTransactionFree(session, firstName, StringUtils.defaultIfEmpty(patronymic, ""), lastName, mobile, remark, genderId,
                            org, ClientCreatedFromType.ARM, "", null, null, null,
                            null, null, clientsMobileHistory);
            guardian.setMeshGUID(personId);
            guardian.setSan(snils);
            guardian.setBirthDate(birthDate);
            if (!CollectionUtils.isEmpty(dulDetails)) {
                guardian.setDulDetail(new HashSet<>(dulDetails));
            }
            session.update(guardian);

            ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
            clientGuardianHistory.setReason("soap метод createMeshPerson");
            clientGuardianHistory.setGuardian(mobile);
            String description = null;
            if (relation != null) {
                description = ClientGuardianRelationType.fromInteger(relation.intValue()).getDescription();
            }
            Client client = DAOUtils.findClientByMeshGuid(session, сhildMeshGuid);
            ClientGuardian clientGuardian = ClientManager
                    .createClientGuardianInfoTransactionFree(session, guardian, description, false,
                            client.getIdOfClient(), ClientCreatedFromType.MPGU, typeOfLegalRepresent, clientGuardianHistory);
            clientGuardian.setRoleType(ClientGuardianRoleType.fromInteger(agentTypeId));
            session.update(clientGuardian);
            session.flush();
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in createGuadianInternal: ", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public MeshGuardianResponse createPersonContact(String meshGuid, Integer typeId, String data, boolean defaultFlag) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Contact parameter = buildPersonContact(meshGuid, typeId, data, defaultFlag);
            String json = objectMapper.writeValueAsString(parameter);
            MeshResponseWithStatusCode result = meshRestClient.executePostMethod(buildCreateContactUrl(meshGuid), json);

            if (result.getCode() == HttpStatus.SC_OK) {
                PersonDocument personDocument = objectMapper.readValue(result.getResponse(), PersonDocument.class);
                return getMeshGuardianConverter().toDTO(personDocument);
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toDTO(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Error in createPersonContact: ", e);
            return new DocumentResponse().internalErrorResponse();
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
            Long idMkDocument = findIdMkDocument(meshGuid, dulDetail);
            MeshResponseWithStatusCode result = meshRestClient.executeDeleteMethod(buildDeleteAndModifyDocumentUrl(meshGuid, idMkDocument));
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

    private Long findIdMkDocument(String meshGuid, DulDetail dulDetail) {
        PersonListResponse personListResponse = searchPersonByMeshGuid(meshGuid);
        if (personListResponse.getResponse() == null || personListResponse.getResponse().isEmpty()) {
            throw new NullPointerException("Не найден клиент по \"meshGuid\"");
        }
        if (personListResponse.getResponse().get(0).getPersonDocuments() == null ||
                personListResponse.getResponse().get(0).getPersonDocuments().isEmpty()) {
            throw new NullPointerException("Не найден документ у клиента");
        }
        PersonDocument personDocument = personListResponse.getResponse().get(0).getPersonDocuments()
                .stream().filter(d -> dulDetail.getDocumentTypeId().equals(d.getDocumentTypeId().longValue()) &&
                        dulDetail.getNumber().equals(d.getNumber()))
                .findFirst().orElse(null);
        if (personDocument == null) {
            throw new NullPointerException("Не найден документ у клиента");
        }
        return personDocument.getDocumentTypeId().longValue();
    }

    public DocumentResponse modifyPersonDocument(String meshGuid, DulDetail dulDetail) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PersonDocument parameter = buildPersonDocument(dulDetail);
            String json = objectMapper.writeValueAsString(parameter);
            Long idMkDocument = findIdMkDocument(meshGuid, dulDetail);
            MeshResponseWithStatusCode result = meshRestClient.executePutMethod(buildDeleteAndModifyDocumentUrl(meshGuid, idMkDocument), json);

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

    Contact buildPersonContact(String personId, Integer typeId, String data, Boolean defaultFlag) {
        Contact contact = new Contact();
        contact.setId(0);
        contact.setPersonId(personId);
        contact.setTypeId(typeId);
        contact.setData(data);
        if (defaultFlag != null) {
            contact.setDefault(defaultFlag);
        }
        return contact;
    }

    PersonDocument buildPersonDocument(DulDetail dulDetail) {
        Format formatter = new SimpleDateFormat("dd.MM.yyyy");
        PersonDocument personDocument = new PersonDocument();
        personDocument.setId(0);
        personDocument.setPersonId(PERSON_ID_STUB);
        personDocument.setDocumentTypeId(dulDetail.getDocumentTypeId().intValue());
        personDocument.setNumber(dulDetail.getNumber());
        personDocument.setSeries(dulDetail.getSeries());
        personDocument.setSubdivisionCode(dulDetail.getSubdivisionCode());
        if (dulDetail.getIssued() != null) {
            personDocument.setIssued(formatter.format(dulDetail.getIssued()));
        }
        personDocument.setIssuer(dulDetail.getIssuer());
        if (dulDetail.getExpiration() != null) {
            personDocument.setExpiration(formatter.format(dulDetail.getExpiration()));
        }
        return personDocument;
    }

    private PersonAgent buildPersonAgent(String firstName,
                                         String patronymic,
                                         String lastName,
                                         Integer genderId,
                                         Date birthDate,
                                         String snils,
                                         String mobile,
                                         String email,
                                         List<DulDetail> dulDetails,
                                         Integer agentTypeId) throws Exception {
        PersonAgent personAgent = new PersonAgent();
        personAgent.setAgentTypeId(agentTypeId);
        personAgent.setId(0);
        personAgent.setPersonId(PERSON_ID_STUB);
        personAgent.setAgentPerson(buildResponsePerson(null, firstName, patronymic, lastName, genderId, birthDate, snils, mobile, email, dulDetails));
        /*if (StringUtils.isEmpty(guardian.getMeshGUID())) {
            personAgent.setAgentPerson(buildResponsePerson(guardian));
        } else {
            personAgent.setAgentPersonId(guardian.getMeshGUID());
        }*/

        return personAgent;
    }

    private String buildDeleteGuardianToClientUrl(String meshGuid, String id) {
        return String.format(PERSONS_DELETE_URL, meshGuid, id);
    }

    private String buildCreatePersonUrl(String meshGuid) {
        return String.format(PERSONS_CREATE_URL, meshGuid);
    }

    private String buildChangePersonUrl(Long id) {
        return String.format(PERSONS_CHANGE_URL, id);
    }

    private String buildCreateDocumentUrl(String meshGuid) {
        return String.format(DOCUMENT_CREATE_URL, meshGuid);
    }

    private String buildCreateContactUrl(String meshGuid) {
        return String.format(CONTACT_CREATE_URL, meshGuid);
    }

    private String buildDeleteAndModifyDocumentUrl(String meshGuid, Long id) {
        return String.format(DOCUMENT_DELETE_URL, meshGuid, id);
    }

    private ResponsePersons buildResponsePerson(String personId,
                                                String firstName,
                                                String patronymic,
                                                String lastName,
                                                Integer genderId,
                                                Date birthDate,
                                                String snils,
                                                String mobile,
                                                String email,
                                                List<DulDetail> dulDetails) throws Exception {
        ResponsePersons person = new ResponsePersons();
        person.setId(0);
        if (personId == null) {
            person.setPersonId(PERSON_ID_STUB);
        } else {
            person.setPersonId(personId);
        }
        person.setLastname(lastName);
        person.setFirstname(firstName);
        person.setPatronymic(patronymic);
        person.setBirthdate(convertDate(birthDate));
        person.setGenderId(genderId);
        person.setSnils(snils);
        if (!StringUtils.isEmpty(mobile) || !StringUtils.isEmpty(email)) {
            List<Contact> contacts = new ArrayList<>();
            if (!StringUtils.isEmpty(mobile)) {
                contacts.add(buildContact(CONTACT_MOBILE_TYPE_ID, convertMobileToMK(mobile)));
            }
            if (!StringUtils.isEmpty(email)) {
                contacts.add(buildContact(CONTACT_EMAIL_TYPE_ID, email));
            }
            person.setContacts(contacts);
        }
        if (!CollectionUtils.isEmpty(dulDetails)) {
            List<PersonDocument> documents = new ArrayList<>();
            for (DulDetail dulDetail : dulDetails) {
                documents.add(buildPersonDocument(dulDetail));
            }
            person.setDocuments(documents);
        }

        return person;
    }

    private Contact buildContact(Integer typeId, String value) {
        Contact contact = new Contact();
        contact.setId(0);
        contact.setPersonId(PERSON_ID_STUB);
        contact.setTypeId(typeId);
        contact.setData(value);
        return contact;
    }

    private String convertMobileToMK(String mobile) {
        if (mobile.startsWith("7") || mobile.startsWith("8")) {
            return mobile.substring(1);
        }
        return mobile;
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

    private String buildSearchPersonParameters(String firstName,
                                               String patronymic,
                                               String lastName,
                                               Integer genderId,
                                               Date birthDate,
                                               String snils,
                                               String mobile,
                                               String email) throws Exception {
        ResponsePersons person = buildResponsePerson(null, firstName, patronymic, lastName, genderId, birthDate, snils, mobile, email, null);

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
        if (isppGender == 0) return 2;
        else return 1;
    }

    public PersonListResponse searchPersonByMeshGuid(String meshGuid) {
        try {
            String parameters = String.format("?person=%s&expand=%s&limit=%s", meshGuid, PERSONS_LIKE_EXPAND, PERSONS_LIKE_LIMIT);
            ObjectMapper objectMapper = new ObjectMapper();
            MeshResponseWithStatusCode result = meshRestClient.executeGetMethod(PERSONS_CHANGE_URL, parameters);
            if (result.getCode() == HttpStatus.SC_OK) {
                TypeFactory typeFactory = objectMapper.getTypeFactory();
                CollectionType collectionType = typeFactory.constructCollectionType(List.class, SimilarPerson.class);
                List<SimilarPerson> similarPersons = objectMapper.readValue(result.getResponse(), collectionType);
                return new PersonListResponse(getMeshGuardianConverter().toDTO(similarPersons)).okResponse();
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toPersonListDTO(errorResponse);
            }
        } catch (MeshGuardianNotEnoughClientDataException e) {
            return new PersonListResponse().notEnoughClientDataResponse(e.getMessage());
        } catch (Exception e) {
            logger.error("Error in searchDocumentsByMeshGuid: ", e);
            return new PersonListResponse().internalErrorResponse();
        }
    }

    public PersonResponse addGuardianToClient(String meshGuid, String childMeshGuid, Integer agentTypeId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            PersonAgent personAgent = new PersonAgent();
            personAgent.setAgentTypeId(agentTypeId);
            personAgent.setId(0);
            personAgent.setPersonId(childMeshGuid);
            personAgent.setAgentPersonId(meshGuid);

            String json = objectMapper.writeValueAsString(personAgent);
            MeshResponseWithStatusCode result = meshRestClient.executePostMethod(buildCreatePersonUrl(childMeshGuid), json);
            if (result.getCode() == HttpStatus.SC_OK) {
                PersonAgent personResult = objectMapper.readValue(result.getResponse(), PersonAgent.class);
                return new PersonResponse(personResult.getAgentPersonId()).okResponse();
            } else if (result.getCode() >= 500) {
                return new PersonResponse().internalErrorResponse("" + result.getCode());
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toPersonDTO(errorResponse);
            }
        } catch (ConnectTimeoutException te) {
            logger.error("Connection timeout in addGuardianToClient: ", te);
            return new PersonResponse().internalErrorResponse("Mesh service connection timeout");
        } catch (Exception e) {
            logger.error("Error in addGuardianToClient: ", e);
            return new PersonResponse().internalErrorResponse();
        }
    }

    public PersonResponse deleteGuardianToClient(String agentMeshGuid, String childMeshGuid) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PersonListResponse personListResponse = searchPersonByMeshGuid(agentMeshGuid);
            if (!personListResponse.getCode().equals(PersonListResponse.OK_CODE))
                return new PersonResponse(personListResponse.getCode(), personListResponse.message);
            Integer id = personListResponse.getResponse().get(0).getId();

            MeshResponseWithStatusCode result = meshRestClient
                    .executeDeleteMethod(buildDeleteGuardianToClientUrl(childMeshGuid, id.toString()));
            if (result.getCode() == HttpStatus.SC_OK) {
                return new PersonResponse().okResponse();
            } else if (result.getCode() >= 500) {
                return new PersonResponse().internalErrorResponse("" + result.getCode());
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toPersonDTO(errorResponse);
            }

        } catch (ConnectTimeoutException te) {
            logger.error("Connection timeout in deleteGuardianToClient: ", te);
            return new PersonResponse().internalErrorResponse("Mesh service connection timeout");
        } catch (Exception e) {
            logger.error("Error in deleteGuardianToClient: ", e);
            return new PersonResponse().internalErrorResponse();
        }
    }

    public PersonResponse createPersonAndAddClient(Long idOfOrg, String firstName, String secondName, String surname, Integer gender,
                                         Date birthDate, String san, String mobile, String email,
                                         List<ClientGuardianItem> clientWardItems) {

        PersonResponse personResponse = createPerson(idOfOrg, firstName, secondName, surname, gender, birthDate, san,
                mobile, email, clientWardItems.get(0).getMeshGuid(), null, clientWardItems.get(0).getRole(),
                clientWardItems.get(0).getRelation(), clientWardItems.get(0).getRepresentativeType());

        if (personResponse.getCode().equals(MeshGuardianResponse.OK_CODE) && clientWardItems.size() > 1){
            for (int i = 1; i < clientWardItems.size(); i++) {
                addGuardianToClient(personResponse.getMeshGuid(),
                        clientWardItems.get(i).getMeshGuid(), clientWardItems.get(i).getRole());
            }
        }
        return personResponse;
    }
}
