/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.paypoint;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 15:05:35
 * To change this template use File | Settings | File Templates.
 */
public class PayPointResponse2 extends PayPointResponse {

    public static final int ID = PayPointRequest2.ID;
    private final long operationId;
    private final Long cardPrintedNo;

    public PayPointResponse2(int requestId, int resultCode, String resultDescription, long operationId,
            Long cardPrintedNo) {
        super(requestId, resultCode, resultDescription);
        this.operationId = operationId;
        this.cardPrintedNo = cardPrintedNo;
    }

    public long getOperationId() {
        return operationId;
    }

    public Long getCardPrintedNo() {
        return cardPrintedNo;
    }

    @Override
    public String toString() {
        return "PayPointResponse2{" + "operationId=" + operationId + ", cardPrintedNo=" + cardPrintedNo + '}';
    }
}
