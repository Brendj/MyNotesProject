/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.card.items.ClientItem;

import org.hibernate.Session;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class CardViewPage extends BasicWorkspacePage {

    public String getPageFilename() {
        return "card/view";
    }

    private Long idOfCard;
    private ClientItem client;
    private Long cardNo;
    private Long longCardNo;
    private Integer cardType;
    private Date createTime;
    private Date updateTime;
    private Integer state;
    private String lockReason;
    private Date validTime;
    private Date issueTime;
    private String externalId;
    private Integer lifeState;
    private Long cardPrintedNo;

    private final HistoryCardListViewPage historyCardListViewPage = new HistoryCardListViewPage();

    public Long getIdOfCard() {
        return idOfCard;
    }

    public ClientItem getClient() {
        return client;
    }

    public Long getCardNo() {
        return cardNo;
    }

    public Integer getCardType() {
        return cardType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public Integer getState() {
        return state;
    }

    public String getLockReason() {
        return lockReason;
    }

    public Date getValidTime() {
        return validTime;
    }

    public Date getIssueTime() {
        return issueTime;
    }

    public Integer getLifeState() {
        return lifeState;
    }

    public Long getCardPrintedNo() {
        return cardPrintedNo;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Long getLongCardNo() {
        return longCardNo;
    }

    public void setLongCardNo(Long longCardNo) {
        this.longCardNo = longCardNo;
    }

    public HistoryCardListViewPage getHistoryCardListViewPage() {
        return historyCardListViewPage;
    }

    public void fill(Session session, Long idOfCard) throws Exception {
        Card card = (Card) session.load(Card.class, idOfCard);
        this.idOfCard = card.getIdOfCard();
        if(card.getClient() != null){
            this.client = new ClientItem(card.getClient());
        }else {
            this.client = new ClientItem();
        }

        this.cardNo = card.getCardNo();
        this.longCardNo = card.getLongCardNo();
        this.cardType = card.getCardType();
        this.createTime = card.getCreateTime();
        this.updateTime = card.getUpdateTime();
        this.state = card.getState();
        this.lockReason = card.getLockReason();
        this.validTime = card.getValidTime();
        this.issueTime = card.getIssueTime();
        this.lifeState = card.getLifeState();
        this.cardPrintedNo = card.getCardPrintedNo();
        this.externalId = card.getExternalId();
        this.historyCardListViewPage.fill(card);
    }

}