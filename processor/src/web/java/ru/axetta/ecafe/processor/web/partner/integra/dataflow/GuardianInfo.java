/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GuardianInfo")
public class GuardianInfo {

    @XmlAttribute(name = "surname")
    protected String surname;

    @XmlAttribute(name = "firstname")
    protected String firstname;

    @XmlAttribute(name = "secondname")
    protected String secondname;

    @XmlAttribute(name = "mobile")
    protected String mobile;

    @XmlAttribute(name = "ssoid")
    protected String ssoid;

    @XmlAttribute(name = "contractDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar contractDate;

    @XmlAttribute(name = "createdFrom")
    protected Integer createdFrom;

    @XmlAttribute(name = "guardianCreatedFrom")
    protected Integer guardianCreatedFrom;

    @XmlAttribute(name = "relation")
    protected Integer relation;

    @XmlAttribute(name = "guid")
    protected String guid;

    @XmlAttribute(name = "contractID")
    protected Long contractID;

    @XmlAttribute(name = "isDeleted")
    protected Boolean isDeleted;

    @XmlAttribute(name = "idDisabled")
    protected Boolean isDisabled;

    @XmlAttribute(name = "lastUpdate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastUpdate;

    public GuardianInfo() {

    }

    public GuardianInfo(String surname, String firstname, String secondname, String mobile, String ssoid,
            XMLGregorianCalendar contractDate, Integer createdFrom, Integer guardianCreatedFrom, Integer relation,
            String guid, Long contractID, Boolean isDeleted, Boolean isDisabled, XMLGregorianCalendar lastUpdate) {
        this.surname = surname;
        this.firstname = firstname;
        this.secondname = secondname;
        this.mobile = mobile;
        this.ssoid = ssoid;
        this.contractDate = contractDate;
        this.createdFrom = createdFrom;
        this.guardianCreatedFrom = guardianCreatedFrom;
        this.relation = relation;
        this.guid = guid;
        this.contractID = contractID;
        this.isDeleted = isDeleted;
        this.isDisabled = isDisabled;
        this.lastUpdate = lastUpdate;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSecondname() {
        return secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSsoid() {
        return ssoid;
    }

    public void setSsoid(String ssoid) {
        this.ssoid = ssoid;
    }

    public XMLGregorianCalendar getContractDate() {
        return contractDate;
    }

    public void setContractDate(XMLGregorianCalendar contractDate) {
        this.contractDate = contractDate;
    }

    public Integer getCreatedFrom() {
        return createdFrom;
    }

    public void setCreatedFrom(Integer createdFrom) {
        this.createdFrom = createdFrom;
    }

    public Integer getGuardianCreatedFrom() {
        return guardianCreatedFrom;
    }

    public void setGuardianCreatedFrom(Integer guardianCreatedFrom) {
        this.guardianCreatedFrom = guardianCreatedFrom;
    }

    public Integer getRelation() {
        return relation;
    }

    public void setRelation(Integer relation) {
        this.relation = relation;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getContractID() {
        return contractID;
    }

    public void setContractID(Long contractID) {
        this.contractID = contractID;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Boolean getDisabled() {
        return isDisabled;
    }

    public void setDisabled(Boolean disabled) {
        isDisabled = disabled;
    }

    public XMLGregorianCalendar getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(XMLGregorianCalendar lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
