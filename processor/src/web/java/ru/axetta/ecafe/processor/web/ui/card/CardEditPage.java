/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.dao.card.CardReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.service.card.CardService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.card.items.ClientItem;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectPage;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class CardEditPage extends BasicWorkspacePage implements ClientSelectPage.CompleteHandler {

    public String getPageFilename() {
        return "card/edit";
    }

    private Long idOfCard;
    private ClientItem client;
    private Long cardNo;
    private Long longCardNo;
    private Integer cardType;
    private Date updateTime;
    private Integer state;
    private String lockReason;
    private Integer lockReasonState;
    private Date validTime;
    private Date issueTime;
    private Integer lifeState;
    private Long cardPrintedNo;
    private String externalId;
    private final CardTypeMenu cardTypeMenu = new CardTypeMenu();
    private final CardStateMenu cardStateMenu = new CardStateMenu();
    private final CardLifeStateMenu cardLifeStateMenu = new CardLifeStateMenu();
    private final CardLockReasonMenu cardLockReasonMenu = new CardLockReasonMenu();

    public Long getCardPrintedNo() {
        return cardPrintedNo;
    }

    public void setCardPrintedNo(Long cardPrintedNo) {
        this.cardPrintedNo = cardPrintedNo;
    }

    public Long getIdOfCard() {
        return idOfCard;
    }

    public void setIdOfCard(Long idOfCard) {
        this.idOfCard = idOfCard;
    }

    public ClientItem getClient() {
        return client;
    }

    public void setClient(ClientItem client) {
        this.client = client;
    }

    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }

    public Long getLongCardNo() {
        return longCardNo;
    }

    public void setLongCardNo(Long longCardNo) {
        this.longCardNo = longCardNo;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
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

    public Integer getLockReasonState() {
        return lockReasonState;
    }

    public void setLockReasonState(Integer lockReasonState) {
        this.lockReasonState = lockReasonState;
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

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public CardTypeMenu getCardTypeMenu() {
        return cardTypeMenu;
    }

    public CardStateMenu getCardStateMenu() {
        return cardStateMenu;
    }

    public CardLifeStateMenu getCardLifeStateMenu() {
        return cardLifeStateMenu;
    }

    public CardLockReasonMenu getCardLockReasonMenu() {
        return cardLockReasonMenu;
    }

    public void fill( Long idOfCard) throws Exception {
        Card card = CardReadOnlyRepository.getInstance().find(idOfCard);
        fill(card);
    }

    public void fill(Session session, Long idOfCard) throws Exception {
        Card card = (Card) session.load(Card.class, idOfCard);
        fill(card);
    }

    public void completeClientSelection(Session session, Long idOfClient) throws HibernateException {
        if (null != idOfClient) {
            Client client = (Client) session.load(Client.class, idOfClient);
            this.client = new ClientItem(client);
        }
    }

    public void updateCard(Session session, Long idOfCard) throws Exception {
        checkCardStateOnUpdate();
        if(state == CardState.TEMPBLOCKED.getValue()){
            state = CardState.BLOCKED.getValue();
        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        if (externalId!=null && externalId.length()==0) externalId=null;
        validTime = CalendarUtils.endOfDay(validTime);
        User user = MainPage.getSessionInstance().getCurrentUser();
        runtimeContext.getCardManager()
                .updateCard(this.client.getIdOfClient(), idOfCard, this.cardType, this.state, this.validTime,
                        this.lifeState, CardLockReasonMenu.getDescriptionByValue(lockReasonState), this.issueTime,
                        this.externalId, user);
        fill(session, this.idOfCard);
    }

    public void fill(Card card) throws Exception {
        this.idOfCard = card.getIdOfCard();
        if(card.getClient() != null){
            this.client = new ClientItem(card.getClient());
        }else {
            client = new ClientItem();
        }

        this.cardNo = card.getCardNo();
        this.longCardNo = card.getLongCardNo();
        this.cardType = card.getCardType();
        this.updateTime = card.getUpdateTime();
        this.state = card.getState();
        this.lockReason = card.getLockReason();
        this.lockReasonState = CardLockReasonMenu.getValueByDescription(this.lockReason);
        this.validTime = card.getValidTime();
        this.issueTime = card.getIssueTime();
        this.lifeState = card.getLifeState();
        this.cardPrintedNo = card.getCardPrintedNo();
        this.externalId = card.getExternalId();

        if (state == CardState.TEMPISSUED.getValue()) {
            cardStateMenu.prepareItemForTempCard();
        }else {
            cardStateMenu.getItems().clear();
            cardStateMenu.getItems().addAll(CardStateMenu.readAllItems());
        }
    }

    private void checkCardStateOnUpdate(){
        if( (CardState.ISSUED.getValue()!=state)
                &&(CardState.TEMPBLOCKED.getValue() != state)
                &&(CardState.TEMPISSUED.getValue() != state)){
            throw new IllegalStateException("Требуется изменить статус карты");
        } /*else {
            if (CardLockReason.EMPTY.getValue() == lockReasonState) {
                throw new IllegalStateException("Введите 'Причину блокировки карты'");
            }
        }*/
    }

    public Object returnCard(String userName) {

        getLogger().error("Return cardNo:" + cardNo + ", username:" + userName);
        CardService.getInstance().reset(cardNo);
        try {
            fill(this.idOfCard);
        } catch (Exception e) {
            getLogger().error("Ошибка ",e);
        }
        MainPage.getSessionInstance().showCardListPage();
        MainPage.getSessionInstance().updateSelectedMainMenu();
        return null;
    }

    public boolean isTempCard(){
        return state == CardState.TEMPISSUED.getValue();
    }

}