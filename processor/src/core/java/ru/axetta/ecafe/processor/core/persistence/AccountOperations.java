/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.sync.SyncRequest;
import ru.axetta.ecafe.processor.core.sync.handlers.registry.operations.account.AccountOperationItem;

import java.io.Serializable;

/**
 * User: Shamil
 * Date: 20.02.15
 * Time: 13:45
 */
public class AccountOperations implements Serializable {

    private long idOfAccountOperation;
    private long idOfOrg;
    private int accountOperationType;
    //data from arm:
    private long idOfOperation;
    private long date;
    private long idOfContract;
    private long value;
    private int type;
    private Long idOfOrder;
    private String staffGuid;
    private Long idOfPos;
    private Long idOfContragent;

    public static final int CASHIER_OPERATIONTYPE = 0;//Операция через кассу

    public static final int TYPE_PAYMENT = 0;
    public static final int TYPE_CANCEL = 1;

    public AccountOperations() {
    }

    public AccountOperations(AccountOperationItem accountOperationItem, SyncRequest request) {
        idOfOrg = request.getIdOfOrg();
        accountOperationType = CASHIER_OPERATIONTYPE;

        idOfOperation = accountOperationItem.getIdOfOperation();
        date = accountOperationItem.getDate();
        idOfContract = accountOperationItem.getIdOfContract();
        value = accountOperationItem.getValue();
        type = accountOperationItem.getType();
        idOfOrder = accountOperationItem.getIdOfOrder();
        staffGuid = accountOperationItem.getStaffGuid();
        idOfPos = accountOperationItem.getIdOfPos();
        idOfContragent = accountOperationItem.getIdOfPos();
    }

    public long getIdOfAccountOperation() {
        return idOfAccountOperation;
    }

    public void setIdOfAccountOperation(long idOfAccountOperation) {
        this.idOfAccountOperation = idOfAccountOperation;
    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public int getAccountOperationType() {
        return accountOperationType;
    }

    public void setAccountOperationType(int accountOperationType) {
        this.accountOperationType = accountOperationType;
    }

    public long getIdOfOperation() {
        return idOfOperation;
    }

    public void setIdOfOperation(long idOfOperation) {
        this.idOfOperation = idOfOperation;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getIdOfContract() {
        return idOfContract;
    }

    public void setIdOfContract(long idOfContract) {
        this.idOfContract = idOfContract;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
    }

    public String getStaffGuid() {
        return staffGuid;
    }

    public void setStaffGuid(String staffGuid) {
        this.staffGuid = staffGuid;
    }

    public Long getIdOfPos() {
        return idOfPos;
    }

    public void setIdOfPos(Long idOfPos) {
        this.idOfPos = idOfPos;
    }

    public Long getIdOfContragent() {
        return idOfContragent;
    }

    public void setIdOfContragent(Long idOfContragent) {
        this.idOfContragent = idOfContragent;
    }
}
