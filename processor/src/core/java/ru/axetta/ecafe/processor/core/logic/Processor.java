/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.AccessDiniedException;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.event.EventNotificator;
import ru.axetta.ecafe.processor.core.event.PaymentProcessEvent;
import ru.axetta.ecafe.processor.core.event.SyncEvent;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.ClientPaymentOrderProcessor;
import ru.axetta.ecafe.processor.core.payment.PaymentProcessor;
import ru.axetta.ecafe.processor.core.payment.PaymentRequest;
import ru.axetta.ecafe.processor.core.payment.PaymentResponse;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.OrderCancelProcessor;
import ru.axetta.ecafe.processor.core.service.SMSService;
import ru.axetta.ecafe.processor.core.subscription.SubscriptionFeeManager;
import ru.axetta.ecafe.processor.core.sync.SyncProcessor;
import ru.axetta.ecafe.processor.core.sync.SyncRequest;
import ru.axetta.ecafe.processor.core.sync.SyncResponse;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrTokenizer;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 21.07.2009
 * Time: 15:51:14
 * To change this template use File | Settings | File Templates.
 */
public class Processor implements SyncProcessor,
        PaymentProcessor,
        ClientPaymentOrderProcessor,
        CardManager,
        OrderCancelProcessor,
        SubscriptionFeeManager {

    public Client getClientInfo(long idOfContract) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        Client client = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            client = DAOUtils.findClientByContractId(persistenceSession, idOfContract);

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return client;
    }

    public enum PaymentProcessResult {

        OK(0, "Ok"),
        UNKNOWN_ERROR(100, "Unknown error"),
        CLIENT_NOT_FOUND(105, "Client not found"),
        CARD_NOT_FOUND(120, "Card acceptable for transfer not found"),
        CONTRAGENT_NOT_FOUND(130, "Contragent not found"),
        PAYMENT_ALREADY_REGISTERED(140, "Payment is already registered"),
        PAYMENT_NOT_FOUND(300, "Payment not found");

        private final int code;
        private final String description;

        private PaymentProcessResult(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(Processor.class);
    private static final int RESPONSE_MENU_PERIOD_IN_DAYS = 7;
    private final SessionFactory persistenceSessionFactory;
    private final EventNotificator eventNotificator;

    public Processor(SessionFactory persistenceSessionFactory, EventNotificator eventNotificator) {
        this.persistenceSessionFactory = persistenceSessionFactory;
        this.eventNotificator = eventNotificator;
    }

    public SyncResponse processSyncRequest(SyncRequest request) throws Exception {
        Date syncStartTime = new Date();
        int syncResult = 0;

        Long idOfPacket = null, idOfSync = null; // регистируются и заполняются только для полной синхронизации

        SyncResponse.ResPaymentRegistry resPaymentRegistry = null;
        SyncResponse.AccRegistry accRegistry = null;
        SyncResponse.AccIncRegistry accIncRegistry = null;
        SyncResponse.ClientRegistry clientRegistry = null;
        SyncResponse.ResOrgStructure resOrgStructure = null;
        SyncResponse.ResMenuExchangeData resMenuExchange = null;
        SyncResponse.ResDiary resDiary = null;
        SyncResponse.ResEnterEvents resEnterEvents = null;
        SyncResponse.ResLibraryData resLibraryData = null;
        SyncResponse.ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = null;
        SyncResponse.CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry = null;
        try {
            if (request.getType() == SyncRequest.TYPE_FULL) {
                // Generate IdOfPacket
                idOfPacket = generateIdOfPacket(request.getIdOfOrg());
                // Register sync history
                idOfSync = addSyncHistory(request.getIdOfOrg(), idOfPacket, syncStartTime);

                // Process paymentRegistry
                resPaymentRegistry = processSyncPaymentRegistry(idOfSync, request.getIdOfOrg(),
                        request.getPaymentRegistry());

                // Process ClientParamRegistry
                try {
                    processSyncClientParamRegistry(idOfSync, request.getIdOfOrg(), request.getClientParamRegistry());
                } catch (Exception e) {
                    logger.error(
                            String.format("Failed to process ClientParamRegistry, IdOfOrg == %s", request.getIdOfOrg()),
                            e);
                }

                // Build client registry
                try {
                    clientRegistry = processSyncClientRegistry(request.getIdOfOrg(),
                            request.getClientRegistryRequest());
                } catch (Exception e) {
                    logger.error(String.format("Failed to build ClientRegistry, IdOfOrg == %s", request.getIdOfOrg()),
                            e);
                }

                // Process OrgStructure
                try {
                    resOrgStructure = processSyncOrgStructure(request.getIdOfOrg(), request.getOrgStructure());
                } catch (Exception e) {
                    resOrgStructure = new SyncResponse.ResOrgStructure(1, "Unexpected error");
                    logger.error(String.format("Failed to process OrgStructure, IdOfOrg == %s", request.getIdOfOrg()),
                                                e);
                }

                // Process menu from Org
                try {
                    processSyncMenu(request.getIdOfOrg(), request.getReqMenu());
                } catch (Exception e) {
                    logger.error(String.format("Failed to process menu, IdOfOrg == %s", request.getIdOfOrg()), e);
                }

                // Copy menu from Contragent (currentTime <= date < currentTime + RESPONSE_MENU_PERIOD_IN_DAYS days) to response
                /*try {
                    resMenu = getContragentMenu(request.getIdOfOrg(), syncStartTime,
                            DateUtils.addDays(syncStartTime, RESPONSE_MENU_PERIOD_IN_DAYS));
                } catch (Exception e) {
                    logger.error(String.format("Failed to build menu, IdOfOrg == %s", request.getIdOfOrg()), e);
                } */
                try {
                    resMenuExchange = getMenuExchangeData(request.getIdOfOrg(), syncStartTime,
                            DateUtils.addDays(syncStartTime, RESPONSE_MENU_PERIOD_IN_DAYS));
                } catch (Exception e) {
                    logger.error(String.format("Failed to build menu, IdOfOrg == %s", request.getIdOfOrg()), e);
                }


                // Build AccRegistry
                try {
                    accRegistry = getAccRegistry(request.getIdOfOrg());
                } catch (Exception e) {
                    logger.error(String.format("Failed to build AccRegistry, IdOfOrg == %s", request.getIdOfOrg()), e);
                    accRegistry = new SyncResponse.AccRegistry();
                }

                // Process ReqDiary
                try {
                    resDiary = processSyncDiary(request.getIdOfOrg(), request.getReqDiary());
                } catch (Exception e) {
                    resDiary = new SyncResponse.ResDiary(1, "Unexpected error");
                }

                // Process enterEvents
                try {
                    if (request.getEnterEvents() != null)
                        resEnterEvents = processSyncEnterEvents(request.getEnterEvents());
                } catch (Exception e) {
                    logger.error(String.format("Failed to process enter events, IdOfOrg == %s", request.getIdOfOrg()),
                            e);
                }

                // Process library data
                try {
                    if (request.getEnterEvents() != null)
                        resLibraryData = processSyncLibraryData(request.getLibraryData());
                } catch (Exception e) {
                    logger.error(String.format("Failed to process library data, IdOfOrg == %s", request.getIdOfOrg()),
                            e);
                }

                // Process ResCategoriesDiscountsAndRules
                try {
                    resCategoriesDiscountsAndRules = processCategoriesDiscountsAndRules(request.getIdOfOrg());
                } catch (Exception e) {
                    logger.error(String.format("Failed to process categories and rules, IdOfOrg == %s",
                            request.getIdOfOrg()), e);
                }

                // Process CorrectingNumbersOrdersRegistry
                try {
                    correctingNumbersOrdersRegistry = processSyncCorrectingNumbersOrdersRegistry(request.getIdOfOrg());
                } catch (Exception e) {
                    logger.error(String.format("Failed to process numbers of Orders, IdOfOrg == %s",
                            request.getIdOfOrg()), e);
                }

            } else if (request.getType() == SyncRequest.TYPE_GET_ACC_INC) {
                // запрос на получение пополнений
                try {
                    accIncRegistry = getAccIncRegistry(request.getIdOfOrg(),
                            request.getAccIncRegistryRequest().dateTime);
                } catch (Exception e) {
                    logger.error(String.format("Failed to build AccIncRegistry, IdOfOrg == %s", request.getIdOfOrg()),
                            e);
                    accIncRegistry = new SyncResponse.AccIncRegistry();
                    accIncRegistry.setDate(request.getAccIncRegistryRequest().dateTime);
                }

                // Process enterEvents
                try {
                    if (request.getEnterEvents() != null)
                        resEnterEvents = processSyncEnterEvents(request.getEnterEvents());
                } catch (Exception e) {
                    logger.error(String.format("Failed to process enter events, IdOfOrg == %s", request.getIdOfOrg()),
                            e);
                }
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to perform synchronization, IdOfOrg == %s", request.getIdOfOrg()), e);
            syncResult = 1;
        }

        Date syncEndTime = new Date();
        if (request.getType() == SyncRequest.TYPE_FULL) {
            // Update sync history - store sync end time and sync result
            updateSyncHistory(idOfSync, syncResult, syncEndTime);
        }

        // Build and return response
        SyncResponse response = new SyncResponse(request.getType(), request.getIdOfOrg(), request.getOrg().getShortName(), idOfPacket,
                request.getProtoVersion(), syncEndTime, "", accRegistry, resPaymentRegistry, accIncRegistry,
                clientRegistry, resOrgStructure, resMenuExchange, resDiary, "", resEnterEvents, resLibraryData,
                resCategoriesDiscountsAndRules, correctingNumbersOrdersRegistry);
        if (request.getType() == SyncRequest.TYPE_FULL) {
            eventNotificator.fire(new SyncEvent.RawEvent(syncStartTime, request, response));
        }
        return response;
    }

    public PaymentResponse processPayRequest(PaymentRequest request) throws Exception {
        Date syncStartTime = new Date();
        int syncResult = 0;

        //todo Generate IdOfPacket
        //Long idOfPacket = generateIdOfPacket(request.getIdOfOrg());
        Long idOfPacket = 0L;

        //todo Register sync history
        //Long idOfSync = addSyncHistory(request.getIdOfOrg(), idOfPacket, syncStartTime);

        checkUserPaymentProcessRights(request.getIdOfUser());
        PaymentResponse.ResPaymentRegistry resPaymentRegistry = null;
        try {
            // Process paymentRegistry
            resPaymentRegistry = processPayPaymentRegistry(request.getIdOfContragent(), request.getPaymentRegistry());
        } catch (Exception e) {
            logger.error(String.format("Failed to perform synchronization, IdOfContragent == %s",
                    request.getIdOfContragent()), e);
            syncResult = 1;
        }

        Date syncEndTime = new Date();
        // Update sync history - store sync end time and sync result
        //todo updateSyncHistory(idOfSync, syncResult, syncEndTime);

        // Build and return full respone
        PaymentResponse response = new PaymentResponse(request.getIdOfContragent(), idOfPacket, request.getVersion(),
                syncEndTime, resPaymentRegistry);

        eventNotificator.fire(new PaymentProcessEvent.RawEvent(syncStartTime, request, response));
        return response;
    }

    private void checkUserPaymentProcessRights(Long idOfUser) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            User user = DAOUtils.findUser(persistenceSession, idOfUser);
            if (null == user) {
                throw new AccessDiniedException();
            }
            if (!user.hasFunction(persistenceSession, Function.FUNC_PAY_PROCESS)) {
                throw new AccessDiniedException();
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private SyncResponse.ResPaymentRegistry processSyncPaymentRegistry(Long idOfSync, Long idOfOrg,
            SyncRequest.PaymentRegistry paymentRegistry) throws Exception {
        SyncResponse.ResPaymentRegistry resPaymentRegistry = new SyncResponse.ResPaymentRegistry();
        Enumeration<SyncRequest.PaymentRegistry.Payment> payments = paymentRegistry.getPayments();
        while (payments.hasMoreElements()) {
            SyncRequest.PaymentRegistry.Payment payment = payments.nextElement();
            SyncResponse.ResPaymentRegistry.Item resAcc;
            try {
                resAcc = processSyncPaymentRegistryPayment(idOfSync, idOfOrg, payment);
                if (resAcc.getResult()!=0) {
                    logger.error("Failure in response payment registry: "+resAcc);
                }
            } catch (Exception e) {
                logger.error(String.format("Failed to process payment == %s", payment), e);
                resAcc = new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 100, "Internal error");
            }
            resPaymentRegistry.addItem(resAcc);
        }
        return resPaymentRegistry;
    }

    private PaymentResponse.ResPaymentRegistry processPayPaymentRegistry(Long idOfContragent,
            PaymentRequest.PaymentRegistry paymentRegistry) throws Exception {
        PaymentResponse.ResPaymentRegistry resPaymentRegistry = new PaymentResponse.ResPaymentRegistry();
        Enumeration<PaymentRequest.PaymentRegistry.Payment> payments = paymentRegistry.getPayments();
        while (payments.hasMoreElements()) {
            PaymentRequest.PaymentRegistry.Payment payment = payments.nextElement();
            PaymentResponse.ResPaymentRegistry.Item resAcc;
            try {
                resAcc = processPayPaymentRegistryPayment(idOfContragent, payment);
            } catch (Exception e) {
                logger.error(String.format("Failed to process payment == %s", payment), e);
                resAcc = new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null,
                        PaymentProcessResult.UNKNOWN_ERROR.getCode(),
                        PaymentProcessResult.UNKNOWN_ERROR.getDescription());
            }
            resPaymentRegistry.addItem(resAcc);
        }
        return resPaymentRegistry;
    }

    private static SyncRequest.PaymentRegistry.Payment.Purchase findPurchase(
            SyncRequest.PaymentRegistry.Payment payment, Long idOfOrderDetail) throws Exception {
        Enumeration<SyncRequest.PaymentRegistry.Payment.Purchase> purchases = payment.getPurchases();
        while (purchases.hasMoreElements()) {
            SyncRequest.PaymentRegistry.Payment.Purchase purchase = purchases.nextElement();
            if (idOfOrderDetail.equals(purchase.getIdOfOrderDetail())) {
                return purchase;
            }
        }
        return null;
    }

    private static void updateOrderDetails(Session session, Order order, SyncRequest.PaymentRegistry.Payment payment)
            throws Exception {
        Set<OrderDetail> orderDetails = order.getOrderDetails();
        for (OrderDetail orderDetail : orderDetails) {
            if (StringUtils.isEmpty(orderDetail.getRootMenu())) {
                SyncRequest.PaymentRegistry.Payment.Purchase purchase = findPurchase(payment,
                        orderDetail.getCompositeIdOfOrderDetail().getIdOfOrderDetail());
                if (null != purchase) {
                    String rootMenu = purchase.getRootMenu();
                    if (StringUtils.isNotEmpty(rootMenu)) {
                        orderDetail.setRootMenu(rootMenu);
                        session.update(orderDetail);
                    }
                }
            }
        }
    }

    private SyncResponse.ResPaymentRegistry.Item processSyncPaymentRegistryPayment(Long idOfSync, Long idOfOrg,
            SyncRequest.PaymentRegistry.Payment payment) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            //SyncHistory syncHistory = (SyncHistory) persistenceSession.load(SyncHistory.class, idOfSync);
            Org organization = DAOUtils.findOrg(persistenceSession, idOfOrg);
            if (null == organization) {
                return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 130,
                        String.format("Organization no found, IdOfOrg == %s, IdOfOrder == %s", idOfOrg,
                                payment.getIdOfOrder()));
            }
            // Check order existence
            Order order = DAOUtils
                    .findOrder(persistenceSession, new CompositeIdOfOrder(idOfOrg, payment.getIdOfOrder()));
            if (null != order) {
                // if order == payment (may be last sync result was not transferred to client)
                Long orderCardNo = order.getCard() == null ? null : order.getCard().getCardNo();
                if ((("" + orderCardNo).equals("" + payment.getCardNo()))
                        && (order.getCreateTime().equals(payment.getTime())) && (order.getSumByCard()
                        .equals(payment.getSumByCard()))) {
                    return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 0,
                            "Order is already registered");
                } else {
                    return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 110, String.format(
                            "Order already registered but attributes differ, IdOfOrg == %s, IdOfOrder == %s", idOfOrg,
                            payment.getIdOfOrder()));
                }
                /* updateOrderDetails(persistenceSession, order, payment);
                // Commit data model transaction
                persistenceSession.flush();
                persistenceTransaction.commit();
                persistenceTransaction = null;
                return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 110,
                        String.format("Order is already registered, IdOfOrg == %s, IdOfOrder == %s", idOfOrg,
                                payment.getIdOfOrder()));
                                */
            }
            // If cardNo specified - load card from data model
            Card card = null;
            Long cardNo = payment.getCardNo();
            if (null != cardNo) {
                card = DAOUtils.findCardByCardNo(persistenceSession, cardNo);
                if (null == card) {
                    return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 200,
                            String.format("Unknown card, IdOfOrg == %s, IdOfOrder == %s, CardNo == %s", idOfOrg,
                                    payment.getIdOfOrder(), cardNo));
                }
            }
            // If client specified - load client from data model
            Client client = null;
            Long idOfClient = payment.getIdOfClient();
            if (null != idOfClient) {
                client = DAOUtils.findClient(persistenceSession, idOfClient);
                // Check client existance
                if (null == client) {
                    return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 210,
                            String.format("Unknown client, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s", idOfOrg,
                                    payment.getIdOfOrder(), idOfClient));
                }
            }
            if (null != card) {
                if (null == client) {
                    // Client is specified if card is specified
                    client = card.getClient();
                } else if (!card.getClient().getIdOfClient().equals(client.getIdOfClient())) {
                    // Specified client isn't the owner of the specified card
                    return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 230, String.format(
                            "Client isn't the owner of the specified card, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s, CardNo == %s",
                            idOfOrg, payment.getIdOfOrder(), idOfClient, cardNo));
                }
            }
            if (null != client && card != null) {
                if (Card.ACTIVE_STATE != card.getState()) {
                    Card newCard = client.findActiveCard(persistenceSession, card);
                    if (logger.isWarnEnabled()) {
                        if (!newCard.getIdOfCard().equals(card.getIdOfCard())) {
                            logger.warn(
                                    String.format("Specified card is inactive. Client: %s, Card: %s. Will use card: %s",
                                            client.toString(), card.toString(), newCard.toString()));
                        }
                    }
                    card = newCard;
                }
            }
            // If client is specified - check if client is registered for the specified organization
            if (null != client) {
                if (!client.getOrg().getIdOfOrg().equals(idOfOrg)) {
                    return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 220, String.format(
                            "Client isn't registered for the specified organization, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s",
                            idOfOrg, payment.getIdOfOrder(), idOfClient));
                }
            }
            // Card check
            if (null != card) {
                //if (Card.ACTIVE_STATE != card.getState()) {
                //    return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 320, String.format(
                //            "Card is locked, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s, CardNo == %s", idOfOrg,
                //            payment.getIdOfOrder(), idOfClient, cardNo));
                //}
                //if (null == card.getIssueTime()) {
                //    return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 330, String.format(
                //            "Card wasn't issued, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s, CardNo == %s",
                //            idOfOrg, payment.getIdOfOrder(), idOfClient, cardNo));
                //}
                //if (payment.getTime().before(card.getIssueTime())) {
                //    return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 340, String.format(
                //            "Card wasn't issued at payment time, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s, CardNo == %s",
                //            idOfOrg, payment.getIdOfOrder(), idOfClient, cardNo));
                //}
                //if (!payment.getTime().before(card.getValidTime())) {
                //    return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 350, String.format(
                //            "Card wasn't valid at payment time, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s, CardNo == %s",
                //            idOfOrg, payment.getIdOfOrder(), idOfClient, cardNo));
                //}
            }
            // Verify spicified sums to be valid non negative numbers
            if (payment.getSumByCard() < 0 || payment.getSumByCash() < 0 || payment.getSocDiscount() < 0
                    || payment.getRSum() < 0 || payment.getGrant() < 0) {
                return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 250,
                        String.format("Negative sum(s) are specified, IdOfOrg == %s, IdOfOrder == %s", idOfOrg,
                                payment.getIdOfOrder()));
            }
            if (0 != payment.getSumByCard() && card==null) {
                // Check if card is specified
                return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 240, String.format(
                        "Payment has card part but doesn't specify CardNo, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s",
                        idOfOrg, payment.getIdOfOrder(), idOfClient));
            }
            // Create order
            RuntimeContext.getFinancialOpsManager().createOrderCharge(persistenceSession, payment, idOfOrg, client,
                    card);
            long totalPurchaseDiscount = 0;
            long totalPurchaseRSum = 0;
            // Register order details (purchase)
            Enumeration<SyncRequest.PaymentRegistry.Payment.Purchase> purchases = payment.getPurchases();
            while (purchases.hasMoreElements()) {
                SyncRequest.PaymentRegistry.Payment.Purchase purchase = purchases.nextElement();
                if (null != DAOUtils.findOrderDetail(persistenceSession,
                        new CompositeIdOfOrderDetail(idOfOrg, purchase.getIdOfOrderDetail()))) {
                    return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 120, String.format(
                            "Order detail is already registered, IdOfOrg == %s, IdOfOrder == %s, IdOfOrderDetail == %s",
                            idOfOrg, payment.getIdOfOrder(), purchase.getIdOfOrderDetail()));
                }
                if (purchase.getDiscount() < 0 || purchase.getRPrice() < 0 || purchase.getQty() < 0) {
                    return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 250, String.format(
                            "Negative sum(s) or quantitiy are specified, IdOfOrg == %s, IdOfOrder == %s, IdOfPurchase == %s",
                            idOfOrg, payment.getIdOfOrder(), purchase.getQty()));
                }
                OrderDetail orderDetail = new OrderDetail(
                        new CompositeIdOfOrderDetail(idOfOrg, purchase.getIdOfOrderDetail()), payment.getIdOfOrder(),
                        purchase.getQty(), purchase.getDiscount(), purchase.getSocDiscount(), purchase.getRPrice(),
                        purchase.getName(), purchase.getRootMenu(), purchase.getMenuGroup(), purchase.getMenuOrigin(),
                        purchase.getMenuOutput(), purchase.getType());
                persistenceSession.save(orderDetail);
                totalPurchaseDiscount += purchase.getDiscount() * purchase.getQty();
                totalPurchaseRSum += purchase.getRPrice() * purchase.getQty();
            }
            // Check payment sums
            if (totalPurchaseRSum != payment.getRSum() || totalPurchaseDiscount != payment
                    .getSocDiscount() + payment.getTrdDiscount() + payment.getGrant()) {
                return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 300,
                        String.format("Invalid total sum by order, IdOfOrg == %s, IdOfOrder == %s", idOfOrg,
                                payment.getIdOfOrder()));
            }
            if (payment.getRSum() != payment.getSumByCard() + payment.getSumByCash()) {
                return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 310,
                        String.format("Invalid sum of order card and cash payments, IdOfOrg == %s, IdOfOrder == %s",
                                idOfOrg, payment.getIdOfOrder()));
            }

            // Commit data model transaction
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            // Return no errors
            return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 0, null);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private static Client findPaymentClient(Session persistenceSession, Contragent contragent, Long contractId,
            Long clientId) throws Exception {
        if (clientId != null) {
            return DAOUtils.findClient(persistenceSession, clientId);
        }
        // Извлекаем из модели данных клиента, на карту которого необходимо перевести платеж
        // Если необходимо преобразовать номер счета, то делаем это
        if (contragent.getNeedAccountTranslate()) {
            ContragentClientAccount contragentClientAccount = DAOUtils.findContragentClientAccount(persistenceSession,
                    new CompositeIdOfContragentClientAccount(contragent.getIdOfContragent(), contractId));
            if (null != contragentClientAccount) {
                return contragentClientAccount.getClient();
                //return DAOUtils.findClientByContractId(persistenceSession, contractId);
                //return null;
            }
        } //else {
        return DAOUtils.findClientByContractId(persistenceSession, contractId);
        ///}
    }

    public synchronized PaymentResponse.ResPaymentRegistry.Item processPayPaymentRegistryPayment(Long idOfContragent,
            PaymentRequest.PaymentRegistry.Payment payment) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Contragent contragent = DAOUtils.findContragent(persistenceSession, idOfContragent);
            if (null == contragent) {
                return new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null,
                        PaymentProcessResult.CONTRAGENT_NOT_FOUND.getCode(),
                        String.format("%s. IdOfContragent == %s, ContractId == %s",
                                PaymentProcessResult.CONTRAGENT_NOT_FOUND.getDescription(), idOfContragent,
                                payment.getContractId()));
            }
            if (DAOUtils.existClientPayment(persistenceSession, contragent, payment.getIdOfPayment())) {
                return new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null,
                        PaymentProcessResult.PAYMENT_ALREADY_REGISTERED.getCode(),
                        String.format("%s. IdOfContragent == %s, IdOfPayment == %s",
                                PaymentProcessResult.PAYMENT_ALREADY_REGISTERED.getDescription(), idOfContragent,
                                payment.getIdOfPayment()));
            }
            Client client = findPaymentClient(persistenceSession, contragent, payment.getContractId(),
                    payment.getClientId());
            if (null == client) {
                return new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null,
                        PaymentProcessResult.CLIENT_NOT_FOUND.getCode(),
                        String.format("%s. IdOfContragent == %s, ContractId == %s, ClientId == %s",
                                PaymentProcessResult.CLIENT_NOT_FOUND.getDescription(), idOfContragent,
                                payment.getContractId(), payment.getClientId()));
            }
            Long idOfClient = client.getIdOfClient();
            //Card paymentCard = client.findActiveCard(persistenceSession, null);
            /*if (null == paymentCard) {
                return new PaymentResponse.ResPaymentRegistry.Item(payment, idOfClient, null, client.getBalance(),
                        PaymentProcessResult.CARD_NOT_FOUND.getCode(),
                        String.format("%s. IdOfContragent == %s, ContractId == %s",
                                PaymentProcessResult.CARD_NOT_FOUND.getDescription(), idOfContragent,
                                payment.getContractId()), client);
            } */
            if (!payment.isCheckOnly()) {
                long paymentSum = payment.getSum();
                if (payment.isResetBalance()) {
                    paymentSum -= client.getBalance();
                    logger.info("Processing payment with balance reset: " + client + "; current balance="
                            + client.getBalance() + "; set balance=" + paymentSum);
                }
                RuntimeContext.getFinancialOpsManager().createClientPayment(persistenceSession, client,
                        payment.getPaymentMethod(), paymentSum, ClientPayment.CLIENT_TO_ACCOUNT_PAYMENT,
                        payment.getPayTime(), payment.getIdOfPayment(), contragent, payment.getAddPaymentMethod(),
                        payment.getAddIdOfPayment());

                persistenceSession.flush();
            }
            PaymentResponse.ResPaymentRegistry.Item result = new PaymentResponse.ResPaymentRegistry.Item(payment,
                    idOfClient, client.getContractId(), null, client.getBalance(),
                    PaymentProcessResult.OK.getCode(), PaymentProcessResult.OK.getDescription(), client, null);

            persistenceTransaction.commit();
            persistenceTransaction = null;
            return result;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private void processSyncClientParamRegistry(Long idOfSync, Long idOfOrg,
            SyncRequest.ClientParamRegistry clientParamRegistry) throws Exception {
        Enumeration<SyncRequest.ClientParamRegistry.ClientParamItem> clientParamItems = clientParamRegistry
                .getPayments();
        while (clientParamItems.hasMoreElements()) {
            SyncRequest.ClientParamRegistry.ClientParamItem clientParamItem = clientParamItems.nextElement();
            try {
                processSyncClientParamRegistryItem(idOfSync, idOfOrg, clientParamItem);
            } catch (Exception e) {
                logger.error(String.format("Failed to process clientParamItem == %s", clientParamItem), e);
            }
        }
    }

    private void processSyncClientParamRegistryItem(Long idOfSync, Long idOfOrg,
            SyncRequest.ClientParamRegistry.ClientParamItem clientParamItem) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client client = DAOUtils.findClient(persistenceSession, clientParamItem.getIdOfClient());
            if (!client.getOrg().getIdOfOrg().equals(idOfOrg)) {
                throw new IllegalArgumentException("Client from another organization");
            }
            client.setFreePayCount(clientParamItem.getFreePayCount());
            client.setFreePayMaxCount(clientParamItem.getFreePayMaxCount());
            client.setLastFreePayTime(clientParamItem.getLastFreePayTime());
            client.setDiscountMode(clientParamItem.getDiscountMode());
            if (clientParamItem.getAddress()!=null) client.setAddress(clientParamItem.getAddress());
            if (clientParamItem.getEmail()!=null) client.setEmail(clientParamItem.getEmail());
            if (clientParamItem.getMobilePhone()!=null) client.setMobile(clientParamItem.getMobilePhone());
            if (clientParamItem.getName()!=null) client.getPerson().setFirstName(clientParamItem.getName());
            if (clientParamItem.getPhone()!=null) client.setPhone(clientParamItem.getPhone());
            if (clientParamItem.getSecondName()!=null) client.getPerson().setSecondName(clientParamItem.getSecondName());
            if (clientParamItem.getSurname()!=null) client.getPerson().setSurname(clientParamItem.getSurname());
            if (clientParamItem.getRemarks()!=null) client.setRemarks(clientParamItem.getRemarks());

            /* распарсим строку с категориями */
            if(clientParamItem.getCategoriesDiscounts()!= null){
                String categories = clientParamItem.getCategoriesDiscounts();
                client.setCategoriesDiscounts(categories);
                if(!categories.equals("")){
                    String[] catArray = categories.split(",");
                    List<Long> idOfCategoryDiscount = new ArrayList<Long>();
                    for (String number: catArray){
                        idOfCategoryDiscount.add(Long.parseLong(number.trim()));
                    }
                    Criteria categoryDiscountCriteria = persistenceSession.createCriteria(CategoryDiscount.class);
                    categoryDiscountCriteria.add(Restrictions.in("idOfCategoryDiscount",idOfCategoryDiscount));
                    Set<CategoryDiscount> categoryDiscountSet = new HashSet<CategoryDiscount>();
                    for(Object object: categoryDiscountCriteria.list()){
                        categoryDiscountSet.add((CategoryDiscount) object);
                    }
                    client.setCategories(categoryDiscountSet);
                }
            }

            persistenceSession.update(client);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    // 12-01-2012 Kadyrov D.I.

    private SyncResponse.CorrectingNumbersOrdersRegistry processSyncCorrectingNumbersOrdersRegistry (Long idOfOrg) throws Exception{
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try{
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria orderCriteria = persistenceSession.createCriteria(Order.class);
            orderCriteria.add(Restrictions.eq("org.idOfOrg",idOfOrg));
            orderCriteria.setProjection(Projections.max("compositeIdOfOrder.idOfOrder"));
            List orderMax=orderCriteria.list();
            Criteria orderDetailCriteria = persistenceSession.createCriteria(OrderDetail.class);
            orderDetailCriteria.add(Restrictions.eq("org.idOfOrg",idOfOrg));
            orderDetailCriteria.setProjection(Projections.max("compositeIdOfOrderDetail.idOfOrderDetail"));
            List orderDetailMax=orderDetailCriteria.list();

            Criteria enterEventCriteria = persistenceSession.createCriteria(EnterEvent.class);
            enterEventCriteria.add(Restrictions.eq("org.idOfOrg",idOfOrg));
            enterEventCriteria.setProjection(Projections.max("compositeIdOfEnterEvent.idOfEnterEvent"));
            List enterEventMax = enterEventCriteria.list();

            persistenceTransaction.commit();
            persistenceTransaction = null;
            Long idOfOrderMax = (Long) orderMax.get(0),
                 idOfOrderDetail = (Long) orderDetailMax.get(0),
                 idOfEnterEvent = (Long) enterEventMax.get(0);
            if (idOfOrderMax == null) idOfOrderMax = 0L;
            if (idOfOrderDetail == null) idOfOrderDetail = 0L;
            if  (idOfEnterEvent == null) idOfEnterEvent = 0L;
            return new SyncResponse.CorrectingNumbersOrdersRegistry(idOfOrderMax, idOfOrderDetail, idOfEnterEvent);
            //return null;
        }  finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private SyncResponse.ResOrgStructure processSyncOrgStructure(Long idOfOrg, SyncRequest.OrgStructure reqStructure)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org organization = DAOUtils.findOrg(persistenceSession, idOfOrg);
            // Ищем "лишние" группы
            List<ClientGroup> superfluousClientGroups = new LinkedList<ClientGroup>();
            for (ClientGroup clientGroup : organization.getClientGroups()) {
                if (clientGroup.isTemporaryGroup()) {
                    // добавляем временную группу в список для удаления если она уже есть в постоянных
                    if (findClientGroupByName(clientGroup.getGroupName(), reqStructure))
                        superfluousClientGroups.add(clientGroup);
                } else {
                    if (!findClientGroupById(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup(), reqStructure)) superfluousClientGroups.add(clientGroup);
                }
            }
            // Удаляем "лишние" группы
            for (ClientGroup clientGroup : superfluousClientGroups) {
                // Отсоединяем группу от организации                
                organization.removeClientGroup(clientGroup);
                // Удаляем из группы всех ее клиентов
                for (Client client : clientGroup.getClients()) {
                    client.setIdOfClientGroup(null);
                    client.setUpdateTime(new Date());
                    persistenceSession.update(client);
                }
                // Удаляем группу из БД
                persistenceSession.delete(clientGroup);
            }
            // Добавляем и обновляем группы согласно запроса
            Enumeration<SyncRequest.OrgStructure.Group> reqGroups = reqStructure.getGroups();
            while (reqGroups.hasMoreElements()) {
                SyncRequest.OrgStructure.Group reqGroup = reqGroups.nextElement();
                try {
                    processSyncOrgStructureGroup(persistenceSession, organization, reqGroup);
                } catch (Exception e) {
                    return new SyncResponse.ResOrgStructure(2, String.format("Failed to process: %s", reqGroup));
                }
            }

            //persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return new SyncResponse.ResOrgStructure();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private static boolean findClientGroupById(long idOfClientGroup, SyncRequest.OrgStructure reqStructure) throws Exception {
        Enumeration<SyncRequest.OrgStructure.Group> reqGroups = reqStructure.getGroups();
        while (reqGroups.hasMoreElements()) {
            if (idOfClientGroup==reqGroups.nextElement().getIdOfGroup()) {
                return true;
            }
        }
        return false;
    }
    private static boolean findClientGroupByName(String groupName, SyncRequest.OrgStructure reqStructure) throws Exception {
        Enumeration<SyncRequest.OrgStructure.Group> reqGroups = reqStructure.getGroups();
        while (reqGroups.hasMoreElements()) {
            if (groupName.equals(reqGroups.nextElement().getName())) {
                return true;
            }
        }
        return false;
    }

    private void processSyncOrgStructureGroup(Session persistenceSession, Org organization,
            SyncRequest.OrgStructure.Group reqGroup) throws Exception {
        CompositeIdOfClientGroup compositeIdOfClientGroup = new CompositeIdOfClientGroup(organization.getIdOfOrg(),
                reqGroup.getIdOfGroup());
        ClientGroup clientGroup = DAOUtils.findClientGroup(persistenceSession, compositeIdOfClientGroup);
        if (null != clientGroup) {
            clientGroup.setGroupName(reqGroup.getName());
            persistenceSession.update(clientGroup);
        } else {
            clientGroup = new ClientGroup(compositeIdOfClientGroup, reqGroup.getName());
            persistenceSession.save(clientGroup);
        }

        // Ищем "лишних" клиентов
        List<Client> superfluousClients = new LinkedList<Client>();
        for (Client client : clientGroup.getClients()) {
            if (!find(client, reqGroup)) {
                superfluousClients.add(client);
            }
        }
        // Убираем из группы "лишних" клиентов        
        for (Client client : superfluousClients) {
            client.setIdOfClientGroup(null);
            client.setUpdateTime(new Date());
            persistenceSession.update(client);
            clientGroup.removeClient(client);
        }

        // Добавляем в группу клиентов согласно запросу
        Enumeration<Long> reqClients = reqGroup.getClients();
        while (reqClients.hasMoreElements()) {
            Long idOfClient = reqClients.nextElement();
            Client client = DAOUtils.findClient(persistenceSession, idOfClient);
            if (null == client) {
                logger.info(String.format("Client with IdOfClient == %s not found", idOfClient));
            } else if (!client.getOrg().getIdOfOrg().equals(organization.getIdOfOrg())) {
                logger.error(String.format(
                        "Client with IdOfClient == %s belongs to other organization. Client: %s, IdOfOrg by request: %s",
                        idOfClient, client, organization.getIdOfOrg()));
            } else {
                ClientGroup curClientGroup = client.getClientGroup();
                if (null == curClientGroup || !curClientGroup.getCompositeIdOfClientGroup()
                        .equals(clientGroup.getCompositeIdOfClientGroup())) {
                    client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                    client.setUpdateTime(new Date());
                    clientGroup.addClient(client);
                    persistenceSession.update(client);
                    persistenceSession.update(clientGroup);
                }
            }
        }
    }

    private static boolean find(Client client, SyncRequest.OrgStructure.Group reqGroup) throws Exception {
        Enumeration<Long> reqClients = reqGroup.getClients();
        while (reqClients.hasMoreElements()) {
            if (client.getIdOfClient().equals(reqClients.nextElement())) {
                return true;
            }
        }
        return false;
    }

    private Long addSyncHistory(Long idOfOrg, Long idOfPacket, Date startTime) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org organization = DAOUtils.getOrgReference(persistenceSession, idOfOrg);
            SyncHistory syncHistory = new SyncHistory(organization, startTime, idOfPacket);
            persistenceSession.save(syncHistory);
            Long idOfSync = syncHistory.getIdOfSync();

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return idOfSync;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private void updateSyncHistory(Long idOfSync, int syncResult, Date endTime) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            SyncHistory syncHistory = DAOUtils.getSyncHistoryReference(persistenceSession, idOfSync);
            syncHistory.setSyncEndTime(endTime);
            syncHistory.setSyncResult(syncResult);
            persistenceSession.update(syncHistory);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private SyncResponse.AccRegistry getAccRegistry(Long idOfOrg) throws Exception {
        SyncResponse.AccRegistry accRegistry = new SyncResponse.AccRegistry();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            for (Object[] v : DAOUtils.getClientsAndCardsForOrg(persistenceSession, idOfOrg)) {
                accRegistry.addItem(new SyncResponse.AccRegistry.Item((Client) v[0], (Card) v[1]));
            }

            /*Org organization = DAOUtils.getOrgReference(persistenceSession, idOfOrg);
            for (Client client : organization.getClients()) {
                for (Card card : client.getCards()) {
                    accRegistry.addItem(new SyncResponse.AccRegistry.Item(card));
                }
            } */

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return accRegistry;
    }

    private SyncResponse.AccIncRegistry getAccIncRegistry(Long idOfOrg, Date fromDateTime) throws Exception {
        SyncResponse.AccIncRegistry accIncRegistry = new SyncResponse.AccIncRegistry();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Date currentDate = new Date();
            for (AccountTransaction accountTransaction : DAOUtils
                    .getAccountTransactionsForOrgSinceTime(persistenceSession, idOfOrg, fromDateTime, currentDate,
                            AccountTransaction.PAYMENT_SYSTEM_TRANSACTION_SOURCE_TYPE)) {
                SyncResponse.AccIncRegistry.Item accIncItem = new SyncResponse.AccIncRegistry.Item(
                        accountTransaction.getIdOfTransaction(), accountTransaction.getClient().getIdOfClient(),
                        accountTransaction.getTransactionTime(), accountTransaction.getTransactionSum());
                accIncRegistry.addItem(accIncItem);
            }
            accIncRegistry.setDate(currentDate);

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return accIncRegistry;
    }


    private SyncResponse.ClientRegistry processSyncClientRegistry(Long idOfOrg,
            SyncRequest.ClientRegistryRequest clientRegistryRequest) throws Exception {
        SyncResponse.ClientRegistry clientRegistry = new SyncResponse.ClientRegistry();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org organization = DAOUtils.getOrgReference(persistenceSession, idOfOrg);
            List clients = DAOUtils
                    .findNewerClients(persistenceSession, organization, clientRegistryRequest.getCurrentVersion());
            for (Object object : clients) {
                Client client = (Client) object;
                clientRegistry.addItem(new SyncResponse.ClientRegistry.Item(client));
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return clientRegistry;
    }

    private void processSyncMenu(Long idOfOrg, SyncRequest.ReqMenu reqMenu) throws Exception {
        if (null != reqMenu) {
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = persistenceSessionFactory.openSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                Org organization = DAOUtils.getOrgReference(persistenceSession, idOfOrg);

                boolean bOrgIsMenuExchangeSource = isOrgMenuExchangeSource(persistenceSession, idOfOrg);

                /// сохраняем секцию Settings
                if (bOrgIsMenuExchangeSource && reqMenu.getSettingsSectionRawXML() != null) {
                    MenuExchange menuExchangeSettings = new MenuExchange(new Date(0), idOfOrg,
                            reqMenu.getSettingsSectionRawXML(), MenuExchange.FLAG_SETTINGS);
                    persistenceSession.saveOrUpdate(menuExchangeSettings);
                }

                Enumeration<SyncRequest.ReqMenu.Item> menuItems = reqMenu.getItems();
                boolean bFirstMenuItem = true;
                while (menuItems.hasMoreElements()) {
                    SyncRequest.ReqMenu.Item item = menuItems.nextElement();
                    /// сохраняем данные меню для распространения
                    if (bOrgIsMenuExchangeSource) {
                        MenuExchange menuExchange = new MenuExchange(item.getDate(), idOfOrg, item.getRawXmlText(),
                                bFirstMenuItem ? MenuExchange.FLAG_ANCHOR_MENU : MenuExchange.FLAG_NONE);
                        persistenceSession.saveOrUpdate(menuExchange);
                    }
                    ///
                    Date menuDate = item.getDate();

                    ////
                    Menu menu = DAOUtils.findMenu(persistenceSession, organization, Menu.ORG_MENU_SOURCE, menuDate);
                    if (null == menu) {
                        menu = new Menu(organization, menuDate, new Date(), Menu.ORG_MENU_SOURCE,
                                bFirstMenuItem ? Menu.FLAG_ANCHOR_MENU : Menu.FLAG_NONE);
                        persistenceSession.save(menu);
                    }
                    processReqAssortment(persistenceSession, organization, menuDate, item.getReqAssortments());
                    processReqMenuDetails(persistenceSession, organization, menuDate, menu, item,
                            item.getReqMenuDetails());
                    processReqComplexInfos(persistenceSession, organization, menuDate, menu, item.getReqComplexInfos());
                    bFirstMenuItem = false;
                }

                persistenceSession.flush();
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }
    }

    private void processReqComplexInfos(Session persistenceSession, Org organization, Date menuDate, Menu menu,
            List<SyncRequest.ReqMenu.Item.ReqComplexInfo> reqComplexInfos) throws Exception {
        DAOUtils.deleteComplexInfoForDate(persistenceSession, organization, menuDate);
        for (SyncRequest.ReqMenu.Item.ReqComplexInfo reqComplexInfo : reqComplexInfos) {
            ComplexInfo complexInfo = new ComplexInfo(reqComplexInfo.getComplexId(), organization, menuDate,
                    reqComplexInfo.getModeFree(), reqComplexInfo.getModeGrant(), reqComplexInfo.getModeOfAdd(),
                    reqComplexInfo.getComplexMenuName());
            persistenceSession.save(complexInfo);

            for (SyncRequest.ReqMenu.Item.ReqComplexInfo.ReqComplexInfoDetail reqComplexInfoDetail : reqComplexInfo
                    .getComplexInfoDetails()) {
                MenuDetail menuDetail = DAOUtils.findMenuDetailByLocalId(persistenceSession, menu,
                        reqComplexInfoDetail.getReqMenuDetail().getIdOfMenu());
                if (menuDetail == null) {
                    throw new Exception(
                            "MenuDetail not found for complex detail with localIdOfMenu=" + reqComplexInfoDetail
                                    .getReqMenuDetail().getIdOfMenu());
                }
                ComplexInfoDetail complexInfoDetail = new ComplexInfoDetail(complexInfo, menuDetail);
                persistenceSession.save(complexInfoDetail);
            }
        }
    }

    private void processReqAssortment(Session persistenceSession, Org organization, Date menuDate,
            List<SyncRequest.ReqMenu.Item.ReqAssortment> reqAssortments) {
        DAOUtils.deleteAssortmentForDate(persistenceSession, organization, menuDate);
        for (SyncRequest.ReqMenu.Item.ReqAssortment reqAssortment : reqAssortments) {
            Assortment assortment = new Assortment(organization, menuDate, reqAssortment.getName(),
                    reqAssortment.getFullName(), reqAssortment.getGroup(), reqAssortment.getMenuOrigin(),
                    reqAssortment.getMenuOutput(), reqAssortment.getPrice(), reqAssortment.getFat(),
                    reqAssortment.getCarbohydrates(), reqAssortment.getCalories(), reqAssortment.getVitB1(),
                    reqAssortment.getVitC(), reqAssortment.getVitA(), reqAssortment.getVitE(), reqAssortment.getMinCa(),
                    reqAssortment.getMinP(), reqAssortment.getMinMg(), reqAssortment.getMinFe());
            persistenceSession.save(assortment);
        }
    }

    private void processReqMenuDetails(Session persistenceSession, Org organization, Date menuDate, Menu menu,
            SyncRequest.ReqMenu.Item item, Enumeration<SyncRequest.ReqMenu.Item.ReqMenuDetail> reqMenuDetails)
            throws Exception {
        while (reqMenuDetails.hasMoreElements()) {
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail = reqMenuDetails.nextElement();
            if (null == DAOUtils.findMenuDetailByLocalId(persistenceSession, menu, reqMenuDetail.getIdOfMenu())) {
                MenuDetail menuDetail = new MenuDetail(menu, reqMenuDetail.getPath(), reqMenuDetail.getName(),
                        reqMenuDetail.getGroup(), reqMenuDetail.getOutput(), reqMenuDetail.getPrice(),
                        reqMenuDetail.getMenuOrigin(), reqMenuDetail.getAvailableNow());
                menuDetail.setLocalIdOfMenu(reqMenuDetail.getIdOfMenu());
                menuDetail.setProtein(reqMenuDetail.getProtein());
                menuDetail.setFat(reqMenuDetail.getFat());
                menuDetail.setCarbohydrates(reqMenuDetail.getCarbohydrates());
                menuDetail.setCalories(reqMenuDetail.getCalories());
                menuDetail.setVitB1(reqMenuDetail.getVitB1());
                menuDetail.setVitC(reqMenuDetail.getVitC());
                menuDetail.setVitA(reqMenuDetail.getVitA());
                menuDetail.setVitE(reqMenuDetail.getVitE());
                menuDetail.setMinCa(reqMenuDetail.getMinCa());
                menuDetail.setMinP(reqMenuDetail.getMinP());
                menuDetail.setMinMg(reqMenuDetail.getMinMg());
                menuDetail.setMinFe(reqMenuDetail.getMinFe());
                persistenceSession.save(menuDetail);
                menu.addMenuDetail(menuDetail);
            }
        }
        // Ищем "лишние" элементы меню
        List<MenuDetail> superfluousMenuDetails = new LinkedList<MenuDetail>();
        for (MenuDetail menuDetail : menu.getMenuDetails()) {
            if (!find(menuDetail, item)) {
                superfluousMenuDetails.add(menuDetail);
            }
        }
        // Удаляем "лишние" элементы меню
        for (MenuDetail menuDetail : superfluousMenuDetails) {
            menu.removeMenuDetail(menuDetail);
            persistenceSession.delete(menuDetail);
        }

    }

    private boolean isOrgMenuExchangeSource(Session persistenceSession, Long idOfOrg) {
        return DAOUtils.isOrgMenuExchangeSource(persistenceSession, idOfOrg);
    }

    private static boolean find(MenuDetail menuDetail, SyncRequest.ReqMenu.Item menuItem) throws Exception {
        Enumeration<SyncRequest.ReqMenu.Item.ReqMenuDetail> reqMenuDetails = menuItem.getReqMenuDetails();
        while (reqMenuDetails.hasMoreElements()) {
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail = reqMenuDetails.nextElement();
            Long localIdOfMenu = menuDetail.getLocalIdOfMenu();
            // если есть локальный ID то ищем по нему, если нет - то по имени
            if ((localIdOfMenu != null && reqMenuDetail.getIdOfMenu() != null && (localIdOfMenu
                    .equals(reqMenuDetail.getIdOfMenu()))) || (localIdOfMenu == null && StringUtils
                    .equals(menuDetail.getMenuDetailName(), reqMenuDetail.getName()))) {
                return true;
            }
        }
        return false;
    }

    private SyncResponse.ResMenuExchangeData getMenuExchangeData(Long idOfOrg, Date startDate, Date endDate)
            throws Exception {
        startDate = CalendarUtils.truncateToDayOfMonth(startDate);
        SyncResponse.ResMenuExchangeData resMenuExData = new SyncResponse.ResMenuExchangeData();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Long idOfSourceOrg = DAOUtils.findMenuExchangeSourceOrg(persistenceSession, idOfOrg);

            if (idOfSourceOrg != null) {
                List<MenuExchange> menuExchangeList = DAOUtils
                        .findMenuExchangeDataBetweenDatesIncludingSettings(persistenceSession, idOfSourceOrg,
                                toDate(startDate), toDate(endDate));
                boolean hasAnchorMenu = false;
                for (MenuExchange menuExchange : menuExchangeList) {
                    if ((menuExchange.getFlags() & MenuExchange.FLAG_ANCHOR_MENU) != 0) {
                        hasAnchorMenu = true;
                        break;
                    }
                }
                /// если в период выборки не попало корневое меню, то ищем его на предыдущие даты
                if (!hasAnchorMenu) {
                    MenuExchange anchorMenu = DAOUtils
                            .findMenuExchangeBeforeDateByEqFlag(persistenceSession, idOfSourceOrg, toDate(startDate),
                                    MenuExchange.FLAG_ANCHOR_MENU);
                    if (anchorMenu != null) {
                        resMenuExData.addItem(anchorMenu.getMenuDataWithDecompress());
                    }
                }

                for (MenuExchange m : menuExchangeList) {
                    resMenuExData.addItem(m.getMenuDataWithDecompress());
                }
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resMenuExData;
    }

    private Date toDate(Date startDate) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(startDate);
        return new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)).getTime();
    }

    //// Сейчас не используется

    private SyncResponse.ResMenu getContragentMenu(Long idOfOrg, Date startDate, Date endDate) throws Exception {
        SyncResponse.ResMenu resMenu = new SyncResponse.ResMenu();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org organization = DAOUtils.getOrgReference(persistenceSession, idOfOrg);
            List menus = DAOUtils
                    .findMenusBetweenDates(persistenceSession, organization, Menu.CONTRAGENT_MENU_SOURCE, startDate,
                            endDate);
            for (Object object : menus) {
                Menu menu = (Menu) object;
                resMenu.addItem(new SyncResponse.ResMenu.Item(menu));
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resMenu;
    }

    private SyncResponse.ResDiary processSyncDiary(Long idOfOrg, SyncRequest.ReqDiary reqDiary) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org organization = DAOUtils.getOrgReference(persistenceSession, idOfOrg);
            // Ищем "лишние" предметы
            List<DiaryClass> superfluousDiaryClasses = new LinkedList<DiaryClass>();
            for (DiaryClass diaryClass : organization.getDiaryClasses()) {
                if (!find(diaryClass, reqDiary)) {
                    superfluousDiaryClasses.add(diaryClass);
                }
            }
            // Удаляем "лишние" предметы            
            for (DiaryClass diaryClass : superfluousDiaryClasses) {
                organization.removeDiaryClass(diaryClass);
                persistenceSession.delete(diaryClass);
            }
            // Добавляем и обновляем предметы согласно запросу
            Enumeration<SyncRequest.ReqDiary.ReqDiaryClass> reqDiaryClasses = reqDiary.getReqDiaryClasses();
            while (reqDiaryClasses.hasMoreElements()) {
                processSyncDiaryClass(persistenceSession, organization, reqDiaryClasses.nextElement());
            }
            // Добавляем или обновляем расписания и добавляем оценки
            Enumeration<SyncRequest.ReqDiary.ReqDiaryTimesheet> reqDiaryTimesheets = reqDiary.getReqDiaryTimesheets();
            while (reqDiaryTimesheets.hasMoreElements()) {
                processDiaryTimesheet(persistenceSession, organization, reqDiaryTimesheets.nextElement());
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return new SyncResponse.ResDiary();
    }

    private SyncResponse.ResEnterEvents processSyncEnterEvents(SyncRequest.EnterEvents enterEvents) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        SyncResponse.ResEnterEvents resEnterEvents = new SyncResponse.ResEnterEvents();
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            for (SyncRequest.EnterEvents.EnterEvent e : enterEvents.getEvents()) {
                // Check enter event existence
                EnterEvent ee = DAOUtils
                    .findEnterEvent(persistenceSession, new CompositeIdOfEnterEvent(e.getIdOfEnterEvent(), e.getIdOfOrg()));
                if (null != ee) {
                    // if enter event exists (may be last sync result was not transferred to client)
                    if (((ee.getClient() == null && e.getIdOfClient() == null) ||
                         (ee.getClient() != null && ee.getClient().getIdOfClient().equals(e.getIdOfClient())))
                        && ee.getEvtDateTime().equals(e.getEvtDateTime())
                        && ((ee.getIdOfTempCard() == null && e.getIdOfTempCard() == null) ||
                            (ee.getIdOfTempCard() != null && ee.getIdOfTempCard().equals(e.getIdOfTempCard())))) {
                        SyncResponse.ResEnterEvents.Item item = new SyncResponse.ResEnterEvents.Item(e.getIdOfEnterEvent(),
                                SyncResponse.ResEnterEvents.Item.RC_OK,
                            "Enter event already registered");
                        resEnterEvents.addItem(item);
                    } else {
                        SyncResponse.ResEnterEvents.Item item = new SyncResponse.ResEnterEvents.Item(e.getIdOfEnterEvent(),
                                SyncResponse.ResEnterEvents.Item.RC_EVENT_EXISTS_WITH_DIFFERENT_ATTRIBUTES,
                            "Enter event already registered but attributes differ, idOfOrg == " + e.getIdOfOrg() +
                                    ", idOfEnterEvent == " + e.getIdOfEnterEvent());
                        resEnterEvents.addItem(item);
                    }
                }
                else {
                    // find client by id
                    Client client = null;
                    if (e.getIdOfClient() != null) {
                        client = (Client) persistenceSession.get(Client.class, e.getIdOfClient());
                        if (client==null) {
                            SyncResponse.ResEnterEvents.Item item = new SyncResponse.ResEnterEvents.Item(e.getIdOfEnterEvent(), SyncResponse.ResEnterEvents.Item.RC_CLIENT_NOT_FOUND,
                                "Client not found: "+e.getIdOfClient());
                            resEnterEvents.addItem(item);
                            continue;
                        }
                    }
                    EnterEvent enterEvent = new EnterEvent();
                    enterEvent.setCompositeIdOfEnterEvent(
                            new CompositeIdOfEnterEvent(e.getIdOfEnterEvent(), e.getIdOfOrg()));
                    enterEvent.setEnterName(e.getEnterName());
                    enterEvent.setTurnstileAddr(e.getTurnstileAddr());
                    enterEvent.setPassDirection(e.getPassDirection());
                    enterEvent.setEventCode(e.getEventCode());
                    enterEvent.setIdOfCard(e.getIdOfCard());
                    enterEvent.setClient(client);
                    enterEvent.setIdOfTempCard(e.getIdOfTempCard());
                    enterEvent.setEvtDateTime(e.getEvtDateTime());
                    enterEvent.setIdOfVisitor(e.getIdOfVisitor());
                    enterEvent.setVisitorFullName(e.getVisitorFullName());
                    enterEvent.setDocType(e.getDocType());
                    enterEvent.setDocSerialNum(e.getDocSerialNum());
                    enterEvent.setIssueDocDate(e.getIssueDocDate());
                    enterEvent.setVisitDateTime(e.getVisitDateTime());
                    persistenceSession.save(enterEvent);


                    SyncResponse.ResEnterEvents.Item item = new SyncResponse.ResEnterEvents.Item(e.getIdOfEnterEvent(), 0,
                            null);
                    resEnterEvents.addItem(item);

                    RuntimeContext runtimeContext = RuntimeContext.getInstance();
                    // отправить уведомление по смс
                    boolean notifyBySMSAboutEnterEvent = runtimeContext.getOptionValueBool(
                            Option.OPTION_NOTIFY_BY_SMS_ABOUT_ENTER_EVENT);
                    /*
                    Criteria criteriaClientBySMS = persistenceSession.createCriteria(Client.class);
                    criteriaClientBySMS.add( Restrictions.and(
                            Restrictions.eq("idOfClient", e.getIdOfClient())),
                            Restrictions.eq("notifyViaSMS", )
                    );
                    
                    boolean sendedSMSOfClient = 
                      */
                    logger.info("Preparing to send SMS, notifyBySMSAboutEnterEvent - " + notifyBySMSAboutEnterEvent
                                + ", today - " + isDateToday(e.getEvtDateTime())
                                + ", date - " + e.getEvtDateTime()
                                + ", idOfClient - " + e.getIdOfClient()
                                + ", direction - " + e.getPassDirection());

                    if (notifyBySMSAboutEnterEvent &&
                        isDateToday(e.getEvtDateTime()) &&
                        e.getIdOfClient() != null &&
                        (e.getPassDirection() == EnterEvent.ENTRY || e.getPassDirection() == EnterEvent.EXIT ||
                        e.getPassDirection() == EnterEvent.RE_ENTRY || e.getPassDirection() == EnterEvent.RE_EXIT))
                        sendEnterEventSms(persistenceSession, client, e.getPassDirection(), e.getEvtDateTime());


                    /// Формирование журнала транзакции
                    if (runtimeContext.getOptionValueBool(Option.OPTION_JOURNAL_TRANSACTIONS) &&
                            (e.getPassDirection() == EnterEvent.ENTRY || e.getPassDirection() == EnterEvent.EXIT ||
                            e.getPassDirection() == EnterEvent.RE_ENTRY || e.getPassDirection() == EnterEvent.RE_EXIT)
                             && e.getIdOfCard()!=null) {
                        Card card = DAOUtils.findCardByCardNo(persistenceSession, e.getIdOfCard());
                        if (card==null) {
                            logger.error("Не найдена карта по событию прохода: idOfOrg="+enterEvent.getCompositeIdOfEnterEvent().getIdOfOrg()+", idOfEnterEvent="+enterEvent.getCompositeIdOfEnterEvent().getIdOfEnterEvent()+", idOfCard="+e.getIdOfCard());
                        }

                        if (card!=null && card.getCardType()==Card.TYPE_UEC) {
                            Criteria orgCriteria = persistenceSession.createCriteria(Org.class);
                            orgCriteria.add(Restrictions.eq("idOfOrg",e.getIdOfOrg()));
                            Org org = (Org) orgCriteria.uniqueResult();
                            String transCode;
                            switch (e.getPassDirection()){
                                case EnterEvent.ENTRY: case EnterEvent.RE_ENTRY: transCode="IN"; break;
                                case EnterEvent.EXIT: case EnterEvent.RE_EXIT: transCode="OUT";  break;
                                default: transCode=null;
                            }
                            if (transCode!=null) {
                                TransactionJournal transactionJournal = new TransactionJournal(enterEvent.getCompositeIdOfEnterEvent().getIdOfOrg(),
                                        enterEvent.getCompositeIdOfEnterEvent().getIdOfEnterEvent(), new Date(), org.getOGRN(), TransactionJournal.SERVICE_CODE_SCHL_ACC,
                                        transCode,
                                        TransactionJournal.CARD_TYPE_CODE_UEC, TransactionJournal.CARD_TYPE_ID_CODE_MUID, Card.TYPE_NAMES[card.getCardType()] , Long.toHexString(card.getCardNo()),
                                        client.getSan(), client.getContractId(), client.getClientGroupTypeAsString(), e.getEnterName());
                                persistenceSession.save(transactionJournal);
                            }
                        }
                    }

                }
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Save enter event to database error: ", e);
            resEnterEvents = new SyncResponse.ResEnterEvents();
            for (SyncRequest.EnterEvents.EnterEvent ee : enterEvents.getEvents()) {
                SyncResponse.ResEnterEvents.Item item = new SyncResponse.ResEnterEvents.Item(ee.getIdOfEnterEvent(), 1,
                        "Save to data base error");
                resEnterEvents.addItem(item);
            }
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resEnterEvents;
    }

    private SyncResponse.ResLibraryData processSyncLibraryData(SyncRequest.LibraryData libraryData) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        SyncResponse.ResLibraryData resLibraryData = new SyncResponse.ResLibraryData();
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            for (SyncRequest.LibraryData.Publications.Publication p : libraryData.getPublications()
                    .getPublicationList()) {
                // Check publication existence
                Publication pbctn = DAOUtils
                    .findPublication(persistenceSession, new CompositeIdOfPublication(p.getIdOfPublication(), p.getIdOfOrg()));
                if (p.getAction().equalsIgnoreCase("n") && null != pbctn) {
                    // if publication exists (may be last sync result was not transferred to client)
                    if (pbctn.getRecordStatus().equals(p.getRecordStatus()) &&
                        pbctn.getRecordType().equals(p.getRecordType()) &&
                        pbctn.getBibliographicLevel().equals(p.getBibliographicLevel()) &&
                        pbctn.getHierarchicalLevel().equals(p.getHierarchicalLevel()) &&
                        pbctn.getCodingLevel().equals(p.getCodingLevel()) &&
                        pbctn.getFormOfCatalogingDescription().equals(p.getFormOfCatalogingDescription())) {
                        SyncResponse.ResLibraryData.Publications.Publication item = new SyncResponse.ResLibraryData.Publications.Publication(
                            p.getIdOfPublication(), p.getVersion(), 0, "Publication already registered");
                        resLibraryData.getPublications().addItem(item);
                    } else {
                        SyncResponse.ResLibraryData.Publications.Publication item = new SyncResponse.ResLibraryData.Publications.Publication(
                            p.getIdOfPublication(), p.getVersion(), 2, "Publication already registered " +
                                "but attributes differ, idOfOrg == " + p.getIdOfOrg() +
                                ", idOfPublication == " + p.getIdOfPublication());
                        resLibraryData.getPublications().addItem(item);
                    }
                } else if (p.getAction().equalsIgnoreCase("u") && null == pbctn) {
                    SyncResponse.ResLibraryData.Publications.Publication item = new SyncResponse.ResLibraryData.Publications.Publication(
                            p.getIdOfPublication(), p.getVersion(), 3, "Can't update publication, " +
                                "record haven't been found, idOfOrg == " + p.getIdOfOrg() +
                                ", idOfPublication == " + p.getIdOfPublication());
                        resLibraryData.getPublications().addItem(item);
                } else {
                    Publication publication = new Publication();
                    publication.setCompositeIdOfPublication(
                            new CompositeIdOfPublication(p.getIdOfPublication(), p.getIdOfOrg()));
                    publication.setRecordStatus(p.getRecordStatus());
                    publication.setRecordType(p.getRecordType());
                    publication.setBibliographicLevel(p.getBibliographicLevel());
                    publication.setHierarchicalLevel(p.getHierarchicalLevel());
                    publication.setCodingLevel(p.getCodingLevel());
                    publication.setFormOfCatalogingDescription(p.getFormOfCatalogingDescription());
                    publication.setData(p.getData());
                    publication.setAuthor(p.getAuthor());
                    publication.setTitle(p.getTitle());
                    publication.setTitle2(p.getTitle2());
                    publication.setPublicationDate(p.getPublicationDate());
                    publication.setPublisher(p.getPublisher());
                    publication.setVersion(p.getVersion());

                    if (p.getAction().equalsIgnoreCase("n")) {
                        persistenceSession.save(publication);
                    } else if (p.getAction().equalsIgnoreCase("u")) {
                        persistenceSession.merge(publication);
                    }

                    SyncResponse.ResLibraryData.Publications.Publication item = new SyncResponse.ResLibraryData.Publications.Publication(
                        p.getIdOfPublication(), p.getVersion(), 0, null);
                    resLibraryData.getPublications().addItem(item);
                }
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Save publication to database error: ", e);
            SyncResponse.ResLibraryData.Publications publications = new SyncResponse.ResLibraryData.Publications();

            for (SyncRequest.LibraryData.Publications.Publication p : libraryData.getPublications()
                    .getPublicationList()) {
                SyncResponse.ResLibraryData.Publications.Publication item = new SyncResponse.ResLibraryData.Publications.Publication(
                        p.getIdOfPublication(), p.getVersion(), 1, "Save to database error");
                publications.addItem(item);
            }
            resLibraryData.setPublications(publications);

        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            for (SyncRequest.LibraryData.Circulations.Circulation c : libraryData.getCirculations()
                    .getCirculationList()) {
                int errCode = 0;
                String error = null;
                // Check circulation existence
                Circulation crcltn = DAOUtils
                    .findCirculation(persistenceSession, new CompositeIdOfCirculation(c.getIdOfCirculation(), c.getIdOfOrg()));
                if (c.getAction().equalsIgnoreCase("n") && null != crcltn) {
                    // if circulation exists (may be last sync result was not transferred to client)
                    if (crcltn.getClient().getIdOfClient().equals(c.getIdOfClient()) &&
                        crcltn.getIdOfPublication() == c.getIdOfPublication() &&
                        crcltn.getIssuanceDate().equals(c.getIssuanceDate()) &&
                        crcltn.getRefundDate().equals(c.getRefundDate())) {
                        SyncResponse.ResLibraryData.Circulations.Circulation item = new SyncResponse.ResLibraryData.Circulations.Circulation(
                            c.getIdOfCirculation(), c.getVersion(), 0, "Circulation already registered");
                        resLibraryData.getCirculations().addItem(item);
                    } else {
                        SyncResponse.ResLibraryData.Circulations.Circulation item = new SyncResponse.ResLibraryData.Circulations.Circulation(
                            c.getIdOfCirculation(), c.getVersion(), 2, "Circulation already registered " +
                                "but attributes differ, idOfOrg == " + c.getIdOfOrg() +
                                ", idOfCirculation == " + c.getIdOfCirculation());
                        resLibraryData.getCirculations().addItem(item);
                    }
                } else if (c.getAction().equalsIgnoreCase("u") && null == crcltn) {
                    SyncResponse.ResLibraryData.Circulations.Circulation item = new SyncResponse.ResLibraryData.Circulations.Circulation(
                        c.getIdOfCirculation(), c.getVersion(), 3, "Can't update circulation, " +
                            "record haven't been found, idOfOrg == " + c.getIdOfOrg() +
                            ", idOfCirculation == " + c.getIdOfCirculation());
                    resLibraryData.getCirculations().addItem(item);
                } else {
                    // Сформировать объект Client
                    Client client = (Client) persistenceSession.get(Client.class, c.getIdOfClient());
                    if (client != null) {
                        // Сформировать объект Publication
                        CompositeIdOfPublication compositeIdOfPublication =
                            new CompositeIdOfPublication(c.getIdOfPublication(), c.getIdOfOrg());
                        Publication publication = (Publication) persistenceSession
                            .get(Publication.class, compositeIdOfPublication);
                        if (publication != null) {
                            Circulation circulation = new Circulation();
                            circulation.setCompositeIdOfCirculation(
                                new CompositeIdOfCirculation(c.getIdOfCirculation(), c.getIdOfOrg()));
                            circulation.setClient(client);
                            circulation.setPublication(publication);
                            circulation.setIdOfPublication(c.getIdOfPublication());
                            circulation.setIdOfOrg(c.getIdOfOrg());
                            circulation.setIssuanceDate(c.getIssuanceDate());
                            circulation.setRefundDate(c.getRefundDate());
                            circulation.setRealRefundDate(c.getRealRefundDate());
                            circulation.setStatus(c.getStatus());
                            circulation.setVersion(c.getVersion());

                            if (c.getAction().equalsIgnoreCase("n")) {
                                persistenceSession.save(circulation);
                            } else if (c.getAction().equalsIgnoreCase("u")) {
                                persistenceSession.merge(circulation);
                            }
                        } else {
                            errCode = 4;
                            error = "Publication not found for circulation with idOfCirculation " + c.getIdOfCirculation();
                        }
                    } else {
                        errCode = 4;
                        error = "Client not found for circulation with idOfCirculation " + c.getIdOfCirculation();
                    }

                    SyncResponse.ResLibraryData.Circulations.Circulation item = new SyncResponse.ResLibraryData.Circulations.Circulation(
                        c.getIdOfCirculation(), c.getVersion(), errCode, error);
                    resLibraryData.getCirculations().addItem(item);
                }
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;

        } catch (Exception e) {
            logger.error("Save circulation to database error: ", e);

            SyncResponse.ResLibraryData.Circulations circulations = new SyncResponse.ResLibraryData.Circulations();

            for (SyncRequest.LibraryData.Circulations.Circulation c : libraryData.getCirculations()
                    .getCirculationList()) {

                SyncResponse.ResLibraryData.Circulations.Circulation item = new SyncResponse.ResLibraryData.Circulations.Circulation(
                        c.getIdOfCirculation(), c.getVersion(), 1, "Save to database error");
                circulations.addItem(item);
            }

            resLibraryData.setCirculations(circulations);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        return resLibraryData;
    }

    private SyncResponse.ResCategoriesDiscountsAndRules processCategoriesDiscountsAndRules(Long idOfOrg) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        SyncResponse.ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules =
                new SyncResponse.ResCategoriesDiscountsAndRules();
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria criteriaDiscountRule = persistenceSession.createCriteria(DiscountRule.class);
            Org org = (Org) persistenceSession.load(Org.class, idOfOrg);
            Set<CategoryOrg> categoryOrgSet = org.getCategories();
            if(!categoryOrgSet.isEmpty()){
                for(Object object: criteriaDiscountRule.list()){
                    DiscountRule discountRule = (DiscountRule) object;
                    /*
                   * проверяем вхождение одного множества в другое
                   * результат categoryOrgSet.containsAll(discountRule.getCategoryOrgs())
                   * вернет true если все категории организации взятые из таблицы организации
                   * пренадлежат категорий организаций приявязанных к Правилам скидок.
                   *
                   * Если все категории организации содержатся в правиле то выводим
                   * */
                    boolean bIncludeRule=false;
                    if (discountRule.getCategoryOrgs().isEmpty()) bIncludeRule = true;
                    else if (categoryOrgSet.containsAll(discountRule.getCategoryOrgs())){
                        bIncludeRule = true;
                    }
                     /*
                    if(categoryOrgSet.isEmpty()){
                        if(discountRule.getCategoryOrgs().isEmpty()) bIncludeRule = true;
                    } else {
                        if(discountRule.getCategoryOrgs().isEmpty()) bIncludeRule = true;
                        else if (categoryOrgSet.containsAll(discountRule.getCategoryOrgs())){
                            bIncludeRule = true;
                        }
                    }

                    if(discountRule.getCategoryOrgs().isEmpty()
                    || (!categoryOrgSet.isEmpty() && categoryOrgSet.containsAll(discountRule.getCategoryOrgs())))
                       */
                    if (bIncludeRule) {
                        SyncResponse.ResCategoriesDiscountsAndRules.DCRI dcri =
                                new SyncResponse.ResCategoriesDiscountsAndRules.DCRI(discountRule);
                        resCategoriesDiscountsAndRules.addDCRI(dcri);
                    }
                }
            }  /* Организация не пренадлежит ни к одной категории*/
               else{
                for(Object object: criteriaDiscountRule.list()){
                    DiscountRule discountRule = (DiscountRule) object;
                    /* если правила не установлены категории организаций то отправляем*/
                    if(discountRule.getCategoryOrgs().isEmpty()){
                        SyncResponse.ResCategoriesDiscountsAndRules.DCRI dcri =
                                new SyncResponse.ResCategoriesDiscountsAndRules.DCRI(discountRule);
                        resCategoriesDiscountsAndRules.addDCRI(dcri);
                    }
                }
            }

            Criteria criteria = persistenceSession.createCriteria(CategoryDiscount.class);
            
            List<CategoryDiscount> categoryDiscounts = criteria.list();
            for (CategoryDiscount categoryDiscount : categoryDiscounts) {
                SyncResponse.ResCategoriesDiscountsAndRules.DCI dci =
                        new SyncResponse.ResCategoriesDiscountsAndRules.DCI(
                                categoryDiscount.getIdOfCategoryDiscount(),
                                categoryDiscount.getCategoryName(),
                                categoryDiscount.getDiscountRules());
                resCategoriesDiscountsAndRules.addDCI(dci);
            }
             /*
            criteria = persistenceSession.createCriteria(DiscountRule.class);
            List<DiscountRule> discountRules = criteria.list();
            for (DiscountRule discountRule : discountRules) {
                SyncResponse.ResCategoriesDiscountsAndRules.DCRI dcri =
                        new SyncResponse.ResCategoriesDiscountsAndRules.DCRI(
                                discountRule.getIdOfRule(),
                                discountRule.getDescription(),
                                discountRule.getCategoryDiscounts(),
                                discountRule.getComplex0(),
                                discountRule.getComplex1(),
                                discountRule.getComplex2(),
                                discountRule.getComplex3(),
                                discountRule.getComplex4(),
                                discountRule.getComplex5(),
                                discountRule.getComplex6(),
                                discountRule.getComplex7(),
                                discountRule.getComplex8(),
                                discountRule.getComplex9(),
                                discountRule.getPriority(),
                                discountRule.isOperationOr());
                resCategoriesDiscountsAndRules.addDCRI(dcri);
            }
               */
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Getting categories and rules error: ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        return resCategoriesDiscountsAndRules;
    }

    private static boolean find(DiaryClass diaryClass, SyncRequest.ReqDiary reqDiary) throws Exception {
        Enumeration<SyncRequest.ReqDiary.ReqDiaryClass> reqDiaryClasses = reqDiary.getReqDiaryClasses();
        while (reqDiaryClasses.hasMoreElements()) {
            SyncRequest.ReqDiary.ReqDiaryClass reqDiaryClass = reqDiaryClasses.nextElement();
            if (diaryClass.getCompositeIdOfDiaryClass().getIdOfClass().equals(reqDiaryClass.getIdOfClass())) {
                return true;
            }
        }
        return false;
    }

    private static void processSyncDiaryClass(Session persistenceSession, Org organization,
            SyncRequest.ReqDiary.ReqDiaryClass reqDiaryClass) throws Exception {
        CompositeIdOfDiaryClass compositeIdOfDiaryClass = new CompositeIdOfDiaryClass(organization.getIdOfOrg(),
                reqDiaryClass.getIdOfClass());
        DiaryClass diaryClass = DAOUtils.findDiaryClass(persistenceSession, compositeIdOfDiaryClass);
        if (null == diaryClass) {
            diaryClass = new DiaryClass(compositeIdOfDiaryClass, reqDiaryClass.getName());
            persistenceSession.save(diaryClass);
            organization.addDiaryClass(diaryClass);
        } else {
            diaryClass.setClassName(reqDiaryClass.getName());
            persistenceSession.update(diaryClass);
        }
    }

    private static void processDiaryTimesheet(Session persistenceSession, Org organization,
            SyncRequest.ReqDiary.ReqDiaryTimesheet reqDiaryTimesheet) throws Exception {
        CompositeIdOfDiaryTimesheet compositeIdOfDiaryTimesheet = new CompositeIdOfDiaryTimesheet(
                organization.getIdOfOrg(), reqDiaryTimesheet.getIdOfClientGroup(), reqDiaryTimesheet.getDate());
        DiaryTimesheet diaryTimesheet = DAOUtils.findDiaryTimesheet(persistenceSession, compositeIdOfDiaryTimesheet);
        if (null == diaryTimesheet) {
            diaryTimesheet = new DiaryTimesheet(compositeIdOfDiaryTimesheet);
            fill(diaryTimesheet, reqDiaryTimesheet.getClasses());
            persistenceSession.save(diaryTimesheet);
            organization.addDiaryTimesheet(diaryTimesheet);
        } else {
            fill(diaryTimesheet, reqDiaryTimesheet.getClasses());
            persistenceSession.update(diaryTimesheet);
        }

        // Заносим в БД все оценки за этот день
        Enumeration<SyncRequest.ReqDiary.ReqDiaryTimesheet.ReqDiaryValue> reqDiaryValues = reqDiaryTimesheet
                .getReqDiaryValues();
        while (reqDiaryValues.hasMoreElements()) {
            processSyncDiaryTimesheetValue(persistenceSession, organization, reqDiaryTimesheet.getDate(),
                    reqDiaryValues.nextElement());
        }
    }

    private static void fill(DiaryTimesheet diaryTimesheet, Long reqClasses[]) throws Exception {
        diaryTimesheet.setC0(getLongNullSafe(reqClasses, 0));
        diaryTimesheet.setC1(getLongNullSafe(reqClasses, 1));
        diaryTimesheet.setC2(getLongNullSafe(reqClasses, 2));
        diaryTimesheet.setC3(getLongNullSafe(reqClasses, 3));
        diaryTimesheet.setC4(getLongNullSafe(reqClasses, 4));
        diaryTimesheet.setC5(getLongNullSafe(reqClasses, 5));
        diaryTimesheet.setC6(getLongNullSafe(reqClasses, 6));
        diaryTimesheet.setC7(getLongNullSafe(reqClasses, 7));
        diaryTimesheet.setC8(getLongNullSafe(reqClasses, 8));
        diaryTimesheet.setC9(getLongNullSafe(reqClasses, 9));
    }

    private static void processSyncDiaryTimesheetValue(Session session, Org organization, Date date,
            SyncRequest.ReqDiary.ReqDiaryTimesheet.ReqDiaryValue reqDiaryValue) throws Exception {
        int reqVType = reqDiaryValue.getVType();
        switch (reqVType) {
            case 0:
                StrTokenizer strTokenizer = new StrTokenizer(reqDiaryValue.getValue(), ":");
                while (strTokenizer.hasNext()) {
                    String dayValueType = strTokenizer.nextToken();
                    String dayValue = strTokenizer.nextToken();
                    processSyncDiaryTimesheetDayValue(session, organization, date, reqDiaryValue, dayValueType,
                            dayValue);
                }
                break;

            case 1:
            case 2:
            case 3:
            case 4:
                processQuarterValue(session, organization, date, reqDiaryValue);
                break;

            case 5:
                processYearValue(session, organization, date, reqDiaryValue);
                break;

            default:
                //todo register error here
                break;
        }
    }

    public Long createPaymentOrder(Long idOfClient, Long idOfContragent, int paymentMethod, Long sum,
            Long contragentSum) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Contragent contragent = DAOUtils.findContragent(persistenceSession, idOfContragent);
            Client client = DAOUtils.getClientReference(persistenceSession, idOfClient);
            ClientPaymentOrder clientPaymentOrder = new ClientPaymentOrder(contragent, client, paymentMethod, sum,
                    contragentSum, new Date());
            persistenceSession.save(clientPaymentOrder);
            Long idOfClientPaymentOrder = clientPaymentOrder.getIdOfClientPaymentOrder();

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return idOfClientPaymentOrder;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public void changePaymentOrderStatus(Long idOfClient, Long idOfClientPaymentOrder, int orderStatus)
            throws Exception {
        if (ClientPaymentOrder.ORDER_STATUS_TRANSFER_ACCEPTED == orderStatus
                || ClientPaymentOrder.ORDER_STATUS_TRANSFER_COMPLETED == orderStatus) {
            throw new IllegalArgumentException("Anacceptable orderStatus");
        }
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            ClientPaymentOrder clientPaymentOrder = DAOUtils
                    .getClientPaymentOrderReference(persistenceSession, idOfClientPaymentOrder);
            Client client = DAOUtils.getClientReference(persistenceSession, idOfClient);
            if (!client.getIdOfClient().equals(clientPaymentOrder.getClient().getIdOfClient())) {
                throw new IllegalArgumentException("Client does't own this order");
            }
            if (clientPaymentOrder.canApplyOrderStatus(orderStatus)) {
                clientPaymentOrder.setOrderStatus(orderStatus);
                persistenceSession.update(clientPaymentOrder);
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public void changePaymentOrderStatus(Long idOfContragent, Long idOfClientPaymentOrder, int orderStatus,
            Long contragentSum, String idOfPayment) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                    "IdOfContragent: %d, IdOfClientPaymentOrder: %d, OrderStatus: %d, ContragentSum: %d, IdOfPayment: %s",
                    idOfContragent, idOfClientPaymentOrder, orderStatus, contragentSum, idOfPayment));
        }
        if (!(ClientPaymentOrder.ORDER_STATUS_TRANSFER_ACCEPTED == orderStatus
                || ClientPaymentOrder.ORDER_STATUS_TRANSFER_COMPLETED == orderStatus)) {
            throw new IllegalArgumentException(String.format("Anacceptable OrderStatus: %d", orderStatus));
        }
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            ClientPaymentOrder clientPaymentOrder = DAOUtils
                    .getClientPaymentOrderReference(persistenceSession, idOfClientPaymentOrder);
            if (!idOfContragent.equals(clientPaymentOrder.getContragent().getIdOfContragent())) {
                throw new IllegalArgumentException(String.format(
                        "Contragent doesn't own this order, IdOfCOntragnet: %d, ClientPaymentOrder is: %s",
                        idOfContragent, clientPaymentOrder));
            }
            if (!contragentSum.equals(clientPaymentOrder.getContragentSum())) {
                logger.warn(
                        String.format("Invalid sum: %d, ClientPaymentOrder: %s", contragentSum, clientPaymentOrder));
                //throw new IllegalArgumentException(
                //        String.format("Invalid sum: %d, ClientPaymentOrder: %s", contragentSum, clientPaymentOrder));
            }
            if (clientPaymentOrder.canApplyOrderStatus(orderStatus)) {
                clientPaymentOrder.setOrderStatus(orderStatus);
                clientPaymentOrder.setIdOfPayment(idOfPayment);
                persistenceSession.update(clientPaymentOrder);
                if (ClientPaymentOrder.ORDER_STATUS_TRANSFER_COMPLETED == orderStatus) {
                    Client client = clientPaymentOrder.getClient();
                    // Ищем подходящую карту
                    /*Card paymentCard = client.findActiveCard(persistenceSession, null);
                    if (null == paymentCard) {
                        // Нет карты, подходящей для зачисления платежа
                        throw new IllegalArgumentException(String.format(
                                "Card approaching for transfer not found, IdOfContragent == %s, IdOfClient == %s",
                                clientPaymentOrder.getContragent().getIdOfContragent(), client.getIdOfClient()));
                    }  */
                    RuntimeContext.getFinancialOpsManager().createClientPaymentWithOrder(persistenceSession,
                            clientPaymentOrder, client);
                }
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private static void processSyncDiaryTimesheetDayValue(Session persistenceSession, Org organization, Date date,
            SyncRequest.ReqDiary.ReqDiaryTimesheet.ReqDiaryValue reqDiaryValue, String parsedDayValueType,
            String parsedDayValue) throws Exception {
        CompositeIdOfDiaryValue compositeIdOfDiaryValue;
        if (parsedDayValueType.equals("0")) {
            compositeIdOfDiaryValue = new CompositeIdOfDiaryValue(organization.getIdOfOrg(),
                    reqDiaryValue.getIdOfClient(), reqDiaryValue.getIdOfClass(), date, DiaryValue.DAY_VALUE1_TYPE);
        } else if (parsedDayValueType.equals("1")) {
            compositeIdOfDiaryValue = new CompositeIdOfDiaryValue(organization.getIdOfOrg(),
                    reqDiaryValue.getIdOfClient(), reqDiaryValue.getIdOfClass(), date, DiaryValue.DAY_VALUE2_TYPE);
        } else if (parsedDayValueType.equals("K")) {
            compositeIdOfDiaryValue = new CompositeIdOfDiaryValue(organization.getIdOfOrg(),
                    reqDiaryValue.getIdOfClient(), reqDiaryValue.getIdOfClass(), date, DiaryValue.DAY_EXAM_VALUE_TYPE);
        } else if (parsedDayValueType.equals("N")) {
            compositeIdOfDiaryValue = new CompositeIdOfDiaryValue(organization.getIdOfOrg(),
                    reqDiaryValue.getIdOfClient(), reqDiaryValue.getIdOfClass(), date,
                    DiaryValue.DAY_PRESENCE_VALUE_TYPE);
        } else if (parsedDayValueType.equals("P")) {
            compositeIdOfDiaryValue = new CompositeIdOfDiaryValue(organization.getIdOfOrg(),
                    reqDiaryValue.getIdOfClient(), reqDiaryValue.getIdOfClass(), date,
                    DiaryValue.DAY_BEHAVIOUR_VALUE_TYPE);
        } else {
            throw new IllegalArgumentException("Unkown day value type");
        }
        DiaryValue diaryValue = DAOUtils.findDiaryValue(persistenceSession, compositeIdOfDiaryValue);
        if (null == diaryValue) {
            diaryValue = new DiaryValue(compositeIdOfDiaryValue, parsedDayValue);
            persistenceSession.save(diaryValue);
        } else {
            //todo register error here
        }
    }

    private static void processQuarterValue(Session persistenceSession, Org organization, Date date,
            SyncRequest.ReqDiary.ReqDiaryTimesheet.ReqDiaryValue reqDiaryValue) throws Exception {
        CompositeIdOfDiaryValue compositeIdOfDiaryValue = new CompositeIdOfDiaryValue(organization.getIdOfOrg(),
                reqDiaryValue.getIdOfClient(), reqDiaryValue.getIdOfClass(), date,
                DiaryValue.QUARTER_VALUE_TYPES[reqDiaryValue.getVType() - 1]);
        DiaryValue diaryValue = DAOUtils.findDiaryValue(persistenceSession, compositeIdOfDiaryValue);
        if (null == diaryValue) {
            diaryValue = new DiaryValue(compositeIdOfDiaryValue, reqDiaryValue.getValue());
            persistenceSession.save(diaryValue);
        } else {
            //todo register error here
        }
    }

    private static void processYearValue(Session persistenceSession, Org organization, Date date,
            SyncRequest.ReqDiary.ReqDiaryTimesheet.ReqDiaryValue reqDiaryValue) throws Exception {
        CompositeIdOfDiaryValue compositeIdOfDiaryValue = new CompositeIdOfDiaryValue(organization.getIdOfOrg(),
                reqDiaryValue.getIdOfClient(), reqDiaryValue.getIdOfClass(), date, DiaryValue.YEAR_VALUE_TYPE);
        DiaryValue diaryValue = DAOUtils.findDiaryValue(persistenceSession, compositeIdOfDiaryValue);
        if (null == diaryValue) {
            diaryValue = new DiaryValue(compositeIdOfDiaryValue, reqDiaryValue.getValue());
            persistenceSession.save(diaryValue);
        } else {
            //todo register error here
        }
    }

    private static void lockActiveCards(Session persistenceSession, Set<Card> lockableCards) throws Exception {
        for (Card card : lockableCards) {
            if (card.getState() == Card.ACTIVE_STATE) {
                card.setState(Card.LOCKED_STATE);
                persistenceSession.update(card);
            }
        }
    }

    public Long createCard(Session persistenceSession, Transaction persistenceTransaction, Long idOfClient, long cardNo, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, Long cardPrintedNo) throws Exception {
        Client client = DAOUtils.getClientReference(persistenceSession, idOfClient);
        if (client==null) throw new Exception("Клиент не найден: "+idOfClient);
        Card c = DAOUtils.findCardByCardNo(persistenceSession, cardNo);
        if (c!=null) {
            throw new Exception("Карта уже зарегистрирована на клиента: "+c.getClient().getIdOfClient());
        }
        if (state == Card.ACTIVE_STATE) {
            lockActiveCards(persistenceSession, client.getCards());
        }

        Card card = new Card(client, cardNo, cardType, state, validTime, lifeState, cardPrintedNo);
        card.setIssueTime(issueTime);
        card.setLockReason(lockReason);
        persistenceSession.save(card);
        return card.getIdOfCard();
    }

    public Long createCard(Long idOfClient, long cardNo, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, Long cardPrintedNo) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Long idOfCard = createCard(persistenceSession, persistenceTransaction, idOfClient, cardNo, cardType, state, validTime, lifeState, lockReason, issueTime, cardPrintedNo);

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
    public void updateCard(Long idOfClient, Long idOfCard, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, String externalId) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client newCardOwner = DAOUtils.getClientReference(persistenceSession, idOfClient);
            Card updatedCard = DAOUtils.getCardReference(persistenceSession, idOfCard);

            if (state == Card.ACTIVE_STATE) {
                Set<Card> clientCards = new HashSet<Card>(newCardOwner.getCards());
                clientCards.remove(updatedCard);
                lockActiveCards(persistenceSession, clientCards);
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

    public void cancelOrder(CompositeIdOfOrder compositeIdOfOrder) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Order order = DAOUtils.findOrder(persistenceSession, compositeIdOfOrder);
            if (null != order) {
                // Update client balance
                Client client = order.getClient();
                if (null != client && 0 != order.getSumByCard()) {
                    RuntimeContext.getFinancialOpsManager().cancelOrder(persistenceSession, order);
                }
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public void addSubcriptionFee(Long idOfClient, CompositeIdOfSubscriptionFee idOfSubscriptionFee) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client client = DAOUtils.getClientReference(persistenceSession, idOfClient);
            Org organization = client.getOrg();
            long subscriptionPrice = organization.getSubscriptionPrice();
            if (subscriptionPrice != 0L) {
                RuntimeContext.getFinancialOpsManager().createSubscriptionFeeCharge(persistenceSession,
                        idOfSubscriptionFee, client, subscriptionPrice);
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private static Long getLongNullSafe(Long values[], int index) {
        if (null == values) {
            return null;
        }
        if (index < 0) {
            return null;
        }
        if (index < values.length) {
            return values[index];
        }
        return null;
    }

    private Long generateIdOfPacket(Long idOfOrg) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Org organization = DAOUtils.getOrgReference(persistenceSession, idOfOrg);
            Long result = organization.getIdOfPacket();
            organization.setIdOfPacket(result + 1);
            persistenceSession.update(organization);
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return result;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private void sendEnterEventSms(Session session, Client client, int passDirection, Date eventDate) throws Exception {
        String eventName = "";
        if (passDirection == EnterEvent.ENTRY)
            eventName = "Вход в школу";
        else if (passDirection == EnterEvent.EXIT)
            eventName = "Выход из школы";
        else if (passDirection == EnterEvent.RE_ENTRY)
            eventName = "Повторный вход в школу";
        else if (passDirection == EnterEvent.RE_EXIT)
            eventName = "Повторный выход из школы";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(eventDate);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String time = (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
        String clientName = client.getPerson().getSurname() + " " + client.getPerson().getFirstName();
        String smsText = String.format(eventName + " " + time + " (" + clientName + "). Баланс: %s р",
                CurrencyStringUtils.copecksToRubles(client.getBalance()));

        SMSService smsService = RuntimeContext.getAppContext().getBean(SMSService.class);
        smsService.sendSMSAsync(client.getIdOfClient(), ClientSms.TYPE_ENTER_EVENT_NOTIFY, smsText);
    }

    private static boolean isDateToday(Date date) {
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        if (today.get(Calendar.DATE) == dateCalendar.get(Calendar.DATE) &&
            today.get(Calendar.MONTH) == dateCalendar.get(Calendar.MONTH) &&
            today.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR))
            return true;
        return false;
    }

}