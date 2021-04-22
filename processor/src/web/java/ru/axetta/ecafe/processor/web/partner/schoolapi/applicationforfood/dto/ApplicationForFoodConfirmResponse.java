/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto;

import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.BaseResponse;

public class ApplicationForFoodConfirmResponse extends BaseResponse {
    private long id;

    private ApplicationForFoodConfirmResponse(long id)
    {
        this.id = id;
        super.result = 0;
        super.errorText = null;
    }

    private ApplicationForFoodConfirmResponse(long id, int result, String errorText)
    {
        this.id = id;
        super.result = result;
        super.errorText = errorText;
    }

    public static ApplicationForFoodConfirmResponse success(long id) { return new ApplicationForFoodConfirmResponse(id); }
    public static ApplicationForFoodConfirmResponse error(long id, String errorText) { return new ApplicationForFoodConfirmResponse(id, 1, errorText); }

    public long getId() { return this.id; }
    public void setId(long id) { this.id = id; }
}
