/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.utils.AbbreviationUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
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

    public static class ClientItem {

        public static class PersonItem {

            private final String firstName;
            private final String surname;
            private final String secondName;
            private final String idDocument;

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

            public PersonItem(Person person) {
                this.firstName = person.getFirstName();
                this.surname = person.getSurname();
                this.secondName = person.getSecondName();
                this.idDocument = person.getIdDocument();
            }
        }

        private final Long idOfClient;
        private final String orgShortName;
        private final PersonItem person;
        private final PersonItem contractPerson;
        private final Long contractId;
        private final Date contractTime;
        private final Integer contractState;

        public String getShortName() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ContractIdFormat.format(contractId)).append(" (")
                    .append(AbbreviationUtils.buildAbbreviation(contractPerson.getFirstName(),
                            contractPerson.getSurname(), contractPerson.getSecondName())).append("): ")
                    .append(AbbreviationUtils.buildAbbreviation(person.getFirstName(), person.getSurname(),
                            person.getSecondName()));
            return stringBuilder.toString();
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
    }

    private Long idOfCard;
    private ClientItem client;
    private Long cardNo;
    private Integer cardType;
    private Date updateTime;
    private Integer state;
    private String lockReason;
    private Date validTime;
    private Date issueTime;
    private Integer lifeState;
    private Long cardPrintedNo;
    private String externalId;
    private final CardTypeMenu cardTypeMenu = new CardTypeMenu();
    private final CardStateMenu cardStateMenu = new CardStateMenu();
    private final CardLifeStateMenu cardLifeStateMenu = new CardLifeStateMenu();

    public String getPageFilename() {
        return "card/edit";
    }

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
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        if (externalId!=null && externalId.length()==0) externalId=null;
        runtimeContext.getCardManager()
                .updateCard(this.client.getIdOfClient(), idOfCard, this.cardType, this.state, this.validTime,
                        this.lifeState, this.lockReason, this.issueTime, this.externalId);
        fill(session, this.idOfCard);
    }

    public void fill(Card card) throws Exception {
        this.idOfCard = card.getIdOfCard();
        this.client = new ClientItem(card.getClient());
        this.cardNo = card.getCardNo();
        this.cardType = card.getCardType();
        this.updateTime = card.getUpdateTime();
        this.state = card.getState();
        this.lockReason = card.getLockReason();
        this.validTime = card.getValidTime();
        this.issueTime = card.getIssueTime();
        this.lifeState = card.getLifeState();
        this.cardPrintedNo = card.getCardPrintedNo();
        this.externalId = card.getExternalId();
    }

}