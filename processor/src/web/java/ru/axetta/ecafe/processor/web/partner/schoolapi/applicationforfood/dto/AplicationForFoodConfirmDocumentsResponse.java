/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto;

import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.BaseResponse;

public class AplicationForFoodConfirmDocumentsResponse extends BaseResponse {
    private long id;

    private AplicationForFoodConfirmDocumentsResponse(long id)
    {
        this.id = id;
        super.result = 0;
        super.errorText = null;
    }

    private AplicationForFoodConfirmDocumentsResponse(long id, int result, String errorText)
    {
        this.id = id;
        super.result = result;
        super.errorText = errorText;
    }

    public static AplicationForFoodConfirmDocumentsResponse success(long id) { return new AplicationForFoodConfirmDocumentsResponse(id); }
    public static AplicationForFoodConfirmDocumentsResponse error(long id, String errorText) { return new AplicationForFoodConfirmDocumentsResponse(id, 1, errorText); }

    public long getId() { return this.id; }
    public void setId(long id) { this.id = id; }
}
