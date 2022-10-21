package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.json.*;
import ru.axetta.ecafe.processor.core.persistence.Client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MeshGuardianPerson {
    private String firstName;
    private String surname;
    private String secondName;
    private String meshGuid;
    private Date birthDate;
    private String snils;
    private Integer meshGender;
    private String mobile;
    private String email;
    private Integer degree;
    private Integer validationStateId;
    private Integer id;
    private List<MeshDocumentResponse> document;
    private List<MeshAgentResponse> agents;
    private Boolean alreadyInISPP;

    public MeshGuardianPerson() {

    }

    private MeshGuardiansService getMeshGuardiansService() {
        return RuntimeContext.getAppContext().getBean(MeshGuardiansService.class);
    }

    public MeshGuardianPerson(SimilarPerson similarPerson) throws Exception {
        this(similarPerson.getPerson());
        this.degree = similarPerson.getDegree();
    }

    public MeshGuardianPerson(ResponsePersons responsePersons) throws Exception {
        this.id = responsePersons.getId();
        this.meshGuid = responsePersons.getPersonId();
        this.firstName = responsePersons.getFirstname();
        this.secondName = responsePersons.getPatronymic();
        this.surname = responsePersons.getLastname();
        this.birthDate = getDateFromString(responsePersons.getBirthdate());
        this.snils = responsePersons.getSnils();
        this.meshGender = responsePersons.getGenderId();
        this.validationStateId = responsePersons.getValidationStateId();

        List<Contact> personContacts = responsePersons.getContacts();
        if (personContacts != null && !personContacts.isEmpty()) {
            this.mobile = Client.checkAndConvertMobile(getContact(personContacts, MeshGuardiansService.CONTACT_MOBILE_TYPE_ID));
            this.email = getContact(personContacts, MeshGuardiansService.CONTACT_EMAIL_TYPE_ID);
        }

        List<PersonDocument> personDocuments = responsePersons.getDocuments();
        if (personDocuments != null && !personDocuments.isEmpty()) {
            List<MeshDocumentResponse> meshDocumentResponses = new ArrayList<>();
            for (PersonDocument personDocument : personDocuments) {
                meshDocumentResponses.add(new MeshDocumentResponse(personDocument));
            }
            this.setDocument(meshDocumentResponses);
        }

        List<PersonAgent> personAgents = responsePersons.getAgents();
        if (personAgents != null && !personAgents.isEmpty()) {
            List<MeshAgentResponse> meshAgentResponses = new ArrayList<>();
            for (PersonAgent personAgent : personAgents) {
                meshAgentResponses.add(new MeshAgentResponse(personAgent));
            }
            this.setAgents(meshAgentResponses);
        }
    }

    public MeshGuardianPerson(PersonAgent personAgent) throws Exception {
        this.meshGuid = personAgent.getAgentPersonId();
        if (personAgent.getAgentPerson() != null) {
            this.id = personAgent.getAgentPerson().getId();
            this.firstName = personAgent.getAgentPerson().getFirstname();
            this.secondName = personAgent.getAgentPerson().getPatronymic();
            this.surname = personAgent.getAgentPerson().getLastname();
            this.birthDate = getDateFromString(personAgent.getAgentPerson().getBirthdate());
            this.snils = personAgent.getAgentPerson().getSnils();
            this.meshGender = personAgent.getAgentPerson().getGenderId();
            if (personAgent.getAgentPerson().getContacts() != null) {
                this.mobile = Client.checkAndConvertMobile(getContact(personAgent.getAgentPerson().getContacts(), MeshGuardiansService.CONTACT_MOBILE_TYPE_ID));
                this.email = getContact(personAgent.getAgentPerson().getContacts(), MeshGuardiansService.CONTACT_EMAIL_TYPE_ID);
            }
            this.validationStateId = personAgent.getAgentPerson().getValidationStateId();

            List<PersonDocument> personDocuments = personAgent.getAgentPerson().getDocuments();
            if (personDocuments != null && !personDocuments.isEmpty()) {
                List<MeshDocumentResponse> meshDocumentResponses = new ArrayList<>();
                for (PersonDocument personDocument : personDocuments) {
                    meshDocumentResponses.add(new MeshDocumentResponse(personDocument));
                }
                this.setDocument(meshDocumentResponses);
            }
        }
    }

    private MeshGuardianConverter getMeshGuardianConverter() {
        return RuntimeContext.getAppContext().getBean(MeshGuardianConverter.class);
    }

    private String getContact(List<Contact> contacts, Integer typeId) {
        List<Contact> list = contacts.stream()
                .filter(contact -> (contact.getTypeId().equals(typeId)) && (contact.getDefault()))
                .collect(Collectors.toList());
        if (list.isEmpty()) {
            list = contacts.stream()
                    .filter(contact -> (contact.getTypeId().equals(typeId)))
                    .sorted((Contact c1, Contact c2) -> getDateTimeFromString(c2.getCreatedAt()).compareTo(getDateTimeFromString(c1.getCreatedAt())))
                    .collect(Collectors.toList());
        }
        return list.isEmpty() ? null : list.get(0).getData();
    }

    private Date getDateFromString(String date) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat(MeshGuardiansService.DATE_PATTERN);
        return dateFormat.parse(date);
    }

    private Date getDateTimeFromString(String date) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(MeshGuardiansService.DATE_TIME_PATTERN);
            return dateFormat.parse(date);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getMeshGuid() {
        return meshGuid;
    }

    public void setMeshGuid(String meshGuid) {
        this.meshGuid = meshGuid;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    public Integer getMeshGender() {
        return meshGender;
    }

    public void setMeshGender(Integer meshGender) {
        this.meshGender = meshGender;
    }

    public Integer getIsppGender() {
        if (this.meshGender == 2) return 0;
        else return 1;
    }

    public Integer getDegree() {
        return degree;
    }

    public void setDegree(Integer degree) {
        this.degree = degree;
    }

    public Integer getValidationStateId() {
        return validationStateId;
    }

    public void setValidationStateId(Integer validationStateId) {
        this.validationStateId = validationStateId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<MeshDocumentResponse> getDocument() {
        return document;
    }

    public void setDocument(List<MeshDocumentResponse> document) {
        this.document = document;
    }

    public List<MeshAgentResponse> getAgents() {
        return agents;
    }

    public void setAgents(List<MeshAgentResponse> agents) {
        this.agents = agents;
    }

    public Boolean getAlreadyInISPP() {
        return alreadyInISPP;
    }

    public void setAlreadyInISPP(Boolean alreadyInISPP) {
        this.alreadyInISPP = alreadyInISPP;
    }
}
