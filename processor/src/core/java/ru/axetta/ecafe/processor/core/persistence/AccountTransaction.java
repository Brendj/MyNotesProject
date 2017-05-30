/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.client.ContractIdGenerator;
import ru.axetta.ecafe.processor.core.service.meal.MealManager;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
public class AccountTransaction {

    public static final int PAYMENT_SYSTEM_TRANSACTION_SOURCE_TYPE = 3;
    public static final int CASHBOX_TRANSACTION_SOURCE_TYPE = 4;
    public static final int CLIENT_ORDER_TRANSACTION_SOURCE_TYPE = 8;
    public static final int INTERNAL_ORDER_TRANSACTION_SOURCE_TYPE = 10;
    public static final int SUBSCRIPTION_FEE_TRANSACTION_SOURCE_TYPE = 20;
    public static final int CANCEL_TRANSACTION_SOURCE_TYPE = 30;
    public static final int ACCOUNT_TRANSFER_TRANSACTION_SOURCE_TYPE = 40;
    public static final int ACCOUNT_REFUND_TRANSACTION_SOURCE_TYPE = 50;

    public static String sourceTypeToString(int sourceType) {
        if (sourceType==PAYMENT_SYSTEM_TRANSACTION_SOURCE_TYPE) return "Платежная система";
        else if (sourceType==CASHBOX_TRANSACTION_SOURCE_TYPE) return "Пополнение через кассовый терминал";
        else if (sourceType==CLIENT_ORDER_TRANSACTION_SOURCE_TYPE) return "Покупка";
        else if (sourceType==INTERNAL_ORDER_TRANSACTION_SOURCE_TYPE) return "Операция";
        else if (sourceType==SUBSCRIPTION_FEE_TRANSACTION_SOURCE_TYPE) return "Плата";
        else if (sourceType==CANCEL_TRANSACTION_SOURCE_TYPE) return "Отмена";
        else if (sourceType==ACCOUNT_TRANSFER_TRANSACTION_SOURCE_TYPE) return "Перевод между счетами";
        else if (sourceType==ACCOUNT_REFUND_TRANSACTION_SOURCE_TYPE) return "Возврат средств";
        return "Неизвестно";
    }

    private Long idOfTransaction;
    private Client client;
    private Card card;
    private Long transactionSum;
    private Long transactionSubBalance1Sum;
    private Long balanceBeforeTransaction;
    private Long balanceAfterTransaction;
    private Long subBalance1BeforeTransaction;
    // Номер счета по умолчаню основной
    private Long sourceBalanceNumber;
    private String source;
    private Integer sourceType;
    private Date transactionTime;
    private Set<ContragentPayment> contragentPayments = new HashSet<ContragentPayment>();
    private Set<ClientPayment> clientPayments = new HashSet<ClientPayment>();
    private Set<Order> orders = new HashSet<Order>();
    private Set<ClientSms> clientSms = new HashSet<ClientSms>();
    private Set<SubscriptionFee> subscriptionFees = new HashSet<SubscriptionFee>();
    private Org org;
    private boolean sendToExternal;

    protected AccountTransaction() {
        // For Hibernate only
    }

    public AccountTransaction(Client client, Card card,Long sourceBalanceNumber,  long transactionSum, String source, int sourceType,
            Date transactionTime) throws Exception {
        this.client = client;
        this.balanceBeforeTransaction = client.getBalance();
        this.card = card;
        this.sourceBalanceNumber = sourceBalanceNumber;
        this.transactionSum = transactionSum;
        this.source = source;
        this.sourceType = sourceType;
        this.transactionTime = transactionTime;
        this.sendToExternal = sourceType != CLIENT_ORDER_TRANSACTION_SOURCE_TYPE && !MealManager.isSendToExternal;
    }

    public Long getIdOfTransaction() {
        return idOfTransaction;
    }

    private void setIdOfTransaction(Long idOfTransaction) {
        // For Hibernate only
        this.idOfTransaction = idOfTransaction;
    }

    public Client getClient() {
        return client;
    }

    private void setClient(Client client) {
        // For Hibernate only
        this.client = client;
    }

    public Card getCard() {
        return card;
    }

    private void setCard(Card card) {
        // For Hibernate only
        this.card = card;
    }

    public Long getSourceBalanceNumber() {
        return sourceBalanceNumber;
    }

    public String getSourceBalanceNumberFormat(){
        if(ContractIdGenerator.luhnTest(String.valueOf(sourceBalanceNumber))){
            return String.format("%08d", sourceBalanceNumber);
        } else {
            return String.format("%010d", sourceBalanceNumber);
        }
    }

    public boolean getSourceBalanceNumberEqualsContractId(){
        return sourceBalanceNumber == null || sourceBalanceNumber.equals(client.getContractId());
    }

    public void setSourceBalanceNumber(Long sourceBalanceNumber) {
        this.sourceBalanceNumber = sourceBalanceNumber;
    }

    public Long getTransactionSum() {
        return transactionSum;
    }

    private void setTransactionSum(Long transactionSum) {
        // For Hibernate only
        this.transactionSum = transactionSum;
    }

    public Long getBalanceBeforeTransaction() {
        return balanceBeforeTransaction;
    }

    public void setBalanceBeforeTransaction(Long balanceBeforeTransaction) {
        this.balanceBeforeTransaction = balanceBeforeTransaction;
    }

    public Long getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    public void setBalanceAfterTransaction(Long balanceAfterTransaction) {
        this.balanceAfterTransaction = balanceAfterTransaction;
    }

    public Long getTransactionSubBalance1Sum() {
        return transactionSubBalance1Sum;
    }

    public void setTransactionSubBalance1Sum(Long transactionSubBalance1Sum) {
        this.transactionSubBalance1Sum = transactionSubBalance1Sum;
    }

    public Long getSubBalance1BeforeTransaction() {
        return subBalance1BeforeTransaction;
    }

    public void setSubBalance1BeforeTransaction(Long subBalance1BeforeTransaction) {
        this.subBalance1BeforeTransaction = subBalance1BeforeTransaction;
    }

    public String getSource() {
        return source;
    }

    protected void setSource(String source) {
        // For Hibernate only
        this.source = source;
    }
    
    public void updateSource(String source) {
        if (sourceType!=ACCOUNT_TRANSFER_TRANSACTION_SOURCE_TYPE && sourceType!=ACCOUNT_REFUND_TRANSACTION_SOURCE_TYPE)
            throw new Error("Source update on type: "+getSourceTypeAsString()+" prohibited");
    }

    public Integer getSourceType() {
        return sourceType;
    }

    private void setSourceType(Integer sourceType) {
        // For Hibernate only
        this.sourceType = sourceType;
    }

    public String getSourceTypeAsString() {
        return sourceTypeToString(sourceType);
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    private void setTransactionTime(Date transactionTime) {
        // For Hibernate only
        this.transactionTime = transactionTime;
    }

    private Set<ContragentPayment> getContragentPaymentsInternal() {
        // For Hibernate only
        return contragentPayments;
    }

    private void setContragentPaymentsInternal(Set<ContragentPayment> contragentPayments) {
        // For Hibernate only
        this.contragentPayments = contragentPayments;
    }

    public Set<ContragentPayment> getContragentPayments() {
        return Collections.unmodifiableSet(getContragentPaymentsInternal());
    }

    private Set<ClientPayment> getClientPaymentsInternal() {
        // For Hibernate only
        return clientPayments;
    }

    private void setClientPaymentsInternal(Set<ClientPayment> clientPayments) {
        // For Hibernate only
        this.clientPayments = clientPayments;
    }

    public Set<ClientPayment> getClientPayments() {
        return Collections.unmodifiableSet(getClientPaymentsInternal());
    }

    private Set<Order> getOrdersInternal() {
        // For Hibernate only
        return orders;
    }

    private void setOrdersInternal(Set<Order> orders) {
        // For Hibernate only
        this.orders = orders;
    }

    public Set<Order> getOrders() {
        return Collections.unmodifiableSet(getOrdersInternal());
    }

    private Set<ClientSms> getClientSmsInternal() {
        // For Hibernate only
        return clientSms;
    }

    private void setClientSmsInternal(Set<ClientSms> clientSms) {
        // For Hibernate only
        this.clientSms = clientSms;
    }

    public Set<ClientSms> getClientSms() {
        return Collections.unmodifiableSet(getClientSmsInternal());
    }

    private Set<SubscriptionFee> getSubscriptionFeesInternal() {
        // For Hibernate only
        return subscriptionFees;
    }

    private void setSubscriptionFeesInternal(Set<SubscriptionFee> subscriptionFees) {
        // For Hibernate only
        this.subscriptionFees = subscriptionFees;
    }

    public Set<SubscriptionFee> getSubscriptionFees() {
        return Collections.unmodifiableSet(getSubscriptionFeesInternal());
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public boolean isSendToExternal() {
        return sendToExternal;
    }

    public void setSendToExternal(boolean sendToExternal) {
        this.sendToExternal = sendToExternal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccountTransaction)) {
            return false;
        }
        final AccountTransaction that = (AccountTransaction) o;
        if (!idOfTransaction.equals(that.getIdOfTransaction())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return idOfTransaction.hashCode();
    }

    @Override
    public String toString() {
        return "AccountTransaction{" + "idOfTransaction=" + idOfTransaction + ", client=" + client + ", card=" + card
                + ", transactionSum=" + transactionSum + ", source='" + source + '\'' + ", sourceType=" + sourceType
                + ", transactionTime=" + transactionTime +
                ", idOfOrg=" + (org != null ? org.getOfficialName() : "unknown") + '}';
    }
}