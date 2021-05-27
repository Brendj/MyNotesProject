/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.dto;

import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.BaseResponse;

public class DeleteOrgCalendarDateResponse extends BaseResponse {
    private long idOfRecord;
    private DeleteOrgCalendarDateResponse() { }

    public static DeleteOrgCalendarDateResponse success(long idOfRecord)
    {
        DeleteOrgCalendarDateResponse deleteOrgCalendarDateResponse = new DeleteOrgCalendarDateResponse();
        deleteOrgCalendarDateResponse.result = 0;
        deleteOrgCalendarDateResponse.idOfRecord = idOfRecord;
        deleteOrgCalendarDateResponse.errorText = null;
        return deleteOrgCalendarDateResponse;
    }
    public static DeleteOrgCalendarDateResponse error(long idOfRecord, int errorCode, String errorText)
    {
        DeleteOrgCalendarDateResponse deleteOrgCalendarDateResponse = new DeleteOrgCalendarDateResponse();
        deleteOrgCalendarDateResponse.result = errorCode;
        deleteOrgCalendarDateResponse.idOfRecord = idOfRecord;
        deleteOrgCalendarDateResponse.errorText = errorText;
        return deleteOrgCalendarDateResponse;
    }

    public long getIdOfRecord() { return this.idOfRecord; }
    public void setIdOfRecord(long idOfRecord) { this.idOfRecord = idOfRecord; }
}
