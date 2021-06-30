/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.dto;

import java.util.Date;

public class CreateOrUpdateOrgCalendarDateRequest {
    private long idOfOrg;
    private long idOfOrgRequester;
    private long idOfGroup;
    private Date date;
    private boolean isWeekend;
    private String comment;

    public long getIdOfOrg() {
        return this.idOfOrg;
    }
    public void setIdOfOrg(long idOfOrg) { this.idOfOrg = idOfOrg; }

    public long getIdOfOrgRequester() {
        return this.idOfOrgRequester;
    }
    public void setIdOfOrgRequester(long idOfOrgRequester) { this.idOfOrgRequester = idOfOrgRequester; }

    public long getIdOfGroup() { return this.idOfGroup; }
    public void setIdOfGroup(long idOfGroup) {
        this.idOfGroup = idOfGroup;
    }

    public Date getDate() {
        return this.date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public boolean getIsWeekend() {
        return this.isWeekend;
    }
    public void setIsWeekend(boolean isWeekend) {
        this.isWeekend = isWeekend;
    }

    public String getComment() {
        return this.comment;
    }
    public void setComment(String comment) { this.comment = comment; }
}
