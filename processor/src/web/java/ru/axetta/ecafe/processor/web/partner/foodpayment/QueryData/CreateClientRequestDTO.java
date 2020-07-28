/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment.QueryData;

import org.codehaus.jackson.annotate.JsonProperty;

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
    private int gender;

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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
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
}
