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
                return;
            }
            Integer statusCode = service.sendPost(info, true);
            if(!statusCode.equals(200)){
                logger.error("The Geoplaner returned code " + statusCode);
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
    public void sendPaymentInfoToGeoplaner(Payment payment, Client client) throws Exception{
        Session session = null;
        Transaction hibernateTransaction = null;
        try {
            if(payment == null || client == null){
                throw new NullPointerException("Payment or Client is null");
            }
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            hibernateTransaction = session.beginTransaction();

            JsonPaymentInfo info = buildJsonPaymentInfo(session, payment, client);
            if (info == null) {
                logger.warn("No PaymentInfo records for send to Geoplaner App");
                return;
            }
            Integer statusCode = service.sendPost(info, false);
            if(!statusCode.equals(200)){
                logger.error("The Geoplaner returned code " + statusCode);
            } else {
                logger.info("Sends PaymentInfo of Order ID= " + payment.getIdOfOrder() + " to Geoplaner ");
            }

            hibernateTransaction.commit();
            hibernateTransaction = null;
        } catch (Exception e) {
            logger.error("Can't send PaymentInfo to Geoplaner App: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(hibernateTransaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private JsonEnterEventInfo buildJsonEnterEventInfo(Session session, EnterEvent event) throws Exception{
        JsonEnterEventInfo info = new JsonEnterEventInfo();
        Org org = null;
        Card card = getCardFromEnterEvent(session, event);
        if(card == null) {
            logger.error("No found Card by EnterEvent, construction of the message is interrupted");
            return null;
        }
        info.setCardType(Card.TYPE_NAMES[card.getCardType()]);
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

    private JsonPaymentInfo buildJsonPaymentInfo(Session session, Payment payment, Client client) throws Exception{
        JsonPaymentInfo info = new JsonPaymentInfo();
        Card card = getCardFromPaymentAndClient(session, payment, client);
        if(card == null){
            logger.error("Can't get Card for Client contractID = " + client.getContractId());
            return null;
        }

        info.setTrackerId(card.getCardPrintedNo());
        info.setTrackerUid(card.getCardNo());
        info.setCardType(Card.TYPE_NAMES[card.getCardType()]);
        info.setOrderTime(payment.getTime());
        info.setOrderType(payment.getOrderType().ordinal());
        info.setRSum(payment.getRSum());
        String purchasesNames = "";
        for(Purchase pc : payment.getPurchases()){
            purchasesNames += pc.getName() + ";";
        }
        info.setPurchasesName(purchasesNames);

        return info;
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

    private Card getCardFromPaymentAndClient(Session session, Payment payment, Client client)throws Exception {
        Card card = null;
        if(payment.getCardNo() != null){
            card = DAOUtils.findCardByCardNoAndIdOfFriendlyOrg(session, payment.getCardNo(), client.getOrg().getIdOfOrg());
        } else if(client.getCards() != null){
            for(Card cardOfClient : client.getCards()){
                if(cardOfClient.getState().equals(CardState.ISSUED.getValue())){
                    card = cardOfClient;
                    break;
                }
            }
        } else {
            throw new Exception("Can't get Card for Client contractID = " + client.getContractId());
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