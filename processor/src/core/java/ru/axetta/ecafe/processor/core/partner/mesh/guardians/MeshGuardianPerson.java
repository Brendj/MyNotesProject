package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.json.Contact;
import ru.axetta.ecafe.processor.core.partner.mesh.json.PersonAgent;
import ru.axetta.ecafe.processor.core.partner.mesh.json.SimilarPerson;
import ru.axetta.ecafe.processor.core.persistence.Client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MeshGuardianPerson {
    private String firstName;
    private String surname;
    private String secondName;
    private String meshGuid;
    private Date birthDate;
    private String snils;
    private Integer gender;
    private String mobile;
    private String email;
    private Integer degree;
    private Integer validationStateId;
    private Integer id;
    private List<MeshDocumentResponse> document;
    private Boolean alreadyInISPP;

    public MeshGuardianPerson() {

    }

    public MeshGuardianPerson(SimilarPerson similarPerson) throws Exception {
        this.meshGuid = similarPerson.getPerson().getPersonId();
        this.firstName = similarPerson.getPerson().getFirstname();
        this.secondName = similarPerson.getPerson().getPatronymic();
        this.surname = similarPerson.getPerson().getLastname();
        this.birthDate = getDateFromString(similarPerson.getPerson().getBirthdate());
        this.snils = similarPerson.getPerson().getSnils();
        this.gender = getMeshGender(similarPerson.getPerson().getGenderId());
        if (similarPerson.getPerson().getContacts() != null) {
            this.mobile = Client.checkAndConvertMobile(getContact(similarPerson.getPerson().getContacts(), MeshGuardiansService.CONTACT_MOBILE_TYPE_ID));
            this.email = getContact(similarPerson.getPerson().getContacts(), MeshGuardiansService.CONTACT_EMAIL_TYPE_ID);
        }
        this.degree = similarPerson.getDegree();
        this.validationStateId = similarPerson.getPerson().getValidationStateId();
        this.id = similarPerson.getPerson().getId();
    }

    private MeshGuardianConverter getMeshGuardianConverter() {
        return RuntimeContext.getAppContext().getBean(MeshGuardianConverter.class);
    }

    private String getContact(List<Contact> contacts, Integer typeId) {
        List<Contact> list = contacts.stream()
                .filter(contact -> contact.getTypeId() == typeId)
                .collect(Collectors.toList());
        if (list.size() > 0) {
            Collections.sort(list);
            return list.get(0).getData();
        }
        return null;
    }

    public MeshGuardianPerson(PersonAgent personAgent) throws Exception {
        this.meshGuid = personAgent.getPersonId();
        if (personAgent.getAgentPerson() != null) {
            this.firstName = personAgent.getAgentPerson().getFirstname();
            this.secondName = personAgent.getAgentPerson().getPatronymic();
            this.surname = personAgent.getAgentPerson().getLastname();
            this.birthDate = getDateFromString(personAgent.getAgentPerson().getBirthdate());
            this.snils = personAgent.getAgentPerson().getSnils();
            this.gender = getMeshGender(personAgent.getAgentPerson().getGenderId());
        }
    }

    private Date getDateFromString(String date) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat(MeshGuardiansService.DATE_PATTERN);
        return dateFormat.parse(date);
    }

    private Integer getMeshGender(Integer gender) {
        return gender;
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

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
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

    public Boolean getAlreadyInISPP() {
        return alreadyInISPP;
    }

    public void setAlreadyInISPP(Boolean alreadyInISPP) {
        this.alreadyInISPP = alreadyInISPP;
    }
}
