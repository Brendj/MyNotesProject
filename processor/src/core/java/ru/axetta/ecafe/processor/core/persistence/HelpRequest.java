/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;

public class HelpRequest implements Serializable {

    private Long idOfHelpRequest;
    private long version;
    private Date requestDate;
    private Date requestUpdateDate;
    private HelpRequestThemeEnumType theme;
    private String message;
    private String declarer;
    private String phone;
    private HelpRequestStatusEnumType status;
    private String requestNumber;
    private Org org;
    private String guid;
    private String comment;

    public HelpRequest(Date requestDate, HelpRequestThemeEnumType theme, String message, String declarer,
            String phone, HelpRequestStatusEnumType status, String requestNumber, Org org, String guid) {
        this.requestDate = requestDate;
        this.requestUpdateDate = new Date();
        this.theme = theme;
        this.message = message;
        this.declarer = declarer;
        this.phone = phone;
        this.status = status;
        this.requestNumber = requestNumber;
        this.org = org;
        this.guid = guid;
    }

    protected HelpRequest() {
        // For Hibernate only
    }

    public Long getIdOfHelpRequest() {
        return idOfHelpRequest;
    }

    public void setIdOfHelpRequest(Long idOfHelpDeskRequest) {
        this.idOfHelpRequest = idOfHelpDeskRequest;
    }

    @Version
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getRequestUpdateDate() {
        return requestUpdateDate;
    }

    public void setRequestUpdateDate(Date requestUpdateDate) {
        this.requestUpdateDate = requestUpdateDate;
    }

    public HelpRequestThemeEnumType getTheme() {
        return theme;
    }

    public void setTheme(HelpRequestThemeEnumType theme) {
        this.theme = theme;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeclarer() {
        return declarer;
    }

    public void setDeclarer(String declarer) {
        this.declarer = declarer;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public HelpRequestStatusEnumType getStatus() {
        return status;
    }

    public void setStatus(HelpRequestStatusEnumType status) {
        this.status = status;
    }

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HelpRequest)) {
            return false;
        }
        final HelpRequest helpRequest = (HelpRequest) o;
        return idOfHelpRequest.equals(helpRequest.getIdOfHelpRequest());
    }

    @Override
    public int hashCode() {
        return idOfHelpRequest.hashCode();
    }

    @Override
    public String toString() {
        return "HelpDeskRequests{" + "idOfHelpRequests=" + idOfHelpRequest + ", version=" + version +
                ", requestDate=" + requestDate + ", requestUpdateDate='" + requestUpdateDate + ", theme='" + theme
                + ", message='" + message + ", declarer='" + declarer + ", phone='" + phone + ", status='" + status
                + ", requestNumber='" + requestNumber + ", idOfOrg='" + org.getIdOfOrg() + '}';
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
