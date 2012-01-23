/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.paypoint;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 15:03:13
 * To change this template use File | Settings | File Templates.
 */
public abstract class PayPointRequest {

    private final int requestId;

    public PayPointRequest(int requestId) {
        this.requestId = requestId;
    }

    public int getRequestId() {
        return requestId;
    }

    @Override
    public String toString() {
        return "PayPointRequest{" + "requestId=" + requestId + '}';
    }
}
