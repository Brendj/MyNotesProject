/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto;

import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.BaseResponse;

public class DeleteGuardianResponse extends BaseResponse
{
    private long recordId;

    private DeleteGuardianResponse(long idOfRecord)
    {
        this.recordId = idOfRecord;
        super.result = 0;
        super.errorText = null;
    }

    private DeleteGuardianResponse(long idOfRecord, int result, String errorText)
    {
        this.recordId = idOfRecord;
        super.result = result;
        super.errorText = errorText;
    }

    public static DeleteGuardianResponse success(long idOfRecord) { return new DeleteGuardianResponse(idOfRecord); }
    public static DeleteGuardianResponse error(long idOfRecord, String errorText) { return new DeleteGuardianResponse(idOfRecord, 1, errorText); }

    public long getRecordId() { return recordId; }
    public void setRecordId(long value) { recordId = value; }
}
