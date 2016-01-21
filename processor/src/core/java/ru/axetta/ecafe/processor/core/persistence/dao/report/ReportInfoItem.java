/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.report;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 19.01.16
 * Time: 15:48
 * To change this template use File | Settings | File Templates.
 */
public class ReportInfoItem {

    private final String reportName;

    private final String orgShortName;

    private final String orgAddress;

    private final XMLGregorianCalendar createdDate;

    private final XMLGregorianCalendar startDate;

    private final XMLGregorianCalendar endDate;

    private final Long reportFile;

    public ReportInfoItem(String reportName, String orgShortName, String orgAddress, Date createdDate, Date startDate, Date endDate, Long reportFile) throws Exception{
        this.reportName = reportName;
        this.orgShortName = orgShortName;
        this.orgAddress = orgAddress;
        this.createdDate = CalendarUtils.getXMLGregorianCalendarByDate(createdDate);
        this.startDate = CalendarUtils.getXMLGregorianCalendarByDate(startDate);
        this.endDate = CalendarUtils.getXMLGregorianCalendarByDate(endDate);
        this.reportFile = reportFile;
    }

    public String getReportName() {
        return reportName;
    }

    public String getOrgShortName() {
        return orgShortName;
    }

    public String getOrgAddress() {
        return orgAddress;
    }

    public XMLGregorianCalendar getCreatedDate() {
        return createdDate;
    }

    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    public Long getReportFile() {
        return reportFile;
    }

}
