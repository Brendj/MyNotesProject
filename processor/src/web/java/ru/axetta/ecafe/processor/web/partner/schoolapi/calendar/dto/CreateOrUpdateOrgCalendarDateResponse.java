/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.dto;

import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.BaseResponse;

import java.util.Date;

public class CreateOrUpdateOrgCalendarDateResponse extends BaseResponse {
    private Long idOfRecord;
    private long idOfOrg;
    private long idOfGroup;
    private Date date;
    private boolean isWeekend;
    private String comment;

    public Long getIdOfRecord() {
        return this.idOfRecord;
    }
    public void setIdOfRecord(Long idOfRecord) {
        this.idOfRecord = idOfRecord;
    }

    public long getIdOfOrg() {
        return this.idOfOrg;
    }
    public void setIdOfOrg(long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public long getIdOfGroup() {
        return this.idOfGroup;
    }
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
    public void setComment(String comment) {
        this.comment = comment;
    }

    private CreateOrUpdateOrgCalendarDateResponse() { }

    public static CreateOrUpdateOrgCalendarDateResponse success(CreateOrUpdateOrgCalendarDateRequest request, long idOfRecord)
    {
        CreateOrUpdateOrgCalendarDateResponse result = new CreateOrUpdateOrgCalendarDateResponse();
        result.result = 0;
        result.errorText = null;
        result.idOfRecord = idOfRecord;
        result.idOfOrg = request.getIdOfOrg();
        result.idOfGroup = request.getIdOfGroup();
        result.date = request.getDate();
        result.isWeekend = request.getIsWeekend();
        result.comment = request.getComment();
        return result;
    }

    public static CreateOrUpdateOrgCalendarDateResponse error(CreateOrUpdateOrgCalendarDateRequest request, int errorCode, String errorText)
    {
        CreateOrUpdateOrgCalendarDateResponse result = new CreateOrUpdateOrgCalendarDateResponse();
        result.result = errorCode;
        result.errorText = errorText;
        result.idOfRecord = null;
        result.idOfOrg = request.getIdOfOrg();
        result.idOfGroup = request.getIdOfGroup();
        result.date = request.getDate();
        result.isWeekend = request.getIsWeekend();
        result.comment = request.getComment();
        return result;
    }
}
