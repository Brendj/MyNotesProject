/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.utils.AbbreviationUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class CardViewPage extends BasicWorkspacePage {

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

    public String getPageFilename() {
        return "card/view";
    }

    private Long idOfCard;
    private ClientItem client;
    private Long cardNo;
    private Integer cardType;
    private Date createTime;
    private Date updateTime;
    private Integer state;
    private String lockReason;
    private Date validTime;
    private Date issueTime;
    private Integer lifeState;
    private Long cardPrintedNo;

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

    public Date getIssueTime() {
        return issueTime;
    }

    public Integer getLifeState() {
        return lifeState;
    }

    public Long getCardPrintedNo() {
        return cardPrintedNo;
    }

    public void fill(Session session, Long idOfCard) throws Exception {
        Card card = (Card) session.load(Card.class, idOfCard);
        this.idOfCard = card.getIdOfCard();
        this.client = new ClientItem(card.getClient());
        this.cardNo = card.getCardNo();
        this.cardType = card.getCardType();
        this.createTime = card.getCreateTime();
        this.updateTime = card.getUpdateTime();
        this.state = card.getState();
        this.lockReason = card.getLockReason();
        this.validTime = card.getValidTime();
        this.issueTime = card.getIssueTime();
        this.lifeState = card.getLifeState();
        this.cardPrintedNo = card.getCardPrintedNo();
    }

}