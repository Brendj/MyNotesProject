/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto;

import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.BaseResponse;

public class DeleteGuardianResponse extends BaseResponse
{
    private long recordId;
    private DeleteGuardianResponse() { }

    public static DeleteGuardianResponse success(long recordId)
    {
        DeleteGuardianResponse deleteGuardianResponse = new DeleteGuardianResponse();
        deleteGuardianResponse.result = 0;
        deleteGuardianResponse.recordId = recordId;
        deleteGuardianResponse.errorText = null;
        return deleteGuardianResponse;
    }
    public static DeleteGuardianResponse error(long recordId, int errorCode, String errorText)
    {
        DeleteGuardianResponse deleteGuardianResponse = new DeleteGuardianResponse();
        deleteGuardianResponse.result = errorCode;
        deleteGuardianResponse.recordId = recordId;
        deleteGuardianResponse.errorText = errorText;
        return deleteGuardianResponse;
    }

    public long getRecordId() { return this.recordId; }
    public void setRecordId(long value) { this.recordId = value; }
}
