/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

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
public class ClientPaymentOrder {

    public static final int ORDER_STATUS_CREATED = 0;
    public static final int ORDER_STATUS_CANCELLED = 1;
    public static final int ORDER_STATUS_FAILED = 2;
    public static final int ORDER_STATUS_ACCEPTED = 3;
    public static final int ORDER_STATUS_TRANSFER_ACCEPTED = 4;
    public static final int ORDER_STATUS_TRANSFER_COMPLETED = 5;

    private Long idOfClientPaymentOrder;
    private Contragent contragent;
    private Client client;
    private Integer paymentMethod;
    private Integer orderStatus;
    private Long paySum;
    private Long contragentSum;
    private Date createTime;
    private String idOfPayment;
    private Set<ru.axetta.ecafe.processor.core.persistence.ClientPayment> clientPayments = new HashSet<ru.axetta.ecafe.processor.core.persistence.ClientPayment>();

    ClientPaymentOrder() {
        // For Hibernate only
    }

    public ClientPaymentOrder(Contragent contragent, Client client, Integer paymentMethod, Long paySum,
            Long contragentSum, Date createTime) {
        this.contragent = contragent;
        this.client = client;
        this.paymentMethod = paymentMethod;
        this.orderStatus = ORDER_STATUS_CREATED;
        this.paySum = paySum;
        this.contragentSum = contragentSum;
        this.createTime = createTime;
        this.idOfPayment = "";
    }

    public Long getIdOfClientPaymentOrder() {
        return idOfClientPaymentOrder;
    }

    private void setIdOfClientPaymentOrder(Long idOfClientPaymentOrder) {
        // For Hibernate only
        this.idOfClientPaymentOrder = idOfClientPaymentOrder;
    }

    public Contragent getContragent() {
        return contragent;
    }

    private void setContragent(Contragent contragent) {
        // For Hibernate only
        this.contragent = contragent;
    }

    public Client getClient() {
        return client;
    }

    private void setClient(Client client) {
        // For Hibernate only
        this.client = client;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    private void setPaymentMethod(Integer paymentMethod) {
        // For Hibernate only
        this.paymentMethod = paymentMethod;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getPaySum() {
        return paySum;
    }

    private void setPaySum(Long paySum) {
        // For Hibernate only
        this.paySum = paySum;
    }

    public Long getContragentSum() {
        return contragentSum;
    }

    private void setContragentSum(Long contragentSum) {
        // For Hibernate only
        this.contragentSum = contragentSum;
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

    public void setIdOfPayment(String idOfPayment) {
        this.idOfPayment = idOfPayment;
    }

    private Set<ru.axetta.ecafe.processor.core.persistence.ClientPayment> getClientPaymentsInternal() {
        // For Hibernate only
        return clientPayments;
    }

    private void setClientPaymentsInternal(Set<ru.axetta.ecafe.processor.core.persistence.ClientPayment> clientPayments) {
        // For Hibernate only
        this.clientPayments = clientPayments;
    }

    public Set<ru.axetta.ecafe.processor.core.persistence.ClientPayment> getClientPayments() {
        return Collections.unmodifiableSet(getClientPaymentsInternal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientPaymentOrder)) {
            return false;
        }
        final ClientPaymentOrder that = (ClientPaymentOrder) o;
        if (!idOfClientPaymentOrder.equals(that.getIdOfClientPaymentOrder())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return idOfClientPaymentOrder.hashCode();
    }

    @Override
    public String toString() {
        return "ClientPaymentOrder{" + "idOfClientPaymentOrder=" + idOfClientPaymentOrder + ", contragent=" + contragent
                + ", client=" + client + ", paymentMethod=" + paymentMethod + ", orderStatus=" + orderStatus
                + ", paySum=" + paySum + ", contragentSum=" + contragentSum + ", createTime=" + createTime
                + ", idOfPayment='" + idOfPayment + '\'' + '}';
    }

    public boolean canApplyOrderStatus(int newOrderStatus) {
        return this.orderStatus < newOrderStatus;
    }
}