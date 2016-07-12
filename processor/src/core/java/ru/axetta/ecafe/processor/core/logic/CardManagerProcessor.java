/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.event.EventNotificator;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 07.07.16
 * Time: 10:37
 */
public class CardManagerProcessor implements CardManager {
    private static final Logger logger = LoggerFactory.getLogger(Processor.class);
    private final SessionFactory persistenceSessionFactory;
    private final EventNotificator eventNotificator;

    public CardManagerProcessor(SessionFactory persistenceSessionFactory, EventNotificator eventNotificator) {
        this.persistenceSessionFactory = persistenceSessionFactory;
        this.eventNotificator = eventNotificator;
    }

    @Override
    public Long createCard(Session persistenceSession, Transaction persistenceTransaction, Long idOfClient, long cardNo,
            int cardType, int state, Date validTime, int lifeState, String lockReason, Date issueTime,
            Long cardPrintedNo) throws Exception {

        logger.debug("check valid date");
        if (validTime.after(CalendarUtils.AFTER_DATE)) {
            throw new Exception("Не верно введена дата");
        }

        logger.debug("check issue date");
        if (issueTime != null && validTime.before(issueTime)) {
            throw new Exception("Не верно введена дата");
        }

        logger.debug("check exist client");
        Client client = getClientReference(persistenceSession, idOfClient);
        if (client == null) {
            throw new Exception("Клиент не найден: " + idOfClient);
        }
        logger.debug("check exist card");
        Card c = findCardByCardNo(persistenceSession, cardNo);
        if (c != null && c.getClient() != null) {
            throw new Exception("Карта уже зарегистрирована на клиента: " + c.getClient().getIdOfClient());
        }
        logger.debug("check exist temp card");
        CardTemp ct = findCardTempByCardNo(persistenceSession, cardNo);
        if (ct != null) {
            if (ct.getClient() != null) {
                throw new Exception(String.format(
                        "Карта с таким номером уже зарегистрирована как временная на клиента: %s. Статус карты - %s.",
                        ct.getClient().getIdOfClient(), ct.getCardStation()));
            }
            if (ct.getVisitor() != null) {
                throw new Exception(String.format(
                        "Карта с таким номером уже зарегистрирована как временная на посетителя: %s. Статус карты - %s.",
                        ct.getVisitor().getIdOfVisitor(), ct.getCardStation()));
            }
        }

        if (state == CardState.ISSUED.getValue()) {
            boolean haveActiveCard = false;
            for (Card card : client.getCards()) {
                if (CardState.ISSUED.getValue() == card.getState()) {
                    haveActiveCard = true;
                }
            }
            if (haveActiveCard && client.getOrg().getOneActiveCard()) {
                throw new Exception("У клиента уже есть активная карта.");
            }
        }

        logger.debug("clear active card");
        if (state == Card.ACTIVE_STATE) {
            lockActiveCards(persistenceSession, client.getCards());
        }

        logger.debug("create card");
        Card card = new Card(client, cardNo, cardType, state, validTime, lifeState, cardPrintedNo);
        card.setIssueTime(issueTime);
        card.setLockReason(lockReason);
        card.setOrg(client.getOrg());
        persistenceSession.save(card);

        //История карты при создании новой карты
        HistoryCard historyCard = new HistoryCard();
        historyCard.setCard(card);
        historyCard.setUpDatetime(new Date());
        historyCard.setNewOwner(client);
        historyCard.setInformationAboutCard("Регистрация новой карты №: " + card.getCardNo());
        persistenceSession.save(historyCard);

        return card.getIdOfCard();
    }

    @Override
    public Long createCard(Long idOfClient, long cardNo, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, Long cardPrintedNo) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Long idOfCard = createCard(persistenceSession, persistenceTransaction, idOfClient, cardNo, cardType, state,
                    validTime, lifeState, lockReason, issueTime, cardPrintedNo);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return idOfCard;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @Override
    public void createTempCard(Long idOfOrg, long cardNo, String cardPrintedNo) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            createTempCard(persistenceSession, idOfOrg, cardNo, cardPrintedNo);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private void createTempCard(Session persistenceSession, Long idOfOrg, long cardNo, String cardPrintedNo)
            throws Exception {
        Org org = getOrgReference(persistenceSession, idOfOrg);
        if (org == null) {
            throw new Exception(String.format("Организация не найдена: %d", idOfOrg));
        }
        Card c = findCardByCardNo(persistenceSession, cardNo);
        if (c != null) {
            throw new Exception(
                    String.format("Карта уже зарегистрирована на клиента: %d", c.getClient().getIdOfClient()));
        }
        CardTemp cardTemp = findCardTempByCardNo(persistenceSession, cardNo);
        if (cardTemp != null) {
            if (cardTemp.getOrg().getIdOfOrg().equals(idOfOrg)) {
                cardTemp.setCardPrintedNo(cardPrintedNo);
            } else {
                String orgInfo = org.getIdOfOrg() + ":" + org.getOfficialName() + " '" + org.getAddress() + "'";
                throw new Exception(String.format(
                        "Временная карта уже зарегистрирована в другой организации: %s. Статус карты - %s.", orgInfo,
                        cardTemp.getCardStation()));
            }
        } else {
            cardTemp = new CardTemp(org, cardNo, cardPrintedNo);
        }
        persistenceSession.save(cardTemp);
    }

    @Override
    public void updateCard(Long idOfClient, Long idOfCard, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, String externalId) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client newCardOwner = getClientReference(persistenceSession, idOfClient);
            Card updatedCard = getCardReference(persistenceSession, idOfCard);

            if (state == Card.ACTIVE_STATE) {
                Set<Card> clientCards = new HashSet<Card>(newCardOwner.getCards());
                clientCards.remove(updatedCard);
                lockActiveCards(persistenceSession, clientCards);
            }

            final long oldClient = (updatedCard.getClient() != null) ? updatedCard.getClient().getIdOfClient() : -1;
            final long newClient = newCardOwner.getIdOfClient();

            //История карты при обновлении информации
            if (oldClient != newClient) {
                HistoryCard historyCard = new HistoryCard();
                historyCard.setCard(updatedCard);
                historyCard.setUpDatetime(new Date());
                historyCard
                        .setInformationAboutCard("Передача карты №: " + updatedCard.getCardNo() + " другому владельцу");
                historyCard.setNewOwner(newCardOwner);
                historyCard.setFormerOwner(updatedCard.getClient());
                persistenceSession.save(historyCard);
            }

            updatedCard.setClient(newCardOwner);
            updatedCard.setCardType(cardType);
            updatedCard.setUpdateTime(new Date());
            updatedCard.setState(state);
            updatedCard.setLockReason(lockReason);
            updatedCard.setValidTime(validTime);
            updatedCard.setIssueTime(issueTime);
            updatedCard.setLifeState(lifeState);
            updatedCard.setExternalId(externalId);
            updatedCard.setUpdateTime(new Date());
            persistenceSession.update(updatedCard);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @Override
    public void changeCardOwner(Long idOfClient, Long cardNo, Date changeTime, Date validTime) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            logger.debug("check valid date");
            if (validTime.after(CalendarUtils.AFTER_DATE)) {
                throw new Exception("Не верно введена дата");
            }

            logger.debug("check issue date");
            if (validTime.before(changeTime)) {
                throw new Exception("Не верно введена дата");
            }

            Client newCardOwner = getClientReference(persistenceSession, idOfClient);
            if (newCardOwner == null) {
                throw new Exception("Клиент не найден: " + idOfClient);
            }

            CardTemp ct = findCardTempByCardNo(persistenceSession, cardNo);
            if (ct != null) {
                if (ct.getClient() != null) {
                    final String format = "Карта с таким номером уже зарегистрирована как временная на клиента: %d";
                    final String message = String.format(format, ct.getClient().getIdOfClient());
                    throw new Exception(message);
                }
                if (ct.getVisitor() != null) {
                    final String format = "Карта с таким номером уже зарегистрирована как временная на посетителя: %d";
                    final String message = String.format(format, ct.getVisitor().getIdOfVisitor());
                    throw new Exception(message);
                }
                if (ct.getVisitor() == null && ct.getClient() == null) {
                    final String format = "Карта с таким номером уже зарегистрирована как временная, но не имеет владельца: %d";
                    final String message = String.format(format, cardNo);
                    throw new Exception(message);
                }
            }

            //Card updatedCard = DAOUtils.getCardReference(persistenceSession, idOfCard);
            Card updatedCard = findCardByCardNo(persistenceSession, cardNo);
            if (updatedCard == null) {
                throw new Exception("Неизвестная карта: " + cardNo);
            }

            //if (state == Card.ACTIVE_STATE) {
            //    Set<Card> clientCards = new HashSet<Card>(newCardOwner.getCards());
            //    clientCards.remove(updatedCard);
            //    lockActiveCards(persistenceSession, clientCards);
            //}

            lockActiveCards(persistenceSession, newCardOwner.getCards());

            final long oldClient = (updatedCard.getClient() != null) ? updatedCard.getClient().getIdOfClient() : -1;
            final long newClient = newCardOwner.getIdOfClient();

            //История карты при смене владельца
            if (oldClient != newClient) {
                HistoryCard historyCard = new HistoryCard();
                historyCard.setCard(updatedCard);
                historyCard.setUpDatetime(new Date());
                historyCard
                        .setInformationAboutCard("Передача карты №: " + updatedCard.getCardNo() + " другому владельцу");
                historyCard.setFormerOwner(updatedCard.getClient());
                historyCard.setNewOwner(newCardOwner);
                persistenceSession.save(historyCard);
            }

            updatedCard.setClient(newCardOwner);
            //updatedCard.setCardType(cardType);
            updatedCard.setUpdateTime(new Date());
            updatedCard.setState(Card.ACTIVE_STATE);
            updatedCard.setLockReason("");
            updatedCard.setValidTime(validTime);
            updatedCard.setIssueTime(changeTime);
            //updatedCard.setLifeState(lifeState);
            //updatedCard.setExternalId(externalId);
            updatedCard.setUpdateTime(new Date());
            persistenceSession.update(updatedCard);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @Override
    public Long createNewCard(Session persistenceSession, Transaction persistenceTransaction, long cardNo,
            Long cardPrintedNo) throws Exception {

        logger.debug("check exist card");
        Card c = findCardByCardNo(persistenceSession, cardNo);
        if (c != null) {
            if(c.getClient() != null){
                throw new Exception(String.format("Карта с таким номером уже зарегистрирована на клиента: %s.", c.getClient().getIdOfClient()));
            }
            throw new Exception("Карта с таким номером уже зарегистрирована.");
        }

        logger.debug("check exist temp card");
        CardTemp ct = findCardTempByCardNo(persistenceSession, cardNo);
        if (ct != null) {
            if (ct.getClient() != null) {
                throw new Exception(String.format(
                        "Карта с таким номером уже зарегистрирована как временная на клиента: %s. Статус карты - %s.",
                        ct.getClient().getIdOfClient(), ct.getCardStation()));
            }
            if (ct.getVisitor() != null) {
                throw new Exception(String.format(
                        "Карта с таким номером уже зарегистрирована как временная на посетителя: %s. Статус карты - %s.",
                        ct.getVisitor().getIdOfVisitor(), ct.getCardStation()));
            }
            throw new Exception("Карта с таким номером уже зарегистрирована как временная.");
        }

        logger.debug("check exist newcard");
        NewCard uCard = findNewCardByCardNo(persistenceSession, cardNo);
        if (uCard != null) {
            throw new Exception("Карта с данным номером уже зарегистрирована как непривязанная карта.");
        }

        logger.debug("create card");
        NewCard card = new NewCard(cardNo, cardPrintedNo);
        persistenceSession.save(card);

        return card.getIdOfNewCard();
    }

    @Override
    public Long createNewCard(long cardNo, Long cardPrintedNo) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Long idOfCard = createNewCard(persistenceSession, persistenceTransaction, cardNo, cardPrintedNo);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return idOfCard;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @Override
    public Long getNewCardPrintedNo(Session persistenceSession, Transaction persistenceTransaction, long cardNo) throws Exception {
        logger.debug("check exist card");
        Card c = findCardByCardNo(persistenceSession, cardNo);
        if (c != null) {
            return c.getCardPrintedNo();
        }
        logger.debug("check exist temp card");
        CardTemp ct = findCardTempByCardNo(persistenceSession, cardNo);
        if (ct != null) {
            return Long.parseLong(ct.getCardPrintedNo());
        }
        logger.debug("check exist newcard");
        NewCard uCard = findNewCardByCardNo(persistenceSession, cardNo);
        if (uCard != null) {
            return  uCard.getCardPrintedNo();
        }
        return null;
    }

    @Override
    public Long getNewCardPrintedNo(long cardNo) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Long cardPrintedNo = getNewCardPrintedNo(persistenceSession, persistenceTransaction, cardNo);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return cardPrintedNo;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private static void lockActiveCards(Session persistenceSession, Set<Card> lockableCards) throws Exception {
        for (Card card : lockableCards) {
            if (card.getState() == Card.ACTIVE_STATE) {
                card.setState(CardState.BLOCKED.getValue());
                card.setLockReason("Выпуск новой карты");
                persistenceSession.update(card);
            }
        }
    }
}