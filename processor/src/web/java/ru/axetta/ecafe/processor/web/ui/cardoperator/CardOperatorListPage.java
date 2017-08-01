/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.cardoperator;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by anvarov on 27.07.2017.
 */
public class CardOperatorListPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    private CardOperatorFilter cardOperatorFilter = new CardOperatorFilter();

    public CardOperatorFilter getCardOperatorFilter() {
        return cardOperatorFilter;
    }

    public void setCardOperatorFilter(CardOperatorFilter cardOperatorFilter) {
        this.cardOperatorFilter = cardOperatorFilter;
    }

    public String getPageFilename() {
        return "cardoperator/card_list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    private List<Item> items = Collections.emptyList();

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        this.cardOperatorFilter.completeOrgSelection(session, idOfOrg);
    }

    public static class Item {

        private String shortNameInfoService;
        private Long cardNo;
        private String personName;
        private String status;
        private Date date;

        public Item(Org org, Card card, String personName, CardOperatorFilter cardFilter) {
            this.shortNameInfoService = org.getShortNameInfoService();
            this.cardNo = card.getCardNo();
            this.personName = personName;
            this.status = cardFilter.getStatus();
            this.date = card.getCreateTime();
        }

        public String getShortNameInfoService() {
            return shortNameInfoService;
        }

        public void setShortNameInfoService(String shortNameInfoService) {
            this.shortNameInfoService = shortNameInfoService;
        }

        public Long getCardNo() {
            return cardNo;
        }

        public void setCardNo(Long cardNo) {
            this.cardNo = cardNo;
        }

        public String getPersonName() {
            return personName;
        }

        public void setPersonName(String personName) {
            this.personName = personName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }

    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();
        if (!cardOperatorFilter.isEmpty()) {
            List cards = cardOperatorFilter.retrieveCards(session);
            for (Object object : cards) {
                Card card = (Card) object;
                Org org = card.getOrg();
                String personName = "";
                if (card.getClient() != null) {
                    Person person = DAOService.getInstance().getPersonByClient(card.getClient());
                    personName = person.getFullName();
                }
                items.add(new Item(org, card, personName, cardOperatorFilter));
            }
        }
        this.items = items;
    }
}
