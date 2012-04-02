/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
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
public class Card {

    public static final String UNKNOWN_TYPE_NAME = "Неизвестный";
    public static final String[] TYPE_NAMES = {UNKNOWN_TYPE_NAME, "Mifare", "EM-Marine", "Соц. карта", "УЭК"};
    public static final int TYPE_UEC=4;

    public static final String[] STATE_NAMES = {"Активна", "Временно заблокирована", "Окончательно заблокирована"};
    public static final int ACTIVE_STATE = 0;
    public static final int TEMPORARY_LOCKED_STATE = 1;
    public static final int LOCKED_STATE = 2;
    public static final String UNKNOWN_STATE_NAME = "Неизвестно";

    public static final String[] LIFE_STATE_NAMES = {
            "На складе", "Выдана клиенту", "Утеряна", "Возращена клиентом", "Уничтожена"};
    public static final int ISSUED_LIFE_STATE = 1;
    public static final String UNKNOWN_LIFE_STATE_NAME = "Неизвестно";

    public static int parseCardType(String type) throws Exception {
        for (int n=0;n<TYPE_NAMES.length;++n) {
            if (TYPE_NAMES[n].equalsIgnoreCase(type)) return n;
        }
        throw new Exception("Неизвестный тип карты: "+type);
    }

    private Long idOfCard;
    private long version;
    private Client client;
    private Long cardNo;
    private Integer cardType;
    private Date createTime;
    private Date updateTime;
    private Integer state;
    private String lockReason;
    private Date validTime;
    private Date issueTime;
    private Integer lifeState;
    private Long cardPrintedNo;
    private String externalId;
    private Set<AccountTransaction> transactions = new HashSet<AccountTransaction>();
    private Set<Order> orders = new HashSet<Order>();

    Card() {
        // For Hibernate only
    }

    public Card(Client client, long cardNo, int cardType, int state, Date validTime, int lifeState, Long cardPrintedNo)
            throws Exception {
        this.client = client;
        this.cardNo = cardNo;
        this.cardType = cardType;
        Date currentTime = new Date();
        this.createTime = currentTime;
        this.updateTime = currentTime;
        this.state = state;
        this.validTime = validTime;
        this.lifeState = lifeState;
        this.cardPrintedNo = cardPrintedNo;
    }

    public Long getIdOfCard() {
        return idOfCard;
    }

    private void setIdOfCard(Long idOfCard) {
        // For Hibernate only
        this.idOfCard = idOfCard;
    }

    private long getVersion() {
        // For Hibernate only
        return version;
    }

    private void setVersion(long version) {
        // For Hibernate only
        this.version = version;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getCardNo() {
        return cardNo;
    }

    private void setCardNo(Long cardNo) {
        // For Hibernate only
        this.cardNo = cardNo;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    private void setCreateTime(Date createTime) {
        // For Hibernate only
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getLockReason() {
        return lockReason;
    }

    public void setLockReason(String lockReason) {
        this.lockReason = lockReason;
    }

    public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    public Date getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Date issueTime) {
        this.issueTime = issueTime;
    }

    public Integer getLifeState() {
        return lifeState;
    }

    public void setLifeState(Integer lifeState) {
        this.lifeState = lifeState;
    }

    public Long getCardPrintedNo() {
        return cardPrintedNo;
    }

    private void setCardPrintedNo(Long cardPrintedNo) {
        // For Hibernate only
        this.cardPrintedNo = cardPrintedNo;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    private Set<AccountTransaction> getTransactionsInternal() {
        // For Hibernate only
        return transactions;
    }

    private void setTransactionsInternal(Set<AccountTransaction> accountTransactions) {
        // For Hibernate only
        this.transactions = accountTransactions;
    }

    public Set<AccountTransaction> getTransactions() {
        return Collections.unmodifiableSet(getTransactionsInternal());
    }

    private Set<Order> getOrdersIntenal() {
        // For Hibernate only
        return orders;
    }

    private void setOrdersIntenal(Set<Order> orders) {
        // For Hibernate only
        this.orders = orders;
    }

    public Set<Order> getOrders() {
        return Collections.unmodifiableSet(getOrdersIntenal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Card)) {
            return false;
        }
        final Card card = (Card) o;
        return idOfCard.equals(card.getIdOfCard());
    }

    @Override
    public int hashCode() {
        return idOfCard.hashCode();
    }

    @Override
    public String toString() {
        return "Card{" + "idOfCard=" + idOfCard + ", version=" + version + ", client=" + client + ", cardNo=" + cardNo
                + ", cardType=" + cardType + ", createTime=" + createTime + ", updateTime=" + updateTime + ", state="
                + state + ", lockReason='" + lockReason + '\'' + ", validTime=" + validTime + ", issueTime=" + issueTime
                + ", lifeState=" + lifeState + ", cardPrintedNo=" + cardPrintedNo + '}';
    }
}