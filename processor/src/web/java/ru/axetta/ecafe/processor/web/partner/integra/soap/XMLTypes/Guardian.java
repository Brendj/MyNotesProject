/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.soap.XMLTypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "guardian")
public class Guardian {
    protected String firstName;
    protected String secondName;
    protected String surname;
    protected String mobile;
    protected Integer gender;
    protected Long childContractId;
    protected String creatorMobile;
    protected String passportNumber;
    protected String passportSeries;
    protected Integer typeCard;
    protected Integer roleRepresentative;
    @XmlElement(required = true)
    protected Long relation;

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Long getChildContractId() {
        return childContractId;
    }

    public void setChildContractId(Long childContractId) {
        this.childContractId = childContractId;
    }

    public String getCreatorMobile() {
        return creatorMobile;
    }

    public void setCreatorMobile(String creatorMobile) {
        this.creatorMobile = creatorMobile;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getPassportSeries() {
        return passportSeries;
    }

    public void setPassportSeries(String passportSeries) {
        this.passportSeries = passportSeries;
    }

    public Integer getTypeCard() {
        return typeCard;
    }

    public void setTypeCard(Integer typeCard) {
        this.typeCard = typeCard;
    }

    public Integer getRoleRepresentative() {
        return roleRepresentative;
    }

    public void setRoleRepresentative(Integer roleRepresentative) {
        this.roleRepresentative = roleRepresentative;
    }

    public Long getRelation() {
        return relation;
    }

    public void setRelation(Long relation) {
        this.relation = relation;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
