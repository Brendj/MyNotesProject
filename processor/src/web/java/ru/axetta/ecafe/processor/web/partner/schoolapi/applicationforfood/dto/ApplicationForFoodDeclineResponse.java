/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.applicationforfood.dto;

import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.BaseResponse;

public class ApplicationForFoodDeclineResponse extends BaseResponse {

    private long id;

    private ApplicationForFoodDeclineResponse(long id)
    {
        this.id = id;
        super.result = 0;
        super.errorText = null;
    }

    private ApplicationForFoodDeclineResponse(long id, int result, String errorText)
    {
        this.id = id;
        super.result = result;
        super.errorText = errorText;
    }

    public static ApplicationForFoodDeclineResponse success(long id) { return new ApplicationForFoodDeclineResponse(id); }
    public static ApplicationForFoodDeclineResponse error(long id, String errorText) { return new ApplicationForFoodDeclineResponse(id, 1, errorText); }

    public long getId() { return this.id; }
    public void setId(long id) { this.id = id; }
}
