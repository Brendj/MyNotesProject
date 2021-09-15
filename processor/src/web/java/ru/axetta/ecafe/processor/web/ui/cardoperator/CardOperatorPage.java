/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.cardoperator;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.utils.AbbreviationUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.card.CardListPage;
import ru.axetta.ecafe.processor.web.ui.card.CardLockReasonMenu;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by anvarov on 20.07.2017.
 */
@Component
@Scope(value = "session")
public class CardOperatorPage extends BasicWorkspacePage implements ClientSelectPage.CompleteHandler {

    private boolean clientHasNotBlockedCard = false;

    private ru.axetta.ecafe.processor.web.ui.card.items.ClientItem client = new ru.axetta.ecafe.processor.web.ui.card.items.ClientItem();

    public ru.axetta.ecafe.processor.web.ui.card.items.ClientItem getClient() {
        return client;
    }

    public void setClient(ru.axetta.ecafe.processor.web.ui.card.items.ClientItem client) {
        this.client = client;
    }
    private Long cardNo;
    private Long selectedCardNo;
    private String lockReason;
    private Item selectedItem;
    private final CardLockReasonMenu cardLockReasonMenu = new CardLockReasonMenu();

    @Override
    public void completeClientSelection(Session session, Long idOfClient) throws Exception {
        if (null != idOfClient) {
            Client client = (Client) session.load(Client.class, idOfClient);
            this.client = new ru.axetta.ecafe.processor.web.ui.card.items.ClientItem(client);
            clientHasNotBlockedCard = false;
            if (client.getCards() != null) {
                for (Card card : client.getCards()) {
                    if (card.getState().intValue() != CardState.BLOCKED.getValue()) {
                        clientHasNotBlockedCard = true;
                        break;
                    }
                }
            }
        }
    }

    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }

    public void onCardRead() {
        clearCardOperatorPage();
        updateCardOperatorPage();
    }

    public Object showBlockCardPanel() {
        if (selectedItem != null) selectedItem.setLockingNow(true);
        return null;
    }

    public Object hideBlockCardPanel() {
        if (selectedItem != null) selectedItem.setLockingNow(false);
        return null;
    }

    public Object blockCard() {
        if (selectedItem == null || StringUtils.isEmpty(CardLockReasonMenu.getDescriptionByValue(selectedItem.getLockReasonState()))) {
            printError("Выберите причину блокировки карты.");
            return null;
        }

        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            User user = MainPage.getSessionInstance().getCurrentUser();
            RuntimeContext.getInstance().getCardManager()
                    .updateCard(selectedItem.getClient().getIdOfClient(), selectedItem.getIdOfCard(), selectedItem.getCardType(),
                            CardState.BLOCKED.getValue(), selectedItem.getValidTime(), selectedItem.getLifeState(),
                            CardLockReasonMenu.getDescriptionByValue(selectedItem.getLockReasonState()),
                            selectedItem.getIssueTime(), selectedItem.getExternalId(),
                            user);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            fill(persistenceSession);
        } catch (Exception e) {
            getLogger().error("Failed to fill card edit page", e);
            printError("Ошибка при попытке заблокировать карту: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            HibernateUtils.close(persistenceSession, getLogger());
        }

        return null;
    }

    public Long getSelectedCardNo() {
        return selectedCardNo;
    }

    public void setSelectedCardNo(Long selectedCardNo) {
        this.selectedCardNo = selectedCardNo;
    }

    public String getLockReason() {
        return lockReason;
    }

    public void setLockReason(String lockReason) {
        this.lockReason = lockReason;
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Item selectedItem) {
        this.selectedItem = selectedItem;
    }

    public CardLockReasonMenu getCardLockReasonMenu() {
        return cardLockReasonMenu;
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
        private String lockReason;
        private final Date validTime;
        private final Integer lifeState;
        private final Long cardPrintedNo;
        private Boolean lockingNow;
        private Integer lockReasonState;
        private final String externalId;

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
            this.lockingNow = false;
            this.externalId = card.getExternalId();
        }

        public String getMessage() {
            try {
                String personNameAction =
                        client.getPerson().getSurname() + " " + client.getPerson().getFirstName() + " " + client.getPerson().getSecondName();
                String message =
                        String.valueOf(client.getContractId()) + ";" + personNameAction + ";" + String.valueOf(cardNo) + ";" + String.valueOf(cardPrintedNo);
                return message;
            } catch (Exception e) {
                return "";
            }
        }

        public Boolean getCanBeBlocked() {
            return this.state.equals(CardState.ISSUED.getValue()) && !lockingNow;
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

        public void setLockReason(String lockReason) {
            this.lockReason = lockReason;
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

        public Boolean getLockingNow() {
            return lockingNow;
        }

        public void setLockingNow(Boolean lockingNow) {
            this.lockingNow = lockingNow;
        }

        public Integer getLockReasonState() {
            return lockReasonState;
        }

        public void setLockReasonState(Integer lockReasonState) {
            this.lockReasonState = lockReasonState;
        }

        public String getExternalId() {
            return externalId;
        }
    }

    public boolean isClientHasNotBlockedCard() {
        return clientHasNotBlockedCard;
    }

    public void setClientHasNotBlockedCard(boolean clientHasNotBlockedCard) {
        this.clientHasNotBlockedCard = clientHasNotBlockedCard;
    }

    private List<Item> items = Collections.emptyList();

    @Override
    public void onShow() throws Exception {

    }

    @Override
    public String getPageFilename() {
        return "cardoperator/card_show";
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Object applyClient() {
        if (client == null || client.getIdOfClient() == null) {
            printError("Для получения списка карт выберите клиента.");
            return null;
        }
        cardNo = null;
        return updateCardOperatorPage();
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

    public void fill(Session persistenceSession) throws Exception {
        Criteria criteria = persistenceSession.createCriteria(Card.class);
        if (client != null) {
            Client cl = DAOReadonlyService.getInstance().findClientById(client.getIdOfClient());
            criteria.add(Restrictions.eq("client", cl));
        } else if (cardNo != null) {
            criteria.add(Restrictions.eq("cardNo", cardNo));
        }
        List<Card> cards = criteria.list();
        List<Item> items2 = new ArrayList<Item>();
        if (cards == null || cards.size() == 0) {
            if (client != null) {
                printMessage("У выбранного клиента нет карт.");
            } else if (cardNo != null) {
                printMessage("Карта не найдена.");
            }
        } else {
            for (Card card : cards) {
                Item item = new Item(card);
                items2.add(item);
            }
        }
        setItems(items2);
    }

    public Object clearCardOperatorPage() {
        items = null;
        client = null;
        return null;
    }


}
