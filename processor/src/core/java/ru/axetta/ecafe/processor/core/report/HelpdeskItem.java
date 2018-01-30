/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.HelpRequest;
import ru.axetta.ecafe.processor.core.persistence.HelpRequestStatusEnumType;

import java.util.Date;

/**
 * Created by i.semenov on 25.01.2018.
 */
public class HelpdeskItem {
    private Long idOfHelpRequest;
    private Date requestDate;
    private Date requestUpdateDate;
    private String theme;
    private String message;
    private String declarer;
    private String phone;
    private HelpRequestStatusEnumType status;
    private String requestNumber;
    private String org;
    private String comment;
    private boolean changed;
    private boolean editMode;
    private final String prevComment;

    public HelpdeskItem(HelpRequest request) {
        this.idOfHelpRequest = request.getIdOfHelpRequest();
        this.requestDate = request.getRequestDate();
        this.requestUpdateDate = request.getRequestUpdateDate();
        this.theme = request.getTheme().toString();
        this.message = request.getMessage();
        this.declarer = request.getDeclarer();
        this.phone = request.getPhone();
        this.status = request.getStatus();
        this.requestNumber = request.getRequestNumber();
        this.org = request.getOrg().getShortNameInfoService();
        this.comment = request.getComment();
        prevComment = request.getComment();
        changed = false;
        editMode = false;
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

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
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

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public Long getIdOfHelpRequest() {
        return idOfHelpRequest;
    }

    public void setIdOfHelpRequest(Long idOfHelpRequest) {
        this.idOfHelpRequest = idOfHelpRequest;
    }

    public HelpRequestStatusEnumType getStatus() {
        return status;
    }

    public void setStatus(HelpRequestStatusEnumType status) {
        this.status = status;
    }

    public String getStatusString() {
        return this.status.toString();
    }

    public Boolean getIsWorkedOut() {
        return this.status.equals(HelpRequestStatusEnumType.WORKED_OUT);
    }

    public Boolean getIsOpened() {
        return this.status.equals(HelpRequestStatusEnumType.OPEN);
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean inEdit) {
        editMode = inEdit;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPrevComment() {
        return prevComment;
    }
}
