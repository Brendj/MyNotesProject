/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.RequestDTO;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientsMobileHistory;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;
import ru.axetta.ecafe.processor.web.partner.schoolapi.util.GroupManagementErrors;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.Session;

import java.util.Date;

public class CreateClientRequestDTO {
    @JsonProperty("iacregid")
    private String iacRegId;

    @JsonProperty("GroupName")
    private String groupName;

    @JsonProperty("Surname")
    private String surname;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Middlename")
    private String middlename;

    @JsonProperty("Gender")
    private Integer gender;

    @JsonProperty("Birthday")
    private Date birthDate;

    @JsonProperty("Mobile")
    private String mobile;

    @JsonProperty("PassportSeries")
    private String passportSeries;

    @JsonProperty("PassportNumber")
    private String passportNumber;

    public String getIacRegId() {
        return iacRegId;
    }

    public void setIacRegId(String iacRegId) {
        this.iacRegId = iacRegId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassportSeries() {
        return passportSeries;
    }

    public void setPassportSeries(String passportSeries) {
        this.passportSeries = passportSeries;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public void validateRequest() throws WebApplicationException {
        String errorMessage = "Поля GroupName, Surname, Name, Gender обязательные для заполнения.";
        if(isEmpty(this.groupName) || isEmpty(this.surname) || isEmpty(this.name) || this.gender == null )
            throw WebApplicationException.badRequest(GroupManagementErrors.VALIDATION_ERROR.getErrorCode(), errorMessage);
        if(this.gender.intValue() != 0 && this.gender.intValue() != 1)
            throw WebApplicationException.badRequest(GroupManagementErrors.VALIDATION_ERROR.getErrorCode(),
                    "Значение поля Gender должно быть 0 или 1.");
    }

    public static Client convertRequestToClient(Session session, CreateClientRequestDTO createClientRequestDTO,
            ClientsMobileHistory clientsMobileHistory) throws Exception{
        Person clientPerson = new Person(createClientRequestDTO.getName(), createClientRequestDTO.getSurname(),
                createClientRequestDTO.getMiddlename());
        Person contractPerson = new Person("","","");
        Client client = new Client(null, clientPerson, contractPerson, 0,false,false,
                false,0,null, 0, "",
                0,0,0,0 );
        client.setIacRegId(createClientRequestDTO.getIacRegId());
        client.setGender(createClientRequestDTO.getGender());
        client.setBirthDate(createClientRequestDTO.getBirthDate());
        session.save(client);
        client.initClientMobileHistory(clientsMobileHistory);
        client.setMobile(createClientRequestDTO.getMobile());
        client.setPassportSeries(createClientRequestDTO.getPassportSeries());
        client.setPassportNumber(createClientRequestDTO.getPassportNumber());
        client.setAddress("");
        return client;
    }

    public static boolean isEmpty( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }
}
