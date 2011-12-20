/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.paypoint;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 15:04:03
 * To change this template use File | Settings | File Templates.
 */
public class PayPointRequest1 extends PayPointRequest {

    public static final int ID = 1;

    private final long clientId;
    private final long operationId;
    private final long terminalId;

    public PayPointRequest1(int requestId, long clientId, long operationId, long terminalId) {
        super(requestId);
        this.clientId = clientId;
        this.operationId = operationId;
        this.terminalId = terminalId;
    }

    public long getClientId() {
        return clientId;
    }

    public long getOperationId() {
        return operationId;
    }

    public long getTerminalId() {
        return terminalId;
    }

    @Override
    public String toString() {
        return "PayPointRequest1{" + "clientId=" + clientId + ", operationId=" + operationId + ", terminalId="
                + terminalId + "} " + super.toString();
    }
}
