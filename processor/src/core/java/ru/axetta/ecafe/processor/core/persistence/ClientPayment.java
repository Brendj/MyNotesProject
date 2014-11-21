/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
public class ClientPayment {

    public static final String[] PAYMENT_METHOD_NAMES = {
            "Банковская карта Visa/MasterCard", "Электронные платёжные системы", "Предоплаченная карта RBK Money",
            "Системы денежных переводов", "Платёжные терминалы", "SMS", "Банковский платёж", "Почта России",
            "Банкомат/терминал", "Интернет банкинг", "Синхронизация реестров платежей", "Интернет-эквайринг",
            "Автоплатеж с банковской карты"};

    public static final String[] PAYMENT_METHOD_SHORT_NAMES = {
            "БК", "ЭПС", "ПК",
            "СДП", "ПТ", "SMS", "БП", "ПР",
            "Б/Т", "ИБ", "РЗ", "ИЭ",
            "АБК"};

    public static final int SYNC_PAYMENT_METHOD = 10;
    public static final int KIOSK_PAYMENT_METHOD = 4;
    public static final int PAY_POINT_PAYMENT_METHOD = 4;
    public static final int CLIENT_TO_ACCOUNT_PAYMENT = 1;
    public static final int CLIENT_TO_SUB_ACCOUNT_PAYMENT = 2;
    public static final int ATM_PAYMENT_METHOD = 8;
    public static final int AUTO_PAYMENT_METHOD = 12;
    public static final int INTERNET_ACQUIRING_METHOD = 11;

    private Long idOfClientPayment;
    private AccountTransaction transaction;
    private Integer paymentMethod;
    private Long paySum;
    private Integer payType;
    private Date createTime;
    private String idOfPayment;
    private Contragent contragent;
    private ClientPaymentOrder clientPaymentOrder;
    private String addPaymentMethod;
    private String addIdOfPayment;
    private Contragent contragentReceiver;

    protected ClientPayment() {
        // For Hibernate only
    }

    ClientPayment(AccountTransaction transaction, Integer paymentMethod, Long paySum, Integer payType, Date createTime,
            String idOfPayment, Contragent contragent, Contragent contragentReceiver) {
        this.transaction = transaction;
        this.paymentMethod = paymentMethod;
        this.paySum = paySum;
        this.payType = payType;
        this.createTime = createTime;
        this.idOfPayment = idOfPayment;
        this.contragentReceiver = contragentReceiver;
        this.contragent = contragent;
    }

    public ClientPayment(AccountTransaction transaction, Integer paymentMethod, Long paySum, Integer payType,
            Date createTime, String idOfPayment, Contragent contragent, Contragent contragentReceiver, String addPaymentMethod, String addIdOfPayment) {
        this.transaction = transaction;
        this.paymentMethod = paymentMethod;
        this.paySum = paySum;
        this.payType = payType;
        this.createTime = createTime;
        this.idOfPayment = idOfPayment;
        this.contragent = contragent;
        this.contragentReceiver = contragentReceiver;
        this.addPaymentMethod = addPaymentMethod;
        this.addIdOfPayment = addIdOfPayment;
    }

    public ClientPayment(AccountTransaction transaction, ClientPaymentOrder clientPaymentOrder, Contragent contragentReceiver, Date createTime) {
        this.transaction = transaction;
        this.paymentMethod = clientPaymentOrder.getPaymentMethod();
        this.paySum = clientPaymentOrder.getPaySum();
        this.payType = CLIENT_TO_ACCOUNT_PAYMENT;
        this.createTime = createTime;
        this.idOfPayment = clientPaymentOrder.getIdOfPayment();
        this.contragent = clientPaymentOrder.getContragent();
        this.contragentReceiver = contragentReceiver;
        this.clientPaymentOrder = clientPaymentOrder;
    }

    public ClientPayment(AccountTransaction transaction, ClientPaymentOrder clientPaymentOrder, Contragent contragentReceiver, Date createTime, String addIdOfPayment) {
        this.transaction = transaction;
        this.paymentMethod = clientPaymentOrder.getPaymentMethod();
        this.paySum = clientPaymentOrder.getPaySum();
        this.payType = CLIENT_TO_ACCOUNT_PAYMENT;
        this.createTime = createTime;
        this.idOfPayment = clientPaymentOrder.getIdOfPayment();
        this.contragent = clientPaymentOrder.getContragent();
        this.clientPaymentOrder = clientPaymentOrder;
        this.contragentReceiver = contragentReceiver;
        this.addIdOfPayment = addIdOfPayment;
    }

    public ClientPayment(Long contragentSum, AccountTransaction transaction, ClientPaymentOrder clientPaymentOrder, Contragent contragentReceiver, Date createTime) {
        this.transaction = transaction;
        this.paymentMethod = clientPaymentOrder.getPaymentMethod();
        this.paySum = contragentSum;
        this.payType = CLIENT_TO_ACCOUNT_PAYMENT;
        this.createTime = createTime;
        this.idOfPayment = clientPaymentOrder.getIdOfPayment();
        this.contragent = clientPaymentOrder.getContragent();
        this.contragentReceiver = contragentReceiver;
        this.clientPaymentOrder = clientPaymentOrder;

    }

    public Long getIdOfClientPayment() {
        return idOfClientPayment;
    }

    private void setIdOfClientPayment(Long idOfClientPayment) {
        // For Hibernate only
        this.idOfClientPayment = idOfClientPayment;
    }

    public AccountTransaction getTransaction() {
        return transaction;
    }

    private void setTransaction(AccountTransaction accountTransaction) {
        // For Hibernate only
        this.transaction = accountTransaction;
    }

    public Long getPaySum() {
        return paySum;
    }

    private void setPaySum(Long paySum) {
        // For Hibernate only
        this.paySum = paySum;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    private void setPaymentMethod(Integer paymentMethod) {
        // For Hibernate only
        this.paymentMethod = paymentMethod;
    }

    public Integer getPayType() {
        return payType;
    }

    private void setPayType(Integer payType) {
        // For Hibernate only
        this.payType = payType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    private void setCreateTime(Date createTime) {
        // For Hibernate only
        this.createTime = createTime;
    }

    public String getIdOfPayment() {
        return idOfPayment;
    }

    private void setIdOfPayment(String idOfPayment) {
        // For Hibernate only
        this.idOfPayment = idOfPayment;
    }

    public Contragent getContragent() {
        return contragent;
    }

    private void setContragent(Contragent contragent) {
        // For Hibernate only
        this.contragent = contragent;
    }

    public Contragent getContragentReceiver() {
        return contragentReceiver;
    }

    private void setContragentReceiver(Contragent contragentReceiver) {
        // For Hibernate only
        this.contragentReceiver = contragentReceiver;
    }

    public ClientPaymentOrder getClientPaymentOrder() {
        return clientPaymentOrder;
    }

    private void setClientPaymentOrder(ClientPaymentOrder clientPaymentOrder) {
        // For Hibernate only
        this.clientPaymentOrder = clientPaymentOrder;
    }

    public String getAddPaymentMethod() {
        return addPaymentMethod;
    }

    private void setAddPaymentMethod(String addPaymentMethod) {
        // For Hibernate only
        this.addPaymentMethod = addPaymentMethod;
    }

    public String getAddIdOfPayment() {
        return addIdOfPayment;
    }

    private void setAddIdOfPayment(String addIdOfPayment) {
        // For Hibernate only
        this.addIdOfPayment = addIdOfPayment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientPayment)) {
            return false;
        }
        final ClientPayment that = (ClientPayment) o;
        if (!idOfClientPayment.equals(that.getIdOfClientPayment())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return idOfClientPayment.hashCode();
    }

    @Override
    public String toString() {
        return "ClientPayment{" + "idOfClientPayment=" + idOfClientPayment + ", accountTransaction=" + transaction
                + ", paymentMethod=" + paymentMethod + ", paySum=" + paySum + ", payType=" + payType + ", createTime="
                + createTime + ", idOfPayment='" + idOfPayment + '\'' + ", contragent=" + contragent
                + ", clientPaymentOrder=" + clientPaymentOrder + ", addPaymentMethod='" + addPaymentMethod + '\''
                + ", addIdOfPayment='" + addIdOfPayment + '\'' + '}';
    }
}