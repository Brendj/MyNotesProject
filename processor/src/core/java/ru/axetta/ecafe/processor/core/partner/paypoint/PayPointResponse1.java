/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.paypoint;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 15:05:20
 * To change this template use File | Settings | File Templates.
 */
public class PayPointResponse1 extends PayPointResponse {

    public static final int ID = PayPointRequest1.ID;
    private final long clientId;
    private final long operationId;
    private final Long balance;
    private final String clientNameAbbreviation;
    private final String clientAddress;
    private final Long cardPrintedNo;

    public PayPointResponse1(int requestId, int resultCode, String resultDescription, long clientId, long operationId,
            Long balance, String clientNameAbbreviation, String clientAddress, Long cardPrintedNo) {
        super(requestId, resultCode, resultDescription);
        this.clientId = clientId;
        this.operationId = operationId;
        this.balance = balance;
        this.clientNameAbbreviation = clientNameAbbreviation;
        this.clientAddress = clientAddress;
        this.cardPrintedNo = cardPrintedNo;
    }

    public long getClientId() {
        return clientId;
    }

    public long getOperationId() {
        return operationId;
    }

    public Long getBalance() {
        return balance;
    }

    public String getClientNameAbbreviation() {
        return clientNameAbbreviation;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public Long getCardPrintedNo() {
        return cardPrintedNo;
    }

    @Override
    public String toString() {
        return "PayPointResponse1{" + "clientId=" + clientId + ", operationId=" + operationId + ", balance=" + balance
                + ", clientNameAbbreviation='" + clientNameAbbreviation + '\'' + ", clientAddress='" + clientAddress
                + '\'' + ", cardPrintedNo=" + cardPrintedNo + '}';
    }
}
