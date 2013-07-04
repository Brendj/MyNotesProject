/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.06.13
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public class CardTempOperation {

    private Long idOfCardTempOperation;
    private Long localId;
    private Org org;
    private CardTemp cardTemp;
    private CardOperationStation operationType;
    private Date operationDate;
    private Client client;
    private Visitor visitor;

    public CardTempOperation(Long idOfCardTempOperation, Org org, CardTemp cardTemp, CardOperationStation operationType,
            Date operationDate, Client client) {
        this.localId = idOfCardTempOperation;
        this.org = org;
        this.cardTemp = cardTemp;
        this.operationType = operationType;
        this.operationDate = operationDate;
        this.client = client;
    }

    public CardTempOperation(Long idOfCardTempOperation, Org org, CardTemp cardTemp, CardOperationStation operationType, Date operationDate,
            Visitor visitor) {
        this.localId = idOfCardTempOperation;
        this.org = org;
        this.cardTemp = cardTemp;
        this.operationType = operationType;
        this.operationDate = operationDate;
        this.visitor = visitor;
    }

    public Long getIdOfCardTempOperation() {
        return idOfCardTempOperation;
    }

    public Long getLocalId() {
        return localId;
    }

    public Org getOrg() {
        return org;
    }

    public CardTemp getCardTemp() {
        return cardTemp;
    }

    public CardOperationStation getOperationType() {
        return operationType;
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public Client getClient() {
        return client;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    void setIdOfCardTempOperation(Long idOfCardTempOperation) {
        this.idOfCardTempOperation = idOfCardTempOperation;
    }

    void setOrg(Org org) {
        this.org = org;
    }

    void setCardTemp(CardTemp cardTemp) {
        this.cardTemp = cardTemp;
    }

    void setOperationType(CardOperationStation operationType) {
        this.operationType = operationType;
    }

    void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    void setClient(Client client) {
        this.client = client;
    }

    void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    void setLocalId(Long localId) {
        this.localId = localId;
    }

    protected CardTempOperation() {
    }
}
