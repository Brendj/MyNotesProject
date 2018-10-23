/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CheckApplicationForFoodResult")
public class CheckApplicationForFoodResult extends Result {

    @XmlElement(name = "applicationExists")
    private Boolean applicationExists;

    @XmlElement(name = "applicantSurname")
    private String applicantSurname;

    @XmlElement(name = "applicantName")
    private String applicantName;

    @XmlElement(name = "applicantSecondName")
    private String applicantSecondName;

    @XmlElement(name = "regDate")
    protected XMLGregorianCalendar regDate;

    public CheckApplicationForFoodResult() {

    }

    public Boolean getApplicationExists() {
        return applicationExists;
    }

    public void setApplicationExists(Boolean applicationExists) {
        this.applicationExists = applicationExists;
    }

    public String getApplicantSurname() {
        return applicantSurname;
    }

    public void setApplicantSurname(String applicantSurname) {
        this.applicantSurname = applicantSurname;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantSecondName() {
        return applicantSecondName;
    }

    public void setApplicantSecondName(String applicantSecondName) {
        this.applicantSecondName = applicantSecondName;
    }

    public XMLGregorianCalendar getRegDate() {
        return regDate;
    }

    public void setRegDate(XMLGregorianCalendar regDate) {
        this.regDate = regDate;
    }
}
