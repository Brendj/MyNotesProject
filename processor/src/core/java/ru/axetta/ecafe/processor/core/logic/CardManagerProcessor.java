/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.event.EventNotificator;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
    private static final Logger logger = LoggerFactory.getLogger(CardManagerProcessor.class);
    private final SessionFactory persistenceSessionFactory;
    private final EventNotificator eventNotificator;
    //private static final Integer SMARTWATCH_CARD_SIGN_CERT_NUM = 16;

    public static Long getPriceOfMifare() {
        return Long.parseLong(RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.card.priceOfMifare"));
    }

    public static Long getPriceOfMifareBracelet() {
        return Long.parseLong(RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.card.priceOfMifareBracelet"));
    }

    public CardManagerProcessor(SessionFactory persistenceSessionFactory, EventNotificator eventNotificator) {
        this.persistenceSessionFactory = persistenceSessionFactory;
        this.eventNotificator = eventNotificator;
    }

    @Override
    public Long createCard(Session persistenceSession, Transaction persistenceTransaction, Long idOfClient, long cardNo,
            int cardType, int state, Date validTime, int lifeState, String lockReason, Date issueTime,
            Long cardPrintedNo, Long longCardNo) throws Exception {
        return createCard(persistenceSession, idOfClient, cardNo, cardType, state, validTime,
            lifeState, lockReason, issueTime, cardPrintedNo, longCardNo, null);
    }

    public Long createCard(Session persistenceSession, Long idOfClient, long cardNo,
            int cardType, int state, Date validTime, int lifeState, String lockReason, Date issueTime,
            Long cardPrintedNo, Long longCardNo, User cardOperatorUser, AccountTransaction transaction) throws Exception {

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
        Card c = null;
        if(longCardNo == null) {
            c = findCardByCardNo(persistenceSession, cardNo);
        } else {
            c = findCardByLongCardNo(persistenceSession, longCardNo);
            if(c == null){
                c = findCardByCardNo(persistenceSession, cardNo);
            }
        }
        if (c != null ) {
            String errorMessage = String.format("Карта %s уже зарегистрирована", cardNo);
            if(c.getClient() != null) errorMessage += String.format(" на клиента с л/с=%s", c.getClient().getContractId());
            throw new Exception(errorMessage);
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
        card.setTransitionState(CardTransitionState.OWN.getCode());
        card.setLongCardNo(longCardNo);
        persistenceSession.save(card);

        //История карты при создании новой карты
        HistoryCard historyCard = new HistoryCard();
        historyCard.setCard(card);
        historyCard.setTransaction(transaction);
        historyCard.setUpDatetime(new Date());
        historyCard.setNewOwner(client);
        historyCard.setInformationAboutCard("Регистрация новой карты №: " + card.getCardNo()
                + " Длинный номер: " + card.getLongCardNo());
        historyCard.setUser(cardOperatorUser);
        persistenceSession.save(historyCard);
        return card.getIdOfCard();
    }

    @Override
    public Long createCard(Long idOfClient, long cardNo, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, Long cardPrintedNo, Long longCardNo) throws Exception {
        return createCard(idOfClient, cardNo, cardType, state, validTime, lifeState, lockReason, issueTime, cardPrintedNo,
                longCardNo, null);
    }

    @Override
    public Long createCardTransactionFree(Session session, Long idOfClient, long cardNo, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, Long cardPrintedNo, Long longCardNo) throws Exception {
        return createCard(session, idOfClient, cardNo, cardType, state, validTime, lifeState, lockReason, issueTime,
                cardPrintedNo, longCardNo, null);
    }

    public Long createCard(Session persistenceSession, Long idOfClient, long cardNo,
            int cardType, int state, Date validTime, int lifeState, String lockReason, Date issueTime,
            Long cardPrintedNo, Long longCardNo, User cardOperatorUser) throws Exception {
        return createCard(persistenceSession, idOfClient, cardNo, cardType, state, validTime, lifeState,
                lockReason, issueTime, cardPrintedNo, longCardNo, cardOperatorUser, null);
    }

    @Override
    public Long createCard(Long idOfClient, long cardNo, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, Long cardPrintedNo, Long longCardNo, User cardOperatorUser) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Long idOfCard = createCard(persistenceSession, idOfClient, cardNo, cardType, state,
                    validTime, lifeState, lockReason, issueTime, cardPrintedNo, longCardNo, cardOperatorUser);

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
    public void createTempCard(Long idOfOrg, long cardNo, String cardPrintedNo, Long longCardNo) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            createTempCard(persistenceSession, idOfOrg, cardNo, cardPrintedNo, longCardNo);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private void createTempCard(Session persistenceSession, Long idOfOrg, long cardNo, String cardPrintedNo,
            Long longCardNo)
            throws Exception {
        Org org = getOrgReference(persistenceSession, idOfOrg);
        if (org == null) {
            throw new Exception(String.format("Организация не найдена: %d", idOfOrg));
        }

        Card c = null;
        if(longCardNo == null) {
            c = findCardByCardNo(persistenceSession, cardNo);
        } else {
            c = findCardByLongCardNo(persistenceSession, longCardNo);
            if(c == null){
                c = findCardByCardNo(persistenceSession, cardNo);
            }
        }

        if (c != null) {
            throw new Exception(
                    String.format("Карта уже зарегистрирована на клиента: %d", c.getClient().getIdOfClient()));
        }

        CardTemp cardTemp = null;
        if(longCardNo == null) {
            cardTemp = findCardTempByCardNo(persistenceSession, cardNo);
        } else {
            cardTemp = findCardTempByLongCardNo(persistenceSession, longCardNo);
        }

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
            cardTemp = new CardTemp(org, cardNo, cardPrintedNo, longCardNo);
        }
        persistenceSession.save(cardTemp);
    }

    @Override
    public void updateCard(Long idOfClient, Long idOfCard, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, String externalId, User user) throws Exception {
        updateCard(idOfClient, idOfCard, cardType, state, validTime, lifeState, lockReason, issueTime, externalId, user, null);
    }

    @Override
    public void updateCard(Long idOfClient, Long idOfCard, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, String externalId) throws Exception {
        updateCard(idOfClient, idOfCard, cardType, state, validTime, lifeState, lockReason, issueTime, externalId, null, null);
    }

    @Override
    public void updateCard(Long idOfClient, Long idOfCard, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, String externalId, User cardOperatorUser, Long idOfOrg) throws Exception {
        updateCard(idOfClient, idOfCard, cardType, state, validTime, lifeState, lockReason, issueTime, externalId,
                cardOperatorUser, idOfOrg, "");
    }

    @Override
    public void updateCard(Long idOfClient, Long idOfCard, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, String externalId, User cardOperatorUser, Long idOfOrg, String informationAboutCard) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            updateCardInSession(persistenceSession, idOfClient, idOfCard, cardType, state, validTime, lifeState,
            lockReason, issueTime, externalId, cardOperatorUser, idOfOrg, informationAboutCard, false);

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public void updateCardInSession(Session persistenceSession, Long idOfClient, Long idOfCard, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, String externalId, User cardOperatorUser, Long idOfOrg, String informationAboutCard, boolean ignoreValidTime) throws Exception {
        String additionalInfoAboutCard = StringUtils.isBlank(informationAboutCard) ? "" : " (" + informationAboutCard + ")";
        Client newCardOwner = getClientReference(persistenceSession, idOfClient);
        Card updatedCard = getCardReference(persistenceSession, idOfCard);

        if (CardTransitionState.GIVEN_AWAY.getCode().equals(updatedCard.getTransitionState()) &&
                state != updatedCard.getState()) {
            throw new Exception("УИД карты передан в пользование другой ОО");
        }

        boolean clientHadCards = newCardOwner.getCards().size() > 0; //есть ли или были ли ранее у клиента хоть какие-то карты
        if (!clientHadCards) {
            Criteria criteria = persistenceSession.createCriteria(HistoryCard.class);
            criteria.add(Restrictions.or(Restrictions.eq("formerOwner", newCardOwner), Restrictions.eq("newOwner", newCardOwner)));
            clientHadCards = criteria.list().size() > 0;
        }

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
            historyCard.setUser(cardOperatorUser);
            persistenceSession.save(historyCard);
        } else if (cardOperatorUser != null || !StringUtils.isEmpty(informationAboutCard)) {
            HistoryCard historyCard = new HistoryCard();
            historyCard.setCard(updatedCard);
            historyCard.setUpDatetime(new Date());
            if (state == CardState.BLOCKED.getValue()) {
                historyCard.setInformationAboutCard("Блокировка карты №: " + updatedCard.getCardNo() + ". Причина: " + lockReason);
            } else {
                historyCard.setInformationAboutCard("Редактирование данных карты №: " + updatedCard.getCardNo()
                        + additionalInfoAboutCard);
            }
            historyCard.setNewOwner(newCardOwner);
            historyCard.setFormerOwner(updatedCard.getClient());
            historyCard.setUser(cardOperatorUser);
            persistenceSession.save(historyCard);
        }
        if(idOfOrg != null){
            Org org = (Org) persistenceSession.get(Org.class, idOfOrg);
            if(org != null) updatedCard.setOrg(org);
        }

        updatedCard.setClient(newCardOwner);
        updatedCard.setCardType(cardType);
        updatedCard.setUpdateTime(new Date());
        updatedCard.setState(state);
        updatedCard.setLockReason(lockReason);
        if (ignoreValidTime) {
            Date validTo;
            if (isSpecialSpbCard(updatedCard.getCardNo())) {
                validTo = CalendarUtils.addYear(new Date(), 12);
            } else {
                validTo = clientHadCards ? CalendarUtils.addDays(new Date(), 10) : CalendarUtils.addYear(new Date(), 12);
            }
            if (oldClient != newClient) updatedCard.setValidTime(validTo); //дату действия меняем, если карту выдаем другому клиенту
            updatedCard.setIssueTime(new Date());
        } else {
            updatedCard.setValidTime(validTime);
            updatedCard.setIssueTime(issueTime);
        }
        updatedCard.setLifeState(lifeState);
        updatedCard.setExternalId(externalId);
        persistenceSession.update(updatedCard);

        persistenceSession.flush();

    }

    public static boolean isSpecialSpbCard(Long cardNo) {
        boolean result = false;
        String str = cardNo.toString();
        if ((str.length() == 13 && !str.startsWith("1")) || str.length() >= 15) result = true;
        return result;
    }

    @Override
    public void changeCardOwner(Long idOfClient, Long cardNo, Long longCardNo, Date changeTime, Date validTime) throws Exception {
        changeCardOwner(idOfClient, cardNo, longCardNo, changeTime, validTime, null);
    }

    @Override
    public void changeCardOwner(Long idOfClient, Long cardNo, Long longCardNo,  Date changeTime, Date validTime,
            User cardOperatorUser) throws Exception {
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
                historyCard.setUser(cardOperatorUser);
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
            Long cardPrintedNo, Long longCardNo, Integer cardType) throws Exception {

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
        NewCard card = new NewCard(cardNo, cardPrintedNo, cardType);
        persistenceSession.save(card);

        return card.getIdOfNewCard();
    }

    @Override
    public Long createNewCard(long cardNo, Long cardPrintedNo, Long longCardNo, Integer cardType) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Long idOfCard = createNewCard(persistenceSession, persistenceTransaction, cardNo, cardPrintedNo,
                    longCardNo, cardType);

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
    public NewCardItem getNewCardPrintedNo(Session persistenceSession, Transaction persistenceTransaction, long cardNo) throws Exception {
        logger.debug("check exist card");
        Card c = findCardByCardNo(persistenceSession, cardNo);
        if (c != null) {
            return new NewCardItem(c.getCardPrintedNo(), c.getCardType());
        }
        logger.debug("check exist temp card");
        CardTemp ct = findCardTempByCardNo(persistenceSession, cardNo);
        if (ct != null) {
            return new NewCardItem(Long.parseLong(ct.getCardPrintedNo()), null);
        }
        logger.debug("check exist newcard");
        NewCard uCard = findNewCardByCardNo(persistenceSession, cardNo);
        if (uCard != null) {
            return new NewCardItem(uCard.getCardPrintedNo(), uCard.getCardType());
        }
        return null;
    }

    @Override
    public NewCardItem getNewCardPrintedNo(long cardNo) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            NewCardItem result = getNewCardPrintedNo(persistenceSession, persistenceTransaction, cardNo);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return result;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @Override
    public Long createCard(Long idOfClient, Long cardNo, Integer cardType, Date validTime, String lockReason,
            Date issueTime, Long cardPrintedNo, Long longCardNo) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Long idOfCard = createCard(persistenceSession, persistenceTransaction, idOfClient, cardNo, cardType,
                    validTime, lockReason, issueTime, cardPrintedNo, longCardNo);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return idOfCard;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @Transactional
    @Override
    public void reissueCard(Session persistenceSession, Long idOfClient, Long cardNo, Integer cardType, Integer state,
            Date validTime, Integer lifeState, String lockReason, Date issueTime, Long cardPrintedNo,
            Long longCardNo, User user) throws Exception {
        Client client = getClientReference(persistenceSession, idOfClient);
        Long price = Card.TYPE_NAMES[cardType].equals("Mifare")? getPriceOfMifare() : getPriceOfMifareBracelet();
        if(client.getBalance() < price){
            throw new Exception("Не хватает средств на лицевом счете. Текущий баланс: " + client.getBalance() / 100 + " р.");
        }
        AccountTransaction accountTransaction = null;
        accountTransaction = ClientAccountManager.processAccountTransaction(persistenceSession, client, null, -price, cardPrintedNo.toString(),
                AccountTransaction.CUSTOMERS_CARD_REVEALING_TRANSACTION_SOURCE_TYPE, null, issueTime, null);
        createCard(persistenceSession, idOfClient, cardNo, cardType, state, validTime, lifeState,
                lockReason, issueTime, cardPrintedNo, longCardNo, user, accountTransaction);
        persistenceSession.save(accountTransaction);
    }

    private Long createCard(Session persistenceSession, Transaction persistenceTransaction, Long idOfClient,
            Long cardNo, Integer cardType, Date validTime, String lockReason, Date issueTime, Long cardPrintedNo,
            Long longCardNo) throws Exception {
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

        logger.debug("create card");
        Card card = new Card(client, cardNo, cardType, validTime, cardPrintedNo);
        card.setIssueTime(issueTime);
        card.setLockReason(lockReason);
        card.setOrg(client.getOrg());
        card.setTransitionState(CardTransitionState.OWN.getCode());
        card.setLongCardNo(longCardNo);
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

    public Long createSmartWatchAsCard(Session session, Long idOfClient, Long trackerIdAsCardPrintedNo, Integer state,
            Date validTime, Integer lifeState, String lockReason, Date issueTime, Long trackerUidAsCardNo, User user,
            Integer cardSignCertNum) throws Exception{
        Integer cardType = Arrays.asList(Card.TYPE_NAMES).indexOf("Часы (Mifare)");
        logger.debug("check valid date");
        if (validTime.after(CalendarUtils.AFTER_DATE)) {
            throw new Exception("Не верно введена дата");
        }
        logger.debug("check issue date");
        if (issueTime != null && validTime.before(issueTime)) {
            throw new Exception("Не верно введена дата");
        }
        logger.debug("check exist client");
        Client client = getClientReference(session, idOfClient);
        if (client == null) {
            throw new Exception("Клиент не найден: " + idOfClient);
        }
        logger.debug("check exist smartWatch");
        Card c = DAOUtils.findCardByCardNo(session, trackerUidAsCardNo);
        if (c != null && c.getClient() != null) {
            throw new Exception("Смарт-часы уже зарегистрированы на клиента: " + c.getClient().getIdOfClient());
        }
        logger.debug("create card");
        Card card = new Card(client, trackerUidAsCardNo, cardType, state, validTime, lifeState, trackerIdAsCardPrintedNo);
        card.setIssueTime(issueTime);
        card.setLockReason(lockReason);
        card.setOrg(client.getOrg());
        card.setCreateTime(new Date());
        card.setTransitionState(CardTransitionState.OWN.getCode());
        card.setCardSignCertNum(cardSignCertNum);
        card.setLongCardNo(calculateLongCardNoByShortCardNo(trackerUidAsCardNo));
        session.save(card);

        HistoryCard historyCard = new HistoryCard();
        historyCard.setCard(card);
        historyCard.setUpDatetime(new Date());
        historyCard.setNewOwner(client);
        historyCard.setInformationAboutCard("Регистрация новых часов №: " + card.getCardNo() + " как карту");
        session.save(historyCard);

        return card.getIdOfCard();
    }

    public static void lockActiveCards(Session persistenceSession, Set<Card> lockableCards) throws Exception {
        for (Card card : lockableCards) {
            if (card.getState() == Card.ACTIVE_STATE) {
                card.setState(CardState.BLOCKED.getValue());
                card.setLockReason("Выпуск новой карты");
                card.setUpdateTime(new Date());
                persistenceSession.update(card);
            }
        }
    }

    public static Long calculateLongCardNoByShortCardNo(Long cardNo){
        if(cardNo == null){
            return null;
        }
        Long longCardId = (cardNo % 0x100) * 0x1000000 + ((cardNo / 0x100) % 0x100)*0x10000
                + ((cardNo / 0x10000) % 0x100)*0x100 + cardNo / 0x1000000;

        return longCardId;
    }
}
