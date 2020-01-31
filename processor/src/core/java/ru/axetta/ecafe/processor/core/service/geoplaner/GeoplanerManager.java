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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component
public class GeoplanerManager {
    private static final Logger logger = LoggerFactory.getLogger(GeoplanerManager.class);
    private final static boolean isOn = managerIsOn();
    private GeoplanerService service = RuntimeContext.getAppContext().getBean(GeoplanerService.class);

    private final String[] CLIENT_GENDERS = {
            "female", "male"
    };

    private static boolean managerIsOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String reqInstance = runtimeContext
                .getConfigProperties().getProperty("ecafe.processor.geoplaner.sendevents", "false");
        return Boolean.parseBoolean(reqInstance);
    }

    @Async
    public void sendEnterEventsToGeoplaner(EnterEvent enterEvent) throws Exception {
        Objects.requireNonNull(enterEvent);

        Session session = null;
        Transaction transaction = null;
        Integer statusCode = null;
        String errorText = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            JsonEnterEventInfo info = buildJsonEnterEventInfo(session, enterEvent);

            statusCode = service.sendPost(info, EventType.ENTER_EVENTS);

            if(statusCode == null){
                throw new Exception("Result code is null");
            }
            else if(!statusCode.equals(200)){
                throw new Exception("The Geoplaner returned code " + statusCode
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
            errorText = String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        saveJournal(errorText, statusCode, statusCode != null,  enterEvent.getClient(),
                enterEvent.getOrg(), EventType.ENTER_EVENTS.ordinal(), enterEvent.getCompositeIdOfEnterEvent().getIdOfEnterEvent(),
                null, null, enterEvent.getCompositeIdOfEnterEvent().getIdOfOrg());
    }

    @Async
    public void sendPurchasesInfoToGeoplaner(Payment purchases, Client client) throws Exception {
        Objects.requireNonNull(purchases);
        Objects.requireNonNull(client);

        Session session = null;
        Transaction hibernateTransaction = null;
        Integer statusCode = null;
        String errorText = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            hibernateTransaction = session.beginTransaction();

            JsonPurchasesInfo info = buildJsonPurchasesInfo(session, purchases, client);

            statusCode = service.sendPost(info, EventType.PURCHASES);

            if(statusCode == null){
                throw new Exception("Result code is null");
            }
            else if(!statusCode.equals(200)){
                throw new Exception("The Geoplaner returned code " + statusCode + " when sent Purchases ID " + purchases.getIdOfOrder());
            } else {
                logger.info("Sends PurchasesInfo of Order ID= " + purchases.getIdOfOrder() + " to Geoplaner ");
            }

            hibernateTransaction.commit();
            hibernateTransaction = null;
        } catch (Exception e) {
            logger.error("Can't send PurchasesInfo to Geoplaner App: ", e);
            errorText = String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage());
        } finally {
            HibernateUtils.rollback(hibernateTransaction, logger);
            HibernateUtils.close(session, logger);
        }
        saveJournal(errorText, statusCode, statusCode != null,  client, purchases.getIdOfOrg() == null ? client.getOrg() : null,
                EventType.PURCHASES.ordinal(), null, purchases.getIdOfOrder(), null, purchases.getIdOfOrg());
    }

    @Async
    public void sendPaymentInfoToGeoplaner(ClientPayment clientPayment, Client client) throws Exception {
        Objects.requireNonNull(clientPayment);
        Objects.requireNonNull(client);

        Session session = null;
        Transaction hibernateTransaction = null;
        Integer statusCode = null;
        String errorText = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            hibernateTransaction = session.beginTransaction();

            JsonPaymentInfo info = buildJsonPaymentInfo(session, clientPayment, client);

            statusCode = service.sendPost(info, EventType.PAYMENTS);
            if(statusCode == null){
                throw new Exception("Result code is null");
            }
            else if(!statusCode.equals(200)){
                throw new Exception("The Geoplaner returned code " + statusCode + " when sent clientPayment ID " + clientPayment.getIdOfPayment());
            } else {
                logger.info("Sends clientPayment of Order ID= " + clientPayment.getIdOfPayment() + " to Geoplaner ");
            }

            hibernateTransaction.commit();
            hibernateTransaction = null;
        } catch (Exception e) {
            logger.error("Can't send clientPayment to Geoplaner App: ", e);
            errorText = String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage());
        } finally {
            HibernateUtils.rollback(hibernateTransaction, logger);
            HibernateUtils.close(session, logger);
        }
        saveJournal(errorText, statusCode, statusCode != null,  client, client.getOrg(), EventType.PAYMENTS.ordinal(),
                null, null, clientPayment.getIdOfClientPayment(), null);
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
        info.setActualBalance(client.getBalance());
        if(client.getGender() == null){
            info.setGender(CLIENT_GENDERS[1]);
        } else {
            info.setGender(CLIENT_GENDERS[client.getGender()]);
        }
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

    private JsonPurchasesInfo buildJsonPurchasesInfo(Session session, Payment purchases, Client client) throws Exception {
        JsonPurchasesInfo info = new JsonPurchasesInfo();
        Card card = getCardFromPaymentAndClient(session, purchases, client);
        if(card == null){
            throw new Exception("Can't get Card for Client contractID = " + client.getContractId());
        }
        info.setCardNo(card.getCardPrintedNo());
        info.setCardPrintedNo(card.getCardNo());
        info.setCardType(Card.TYPE_NAMES[card.getCardType()]);
        info.setContractId(client.getContractId());
        info.setActualBalance(client.getBalance());
        if(client.getGender() == null){
            info.setGender(CLIENT_GENDERS[1]);
        } else {
            info.setGender(CLIENT_GENDERS[client.getGender()]);
        }
        info.setOrderTime(purchases.getTime());
        info.setOrderType(purchases.getOrderType().ordinal());
        info.setRSum(purchases.getRSum());

        StringBuilder purchasesNames = new StringBuilder();
        for(Purchase pc : purchases.getPurchases()){
            purchasesNames.append(pc.getName()).append(";");
        }
        info.setPurchasesName(purchasesNames.toString());

        return info;
    }

    private JsonPaymentInfo buildJsonPaymentInfo(Session session, ClientPayment clientPayment, Client client) throws Exception{
        JsonPaymentInfo info = new JsonPaymentInfo();
        Card card = getCardFromClientPaymentAndClient(session, clientPayment, client);
        if(card == null){
           throw new Exception("Can't get Card for Client contractID = " + client.getContractId());
        }
        info.setCardNo(card.getCardNo());
        info.setCardPrintedNo(card.getCardPrintedNo());
        info.setCardType(Card.TYPE_NAMES[card.getCardType()]);
        info.setContractId(client.getContractId());
        info.setActualBalance(client.getBalance());
        if(client.getGender() == null){
            info.setGender(CLIENT_GENDERS[1]);
        } else {
            info.setGender(CLIENT_GENDERS[client.getGender()]);
        }
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

    private void saveJournal(String errorText, Integer responseCode, Boolean isSend, Client client, Org org,
            Integer eventType, Long idOfEnterEvents, Long idOfOrder, Long idOfClientPayment, Long idOfOrg) {
        Session session = null;
        Transaction transaction = null;
        try{
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            String nodeName = RuntimeContext.getInstance().getNodeName();

            if(org == null){
                org = (Org) session.get(Org.class, idOfOrg);
            }

            GeoplanerNotificationJournal journal = GeoplanerNotificationJournal.Builder
                    .build(errorText, responseCode, isSend, client, org, eventType, idOfEnterEvents, idOfOrder,
                            idOfClientPayment, nodeName);

            session.save(journal);

            transaction.commit();
            transaction = null;
        } catch (Exception e){
            logger.error("Can't save GeoplanerNotificationJournal: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }
}