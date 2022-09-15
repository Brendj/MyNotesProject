package ru.axetta.ecafe.processor.web.partner.mesh_guardians.dao;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ClientInfo implements IDAOEntity {
    private String personGUID;
    private String firstname;
    private String patronymic;
    private String lastname;
    private Integer genderId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy", timezone = "Europe/Moscow")
    private Date birthdate;

    private String mobile;
    private String email;
    private List<DocumentInfo> documents = new LinkedList<>();
    private Integer agentTypeId;
    private String snils;
    private Integer updateOperation;
    public String getPersonGUID() {
        return personGUID;
    }

    public void setPersonGUID(String personGUID) {
        this.personGUID = personGUID;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getGenderId() {
        return genderId;
    }

    public void setGenderId(Integer genderId) {
        this.genderId = genderId;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
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

    public Integer getUpdateOperation() {
        return updateOperation;
    }

    public void setUpdateOperation(Integer updateOperation) {
        this.updateOperation = updateOperation;
    }

    public static ClientInfo build(Client c) {
        ClientInfo i = new ClientInfo();
        i.setFirstname(c.getPerson().getFirstName());
        i.setLastname(c.getPerson().getSurname());
        i.setPatronymic(c.getPerson().getSecondName());
        i.setPersonGUID(c.getMeshGUID());
        i.setGenderId(c.getGender());

        return i;
    }

    public List<DocumentInfo> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentInfo> documents) {
        this.documents = documents;
    }

    public Integer getAgentTypeId() {
        return agentTypeId;
    }

    public void setAgentTypeId(Integer agentTypeId) {
        this.agentTypeId = agentTypeId;
    }

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    public Integer getIsppGender() {
        if (this.genderId == 2) return 0;
        else return 1;
    }
}
