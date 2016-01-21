/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.report.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 19.01.16
 * Time: 11:48
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "reportName",
        "orgShortName",
        "orgAddress",
        "createdDate",
        "startDate",
        "endDate",
        "reportFile"
})

public class RepositoryReportItem {

    @XmlElement
    protected String reportName;

    @XmlElement
    protected String orgShortName;

    @XmlElement
    protected String orgAddress;

    @XmlElement
    protected XMLGregorianCalendar createdDate;

    @XmlElement
    protected XMLGregorianCalendar startDate;

    @XmlElement
    protected XMLGregorianCalendar endDate;

    @XmlElement
    protected Long reportFile;

    public RepositoryReportItem() {}

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getOrgShortName() {
        return orgShortName;
    }

    public void setOrgShortName(String orgShortName) {
        this.orgShortName = orgShortName;
    }

    public String getOrgAddress() {
        return orgAddress;
    }

    public void setOrgAddress(String orgAddress) {
        this.orgAddress = orgAddress;
    }

    public XMLGregorianCalendar getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(XMLGregorianCalendar createdDate) {
        this.createdDate = createdDate;
    }

    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    public void setStartDate(XMLGregorianCalendar startDate) {
        this.startDate = startDate;
    }

    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    public void setEndDate(XMLGregorianCalendar endDate) {
        this.endDate = endDate;
    }

    public Long getReportFile() {
        return reportFile;
    }

    public void setReportFile(Long reportFile) {
        this.reportFile = reportFile;
    }
}
