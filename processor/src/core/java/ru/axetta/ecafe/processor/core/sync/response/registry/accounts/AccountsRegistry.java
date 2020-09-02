/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response.registry.accounts;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.List;

/**
 * User: regal
 * Date: 05.05.15
 * Time: 9:35
 */
public class AccountsRegistry implements AbstractToElement{
    public static final String SYNC_NAME = "AccountsRegistry";

    private List<AccountItem> accountItems = new LinkedList<AccountItem>();
    private List<VisitorItem> visitorItems = new LinkedList<VisitorItem>();
    private List<CardsItem> freeCardsItems = new LinkedList<CardsItem>();
    private List<CardsItem> changedCardItems = new LinkedList<CardsItem>();

    public AccountsRegistry() {
    }

    public AccountsRegistry handler(){
        return new AccountsRegistry();
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement(SYNC_NAME);
        Element accountItemsElement = document.createElement("AccountsWithCards");
        for (AccountItem item : this.accountItems) {
            accountItemsElement.appendChild(item.toElement(document));
        }
        element.appendChild(accountItemsElement);

        Element visitorItemsElement = document.createElement("VisitorCards");
        for (VisitorItem item : this.visitorItems) {
            visitorItemsElement.appendChild(item.toElement(document));
        }
        element.appendChild(visitorItemsElement);

        Element freeCardsItemsElement = document.createElement("FreeCards");
        for (CardsItem item : this.freeCardsItems) {
            freeCardsItemsElement.appendChild(item.toElement(document));
        }
        element.appendChild(freeCardsItemsElement);

        Element changedCardsItemsElement = document.createElement("ChangedCards");
        for (CardsItem item : this.changedCardItems) {
            changedCardsItemsElement.appendChild(item.toElement(document));
        }
        element.appendChild(changedCardsItemsElement);
        return element;
    }

    public List<AccountItem> getAccountItems() {
        return accountItems;
    }

    public void setAccountItems(List<AccountItem> accountItems) {
        this.accountItems = accountItems;
    }

    public List<VisitorItem> getVisitorItems() {
        return visitorItems;
    }

    public void setVisitorItems(List<VisitorItem> visitorItems) {
        this.visitorItems = visitorItems;
    }

    public List<CardsItem> getFreeCardsItems() {
        return freeCardsItems;
    }

    public void setFreeCardsItems(List<CardsItem> freeCardsItems) {
        this.freeCardsItems = freeCardsItems;
    }


    public List<CardsItem> getChangedCardItems() {
        return changedCardItems;
    }

    public void setChangedCardItems(List<CardsItem> changedCardItems) {
        this.changedCardItems = changedCardItems;
    }
}
