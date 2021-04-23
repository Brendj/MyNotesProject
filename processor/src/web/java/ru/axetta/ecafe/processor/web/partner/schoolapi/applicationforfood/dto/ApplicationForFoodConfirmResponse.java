/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto;

import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.BaseResponse;

public class ApplicationForFoodConfirmResponse extends BaseResponse {
    private long recordId;

    private ApplicationForFoodConfirmResponse() { }

    public static ApplicationForFoodConfirmResponse success(long recordId)
    {
        ApplicationForFoodConfirmResponse result = new ApplicationForFoodConfirmResponse();
        result.recordId = recordId;
        result.result = 0;
        result.errorText = null;
        return result;
    }
    public static ApplicationForFoodConfirmResponse error(long recordId, int errorCode, String errorText)
    {
        ApplicationForFoodConfirmResponse result = new ApplicationForFoodConfirmResponse();
        result.recordId = recordId;
        result.result = errorCode;
        result.errorText = errorText;
        return result;
    }

    public long getRecordId() { return this.recordId; }
    public void setRecordId(long recordId) { this.recordId = recordId; }
}
