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
    public static final String[] TYPE_NAMES = {UNKNOWN_TYPE_NAME, "Mifare", "EM-Marine", "Соц. карта", "УЭК",
            "Транспортная карта", "Банковская карта", "Соц. карта учащегося", "Соц. карта москвича", "Браслет (Mifare)",
            "Часы (Mifare)", "Брелок (Mifare)", "Тройка-Москвёнок карта", "Тройка-Москвёнок браслет", "Тройка-Москвёнок брелок",
            "Фитнес-Браслет", "Смарт-Кольцо"};
    public static final int TYPE_UEC=4;
    public static final Integer[] DEPRECATED_TYPES = {2, 3, 4}; //Устаревшие типы карт, скрываем из списка типов на странице регистрации карты

    public static final String[] STATE_NAMES = {"Активна", "Временно заблокирована", "Окончательно заблокирована"};
    public static final int ACTIVE_STATE = 0;
    public static final String UNKNOWN_STATE_NAME = "Неизвестно";

    public static final String[] LIFE_STATE_NAMES = {
            "На складе", "Выдана клиенту", "Утеряна", "Возвращена клиентом", "Уничтожена", "Готова к выдаче"};
    public static final int ISSUED_LIFE_STATE = 1;
    public static final int READY_LIFE_STATE = 1;
    public static final String UNKNOWN_LIFE_STATE_NAME = "Неизвестно";

    public static final long DEFAULT_CARD_VALID_TIME = 31536000000L; // 1year
    public static final long DEFAULT_TEMP_CARD_VALID_TIME = 86400000L; //24 hours

    public static int parseCardType(String type) throws Exception {
        for (int n=0;n<TYPE_NAMES.length;++n) {
            if (TYPE_NAMES[n].equalsIgnoreCase(type)) return n;
        }
        throw new Exception("Неизвестный тип карты: "+type);
    }

    private Long idOfCard;
    private long version;
    private Client client;
    private Visitor visitor;
    private Org org;
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
    private Integer cardSignCertNum;
    private Set<AccountTransaction> transactions = new HashSet<AccountTransaction>();
    private Set<Order> orders = new HashSet<Order>();
    private Set<HistoryCard> historyCards = new HashSet<HistoryCard>();
    private Boolean isLongUid;
    private Integer transitionState;
    private Set<CardSync> cardsync;
    private MeshClientCardRef meshCardClientRef;

    protected Card() {
        // For Hibernate only
    }

    public Card(Client client, Long cardNo, Integer cardType, Date validTime, Long cardPrintedNo) {
        this.client = client;
        this.cardNo = cardNo;
        this.cardType = cardType;
        this.validTime = validTime;
        this.cardPrintedNo = cardPrintedNo;
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

    public Card( Org org, long cardNo, int cardType, int state, Long cardPrintedNo, int lifeState){
        this.org = org;
        this.cardNo = cardNo;
        this.cardType = cardType;
        Date currentTime = new Date();
        this.createTime = currentTime;
        this.updateTime = currentTime;
        this.validTime = currentTime;
        this.state = state;
        this.cardPrintedNo = cardPrintedNo;
        this.lifeState = lifeState;
    }

    public boolean isActive() {
        if (state == null) return false;
        return state.equals(CardState.ISSUED.getValue()) || state.equals(CardState.TEMPISSUED.getValue());
    }

    public static boolean isSocial(int type) {
        return (type == 7 || type == 8);
    }

    public static boolean isServiceType(int type) {
        return type == 1;
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

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
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

    public void setCreateTime(Date createTime) {
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

    public void setCardPrintedNo(Long cardPrintedNo) {
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

    private Set<HistoryCard> getHistoryCardsInternal() {
        return historyCards;
    }

    private void setHistoryCardsInternal(Set<HistoryCard> historyCards) {
        this.historyCards = historyCards;
    }

    public Set<HistoryCard> getHistoryCards() {
        return Collections.unmodifiableSet(getHistoryCardsInternal());
    }

    public Boolean getIsLongUid() {
        return isLongUid;
    }

    public void setIsLongUid(Boolean longUid) {
        isLongUid = longUid;
    }

    public Integer getTransitionState() {
        return transitionState;
    }

    public void setTransitionState(Integer transitionState) {
        this.transitionState = transitionState;
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

    public Integer getCardSignCertNum() {
        return cardSignCertNum;
    }

    public void setCardSignCertNum(Integer cardSignCertNum) {
        this.cardSignCertNum = cardSignCertNum;
    }

    public Set<CardSync> getCardsync() {
        return cardsync;
    }

    public void setCardsync(Set<CardSync> cardsync) {
        this.cardsync = cardsync;
    }

    public MeshClientCardRef getMeshCardClientRef() {
        return meshCardClientRef;
    }

    public void setMeshCardClientRef(MeshClientCardRef meshCardClientRef) {
        this.meshCardClientRef = meshCardClientRef;
    }

    public boolean refNotExists() {
        return  getMeshCardClientRef() == null;
    }
}