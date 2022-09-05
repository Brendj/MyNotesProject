package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
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
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.partner.mesh.MeshPersonsSyncService;
import ru.axetta.ecafe.processor.core.partner.mesh.MeshResponseWithStatusCode;
import ru.axetta.ecafe.processor.core.partner.mesh.json.*;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.service.DulDetailService;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@DependsOn("runtimeContext")
@Component("meshGuardiansService")
public class MeshGuardiansService extends MeshPersonsSyncService {
    private static final Logger logger = LoggerFactory.getLogger(MeshGuardiansService.class);
    private static final String PERSONS_LIKE_URL = "/persons/like";
    private static final String PERSONS_WITH_EDUCATION_CREATE_URL = "/persons/%s/agents";
    private static final String PERSONS_CREATE_URL = "/persons";
    private static final String PERSONS_CHANGE_URL = "/persons/%s";
    private static final String PERSONS_DELETE_URL = "/persons/%s/agents/%s";
    private static final String DOCUMENT_CREATE_URL = "/persons/%s/documents";
    private static final String DOCUMENT_DELETE_URL = "/persons/%s/documents/%s";
    private static final String CONTACT_CREATE_URL = "/persons/%s/contacts";
    private static final String CONTACT_CHANGE_URL = "/persons/%s/contacts/%s";
    public static final String PERSONS_LIKE_EXPAND = "children,documents,contacts";
    public static final String PERSONS_SEARCH_EXPAND = "documents,contacts,agents.documents,children";
    public static final String ID_SEARCH_EXPAND = "agents,documents,contacts";
    public static final Integer PERSONS_LIKE_LIMIT = 5;
    private static final String PERSON_ID_STUB = "00000000-0000-0000-0000-000000000000";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String MK_ERROR = "Ошибка в МЭШ Контингент: %s";
    public static final Integer CONTACT_MOBILE_TYPE_ID = 1;
    public static final Integer CONTACT_EMAIL_TYPE_ID = 3;


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
                                           String email,
                                           List<DulDetail> dulDetails) {
        try {
            //checkClientData(client);
            String parameters = String.format("?person=%s&expand=%s&limit=%s", URLEncoder
                    .encode(buildSearchPersonParameters(
                            firstName, patronymic, lastName, genderId, birthDate, snils, mobile, email, dulDetails), "UTF-8"), PERSONS_LIKE_EXPAND, PERSONS_LIKE_LIMIT);
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

    public MeshAgentResponse createPersonWithEducation(Session persistenceSession, Long idOfOrg,
                                                       String firstName,
                                                       String patronymic,
                                                       String lastName,
                                                       Integer genderId,
                                                       Date birthDate,
                                                       String snils,
                                                       String mobile,
                                                       String email,
                                                       Client child,
                                                       List<DulDetail> dulDetails,
                                                       Integer agentTypeId,
                                                       Integer relation,
                                                       Integer typeOfLegalRepresent,
                                                       Boolean informing) {
        try {
            Client guardian = createGuardianInternal(persistenceSession, idOfOrg, firstName, patronymic, lastName,
                    genderId, birthDate, snils, mobile, child, dulDetails, relation, typeOfLegalRepresent, agentTypeId, informing);
            ObjectMapper objectMapper = new ObjectMapper();
            PersonAgent personAgent = buildPersonAgent(firstName, patronymic, lastName, genderId, birthDate, snils, mobile,
                    email, dulDetails, agentTypeId);
            String json = objectMapper.writeValueAsString(personAgent);
            MeshResponseWithStatusCode result = meshRestClient.executePostMethod(buildCreatePersonUrl(child.getMeshGUID()), json);
            if (result.getCode() == HttpStatus.SC_OK) {
                PersonAgent personResult = objectMapper.readValue(result.getResponse(), PersonAgent.class);
                guardian.setMeshGUID(personResult.getAgentPersonId());
                persistenceSession.update(guardian);
                return getMeshGuardianConverter().toAgentDTO(personResult).okResponse();
            } else if (result.getCode() >= 500) {
                return new MeshAgentResponse().internalErrorResponse("" + result.getCode());
            } else {
                return getMeshAgentResponse(objectMapper, result);
            }
        } catch (ConnectTimeoutException te) {
            logger.error("Connection timeout in createPersonWithEducation: ", te);
            return new MeshAgentResponse().internalErrorResponse("Mesh service connection timeout");
        } catch (Exception e) {
            logger.error("Error in createPersonWithEducation: ", e);
            return new MeshAgentResponse().internalErrorResponse();
        }
    }

    public PersonResponse changePerson(String personId, String firstName,
                                       String patronymic, String lastName,
                                       Integer genderId, Date birthDate, String snils) {
        try {
            PersonResponse personResponse = searchPersonByMeshGuid(personId);
            if (!personResponse.getCode().equals(PersonListResponse.OK_CODE))
                return new PersonResponse(personResponse.getCode(), personResponse.message);
            Integer id = personResponse.getResponse().getId();
            ObjectMapper objectMapper = new ObjectMapper();
            ResponsePersons request = buildResponsePerson(personId, firstName, patronymic, lastName, genderId,
                    birthDate, snils, null, null, null);
            String json = objectMapper.writeValueAsString(request);
            MeshResponseWithStatusCode result = meshRestClient.executePutMethod(buildChangePersonUrl(id.longValue()), json);
            if (result.getCode() == HttpStatus.SC_OK) {
                ResponsePersons responsePersons = objectMapper.readValue(result.getResponse(), ResponsePersons.class);
                return new PersonResponse(getMeshGuardianConverter().toDTO(responsePersons)).okResponse();
            } else if (result.getCode() >= 500) {
                return new PersonResponse().internalErrorResponse("" + result.getCode());
            } else {
                return getPersonResponse(objectMapper, result);
            }
        } catch (ConnectTimeoutException te) {
            logger.error("Connection timeout in changePerson: ", te);
            return new PersonResponse().internalErrorResponse("Mesh service connection timeout");
        } catch (Exception e) {
            logger.error("Error in changePerson: ", e);
            return new PersonResponse().internalErrorResponse();
        }
    }

    private Client createGuardianInternal(Session session, Long idOfOrg, String firstName,
                                        String patronymic, String lastName,
                                        Integer genderId, Date birthDate, String snils,
                                        String mobile, Client child,
                                        List<DulDetail> dulDetails, Integer relation,
                                        Integer typeOfLegalRepresent, Integer agentTypeId,
                                        Boolean informing) throws Exception {

        Org org = session.load(Org.class, idOfOrg);
        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("soap метод createMeshPerson");
        clientsMobileHistory.setShowing("АРМ");
        String remark = String.format("Создано в АРМ %s", new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
        Client guardian = ClientManager
                .createGuardianTransactionFree(session, firstName, StringUtils.defaultIfEmpty(patronymic, ""), lastName, mobile, remark, genderId,
                        org, ClientCreatedFromType.ARM, "", null, null, null,
                        null, null, clientsMobileHistory);
        guardian.setSan(snils);
        guardian.setBirthDate(birthDate);
        session.update(guardian);
        RuntimeContext.getAppContext().getBean(DulDetailService.class)
                .saveDulOnlyISPP(session, dulDetails, guardian.getIdOfClient());

        ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
        clientGuardianHistory.setReason("soap метод createMeshPerson");
        clientGuardianHistory.setGuardian(mobile);
        String description = null;
        if (relation != null) {
            description = ClientGuardianRelationType.fromInteger(relation).getDescription();
        }
        ClientGuardian clientGuardian = ClientManager
                .createClientGuardianInfoTransactionFree(session, guardian, description, null, informing,
                        child.getIdOfClient(), ClientCreatedFromType.MPGU, typeOfLegalRepresent, clientGuardianHistory);
        clientGuardian.setRoleType(ClientGuardianRoleType.fromInteger(agentTypeId));
        session.update(clientGuardian);
        return guardian;
    }

    public Client createGuardianInternalByMeshGuardianPerson(
            Session session, MeshGuardianPerson guardianPerson, Org org, String remark,
            ClientCreatedFromType clientCreatedFromType, ClientsMobileHistory clientsMobileHistory) throws Exception {

        Client guardian = ClientManager
                .createGuardianTransactionFree(session, guardianPerson.getFirstName(), guardianPerson.getSecondName(),
                        guardianPerson.getSurname(), guardianPerson.getMobile(), remark, guardianPerson.getIsppGender(),
                        org, clientCreatedFromType, "", null, null, null,
                        null, null, clientsMobileHistory);
        guardian.setMeshGUID(guardianPerson.getMeshGuid());
        guardian.setSan(guardianPerson.getSnils());
        guardian.setBirthDate(guardianPerson.getBirthDate());
        session.update(guardian);

        if (guardianPerson.getDocument() != null && !guardianPerson.getDocument().isEmpty()) {
            List<DulDetail> dulDetails = guardianPerson.getDocument()
                    .stream().map(MeshDocumentResponse::getDulDetail).collect(Collectors.toList());

            RuntimeContext.getAppContext().getBean(DulDetailService.class).saveDulOnlyISPP(session, dulDetails, guardian.getIdOfClient());
        }

        return guardian;
    }

    /**
     * Принимаем контакты -> приходит пустой контакт или null?
     * Нет. Ищем контакт (по типу) у персоны -> если контакт есть - меняем, иначе - создаем (изменение, создание)
     * Да. Ищем контакт у персоны (по типу) -> если контакт есть - удаляем, иначе - ничего не делаем (удаление)
     */
    public MeshContactResponse savePersonContact(String meshGuid, Map<Integer, String> contacts) {
        IdListResponse contactListResponse = searchIdByMeshGuid(meshGuid);
        MeshContactResponse meshContactResponse = new MeshContactResponse().okResponse();
        if (!contactListResponse.getCode().equals(PersonListResponse.OK_CODE))
            return new MeshContactResponse(contactListResponse.getCode(), contactListResponse.message);
        for (Map.Entry<Integer, String> contact : contacts.entrySet()) {
            contact.setValue(validateContact(contact.getKey(), contact.getValue()));
            boolean isKeyAlreadyInMk = false;
            for (ContactsIdResponse contactsIdResponse : contactListResponse.getContactResponse()) {
                if (contactsIdResponse.getContactType().equals(contact.getKey()) && contactsIdResponse.getDefault()) {
                    isKeyAlreadyInMk = true;
                    if (contact.getValue() == null || contact.getValue().isEmpty()) {
                        meshContactResponse = deletePersonContact(meshGuid, contactsIdResponse.getContactId());
                    } else {
                        meshContactResponse = modifyPersonContact(meshGuid, contact.getKey(), contact.getValue(),
                                contactsIdResponse.getContactId());
                    }
                    break;
                }
            }
            if (!isKeyAlreadyInMk && contact.getValue() != null && !contact.getValue().isEmpty()) {
                meshContactResponse = createPersonContact(meshGuid, contact.getKey(), contact.getValue());
            }
            if (!meshContactResponse.getCode().equals(PersonListResponse.OK_CODE))
                return new MeshContactResponse(meshContactResponse.getCode(), meshContactResponse.message);
        }
        return new MeshContactResponse().okResponse();
    }

    private String validateContact(Integer key, String value) {
        if (Objects.equals(key, MeshGuardiansService.CONTACT_MOBILE_TYPE_ID)) {
            if (value != null && !value.isEmpty()) {
                return convertMobileToMK(value);
            }
        }
        return value;
    }

    public MeshContactResponse createPersonContact(String meshGuid, Integer typeId, String data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Contact parameter = buildPersonContact(meshGuid, typeId, data);
            String json = objectMapper.writeValueAsString(parameter);
            MeshResponseWithStatusCode result = meshRestClient.executePostMethod(buildCreateContactUrl(meshGuid), json);
            if (result.getCode() == HttpStatus.SC_OK) {
                Contact contact = objectMapper.readValue(result.getResponse(), Contact.class);
                return getMeshGuardianConverter().toContactDTO(contact).okResponse();
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toContactErrorDTO(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Error in createPersonContact: ", e);
            return new MeshContactResponse().internalErrorResponse();
        }
    }

    public MeshContactResponse modifyPersonContact(String meshGuid, Integer typeId, String data, Integer id) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Contact parameter = buildEditPersonContact(meshGuid, typeId, data, id);
            String json = objectMapper.writeValueAsString(parameter);
            MeshResponseWithStatusCode result = meshRestClient.executePutMethod(buildChangeContactUrl(meshGuid, id), json);
            if (result.getCode() == HttpStatus.SC_OK) {
                Contact contact = objectMapper.readValue(result.getResponse(), Contact.class);
                return getMeshGuardianConverter().toContactDTO(contact).okResponse();
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toContactErrorDTO(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Error in modifyPersonContact: ", e);
            return new MeshContactResponse().internalErrorResponse();
        }
    }

    public MeshContactResponse deletePersonContact(String meshGuid, Integer id) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MeshResponseWithStatusCode result = meshRestClient.executeDeleteMethod(buildChangeContactUrl(meshGuid, id));
            if (result.getCode() == HttpStatus.SC_OK) {
                return new MeshContactResponse().okResponse();
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toContactErrorDTO(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Error in deletePersonContact: ", e);
            return new MeshContactResponse().internalErrorResponse();
        }
    }

    private String buildChangeContactUrl(String meshGuid, Integer id) {
        return String.format(CONTACT_CHANGE_URL, meshGuid, id);
    }

    public MeshDocumentResponse createPersonDocument(String meshGuid, DulDetail dulDetail) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PersonDocument parameter = buildPersonDocument(dulDetail);
            String json = objectMapper.writeValueAsString(parameter);
            MeshResponseWithStatusCode result = meshRestClient.executePostMethod(buildCreateDocumentUrl(meshGuid), json);

            if (result.getCode() == HttpStatus.SC_OK) {
                PersonDocument personDocument = objectMapper.readValue(result.getResponse(), PersonDocument.class);
                return getMeshGuardianConverter().toDTO(personDocument).okResponse();
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toDTO(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Error in createPersonDocument: ", e);
            return new MeshDocumentResponse().internalErrorResponse();
        }
    }

    public MeshDocumentResponse deletePersonDocument(String meshGuid, DulDetail dulDetail) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            IdListResponse idListResponse = findIdMkDocument(meshGuid, dulDetail.getDocumentTypeId());
            if (!idListResponse.getCode().equals(IdListResponse.OK_CODE))
                return new MeshDocumentResponse(idListResponse.code, idListResponse.message);
            Integer id = idListResponse.getDocumentResponse().get(0).getDocumentId();
            if (id == 0)
                return new MeshDocumentResponse().okResponse();
            MeshResponseWithStatusCode result = meshRestClient.executeDeleteMethod(buildDeleteAndModifyDocumentUrl(meshGuid, id));
            if (result.getCode() == HttpStatus.SC_OK) {
                return new MeshDocumentResponse().okResponse();
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toDTO(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Error in deletePersonDocument: ", e);
            return new MeshDocumentResponse().internalErrorResponse();
        }
    }

    private IdListResponse findIdMkDocument(String meshGuid, Long documentTypeId) {
        IdListResponse agentListResponse = searchIdByMeshGuid(meshGuid);
        if (!agentListResponse.getCode().equals(PersonListResponse.OK_CODE))
            return new IdListResponse(agentListResponse.getCode(), agentListResponse.message);
        Integer id = 0;
        for (DocumentIdResponse documentIdResponse : agentListResponse.getDocumentResponse()) {
            if (documentIdResponse.getDocumentType() == documentTypeId.intValue())
                id = documentIdResponse.getDocumentId();
        }
        IdListResponse idListResponse = new IdListResponse();
        idListResponse.setDocumentResponse(Collections.singletonList(new DocumentIdResponse(id, null)));
        return idListResponse.okResponse();
    }

    public MeshDocumentResponse modifyPersonDocument(String meshGuid, DulDetail dulDetail) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PersonDocument parameter = buildPersonDocument(dulDetail);
            String json = objectMapper.writeValueAsString(parameter);
            IdListResponse idListResponse = findIdMkDocument(meshGuid, dulDetail.getDocumentTypeId());
            if (!idListResponse.getCode().equals(IdListResponse.OK_CODE))
                return new MeshDocumentResponse(idListResponse.code, idListResponse.message);
            Integer id = idListResponse.getDocumentResponse().get(0).getDocumentId();
            MeshResponseWithStatusCode result = meshRestClient.executePutMethod(buildDeleteAndModifyDocumentUrl(meshGuid, id), json);
            if (result.getCode() == HttpStatus.SC_OK) {
                PersonDocument personDocument = objectMapper.readValue(result.getResponse(), PersonDocument.class);
                return getMeshGuardianConverter().toDTO(personDocument).okResponse();
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toDTO(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Error in modifyPersonDocument: ", e);
            return new MeshDocumentResponse().internalErrorResponse();
        }
    }

    private Contact buildEditPersonContact(String meshGuid, Integer typeId, String data, Integer id) {
        Contact contact = new Contact();
        contact.setId(id);
        contact.setPersonId(meshGuid);
        contact.setTypeId(typeId);
        contact.setData(data);
        contact.setDefault(true);
        return contact;
    }

    private Contact buildPersonContact(String personId, Integer typeId, String data) {
        Contact contact = new Contact();
        contact.setId(0);
        contact.setPersonId(personId);
        contact.setTypeId(typeId);
        contact.setData(data);
        contact.setDefault(true);
        return contact;
    }

    PersonDocument buildPersonDocument(DulDetail dulDetail) {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
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
        return String.format(PERSONS_WITH_EDUCATION_CREATE_URL, meshGuid);
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

    private String buildDeleteAndModifyDocumentUrl(String meshGuid, Integer id) {
        return String.format(DOCUMENT_DELETE_URL, meshGuid, id);
    }

    private String buildChangeAgentUrl(String meshGuid, Integer id) {
        return String.format(PERSONS_DELETE_URL, meshGuid, id);
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
        if (dulDetails != null && !CollectionUtils.isEmpty(dulDetails)) {
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
        contact.setDefault(true);
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
                                               String email,
                                               List<DulDetail> dulDetails) throws Exception {
        ResponsePersons person = buildResponsePerson(null, firstName, patronymic, lastName, genderId, birthDate, snils, mobile, email, dulDetails);

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

    public PersonResponse searchPersonByMeshGuid(String meshGuid) {
        try {
            String parameters = String.format("/%s?&expand=%s", meshGuid, PERSONS_SEARCH_EXPAND);
            ObjectMapper objectMapper = new ObjectMapper();
            MeshResponseWithStatusCode result = meshRestClient.executeGetMethod(PERSONS_CREATE_URL, parameters);
            if (result.getCode() == HttpStatus.SC_OK) {
                ResponsePersons responsePersons = objectMapper.readValue(result.getResponse(), ResponsePersons.class);
                return new PersonResponse(getMeshGuardianConverter().toDTO(responsePersons)).okResponse();
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toPersonDTO(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Error in searchPersonByMeshGuid: ", e);
            return new PersonResponse().internalErrorResponse();
        }
    }

    public IdListResponse searchIdByMeshGuid(String meshGuid) {
        try {
            String parameters = String.format("/%s?&expand=%s", meshGuid, ID_SEARCH_EXPAND);
            ObjectMapper objectMapper = new ObjectMapper();
            MeshResponseWithStatusCode result = meshRestClient.executeGetMethod(PERSONS_CREATE_URL, parameters);
            if (result.getCode() == HttpStatus.SC_OK) {
                ResponsePersons responsePersons = objectMapper.readValue(result.getResponse(), ResponsePersons.class);
                IdListResponse idListResponse = new IdListResponse();
                idListResponse.setAgentResponse(getMeshGuardianConverter().agentIdToDTO(responsePersons));
                idListResponse.setDocumentResponse(getMeshGuardianConverter().documentIdToDTO(responsePersons));
                idListResponse.setContactResponse(getMeshGuardianConverter().contactIdToDTO(responsePersons));
                return idListResponse.okResponse();
            } else {
                ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
                return getMeshGuardianConverter().toIdListDTO(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Error in searchIdByMeshGuid: ", e);
            return new IdListResponse().internalErrorResponse();
        }
    }

    public MeshAgentResponse addGuardianToClient(String meshGuid, String childMeshGuid, Integer agentTypeId) {
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
                return getMeshGuardianConverter().toAgentDTO(personResult).okResponse();
            } else if (result.getCode() >= 500) {
                return new MeshAgentResponse().internalErrorResponse("" + result.getCode());
            } else {
                return getMeshAgentResponse(objectMapper, result);
            }
        } catch (ConnectTimeoutException te) {
            logger.error("Connection timeout in addGuardianToClient: ", te);
            return new MeshAgentResponse().internalErrorResponse("Mesh service connection timeout");
        } catch (Exception e) {
            logger.error("Error in addGuardianToClient: ", e);
            return new MeshAgentResponse().internalErrorResponse();
        }
    }

    public MeshAgentResponse deleteGuardianToClient(String agentMeshGuid, String childMeshGuid) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            IdListResponse agentListResponse = searchIdByMeshGuid(childMeshGuid);
            if (!agentListResponse.getCode().equals(PersonListResponse.OK_CODE))
                return new MeshAgentResponse(agentListResponse.getCode(), agentListResponse.message);
            Integer id = 0;
            for (AgentIdResponse agentResponse : agentListResponse.getAgentResponse()) {
                if (agentResponse.getAgentMeshGuid().equals(agentMeshGuid))
                    id = agentResponse.getAgentId();
            }
            if (id == 0)
                return new MeshAgentResponse().okResponse();
            MeshResponseWithStatusCode result = meshRestClient
                    .executeDeleteMethod(buildDeleteGuardianToClientUrl(childMeshGuid, id.toString()));
            if (result.getCode() == HttpStatus.SC_OK) {
                return new MeshAgentResponse().okResponse();
            } else if (result.getCode() >= 500) {
                return new MeshAgentResponse().internalErrorResponse("" + result.getCode());
            } else {
                return getMeshAgentResponse(objectMapper, result);
            }

        } catch (ConnectTimeoutException te) {
            logger.error("Connection timeout in deleteGuardianToClient: ", te);
            return new MeshAgentResponse().internalErrorResponse("Mesh service connection timeout");
        } catch (Exception e) {
            logger.error("Error in deleteGuardianToClient: ", e);
            return new MeshAgentResponse().internalErrorResponse();
        }
    }

    public MeshAgentResponse changeGuardianToClient(String agentMeshGuid, String childMeshGuid, Integer agentTypeId) {
        try {
            IdListResponse agentListResponse = searchIdByMeshGuid(childMeshGuid);
            if (!agentListResponse.getCode().equals(PersonListResponse.OK_CODE)) {
                return new MeshAgentResponse(agentListResponse.getCode(), agentListResponse.message);
            }
            Integer id = 0;
            for (AgentIdResponse agentResponse : agentListResponse.getAgentResponse()) {
                if (agentResponse.getAgentMeshGuid().equals(agentMeshGuid))
                    id = agentResponse.getAgentId();
            }
            if (id == 0) {
                return new MeshAgentResponse().internalErrorResponse("Agent not found");
            }
            ObjectMapper objectMapper = new ObjectMapper();
            PersonAgent personAgent = new PersonAgent();
            personAgent.setAgentTypeId(agentTypeId);
            personAgent.setId(id);
            personAgent.setPersonId(childMeshGuid);
            String json = objectMapper.writeValueAsString(personAgent);
            MeshResponseWithStatusCode result = meshRestClient.executePutMethod(buildChangeAgentUrl(childMeshGuid, id), json);
            if (result.getCode() == HttpStatus.SC_OK) {
                return new MeshAgentResponse().okResponse();
            } else if (result.getCode() >= 500) {
                return new MeshAgentResponse().internalErrorResponse("" + result.getCode());
            } else {
                return getMeshAgentResponse(objectMapper, result);
            }
        } catch (ConnectTimeoutException te) {
            logger.error("Connection timeout in changeGuardianToClient: ", te);
            return new MeshAgentResponse().internalErrorResponse("Mesh service connection timeout");
        } catch (Exception e) {
            logger.error("Error in changeGuardianToClient: ", e);
            return new MeshAgentResponse().internalErrorResponse();
        }
    }

    public MeshAgentResponse createPersonOnlyMK(String firstName,
                                                String patronymic,
                                                String lastName,
                                                Integer genderId,
                                                Date birthDate,
                                                String snils,
                                                String mobile,
                                                String email,
                                                List<DulDetail> dulDetail,
                                                Integer agentTypeId,
                                                String childMeshGuid) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PersonAgent personAgent = buildPersonAgent(firstName, patronymic, lastName, genderId, birthDate, snils, mobile,
                    email, dulDetail, agentTypeId);
            String json = objectMapper.writeValueAsString(personAgent);
            MeshResponseWithStatusCode result = meshRestClient.executePostMethod(buildCreatePersonUrl(childMeshGuid), json);
            if (result.getCode() == HttpStatus.SC_OK) {
                PersonAgent personResult = objectMapper.readValue(result.getResponse(), PersonAgent.class);
                return getMeshGuardianConverter().toAgentDTO(personResult).okResponse();
            } else if (result.getCode() >= 500) {
                return new MeshAgentResponse().internalErrorResponse("" + result.getCode());
            } else {
                return getMeshAgentResponse(objectMapper, result);
            }
        } catch (ConnectTimeoutException te) {
            logger.error("Connection timeout in createPersonOnlyMK: ", te);
            return new MeshAgentResponse().internalErrorResponse("Mesh service connection timeout");
        } catch (Exception e) {
            logger.error("Error in createPersonOnlyMK: ", e);
            return new MeshAgentResponse().internalErrorResponse();
        }
    }

    private PersonResponse getPersonResponse(ObjectMapper objectMapper, MeshResponseWithStatusCode result) throws IOException {
        try {
            ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
            return getMeshGuardianConverter().toPersonDTO(errorResponse);
        } catch (JsonParseException e) {
            logger.error(e.getMessage());
            return new PersonResponse(result.getCode(), new String(result.getResponse(), StandardCharsets.UTF_8));
        }
    }

    private MeshAgentResponse getMeshAgentResponse(ObjectMapper objectMapper, MeshResponseWithStatusCode result) throws IOException {
        try {
            ErrorResponse errorResponse = objectMapper.readValue(result.getResponse(), ErrorResponse.class);
            return getMeshGuardianConverter().toAgentErrorDTO(errorResponse);
        } catch (JsonParseException e) {
            logger.error(e.getMessage());
            return new MeshAgentResponse(result.getCode(), new String(result.getResponse(), StandardCharsets.UTF_8));
        }
    }
}
