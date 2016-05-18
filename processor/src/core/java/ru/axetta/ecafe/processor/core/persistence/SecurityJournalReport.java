/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 10.05.16
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
public class SecurityJournalReport {
    private Long idOfJournalReport;
    private String eventType;
    private Date eventDate;
    private Boolean success;
    private User user;

    public static SecurityJournalReport createJournalRecord(String eventType, Date eventDate) {
        SecurityJournalReport process = new SecurityJournalReport();
        process.setEventType(eventType);
        process.setEventDate(eventDate);
        setUserFromSession(process);
        return process;
    }

    private static void setUserFromSession(SecurityJournalReport process) {
        process.setUser(DAOReadonlyService.getInstance().getUserFromSession());
    }

    public void saveWithSuccess(Boolean success) {
        setSuccess(success);
        DAOService.getInstance().writeReportJournalRecord(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecurityJournalReport)) {
            return false;
        }

        SecurityJournalReport that = (SecurityJournalReport) o;

        if (!idOfJournalReport.equals(that.idOfJournalReport)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return idOfJournalReport.hashCode();
    }

    public Long getIdOfJournalReport() {
        return idOfJournalReport;
    }

    public void setIdOfJournalReport(Long idOfJournalReport) {
        this.idOfJournalReport = idOfJournalReport;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
