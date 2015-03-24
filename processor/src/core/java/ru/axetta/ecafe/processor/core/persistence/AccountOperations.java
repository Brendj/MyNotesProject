/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
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

    private Long idOfClientPayment;

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
        idOfContragent = accountOperationItem.getIdOfContragent();
    }

    public AccountOperations(AccountOperationItem accountOperationItem, SyncRequest request,
            OnlinePaymentProcessor.PayResponse payResponse) {
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
        idOfContragent = accountOperationItem.getIdOfContragent();
        idOfClientPayment = payResponse.getIdOfClientPayment();
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

    public Long getIdOfClientPayment() {
        return idOfClientPayment;
    }

    public void setIdOfClientPayment(Long idOfClientPayment) {
        this.idOfClientPayment = idOfClientPayment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AccountOperations that = (AccountOperations) o;

        if (date != that.date) {
            return false;
        }
        if (idOfContract != that.idOfContract) {
            return false;
        }
        if (idOfOperation != that.idOfOperation) {
            return false;
        }
        if (type != that.type) {
            return false;
        }
        if (value != that.value) {
            return false;
        }
        if (idOfContragent != null ? !idOfContragent.equals(that.idOfContragent) : that.idOfContragent != null) {
            return false;
        }
        if (idOfOrder != null ? !idOfOrder.equals(that.idOfOrder) : that.idOfOrder != null) {
            return false;
        }
        if (idOfPos != null ? !idOfPos.equals(that.idOfPos) : that.idOfPos != null) {
            return false;
        }
        if (staffGuid != null ? !staffGuid.equals(that.staffGuid) : that.staffGuid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idOfAccountOperation ^ (idOfAccountOperation >>> 32));
        result = 31 * result + (int) (idOfOrg ^ (idOfOrg >>> 32));
        result = 31 * result + accountOperationType;
        result = 31 * result + (int) (idOfOperation ^ (idOfOperation >>> 32));
        result = 31 * result + (int) (date ^ (date >>> 32));
        result = 31 * result + (int) (idOfContract ^ (idOfContract >>> 32));
        result = 31 * result + (int) (value ^ (value >>> 32));
        result = 31 * result + type;
        result = 31 * result + (idOfOrder != null ? idOfOrder.hashCode() : 0);
        result = 31 * result + (staffGuid != null ? staffGuid.hashCode() : 0);
        result = 31 * result + (idOfPos != null ? idOfPos.hashCode() : 0);
        result = 31 * result + (idOfContragent != null ? idOfContragent.hashCode() : 0);
        return result;
    }
}
