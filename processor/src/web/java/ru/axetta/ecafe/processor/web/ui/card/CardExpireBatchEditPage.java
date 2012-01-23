/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.utils.AbbreviationUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

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
public class CardExpireBatchEditPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    public static class OrgItem {

        private final Long idOfOrg;
        private final String shortName;
        private final String officialName;

        public OrgItem() {
            this.idOfOrg = null;
            this.shortName = null;
            this.officialName = null;
        }

        public OrgItem(Org org) {
            this.idOfOrg = org.getIdOfOrg();
            this.shortName = org.getShortName();
            this.officialName = org.getOfficialName();
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public String getOfficialName() {
            return officialName;
        }
    }

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
        private final Long balance;
        private final Long limit;

        public ClientItem(Client client) {
            this.idOfClient = client.getIdOfClient();
            this.orgShortName = client.getOrg().getShortName();
            this.person = new PersonItem(client.getPerson());
            this.contractPerson = new PersonItem(client.getContractPerson());
            this.contractId = client.getContractId();
            this.contractTime = client.getContractTime();
            this.contractState = client.getContractState();
            this.balance = client.getBalance();
            this.limit = client.getLimit();
        }

        public Long getBalance() {
            return balance;
        }

        public Long getLimit() {
            return limit;
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
    }

    public static class CardItem {

        private final ClientItem client;
        private final Long idOfCard;
        private final Long cardNo;
        private final Long cardPrintedNo;

        public CardItem(Card card) {
            this.client = new ClientItem(card.getClient());
            this.idOfCard = card.getIdOfCard();
            this.cardNo = card.getCardNo();
            this.cardPrintedNo = card.getCardPrintedNo();
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

        public Long getCardPrintedNo() {
            return cardPrintedNo;
        }

    }

    public static class UpdateResult {

        private final int resultCode;
        private final String message;
        private final CardItem card;

        public UpdateResult(int resultCode, String message, Card card) {
            this.resultCode = resultCode;
            this.message = message;
            this.card = new CardItem(card);
        }

        public int getResultCode() {
            return resultCode;
        }

        public String getMessage() {
            return message;
        }

        public CardItem getCard() {
            return card;
        }
    }

    private Date expireDate = new Date();
    private OrgItem org = new OrgItem();
    private List<UpdateResult> results = Collections.emptyList();

    public String getPageFilename() {
        return "card/batch_expire_edit";
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public List<UpdateResult> getResults() {
        return results;
    }

    public OrgItem getOrg() {
        return org;
    }

    public void fill(Session session) throws Exception {

    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.org = new OrgItem(org);
        }
    }

    public void updateExpireDate(Session session) throws Exception {
        List<UpdateResult> updateResults = new LinkedList<UpdateResult>();
        Org org = (Org) session.get(Org.class, this.org.getIdOfOrg());
        Criteria cardCriteria = session.createCriteria(Card.class);
        cardCriteria.add(Restrictions.eq("state", Card.ACTIVE_STATE));
        cardCriteria.createCriteria("client").add(Restrictions.eq("org", org));
        ScrollableResults scrollableResults = cardCriteria.scroll();
        while (scrollableResults.next()) {
            Card card = (Card) scrollableResults.get(0);
            UpdateResult updateResult;
            try {
                card.setValidTime(this.expireDate);
                card.setUpdateTime(new Date());
                session.update(card);
                updateResult = new UpdateResult(0, "Ok", card);
            } catch (Exception e) {
                updateResult = new UpdateResult(1, e.getMessage(), card);
            }
            updateResults.add(updateResult);
        }
        scrollableResults.close();
        this.results = updateResults;
    }

}