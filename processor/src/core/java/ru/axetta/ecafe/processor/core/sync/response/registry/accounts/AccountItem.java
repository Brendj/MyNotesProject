/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response.registry.accounts;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.dao.card.CardReadOnlyRepository;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * User: regal
 * Date: 05.05.15
 * Time: 9:38
 */
public class AccountItem {

    public static final String SYNC_NAME = "AI";

    private long id;
    private long idOfClient;
    private int state;
    private long balance;
    private long sBalance1;
    private long overLimit;
    private long maxDailyLimit;
    private Date issueDate;
    private List<CardsItem> cardsItems = new LinkedList<CardsItem>();


    public AccountItem() {
    }

    public AccountItem(long id, long idOfClient, int state, long balance, long sBalance1, long overLimit,
            long maxDailyLimit, Date issueDate) {
        this.id = id;
        this.idOfClient = idOfClient;
        this.state = state;
        this.balance = balance;
        this.sBalance1 = sBalance1;
        this.overLimit = overLimit;
        this.maxDailyLimit = maxDailyLimit;
        this.issueDate = issueDate;
    }

    public AccountItem(Client client) {
        this.id = client.getContractId();
        this.idOfClient = client.getIdOfClient();
        this.state = client.getContractState();
        this.balance = client.getBalance();
        if(client.getSubBalance1() != null){
            this.sBalance1 = client.getSubBalance1();
        }else {
            this.sBalance1 = 0L;
        }

        this.overLimit = client.getLimit();
        this.maxDailyLimit = client.getExpenditureLimit();
        this.issueDate = client.getContractTime();
        List<Card> allByClient = CardReadOnlyRepository.getInstance().findAllByClient(client);
        for (Card card : allByClient) {
            cardsItems.add(new CardsItem(card, client));
        }
    }

    public AccountItem(Client client, List<Card> cards) {
        this.id = client.getContractId();
        this.idOfClient = client.getIdOfClient();
        this.state = client.getContractState();
        this.balance = client.getBalance();
        if(client.getSubBalance1() != null){
            this.sBalance1 = client.getSubBalance1();
        }else {
            this.sBalance1 = 0L;
        }

        this.overLimit = client.getLimit();
        this.maxDailyLimit = client.getExpenditureLimit();
        this.issueDate = client.getContractTime();
        for (Card card : cards) {
            if (card.getClient() != null && card.getClient().getIdOfClient().equals(client.getIdOfClient())) {
                cardsItems.add(new CardsItem(card, client));
            }
        }
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getsBalance1() {
        return sBalance1;
    }

    public void setsBalance1(long sBalance1) {
        this.sBalance1 = sBalance1;
    }

    public long getOverLimit() {
        return overLimit;
    }

    public void setOverLimit(long overLimit) {
        this.overLimit = overLimit;
    }

    public long getMaxDailyLimit() {
        return maxDailyLimit;
    }

    public void setMaxDailyLimit(long maxDailyLimit) {
        this.maxDailyLimit = maxDailyLimit;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public List<CardsItem> getCardsItems() {
        return cardsItems;
    }

    public void setCardsItems(List<CardsItem> cardsItems) {
        this.cardsItems = cardsItems;
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement(SYNC_NAME);
        element.setAttribute("Id", Long.toString(this.id));
        element.setAttribute("IdOfClient", Long.toString(this.idOfClient));
        element.setAttribute("State", Integer.toString(this.state));
        element.setAttribute("Balance", Long.toString(this.balance));
        element.setAttribute("SBalance1", Long.toString(this.sBalance1));
        element.setAttribute("OverLimit", Long.toString(this.overLimit));
        element.setAttribute("MaxDailyLimit", Long.toString(this.maxDailyLimit));
        element.setAttribute("IssueDate", CalendarUtils.dateTimeToString(this.issueDate)); // todo
        for (CardsItem cardsItem : cardsItems){
            element.appendChild(cardsItem.toElement(document));
        }
        return element;
    }

}
