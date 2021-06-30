/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto;

import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.BaseResponse;

public class AplicationForFoodConfirmDocumentsResponse extends BaseResponse {
    private long recordId;
    private AplicationForFoodConfirmDocumentsResponse() { }

    public static AplicationForFoodConfirmDocumentsResponse success(long recordId)
    {
        AplicationForFoodConfirmDocumentsResponse result = new AplicationForFoodConfirmDocumentsResponse();
        result.recordId = recordId;
        result.result = 0;
        result.errorText = null;
        return result;
    }

    public static AplicationForFoodConfirmDocumentsResponse error(long recordId, int errorCode, String errorText)
    {
        AplicationForFoodConfirmDocumentsResponse result = new AplicationForFoodConfirmDocumentsResponse();
        result.recordId = recordId;
        result.result = errorCode;
        result.errorText = errorText;
        return result;
    }

    public long getRecordId() { return this.recordId; }
    public void setRecordId(long recordId) { this.recordId = recordId; }
}
