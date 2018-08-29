/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.geoplaner;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.AccountTransaction;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.EnterEvent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class GeoplanerManager {
    private static final Logger logger = LoggerFactory.getLogger(GeoplanerManager.class);
    private final static boolean isOn = managerIsOn();
    private GeoplanerService service = RuntimeContext.getAppContext().getBean(GeoplanerService.class);

    private static boolean managerIsOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String reqInstance = runtimeContext
                .getConfigProperties().getProperty("ecafe.processor.geoplaner.sendevents", "false");
        return Boolean.parseBoolean(reqInstance);
    }

    @Async
    public void sendEnterEventsToGeoplaner(EnterEvent enterEvent) throws Exception{
        Session session = null;
        Transaction transaction = null;
        try {
            if(enterEvent == null){
                throw new Exception("EnterEvent is null");
            }
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            JsonEnterEventInfo info = buildJsonEnterEventInfo(session, enterEvent);
            if (info == null) {
                logger.warn("No EnterEventSendInfo records for send to Geoplaner App");
            }
            Integer statusCode = service.sendPost(info, true);
            if(!statusCode.equals(200)){
                logger.error("The Geoplaner returned code " + statusCode);
            } else {
                logger.info("EnterEvent ID: " + enterEvent.getCompositeIdOfEnterEvent().getIdOfEnterEvent()
                        + " idOfOrg: " + enterEvent.getCompositeIdOfEnterEvent().getIdOfOrg()
                        + " send to Geoplaner");
            }

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Can't send EnterEventSendInfo to Geoplaner App: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @Async
    public void sendTransactionalInfoToGeoplaner(AccountTransaction accountTransaction) throws Exception{
        Session session = null;
        Transaction hibernateTransaction = null;
        try {
            if(accountTransaction == null){
                throw new Exception("Transaction is null");
            }
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            hibernateTransaction = session.beginTransaction();

            JsonTransactionInfo infoList = buildJsonTransactionInfo(session, accountTransaction);
            if (infoList == null) {
                logger.warn("No Transaction records for send to Geoplaner App");
                return;
            }
            Integer statusCode = service.sendPost(infoList, false);
            if(!statusCode.equals(200)){
                logger.error("The Geoplaner returned code " + statusCode);
            } else {
                logger.info("Transaction ID: " + accountTransaction.getIdOfTransaction()
                        + " send to Geoplaner");
            }

            hibernateTransaction.commit();
            hibernateTransaction = null;
        } catch (Exception e) {
            logger.error("Can't send EnterEventSendInfo to Geoplaner App: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(hibernateTransaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private JsonEnterEventInfo buildJsonEnterEventInfo(Session session, EnterEvent event) throws Exception{
        JsonEnterEventInfo info = new JsonEnterEventInfo();
        Org org = null;
        Card card = getCardFromEnterEvent(session, event);
        if(card == null){
            logger.error("No found Card by ID: " + event.getIdOfCard());
            return null;
        }

        info.setTrackerId(card.getCardPrintedNo());
        info.setTrackerUid(card.getCardNo());
        info.setEvtDateTime(event.getEvtDateTime());
        info.setDirection(event.getPassDirection());
        if(event.getOrg() == null){
            org = (Org) session.get(Org.class, event.getCompositeIdOfEnterEvent().getIdOfOrg());
        } else {
            org = event.getOrg();
        }
        info.setShortAddress(org.getShortAddress());
        info.setShortName(org.getShortName());

        return info;
    }

    private JsonTransactionInfo buildJsonTransactionInfo(Session session, AccountTransaction transaction) throws Exception{
        JsonTransactionInfo info = new JsonTransactionInfo();
        Card card = getCardFromTransaction(session, transaction);
        if(card == null){
            logger.error("No found Card for Transaction ID" + transaction.getIdOfTransaction());
            return null;
        }

        info.setTrackerId(card.getCardPrintedNo());
        info.setTrackerUid(card.getCardNo());
        info.setTransactionTime(transaction.getTransactionTime());
        info.setSourceType(transaction.getSourceType());
        info.setTransactionSum(transaction.getTransactionSum());
        info.setSourceName(transaction.getSource());

        return info;
    }

    private Card getCardFromEnterEvent(Session session, EnterEvent event) throws Exception {
        Card card = null;
        if(event.getIdOfCard() != null){
            card = DAOUtils.findCardByCardNo(session, event.getIdOfCard());
        } else if(event.getClient() != null){
            card = DAOUtils.getLastCardByClient(session, event.getClient());
        } else {
            throw new Exception("EnterEvent ID: " + event.getCompositeIdOfEnterEvent().getIdOfEnterEvent()
                    + " Org ID: " +  event.getCompositeIdOfEnterEvent().getIdOfOrg()
                    + " without Client and Card");
        }
        if(card == null){
            return null;
        }

        if(!Card.TYPE_NAMES[card.getCardType()].equals("Часы (Mifare)")){
            throw new Exception("Card with CardNo: " + card.getCardNo()
                    + " and CardPrintedNo: " + card.getCardPrintedNo()
                    + " is not SmartWatch");
        }
        return card;
    }

    private Card getCardFromTransaction(Session session, AccountTransaction transaction)throws Exception {
        Card card = null;
        if(transaction.getCard() != null){
            card = transaction.getCard();
        } else if(transaction.getClient() != null){
            card = DAOUtils.getLastCardByClient(session, transaction.getClient());
        } else {
            throw new Exception("Transaction ID: " + transaction.getIdOfTransaction() + " without Client and Card");
        }
        if(card == null){
            return null;
        }

        if(!Card.TYPE_NAMES[card.getCardType()].equals("Часы (Mifare)")){
            throw new Exception("Card with CardNo: " + card.getCardNo()
                    + " and CardPrintedNo: " + card.getCardPrintedNo()
                    + " is not SmartWatch");
        }
        return card;
    }

    public static boolean isOn() {
        return isOn;
    }
}