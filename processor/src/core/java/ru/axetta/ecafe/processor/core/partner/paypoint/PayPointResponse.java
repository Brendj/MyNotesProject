/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.paypoint;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 15:00:47
 * To change this template use File | Settings | File Templates.
 */
public abstract class PayPointResponse {

    private final int requestId;
    private final int resultCode;
    private final String resultDescription;

    public PayPointResponse(int requestId, int resultCode, String resultDescription) {
        this.requestId = requestId;
        this.resultCode = resultCode;
        this.resultDescription = resultDescription;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    @Override
    public String toString() {
        return "PayPointResponse{" + "requestId=" + requestId + ", resultCode=" + resultCode + ", resultDescription='"
                + resultDescription + '\'' + '}';
    }
}
