/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.service.card.CardService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.card.items.ClientItem;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectPage;

import org.hibernate.Session;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class CardCreatePage extends BasicWorkspacePage implements ClientSelectPage.CompleteHandler {
    private boolean useOnlyOneActiveMainCard;

    private ClientItem client = new ClientItem();
    private long idOfOrg;
    private Long cardNo;
    private Long longCardNo;
    private Integer cardType;
    private Integer state;
    private String lockReason;
    private Date validTime;
    private Date issueTime;
    private Integer lifeState;
    private Long cardPrintedNo;
    private final CardTypeMenu cardTypeMenu = new CardTypeMenu();
    private final CardStateMenu cardStateMenu = new CardStateMenu();
    private final CardLifeStateMenu cardLifeStateMenu = new CardLifeStateMenu();

    private boolean clientHasNotBlockedCard = false;

    public String getPageFilename() {
        return "card/create";
    }

    public Long getCardPrintedNo() {
        return cardPrintedNo;
    }

    public void setCardPrintedNo(Long cardPrintedNo) {
        this.cardPrintedNo = cardPrintedNo;
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

    public CardTypeMenu getCardTypeMenu() {
        return cardTypeMenu;
    }

    public CardStateMenu getCardStateMenu() {
        return cardStateMenu;
    }

    public CardLifeStateMenu getCardLifeStateMenu() {
        return cardLifeStateMenu;
    }

    public String getShortName() {
        return cardNo.toString();
    }

    public void fill(Session session) throws Exception {
        this.client = new ClientItem();
        this.cardType = 1;
        this.state = 0;
        this.lockReason = null;
        this.validTime = new Date();
        this.validTime = CalendarUtils.addYear(validTime, 2);
        this.issueTime = new Date();
        this.lifeState = 0;
        this.idOfOrg = -1;
    }

    public void completeClientSelection(Session session, Long idOfClient) throws Exception {
        if (null != idOfClient) {
            Client client = (Client) session.load(Client.class, idOfClient);
            this.client = new ClientItem(client);
            clientHasNotBlockedCard = false;
            if (client.getCards() != null) {
                for (Card card : client.getCards()) {
                    if (card.getState().intValue() != CardState.BLOCKED.getValue()) {
                        clientHasNotBlockedCard = true;
                        break;
                    }
                }
            }
        }
    }

    public void createCard(Session session) throws Exception {
        if (isClientHasNotBlockedCard()){
            throw new IllegalStateException("Данный клиент имеет незаблокированную(ые) карту(ы).");
        }
        CardService.getInstance().resetAllCards(client.getIdOfClient());

        RuntimeContext runtimeContext = null;
        runtimeContext = RuntimeContext.getInstance();
        validTime = CalendarUtils.endOfDay(validTime);
        runtimeContext.getCardManager()
                .createCard(this.client.getIdOfClient(), this.cardNo, this.cardType, this.state, this.validTime,
                        this.lifeState, this.lockReason, this.issueTime, this.cardPrintedNo, this.longCardNo);
    }


    public boolean isClientHasNotBlockedCard() {
        return clientHasNotBlockedCard;
    }
}