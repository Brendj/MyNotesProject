/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.paypoint;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 15:05:51
 * To change this template use File | Settings | File Templates.
 */
public class PayPointResponse3 extends PayPointResponse {

    public static final int ID = PayPointRequest3.ID;
    private final long operationId;
    private final Long sum;

    public PayPointResponse3(int requestId, int resultCode, String resultDescription, long operationId, Long sum) {
        super(requestId, resultCode, resultDescription);
        this.operationId = operationId;
        this.sum = sum;
    }

    public long getOperationId() {
        return operationId;
    }

    public Long getSum() {
        return sum;
    }

    @Override
    public String toString() {
        return "PayPointResponse3{" + "operationId=" + operationId + ", sum=" + sum + "} " + super.toString();
    }
}
