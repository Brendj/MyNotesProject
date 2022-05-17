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
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.partner.mesh.MeshPersonsSyncService;
import ru.axetta.ecafe.processor.core.partner.mesh.MeshResponseWithStatusCode;
import ru.axetta.ecafe.processor.core.partner.mesh.json.*;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import javax.jws.WebParam;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@DependsOn("runtimeContext")
@Component("meshGuardiansService")
public class MeshGuardiansService extends MeshPersonsSyncService {
    private static final Logger logger = LoggerFactory.getLogger(MeshGuardiansService.class);
    private static final String PERSONS_LIKE_URL = "/persons/like";
    private static final String PERSONS_CREATE_URL = "/persons/%s/agents";
    private static final String DOCUMENT_CREATE_URL = "/persons/%s/documents";
    private static final String DOCUMENT_DELETE_URL = "/persons/%s/documents/%s";
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
                                       String сhildMeshGuid,
                                       List<DulDetail> dulDetails,
                                       Integer agentTypeId,
                                       Integer relation,
                                       Integer typeOfLegalRepresent) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PersonAgent personAgent = buildPersonAgent(firstName, patronymic, lastName, genderId, birthDate, snils, mobile,
                    email, dulDetails, agentTypeId);
            String json = objectMapper.writeValueAsString(personAgent);
            MeshResponseWithStatusCode result = meshRestClient.executePostMethod(buildCreatePersonUrl(сhildMeshGuid), json);
            if (result.getCode() == HttpStatus.SC_OK) {
                PersonAgent personResult = objectMapper.readValue(result.getResponse(), PersonAgent.class);
                createGuadianInternal(idOfOrg, personResult.getAgentPersonId(), firstName, patronymic, lastName,
                        genderId, birthDate, snils, mobile, сhildMeshGuid, dulDetails, relation, typeOfLegalRepresent);
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

    private void createGuadianInternal(Long idOfOrg, String personId, String firstName,
                                       String patronymic, String lastName,
                                       Integer genderId, Date birthDate, String snils,
                                       String mobile, String сhildMeshGuid,
                                       List<DulDetail> dulDetails, Integer relation,
                                       Integer typeOfLegalRepresent) throws Exception {
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
        personAgent.setAgentPerson(buildResponsePerson(firstName, patronymic, lastName, genderId, birthDate, snils, mobile, email, dulDetails));
        /*if (StringUtils.isEmpty(guardian.getMeshGUID())) {
            personAgent.setAgentPerson(buildResponsePerson(guardian));
        } else {
            personAgent.setAgentPersonId(guardian.getMeshGUID());
        }*/

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

    private ResponsePersons buildResponsePerson(String firstName,
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
        person.setPersonId(PERSON_ID_STUB);
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
        ResponsePersons person = buildResponsePerson(firstName, patronymic, lastName, genderId, birthDate, snils, mobile, email, null);

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

}
