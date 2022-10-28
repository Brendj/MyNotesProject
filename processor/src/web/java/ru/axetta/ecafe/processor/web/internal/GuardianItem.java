package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.partner.mesh.guardians.MeshDocumentResponse;
import ru.axetta.ecafe.processor.core.persistence.Client;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class GuardianItem {

    private Long idOfClient;
    private String lastName;
    private String firstName;
    private String patronymic;
    private String snils;
    private String mobile;
    private Long idOfOrg;
    private String addressOrg;
    private Long idOfClientGroup;
    private String groupName;
    private String meshGuid;
    private Integer gender;

    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar birthDate;
    private String email;
    private Integer degree;
    private Integer validationStateId;
    private Boolean alreadyInISPP;

    private List<MeshDocumentResponse> document;

    public GuardianItem() {
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getAddressOrg() {
        return addressOrg;
    }

    public void setAddressOrg(String addressOrg) {
        this.addressOrg = addressOrg;
    }

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMeshGuid() {
        return meshGuid;
    }

    public void setMeshGuid(String meshGuid) {
        this.meshGuid = meshGuid;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public XMLGregorianCalendar getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(XMLGregorianCalendar birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Boolean getAlreadyInISPP() {
        return alreadyInISPP;
    }

    public void setAlreadyInISPP(Boolean alreadyInISPP) {
        this.alreadyInISPP = alreadyInISPP;
    }

    public List<MeshDocumentResponse> getDocument() {
        return document;
    }

    public void setDocument(List<MeshDocumentResponse> document) {
        this.document = document;
    }
}
