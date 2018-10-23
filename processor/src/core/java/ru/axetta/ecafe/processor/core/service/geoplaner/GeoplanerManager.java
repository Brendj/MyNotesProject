/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.geoplaner;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.Payment;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.Purchase;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
@Scope("singleton")
public class GeoplanerManager {
    private static final Logger logger = LoggerFactory.getLogger(GeoplanerManager.class);
    private final static boolean isOn = managerIsOn();
    private GeoplanerService service = RuntimeContext.getAppContext().getBean(GeoplanerService.class);

    private final int ENTER_EVENTS = 1;
    private final int PURCHASES = 2;
    private final int PAYMENTS = 3;

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
                return;
            }
            Integer statusCode = service.sendPost(info, ENTER_EVENTS);
            if(statusCode == null){
                logger.error("Result code is null");
            }
            else if(!statusCode.equals(200)){
                logger.error("The Geoplaner returned code " + statusCode
                        + " when sent EnterEvent IdOfOrg " + enterEvent.getCompositeIdOfEnterEvent().getIdOfOrg()
                        + " ID enterEvent " + enterEvent.getCompositeIdOfEnterEvent().getIdOfEnterEvent());
            } else {
                logger.info("Sends  EnterEvent ID: " + enterEvent.getCompositeIdOfEnterEvent().getIdOfEnterEvent()
                        + " idOfOrg: " + enterEvent.getCompositeIdOfEnterEvent().getIdOfOrg()
                        + " to Geoplaner");
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
    public void sendPurchasesInfoToGentler(Payment purchases, Client client) throws Exception{
        Session session = null;
        Transaction hibernateTransaction = null;
        try {
            if(purchases == null || client == null){
                throw new NullPointerException("Purchases or Client is null");
            }
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            hibernateTransaction = session.beginTransaction();

            JsonPurchasesInfo info = buildJsonPurchasesInfo(session, purchases, client);
            if (info == null) {
                logger.warn("No Purchases records for send to Geoplaner App");
                return;
            }
            Integer statusCode = service.sendPost(info, PURCHASES);
            if(statusCode == null){
                logger.error("Result code is null");
            }
            else if(!statusCode.equals(200)){
                logger.error("The Geoplaner returned code " + statusCode + " when sent Purchases ID " + purchases.getIdOfOrder());
            } else {
                logger.info("Sends PurchasesInfo of Order ID= " + purchases.getIdOfOrder() + " to Geoplaner ");
            }

            hibernateTransaction.commit();
            hibernateTransaction = null;
        } catch (Exception e) {
            logger.error("Can't send PurchasesInfo to Geoplaner App: ", e);
        } finally {
            HibernateUtils.rollback(hibernateTransaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @Async
    public void sendPaymentInfoToGeoplaner(ClientPayment clientPayment, Client client) throws Exception{
        Session session = null;
        Transaction hibernateTransaction = null;
        try {
            if(clientPayment == null || client == null){
                throw new NullPointerException("clientPayment or Client is null");
            }
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            hibernateTransaction = session.beginTransaction();

            JsonPaymentInfo info = buildJsonPaymentInfo(session, clientPayment, client);
            if (info == null) {
                logger.warn("No clientPayment records for send to Geoplaner App");
                return;
            }
            Integer statusCode = service.sendPost(info, PAYMENTS);
            if(statusCode == null){
                logger.error("Result code is null");
            }
            else if(!statusCode.equals(200)){
                logger.error("The Geoplaner returned code " + statusCode + " when sent clientPayment ID " + clientPayment.getIdOfPayment());
            } else {
                logger.info("Sends clientPayment of Order ID= " + clientPayment.getIdOfPayment() + " to Geoplaner ");
            }

            hibernateTransaction.commit();
            hibernateTransaction = null;
        } catch (Exception e) {
            logger.error("Can't send clientPayment to Geoplaner App: ", e);
        } finally {
            HibernateUtils.rollback(hibernateTransaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private JsonEnterEventInfo buildJsonEnterEventInfo(Session session, EnterEvent event) throws Exception{
        JsonEnterEventInfo info = new JsonEnterEventInfo();
        Org org = null;
        Client client;
        Card card = getCardFromEnterEvent(session, event);
        if(card == null) {
            logger.error("No found Card by EnterEvent, construction of the message is interrupted");
            if(event.getClient() != null){
                client = event.getClient();
            } else {
                throw new Exception("No found Client and Card by EnterEvent");
            }
        } else {
            client = card.getClient();
        }

        info.setCardType(Card.TYPE_NAMES[card.getCardType()]);
        info.setCardPrintedNo(card.getCardPrintedNo());
        info.setCardNo(card.getCardNo());
        info.setContractId(client.getContractId());
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

    private JsonPurchasesInfo buildJsonPurchasesInfo(Session session, Payment purchases, Client client) throws Exception{
        JsonPurchasesInfo info = new JsonPurchasesInfo();
        Card card = getCardFromPaymentAndClient(session, purchases, client);
        if(card == null){
            logger.error("Can't get Card for Client contractID = " + client.getContractId());
            return null;
        }
        info.setCardNo(card.getCardPrintedNo());
        info.setCardPrintedNo(card.getCardNo());
        info.setCardType(Card.TYPE_NAMES[card.getCardType()]);
        info.setContractID(client.getContractId());
        info.setOrderTime(purchases.getTime());
        info.setOrderType(purchases.getOrderType().ordinal());
        info.setRSum(purchases.getRSum());

        String purchasesNames = "";
        for(Purchase pc : purchases.getPurchases()){
            purchasesNames += pc.getName() + ";";
        }
        info.setPurchasesName(purchasesNames);

        return info;
    }

    private JsonPaymentInfo buildJsonPaymentInfo(Session session, ClientPayment clientPayment, Client client) throws Exception{
        JsonPaymentInfo info = new JsonPaymentInfo();
        Card card = getCardFromClientPaymentAndClient(session, clientPayment, client);
        if(card == null){
            logger.error("Can't get Card for Client contractID = " + client.getContractId());
            return null;
        }
        info.setCardNo(card.getCardNo());
        info.setCardPrintedNo(card.getCardPrintedNo());
        info.setCardType(Card.TYPE_NAMES[card.getCardType()]);
        info.setContractId(client.getContractId());
        info.setCreateTime(clientPayment.getCreateTime());
        if(clientPayment.getTransaction()!= null){
            info.setSourceType(clientPayment.getTransaction().getSourceType());
            info.setBalanceBefore(clientPayment.getTransaction().getBalanceBeforeTransaction());
        } else {
            info.setBalanceBefore(client.getBalance() - clientPayment.getPaySum());
        }
        info.setPaySum(clientPayment.getPaySum());

        return info;
    }

    private Card getCardFromClientPaymentAndClient(Session session, ClientPayment clientPayment, Client client) {
        Card card = null;
        if(clientPayment.getTransaction() != null && clientPayment.getTransaction().getCard() != null){
            card = clientPayment.getTransaction().getCard();
        } else {
            card = DAOUtils.getLastCardByClient(session, client);
        }
        return card;
    }

    private Card getCardFromEnterEvent(Session session, EnterEvent event) throws Exception {
        Card card = null;
        if(event.getIdOfCard() != null){
            card = DAOUtils.findCardByCardNoAndIdOfFriendlyOrg(session, event.getIdOfCard(),
                    event.getCompositeIdOfEnterEvent().getIdOfOrg());
        } else if(event.getClient() != null){
            card = DAOUtils.getLastCardByClient(session, event.getClient());
        } else {
            throw new Exception("EnterEvent ID: " + event.getCompositeIdOfEnterEvent().getIdOfEnterEvent()
                    + " Org ID: " +  event.getCompositeIdOfEnterEvent().getIdOfOrg()
                    + " without Client and Card");
        }
        return card;
    }

    private Card getCardFromPaymentAndClient(Session session, Payment payment, Client client)throws Exception {
        Card card = null;
        if(payment.getCardNo() != null){
            card = DAOUtils.findCardByCardNoAndIdOfFriendlyOrg(session, payment.getCardNo(), client.getOrg().getIdOfOrg());
        } else {
            card = DAOUtils.getLastCardByClient(session, client);
        }
        return card;
    }

    public static boolean isOn() {
        return isOn;
    }
}