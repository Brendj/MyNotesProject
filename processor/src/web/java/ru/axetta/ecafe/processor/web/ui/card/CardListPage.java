/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.utils.AbbreviationUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class CardListPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

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
        private final String clientGroup;
        private final String orgShortAdress;
        private final String orgDistrict;

        public ClientItem(Client client) {
            this.idOfClient = client.getIdOfClient();
            this.orgShortName = client.getOrg().getShortName();
            this.person = new PersonItem(client.getPerson());
            this.contractPerson = new PersonItem(client.getContractPerson());
            this.contractId = client.getContractId();
            this.contractTime = client.getContractTime();
            this.contractState = client.getContractState();
            this.clientGroup = client.getClientGroup() != null ? client.getClientGroup().getGroupName() : "";
            this.orgShortAdress = client.getOrg().getShortAddress();
            this.orgDistrict = client.getOrg().getDistrict();
        }

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

        public String getShortName() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ContractIdFormat.format(contractId)).append(" (")
                    .append(AbbreviationUtils.buildAbbreviation(contractPerson.getFirstName(),
                            contractPerson.getSurname(), contractPerson.getSecondName())).append("): ")
                    .append(AbbreviationUtils.buildAbbreviation(person.getFirstName(), person.getSurname(),
                            person.getSecondName()));
            return stringBuilder.toString();
        }

        public String getClientGroup() { return clientGroup;}

        public String getOrgShortAdress() { return orgShortAdress;}

        public String getOrgDistrict() { return orgDistrict;}
    }

    public static class Item {

        private final Long idOfCard;
        private final ClientItem client;
        private final Long cardNo;
        private final Integer cardType;
        private final Date createTime;
        private final Date updateTime;
        private final Date issueTime;
        private final Integer state;
        private final String lockReason;
        private final Date validTime;
        private final Integer lifeState;
        private final Long cardPrintedNo;

        public Item(Card card) {
            this.idOfCard = card.getIdOfCard();
            if(card.getClient() != null){
                this.client = new ClientItem(card.getClient());
            }else {
                client= null;
            }
            this.cardNo = card.getCardNo();
            this.cardType = card.getCardType();
            this.createTime = card.getCreateTime();
            this.updateTime = card.getUpdateTime();
            this.state = card.getState();
            this.lockReason = card.getLockReason();
            this.validTime = card.getValidTime();
            this.lifeState = card.getLifeState();
            this.cardPrintedNo = card.getCardPrintedNo();
            this.issueTime = card.getIssueTime();
        }

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

        public Date getIssueTime() {
            return issueTime;
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

        public Long getCardPrintedNo() {
            return cardPrintedNo;
        }
    }

    private List<Item> items = Collections.emptyList();
    private CardFilter cardFilter = new CardFilter();

    public String getPageFilename() {
        return "card/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<Item> getItems() {
        return items;
    }

    public CardFilter getCardFilter() {
        return cardFilter;
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        this.cardFilter.completeOrgSelection(session, idOfOrg);
    }

    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();
        if (!cardFilter.isEmpty()) {
            List cards = cardFilter.retrieveCards(session);
            for (Object object : cards) {
                Card card = (Card) object;
                items.add(new Item(card));
            }
        }
        this.items = items;
    }

}