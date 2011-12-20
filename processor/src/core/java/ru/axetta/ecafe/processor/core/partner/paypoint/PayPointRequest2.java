/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.paypoint;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 15:04:20
 * To change this template use File | Settings | File Templates.
 */
public class PayPointRequest2 extends PayPointRequest {

    public static final int ID = PayPointRequest1.ID + 1;

    private final long clientId;
    private final long operationId;
    private final long terminalId;
    private final long sum;
    private final long sumf;
    private final Date time;

    public PayPointRequest2(int requestId, long clientId, long operationId, long terminalId, long sum, long sumf,
            Date time) {
        super(requestId);
        this.clientId = clientId;
        this.operationId = operationId;
        this.terminalId = terminalId;
        this.sum = sum;
        this.sumf = sumf;
        this.time = time;
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

    public long getSum() {
        return sum;
    }

    public long getSumf() {
        return sumf;
    }

    public Date getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "PayPointRequest2{" + "clientId=" + clientId + ", operationId=" + operationId + ", terminalId="
                + terminalId + ", sum=" + sum + ", sumf=" + sumf + ", time=" + time + "} " + super.toString();
    }
}
