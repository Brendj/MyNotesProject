/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
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
public class Order{
    public static final int STATE_COMMITED=0, STATE_CANCELED=1;

    public static final long INTERNAL_ID_OF_CASHIER = 0L;

    private CompositeIdOfOrder compositeIdOfOrder;
    private Org org;
    private Card card;
    private Client client;
    private AccountTransaction transaction;
    private Long idOfCashier;
    private Long socDiscount;
    private Long trdDiscount;
    private Long grantSum;
    private Long rSum;
    private Date createTime;
    private Long sumByCard;
    private Long sumByCash;
    private Set<OrderDetail> orderDetails = new HashSet<OrderDetail>();
    private POS pos;
    private Contragent contragent;
    private int state;
    private Long confirmerId;
    private Date orderDate;
    private String comments;
    private OrderTypeEnumType orderType;
    private Long idOfClientGroup;
    private Long idOfPayForClient;
    private Boolean isFromFriendlyOrg;
    private Long idOrgPayment;

    public OrderTypeEnumType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderTypeEnumType orderType) {
        this.orderType = orderType;
    }

    protected Order() {
        // For Hibernate only
    }

    public Order(CompositeIdOfOrder compositeIdOfOrder, long idOfCashier, long socDiscount, Long trdDiscount, long grantSum, long rSum,
            Date createTime, Date orderDate, long sumByCard, long sumByCash, String comments, Client client, Card card, AccountTransaction transaction,
            POS pos, Contragent contragent, OrderTypeEnumType orderType, Long idOfPayForClient) {
        this.compositeIdOfOrder = compositeIdOfOrder;
        this.idOfCashier = idOfCashier;
        this.socDiscount = socDiscount;
        this.trdDiscount = trdDiscount;
        this.grantSum = grantSum;
        this.rSum = rSum;
        this.createTime = createTime;
        this.orderDate = orderDate;
        this.sumByCard = sumByCard;
        this.sumByCash = sumByCash;
        this.client = client;
        this.card = card;
        this.transaction = transaction;
        this.pos = pos;
        this.contragent = contragent;
        this.comments = comments;
        this.orderType = orderType;
        this.idOfPayForClient = idOfPayForClient;
        this.isFromFriendlyOrg = false;
    }

    public CompositeIdOfOrder getCompositeIdOfOrder() {
        return compositeIdOfOrder;
    }

    private void setCompositeIdOfOrder(CompositeIdOfOrder compositeIdOfOrder) {
        // For Hibernate only
        this.compositeIdOfOrder = compositeIdOfOrder;
    }

    public Long getConfirmerId() {
        return confirmerId;
    }

    public void setConfirmerId(Long confirmerId) {
        this.confirmerId = confirmerId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    protected void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Long getIdOfCashier() {
        return idOfCashier;
    }

    private void setIdOfCashier(Long idOfCashier) {
        // For Hibernate only
        this.idOfCashier = idOfCashier;
    }

    public Long getSocDiscount() {
        return socDiscount;
    }

    public void setSocDiscount(Long socDiscount) {
        this.socDiscount = socDiscount;
    }

    public Long getTrdDiscount() {
        return trdDiscount;
    }

    public void setTrdDiscount(Long trdDiscount) {
        this.trdDiscount = trdDiscount;
    }

    public Long getGrantSum() {
        return grantSum;
    }

    private void setGrantSum(Long grantSum) {
        // For Hibernate only
        this.grantSum = grantSum;
    }

    public Long getRSum() {
        return rSum;
    }

    private void setRSum(Long rSum) {
        // For Hibernate only
        this.rSum = rSum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    private void setCreateTime(Date createTime) {
        // For Hibernate only
        this.createTime = createTime;
    }

    public Long getSumByCard() {
        return sumByCard;
    }

    private void setSumByCard(Long sumByCard) {
        // For Hibernate
        this.sumByCard = sumByCard;
    }

    public Long getSumByCash() {
        return sumByCash;
    }

    private void setSumByCash(Long sumByCash) {
        // For Hibernate
        this.sumByCash = sumByCash;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Org getOrg() {
        return org;
    }

    private void setOrg(Org org) {
        // For Hibernate only
        this.org = org;
    }

    public Card getCard() {
        return card;
    }

    private void setCard(Card card) {
        // For Hibernate only
        this.card = card;
    }

    public Client getClient() {
        return client;
    }

    private void setClient(Client client) {
        // For Hibernate only
        this.client = client;
    }

    public AccountTransaction getTransaction() {
        return transaction;
    }

    private void setTransaction(AccountTransaction accountTransaction) {
        // For Hibernate only
        this.transaction = accountTransaction;
    }

    public POS getPos() {
        return pos;
    }

    public void setPos(POS pos) {
        this.pos = pos;
    }

    public Contragent getContragent() {
        return contragent;
    }

    public void setContragent(Contragent contragent) {
        this.contragent = contragent;
    }

    private Set<OrderDetail> getOrderDetailsInternal() {
        // For Hibernate only
        return orderDetails;
    }

    private void setOrderDetailsInternal(Set<OrderDetail> orderDetails) {
        // For Hibernate only
        this.orderDetails = orderDetails;
    }

    public Set<OrderDetail> getOrderDetails() {
        return Collections.unmodifiableSet(getOrderDetailsInternal());
    }

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }

    public Long getIdOfPayForClient() {
        return idOfPayForClient;
    }

    public void setIdOfPayForClient(Long idOfPayForClient) {
        this.idOfPayForClient = idOfPayForClient;
    }

    public Boolean getIsFromFriendlyOrg() {
        return isFromFriendlyOrg;
    }

    public void setIsFromFriendlyOrg(Boolean isFromFriendlyOrg) {
        this.isFromFriendlyOrg = isFromFriendlyOrg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order)) {
            return false;
        }
        final Order order = (Order) o;
        return compositeIdOfOrder.equals(order.getCompositeIdOfOrder());
    }

    @Override
    public int hashCode() {
        return compositeIdOfOrder.hashCode();
    }

    @Override
    public String toString() {
        return "Order{" + "compositeIdOfOrder=" + compositeIdOfOrder + ", org=" + org + ", card=" + card + ", client="
                + client + ", accountTransaction=" + transaction + ", idOfCashier=" + idOfCashier + ", socDiscount="
                + socDiscount + ", trdDiscount=" + trdDiscount + ", grantSum=" + grantSum + ", rSum=" + rSum
                + ", createTime=" + createTime + ", sumByCard=" + sumByCard + ", sumByCash=" + sumByCash + ", pos="
                + pos + ", contragent=" + contragent + '}';
    }

    public String getStateAsString() {
        if (state==STATE_COMMITED) return "Проведен";
        else if (state==STATE_CANCELED) return "Отменен";
        return "Неизвестно";
    }

    public Long getIdOrgPayment() {
        return idOrgPayment;
    }

    public void setIdOrgPayment(Long idOrgPayment) {
        this.idOrgPayment = idOrgPayment;
    }
}