/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.cardoperator;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.utils.AbbreviationUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.card.CardListPage;
import ru.axetta.ecafe.processor.web.ui.card.items.ClientItem;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by anvarov on 20.07.2017.
 */
@Component
@Scope(value = "session")
public class CardOperatorPage extends BasicWorkspacePage {

    private ru.axetta.ecafe.processor.web.ui.card.items.ClientItem client = new ru.axetta.ecafe.processor.web.ui.card.items.ClientItem();

    public ru.axetta.ecafe.processor.web.ui.card.items.ClientItem getClient() {
        return client;
    }

    public void setClient(ru.axetta.ecafe.processor.web.ui.card.items.ClientItem client) {
        this.client = client;
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
        private final CardListPage.PersonItem person;
        private final CardListPage.PersonItem contractPerson;
        private final Long contractId;
        private final Date contractTime;
        private final Integer contractState;

        public ClientItem(Client client) {
            this.idOfClient = client.getIdOfClient();
            this.orgShortName = client.getOrg().getShortName();
            this.person = new CardListPage.PersonItem(client.getPerson());
            this.contractPerson = new CardListPage.PersonItem(client.getContractPerson());
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

        public CardListPage.PersonItem getPerson() {
            return person;
        }

        public CardListPage.PersonItem getContractPerson() {
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

    public static class Item {

        private final Long idOfCard;
        private final CardListPage.ClientItem client;
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
                this.client = new CardListPage.ClientItem(card.getClient());
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

        public CardListPage.ClientItem getClient() {
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

    private List<CardListPage.Item> items = Collections.emptyList();

    @Override
    public void onShow() throws Exception {

    }

    @Override
    public String getPageFilename() {
        return "cardoperator/card_show";
    }

    public List<CardListPage.Item> getItems() {
        return items;
    }

    public void setItems(List<CardListPage.Item> items) {
        this.items = items;
    }

    public Object updateCardOperatorPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка карт: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, null);
            HibernateUtils.close(persistenceSession, null);
        }
        return null;
    }

    private void fill(Session persistenceSession) {

    }

    public Object clearCardOperatorPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка карт: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, null);
            HibernateUtils.close(persistenceSession, null);
        }
        return null;
    }


}
