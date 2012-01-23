/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.paypoint;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 15:04:42
 * To change this template use File | Settings | File Templates.
 */
public class PayPointRequest3 extends PayPointRequest {

    public static final int ID = PayPointRequest2.ID + 1;

    private final long operationId;

    public PayPointRequest3(int requestId, long operationId) {
        super(requestId);
        this.operationId = operationId;
    }

    public long getOperationId() {
        return operationId;
    }

    @Override
    public String toString() {
        return "PayPointRequest3{" + "operationId=" + operationId + "} " + super.toString();
    }
}
