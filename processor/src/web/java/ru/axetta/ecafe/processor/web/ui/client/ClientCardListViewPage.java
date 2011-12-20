/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Person;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientCardListViewPage {

    public static class PersonItem {

        private final String firstName;
        private final String surname;
        private final String secondName;
        private final String idDocument;

        public PersonItem(Person person) {
            this.firstName = person.getFirstName();
            this.surname = person.getSurname();
            this.secondName = person.getSecondName();
            this.idDocument = person.getIdDocument();
        }

        public String getFirstName() {
            return firstName;
        }

        public String getSurname() {
            return surname;
        }

        public String getSecondName() {
            return secondName;
        }

        public String getIdDocument() {
            return idDocument;
        }
    }

    public static class ClientItem {

        private final Long idOfClient;
        private final String orgShortName;
        private final PersonItem person;
        private final PersonItem contractPerson;
        private final Long contractId;
        private final Date contractTime;
        private final Integer contractState;

        public Long getIdOfClient() {
            return idOfClient;
        }

        public String getOrgShortName() {
            return orgShortName;
        }

        public PersonItem getPerson() {
            return person;
        }

        public PersonItem getContractPerson() {
            return contractPerson;
        }

        public Long getContractId() {
            return contractId;
        }

        public Date getContractTime() {
            return contractTime;
        }

        public Integer getContractState() {
            return contractState;
        }

        public ClientItem(Client client) {
            this.idOfClient = client.getIdOfClient();
            this.orgShortName = client.getOrg().getShortName();
            this.person = new PersonItem(client.getPerson());
            this.contractPerson = new PersonItem(client.getContractPerson());
            this.contractId = client.getContractId();
            this.contractTime = client.getContractTime();
            this.contractState = client.getContractState();
        }
    }

    public static class Item {

        private Long idOfCard;
        private ClientItem client;
        private Long cardNo;
        private Integer cardType;
        private Date createTime;
        private Date updateTime;
        private Integer state;
        private String lockReason;
        private Date validTime;
        private Integer lifeState;

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

        public Integer getLifeState() {
            return lifeState;
        }

        public Item(Card card) {
            this.idOfCard = card.getIdOfCard();
            this.client = new ClientItem(card.getClient());
            this.cardNo = card.getCardNo();
            this.cardType = card.getCardType();
            this.createTime = card.getCreateTime();
            this.updateTime = card.getUpdateTime();
            this.state = card.getState();
            this.lockReason = card.getLockReason();
            this.validTime = card.getValidTime();
            this.lifeState = card.getLifeState();
        }
    }

    private List<Item> items = Collections.emptyList();

    public List<Item> getItems() {
        return items;
    }

    public int getItemCount() {
        return items.size();
    }

    public void fill(Client client) throws Exception {
        List<Item> items = new LinkedList<Item>();
        Set<Card> cards = client.getCards();
        for (Card card : cards) {
            items.add(new Item(card));
        }
        this.items = items;
    }

}