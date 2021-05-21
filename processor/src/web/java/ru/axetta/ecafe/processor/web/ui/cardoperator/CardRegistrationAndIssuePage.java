/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.cardoperator;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.card.CardLifeStateMenu;
import ru.axetta.ecafe.processor.web.ui.card.CardStateMenu;
import ru.axetta.ecafe.processor.web.ui.card.CardTypeMenu;
import ru.axetta.ecafe.processor.web.ui.card.items.ClientItem;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectPage;

import org.hibernate.Session;

import java.util.Date;

/**
 * Created by anvarov on 08.08.2017.
 */
public class CardRegistrationAndIssuePage extends BasicWorkspacePage implements ClientSelectPage.CompleteHandler {

    private ClientItem client = new ClientItem();
    private long idOfOrg;
    private Long cardNo;
    private Long cardNoHidden;
    private Integer cardType;

    private String lockReason;
    private Date validTime;
    private Date issueTime;
    private Long cardPrintedNo;
    private final CardTypeMenu cardTypeMenu = new CardTypeMenu();
    private final CardStateMenu cardStateMenu = new CardStateMenu();
    private final CardLifeStateMenu cardLifeStateMenu = new CardLifeStateMenu();

    private boolean clientHasNotBlockedCard = false;

    public String getPageFilename() {
        return "cardoperator/card_registration";
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

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
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
        this.cardNo = null;
        this.cardPrintedNo = null;
        this.lockReason = null;
        this.validTime = new Date();
        this.validTime = CalendarUtils.addYear(validTime, 5);
        this.issueTime = new Date();
        this.idOfOrg = -1;
    }

    public void completeClientSelection(Session session, Long idOfClient) throws Exception {
        if (null != idOfClient) {
            Client client = (Client) session.load(Client.class, idOfClient);
            this.client = new ClientItem(client);
            clientHasNotBlockedCard = false;
            if (client.getCards() != null) {
                for (Card card : client.getCards()) {
                    if (card.getState().intValue() != CardState.BLOCKED.getValue() && card.getState().intValue() != CardState.TEMPBLOCKED.getValue()) {
                        clientHasNotBlockedCard = true;
                        break;
                    }
                }
            }
        } else this.client = new ClientItem();
    }

    public void createCard() throws Exception {
        if (client == null || client.getIdOfClient() == null) {
            printError("Выберите клиента для привязки карты");
            return;
        }
        if (isClientHasNotBlockedCard()){
            printError("Данный клиент имеет незаблокированную(ые) карту(ы).");
            return;
        }
        if (cardNo == null || cardNo.equals(0L)) {
            printError("Введите номер карты.");
            return;
        }

        validTime = CalendarUtils.endOfDay(validTime);
        try {
            User user = MainPage.getSessionInstance().getCurrentUser();
            RuntimeContext.getInstance().getCardManager()
                    .createCard(this.client.getIdOfClient(), this.cardNo, this.cardType, CardState.ISSUED.getValue(),
                            this.validTime, 1 /*Card.LIFE_STATE_NAMES[1]="Выдана клиенту"*/, this.lockReason, this.issueTime,
                            this.cardPrintedNo, null, user);
            printMessage("Карта зарегистрирована успешно");
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }


    public boolean isClientHasNotBlockedCard() {
        return clientHasNotBlockedCard;
    }

    public Long getCardNoHidden() {
        return cardNoHidden;
    }

    public void setCardNoHidden(Long cardNoHidden) {
        this.cardNoHidden = cardNoHidden;
    }

    public void onCardRead() {
        cardNo = cardNoHidden;
    }

    public void reissueCard(Session session) throws Exception {
        if (client == null || client.getIdOfClient() == null) {
            throw new Exception("Выберите клиента для привязки карты");
        }
        if (isClientHasNotBlockedCard()){
            throw new Exception("Данный клиент имеет незаблокированную(ые) карту(ы).");
        }
        if (cardNo == null || cardNo.equals(0L)) {
            throw new Exception("Введите номер карты.");
        }
        User user = MainPage.getSessionInstance().getCurrentUser();
        validTime = CalendarUtils.endOfDay(validTime);
        RuntimeContext.getInstance().getCardManager().reissueCard(session, this.client.getIdOfClient(),
                this.cardNo, this.cardType, CardState.ISSUED.getValue(), this.validTime, 1, this.lockReason,
                this.issueTime, this.cardPrintedNo, null, user);
    }
}
