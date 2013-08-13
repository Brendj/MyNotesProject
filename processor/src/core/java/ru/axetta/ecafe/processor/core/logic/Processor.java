/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.AccessDiniedException;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.event.EventNotificator;
import ru.axetta.ecafe.processor.core.event.PaymentProcessEvent;
import ru.axetta.ecafe.processor.core.event.SyncEvent;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.ClientPaymentOrderProcessor;
import ru.axetta.ecafe.processor.core.payment.PaymentProcessor;
import ru.axetta.ecafe.processor.core.payment.PaymentRequest;
import ru.axetta.ecafe.processor.core.payment.PaymentResponse;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.service.OrderCancelProcessor;
import ru.axetta.ecafe.processor.core.subscription.SubscriptionFeeManager;
import ru.axetta.ecafe.processor.core.sync.*;
import ru.axetta.ecafe.processor.core.sync.handlers.client.request.ClientRequests;
import ru.axetta.ecafe.processor.core.sync.handlers.client.request.TempCardOperationData;
import ru.axetta.ecafe.processor.core.sync.handlers.client.request.TempCardRequestProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.complex.roles.ComplexRoleProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.complex.roles.ComplexRoles;
import ru.axetta.ecafe.processor.core.sync.handlers.org.owners.OrgOwnerProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations.ResTempCardsOperations;
import ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations.TempCardOperationProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations.TempCardsOperations;
import ru.axetta.ecafe.processor.core.sync.manager.Manager;
import ru.axetta.ecafe.processor.core.sync.response.DirectiveElement;
import ru.axetta.ecafe.processor.core.sync.response.GoodsBasicBasketData;
import ru.axetta.ecafe.processor.core.sync.handlers.org.owners.OrgOwnerData;
import ru.axetta.ecafe.processor.core.sync.response.QuestionaryData;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ParameterStringUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrTokenizer;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
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

    private static final Logger logger = LoggerFactory.getLogger(Processor.class);
    private static final int RESPONSE_MENU_PERIOD_IN_DAYS = 7;
    private final SessionFactory persistenceSessionFactory;
    private final EventNotificator eventNotificator;

    public enum PaymentProcessResult {

        OK(0, "Ok"),
        UNKNOWN_ERROR(100, "Unknown error"),
        CLIENT_NOT_FOUND(105, "Client not found"),
        CARD_NOT_FOUND(120, "Card acceptable for transfer not found"),
        CONTRAGENT_NOT_FOUND(130, "Contragent not found"),
        PAYMENT_ALREADY_REGISTERED(140, "Payment is already registered"),
        TSP_CONTRAGENT_IS_PROHIBITED(150, "Merchant (TSP) contragent is prohibited for this client"),
        PAYMENT_NOT_FOUND(300, "Payment not found"),;

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

    public Processor(SessionFactory persistenceSessionFactory, EventNotificator eventNotificator) {
        this.persistenceSessionFactory = persistenceSessionFactory;
        this.eventNotificator = eventNotificator;
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
                return new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null, null,
                        PaymentProcessResult.CONTRAGENT_NOT_FOUND.getCode(),
                        String.format("%s. IdOfContragent == %s, ContractId == %s",
                                PaymentProcessResult.CONTRAGENT_NOT_FOUND.getDescription(), idOfContragent,
                                payment.getContractId()), null);
            }
            if (DAOUtils.existClientPayment(persistenceSession, contragent, payment.getIdOfPayment())) {
                return new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null, null,
                        PaymentProcessResult.PAYMENT_ALREADY_REGISTERED.getCode(),
                        String.format("%s. IdOfContragent == %s, IdOfPayment == %s",
                                PaymentProcessResult.PAYMENT_ALREADY_REGISTERED.getDescription(), idOfContragent,
                                payment.getIdOfPayment()), null);
            }
            Client client = findPaymentClient(persistenceSession, contragent, payment.getContractId(),
                    payment.getClientId());
            if (null == client) {
                return new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null, null,
                        PaymentProcessResult.CLIENT_NOT_FOUND.getCode(),
                        String.format("%s. IdOfContragent == %s, ContractId == %s, ClientId == %s",
                                PaymentProcessResult.CLIENT_NOT_FOUND.getDescription(), idOfContragent,
                                payment.getContractId(), payment.getClientId()), null);
            }
            Long idOfClient = client.getIdOfClient();
            Long paymentTspContragentId = null;
            HashMap<String, String> payAddInfo = new HashMap<String, String>();
            Contragent defaultTsp = client.getOrg().getDefaultSupplier();
            if (payment.getTspContragentId() != null) {
                // если явно указан контрагент ТСП получатель, проверяем что он соответствует организации клиента
                if (defaultTsp == null || !defaultTsp.getIdOfContragent().equals(payment.getTspContragentId())) {
                    return new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null, null,
                            PaymentProcessResult.TSP_CONTRAGENT_IS_PROHIBITED.getCode(),
                            String.format("%s. IdOfTspContragent == %s, ContractId == %s, ClientId == %s",
                                    PaymentProcessResult.TSP_CONTRAGENT_IS_PROHIBITED.getDescription(),
                                    payment.getTspContragentId(), payment.getContractId(), payment.getClientId()),
                            null);
                }
            }
            if (defaultTsp != null) {
                paymentTspContragentId = defaultTsp.getIdOfContragent();
                processContragentAddInfo(defaultTsp, payAddInfo);
            }
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
                    logger.info("Processing payment with balance reset: " + client + "; current balance=" + client
                            .getBalance() + "; set balance=" + paymentSum);
                }
                RuntimeContext.getFinancialOpsManager()
                        .createClientPayment(persistenceSession, client, payment.getPaymentMethod(), paymentSum,
                                ClientPayment.CLIENT_TO_ACCOUNT_PAYMENT, payment.getPayTime(), payment.getIdOfPayment(),
                                contragent, payment.getAddPaymentMethod(), payment.getAddIdOfPayment());

                persistenceSession.flush();
            }
            PaymentResponse.ResPaymentRegistry.Item result = new PaymentResponse.ResPaymentRegistry.Item(payment,
                    idOfClient, client.getContractId(), paymentTspContragentId, null, client.getBalance(),
                    PaymentProcessResult.OK.getCode(), PaymentProcessResult.OK.getDescription(), client, null,
                    payAddInfo);

            persistenceTransaction.commit();
            persistenceTransaction = null;
            return result;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @Override
    public void changePaymentOrderStatus(Long idOfContragent, Long idOfClientPaymentOrder, int orderStatus,
            Long contragentSum, String idOfPayment, String addIdOfPayment) throws Exception {
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
                    RuntimeContext.getFinancialOpsManager()
                            .createClientPaymentWithOrder(persistenceSession, clientPaymentOrder, client,
                                    addIdOfPayment);
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

    @Override
    public SyncResponse processSyncRequest(SyncRequest request) throws Exception {
        Date syncStartTime = new Date();
        int syncResult = 0;
        SyncResponse response = null;

        try {
            switch (request.getSyncType()){
                case TYPE_FULL:{
                    // обработка полной синхронизации
                    response = buildFullSyncResponse(request, syncStartTime, syncResult);
                    break;
                }
                case TYPE_GET_ACC_INC:{
                    // обработка синхронизации покупок и прохода клиентов
                    response = buildAccIncSyncResponse(request);
                    break;
                }
                case TYPE_GET_CLIENTS_PARAMS:{
                    // обработка синхронизации параметров клиента
                    response = buildClientsParamsSyncResponse(request);
                    break;
                }
            }

        } catch (Exception e) {
            logger.error(String.format("Failed to perform synchronization, IdOfOrg == %s", request.getIdOfOrg()), e);
            syncResult = 1;
        }

        // Build and return response
        if (request.getSyncType() ==SyncType.TYPE_FULL) {
            eventNotificator.fire(new SyncEvent.RawEvent(syncStartTime, request, response));
        }
        return response;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public Long createCard(Session persistenceSession, Transaction persistenceTransaction, Long idOfClient, long cardNo,
            int cardType, int state, Date validTime, int lifeState, String lockReason, Date issueTime,
            Long cardPrintedNo) throws Exception {
        Client client = DAOUtils.getClientReference(persistenceSession, idOfClient);
        if (client == null) {
            throw new Exception("Клиент не найден: " + idOfClient);
        }
        Card c = DAOUtils.findCardByCardNo(persistenceSession, cardNo);
        if (c != null) {
            throw new Exception("Карта уже зарегистрирована на клиента: " + c.getClient().getIdOfClient());
        }
        CardTemp ct = DAOUtils.findCardTempByCardNo(persistenceSession, cardNo);
        if (ct != null) {
            throw new Exception("Временная карта уже зарегистрирована на клиента: " );
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

    @Override
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

    @Override
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
                RuntimeContext.getFinancialOpsManager()
                        .createSubscriptionFeeCharge(persistenceSession, idOfSubscriptionFee, client,
                                subscriptionPrice);
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

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

    /* Do process full synchronization */
    private SyncResponse buildFullSyncResponse(SyncRequest request, Date syncStartTime, int syncResult) throws Exception {

        Long idOfPacket = null;
        SyncHistory syncHistory = null; // регистируются и заполняются только для полной синхронизации

        SyncResponse.ResPaymentRegistry resPaymentRegistry = null;
        SyncResponse.AccRegistry accRegistry = null;
        SyncResponse.AccIncRegistry accIncRegistry = null;
        SyncResponse.ClientRegistry clientRegistry = null;
        SyncResponse.ResOrgStructure resOrgStructure = null;
        SyncResponse.ResMenuExchangeData resMenuExchange = null;
        SyncResponse.ResDiary resDiary = null;
        SyncResponse.ResEnterEvents resEnterEvents = null;
        ResTempCardsOperations resTempCardsOperations = null;
        TempCardOperationData tempCardOperationData = null;
        SyncResponse.ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = null;
        ComplexRoles complexRoles = null;
        SyncResponse.CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry = null;
        Manager manager = null;
        OrgOwnerData orgOwnerData = null;
        QuestionaryData questionaryData = null;
        GoodsBasicBasketData goodsBasicBasketData = null;
        DirectiveElement directiveElement = null;

        List<Long> errorClientIds = new ArrayList<Long>();

        idOfPacket = generateIdOfPacket(request.getIdOfOrg());
        // Register sync history
        syncHistory = addSyncHistory(request.getIdOfOrg(), idOfPacket, syncStartTime, request.getClientVersion(),
                request.getRemoteAddr());
        addClientVersionAndRemoteAddressByOrg(request.getIdOfOrg(), request.getClientVersion(),
                request.getRemoteAddr());
        // Process paymentRegistry
        try {
            if (request.getPaymentRegistry().getPayments().hasMoreElements()) {
                if (!RuntimeContext.getInstance().isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_P)) {
                    createSyncHistory(request.getIdOfOrg(),syncHistory, "no license slots available");
                    throw new Exception("no license slots available");
                }
            }
            resPaymentRegistry = processSyncPaymentRegistry(syncHistory.getIdOfSync(), request.getIdOfOrg(),
                    request.getPaymentRegistry(), errorClientIds);
        } catch (Exception e) {
            String message = String.format("Failed to process PaymentRegistry, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistory(request.getIdOfOrg(),syncHistory, message);
            logger.error(message, e);
        }

        // Process ClientParamRegistry
        try {
            processSyncClientParamRegistry(syncHistory, request.getIdOfOrg(), request.getClientParamRegistry(), errorClientIds);
        } catch (Exception e) {
            String message = String.format("Failed to process ClientParamRegistry, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistory(request.getIdOfOrg(),syncHistory, message);
            logger.error(message, e);
        }

        // Process OrgStructure
        try {
            if (request.getOrgStructure() != null) {
                resOrgStructure = processSyncOrgStructure(request.getIdOfOrg(), request.getOrgStructure(), syncHistory);
            }
        } catch (Exception e) {
            resOrgStructure = new SyncResponse.ResOrgStructure(1, "Unexpected error");
            String message = String.format("Failed to process OrgStructure, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistory(request.getIdOfOrg(),syncHistory, message);
            logger.error(message, e);
        }

        // Build client registry
        try {
            clientRegistry = processSyncClientRegistry(request.getIdOfOrg(),
                    request.getClientRegistryRequest(), errorClientIds);
        } catch (Exception e) {
            String message = String.format("Failed to build ClientRegistry, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistory(request.getIdOfOrg(),syncHistory, message);
            logger.error(message, e);
        }

        // Process menu from Org
        try {
            processSyncMenu(request.getIdOfOrg(), request.getReqMenu());
        } catch (Exception e) {
            String message = String.format("Failed to process menu, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistory(request.getIdOfOrg(),syncHistory, message);
            logger.error(message, e);
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
            String message = String.format("Failed to build menu, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistory(request.getIdOfOrg(),syncHistory, message);
            logger.error(message, e);
        }


        // Build AccRegistry
        try {
            accRegistry = getAccRegistry(request.getIdOfOrg());
        } catch (Exception e) {
            accRegistry = new SyncResponse.AccRegistry();
            String message = String.format("Failed to build AccRegistry, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistory(request.getIdOfOrg(),syncHistory, message);
            logger.error(message, e);

        }

        // Process ReqDiary
        try {
            resDiary = processSyncDiary(request.getIdOfOrg(), request.getReqDiary());
        } catch (Exception e) {
            resDiary = new SyncResponse.ResDiary(1, "Unexpected error");
            String message = "SyncResponse.ResDiary: Unexpected error";
            createSyncHistory(request.getIdOfOrg(),syncHistory, message);
            logger.error(message, e);
        }

        // Process enterEvents
        try {
            if (request.getEnterEvents() != null) {
                if (request.getEnterEvents().getEvents().size() > 0) {
                    if (!RuntimeContext.getInstance()
                            .isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_S)) {
                        createSyncHistory(request.getIdOfOrg(),syncHistory, "no license slots available");
                        throw new Exception("no license slots available");
                    }
                }
                resEnterEvents = processSyncEnterEvents(request.getEnterEvents());
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to process enter events, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
        }

        try {
            if(request.getTempCardsOperations()!=null){
                resTempCardsOperations = processTempCardsOperations(request.getTempCardsOperations());
            }
        } catch (Exception e) {
            String message = String.format("processTempCardsOperations: %s", e.getMessage());
            createSyncHistory(request.getIdOfOrg(),syncHistory, message);
            logger.error(message, e);
        }

        try {
            if(request.getClientRequests()!=null){
                ClientRequests clientRequests = request.getClientRequests();
                if(clientRequests.getResponseTempCardOperation()) {
                    tempCardOperationData = processClientRequestsOperations(request.getIdOfOrg());
                }
            }
        } catch (Exception e) {
            String message = String.format("processTempCardsOperations: %s", e.getMessage());
            createSyncHistory(request.getIdOfOrg(),syncHistory, message);
            logger.error(message, e);
        }

        // Process library data
                /*try {
                    if (request.getLibraryData() != null) {
                        if (!RuntimeContext.getInstance().isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_B)) {
                            throw new Exception("no license slots available");
                        }

                        resLibraryData = processSyncLibraryData(request.getLibraryData());
                    }
                } catch (Exception e) {
                    logger.error(String.format("Failed to process library data, IdOfOrg == %s", request.getIdOfOrg()),
                            e);
                }

                try {
                    if (request.getLibraryData2() != null) {
                        if (!RuntimeContext.getInstance().isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_B)) {
                            throw new Exception("no license slots available");
                        }

                        resLibraryData2 = processSyncLibraryData2(request.getLibraryData2());
                    }
                } catch (Exception e) {
                    logger.error(String.format("Failed to process library data 2, IdOfOrg == %s", request.getIdOfOrg()),
                            e);
                } */

        // Process ResCategoriesDiscountsAndRules
        try {
            resCategoriesDiscountsAndRules = processCategoriesDiscountsAndRules(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String.format("Failed to process categories and rules, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistory(request.getIdOfOrg(),syncHistory, message);
            logger.error(message, e);
        }

        // Process ComplexRoles
        try {
            complexRoles = processComplexRoles();
        } catch (Exception e) {
            String message = String.format("processComplexRoles: %s", e.getMessage());
            logger.error(message, e);
        }

        // Process CorrectingNumbersOrdersRegistry
        try {
            correctingNumbersOrdersRegistry = processSyncCorrectingNumbersOrdersRegistry(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String.format("Failed to process numbers of Orders and EnterEvent, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistory(request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            orgOwnerData = processOrgOwnerData(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String.format("Failed to process org owner data, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistory(request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            questionaryData = processQuestionaryData(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String.format("Failed to process questionary data, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistory(request.getIdOfOrg(),syncHistory, message);
            logger.error(message, e);
        }

        try {
            goodsBasicBasketData = processGoodsBasicBasketData(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String.format("Failed to process goods basic basket data , IdOfOrg == %s",request.getIdOfOrg());
            createSyncHistory(request.getIdOfOrg(),syncHistory, message);
            logger.error(message, e);
        }

        try {
            if (request.getManager() != null) {
                manager = request.getManager();
                manager.process(persistenceSessionFactory);
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to process of Distribution Manager, IdOfOrg == %s",
                    request.getIdOfOrg()), e);
        }

        Date syncEndTime = new Date();

        if (request.getSyncType() == SyncType.TYPE_FULL) {
            // Update sync history - store sync end time and sync result
            updateSyncHistory(syncHistory.getIdOfSync(), syncResult, syncEndTime);
            updateFullSyncParam(request.getIdOfOrg());
        }
        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(),
                request.getOrg().getShortName(), idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, accIncRegistry, clientRegistry, resOrgStructure, resMenuExchange, resDiary, "",
                resEnterEvents, resTempCardsOperations, tempCardOperationData, resCategoriesDiscountsAndRules, complexRoles,
                correctingNumbersOrdersRegistry, manager, orgOwnerData, questionaryData, goodsBasicBasketData,
                directiveElement);
    }

    /* Do process short synchronization for update Client parameters */
    private SyncResponse buildClientsParamsSyncResponse(SyncRequest request) {

        Long idOfPacket = null; // регистируются и заполняются только для полной синхронизации
        SyncHistory idOfSync = null;

        SyncResponse.ResPaymentRegistry resPaymentRegistry = null;
        SyncResponse.AccRegistry accRegistry = null;
        SyncResponse.AccIncRegistry accIncRegistry = null;
        SyncResponse.ClientRegistry clientRegistry = null;
        SyncResponse.ResOrgStructure resOrgStructure = null;
        SyncResponse.ResMenuExchangeData resMenuExchange = null;
        SyncResponse.ResDiary resDiary = null;
        SyncResponse.ResEnterEvents resEnterEvents = null;
        ResTempCardsOperations resTempCardsOperations = null;
        TempCardOperationData tempCardOperationData = null;
        SyncResponse.ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = null;
        ComplexRoles complexRoles = null;
        SyncResponse.CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry = null;
        Manager manager = null;
        OrgOwnerData orgOwnerData = null;
        QuestionaryData questionaryData = null;
        GoodsBasicBasketData goodsBasicBasketData = null;
        DirectiveElement directiveElement = null;
        List<Long> errorClientIds = new ArrayList<Long>();

        try {
            processSyncClientParamRegistry(idOfSync, request.getIdOfOrg(), request.getClientParamRegistry(),
                    errorClientIds);
        } catch (Exception e) {
            logger.error(
                    String.format("Failed to process ClientParamRegistry, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
        }
        // Build client registry
        try {
            clientRegistry = processSyncClientRegistry(request.getIdOfOrg(),
                    request.getClientRegistryRequest(), errorClientIds);
        } catch (Exception e) {
            logger.error(String.format("Failed to build ClientRegistry, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
        }

        try {
            directiveElement = processSyncDirective(request.getIdOfOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build Directive, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
        }

        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(),
                request.getOrg().getShortName(), idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, accIncRegistry, clientRegistry, resOrgStructure, resMenuExchange, resDiary, "",
                resEnterEvents, resTempCardsOperations, tempCardOperationData, resCategoriesDiscountsAndRules, complexRoles,
                correctingNumbersOrdersRegistry, manager, orgOwnerData, questionaryData, goodsBasicBasketData,
                directiveElement);
    }

    /* Do process short synchronization for update payment register and account inc register */
    private SyncResponse buildAccIncSyncResponse(SyncRequest request) {

        Long idOfPacket = null, idOfSync = null; // регистируются и заполняются только для полной синхронизации

        SyncResponse.ResPaymentRegistry resPaymentRegistry = null;
        SyncResponse.AccRegistry accRegistry = null;
        SyncResponse.AccIncRegistry accIncRegistry = null;
        SyncResponse.ClientRegistry clientRegistry = null;
        SyncResponse.ResOrgStructure resOrgStructure = null;
        SyncResponse.ResMenuExchangeData resMenuExchange = null;
        SyncResponse.ResDiary resDiary = null;
        SyncResponse.ResEnterEvents resEnterEvents = null;
        ResTempCardsOperations resTempCardsOperations = null;
        TempCardOperationData tempCardOperationData = null;
        ComplexRoles complexRoles = null;
        SyncResponse.ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = null;
        SyncResponse.CorrectingNumbersOrdersRegistry correctingNumbersOrdersRegistry = null;
        Manager manager = null;
        OrgOwnerData orgOwnerData = null;
        QuestionaryData questionaryData = null;
        GoodsBasicBasketData goodsBasicBasketData = null;
        DirectiveElement directiveElement = null;
        List<Long> errorClientIds = new ArrayList<Long>();

        boolean bError = false;

        try {
            accIncRegistry = getAccIncRegistry(request.getIdOfOrg(),
                    request.getAccIncRegistryRequest().dateTime);
        } catch (Exception e) {
            logger.error(String.format("Failed to build AccIncRegistry, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
            accIncRegistry = new SyncResponse.AccIncRegistry();
            accIncRegistry.setDate(request.getAccIncRegistryRequest().dateTime);
            bError = true;
        }

        // Process paymentRegistry
        try {
            if (request.getPaymentRegistry() != null) {
                if (request.getPaymentRegistry().getPayments() != null) {
                    if (request.getPaymentRegistry().getPayments().hasMoreElements()) {
                        if (!RuntimeContext.getInstance()
                                .isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_P)) {
                            SyncHistory syncHistory = addSyncHistory(request.getIdOfOrg(), idOfPacket, new Date(), request.getClientVersion(),
                                    request.getRemoteAddr());
                            final String s = String.format("Failed to process PaymentRegistry, IdOfOrg == %s, no license slots available", request.getIdOfOrg());
                            createSyncHistory(request.getIdOfOrg(),syncHistory, s);
                            throw new Exception("no license slots available");
                        }
                    }
                    resPaymentRegistry = processSyncPaymentRegistry(idOfSync, request.getIdOfOrg(),
                            request.getPaymentRegistry(), errorClientIds);
                }
            }
        } catch (Exception e) {
            logger.error(
                    String.format("Failed to process PaymentRegistry, IdOfOrg == %s", request.getIdOfOrg()), e);
            bError = true;
        }

        try {
            directiveElement = processSyncDirective(request.getIdOfOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build Directive, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
        }

        // Process enterEvents
        try {
            if (request.getEnterEvents() != null) {
                resEnterEvents = processSyncEnterEvents(request.getEnterEvents());
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to process enter events, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
            bError = true;
        }

        try {
            if(request.getTempCardsOperations()!=null){
                resTempCardsOperations = processTempCardsOperations(request.getTempCardsOperations());
            }
        } catch (Exception e) {
            String message = String.format("processTempCardsOperations: %s", e.getMessage());
            logger.error(message, e);
        }

        if (bError) {
            DAOService.getInstance().updateLastUnsuccessfulBalanceSync(request.getIdOfOrg());
        } else {
            DAOService.getInstance().updateLastSuccessfulBalanceSync(request.getIdOfOrg());
        }

        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(),
                request.getOrg().getShortName(), idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, accIncRegistry, clientRegistry, resOrgStructure, resMenuExchange, resDiary, "",
                resEnterEvents, resTempCardsOperations, tempCardOperationData, resCategoriesDiscountsAndRules, complexRoles,
                correctingNumbersOrdersRegistry, manager, orgOwnerData, questionaryData, goodsBasicBasketData,
                directiveElement);
    }

    private void createSyncHistory(long idOfOrg, SyncHistory syncHistory, String s) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Org org = DAOUtils.getOrgReference(persistenceSession, idOfOrg);
            SyncHistoryException syncHistoryException = new SyncHistoryException(org, syncHistory, s);
            persistenceSession.save(syncHistoryException);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("createSyncHistory exception: ",e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private void updateFullSyncParam(long idOfOrg) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            DAOUtils.falseFullSyncByOrg(persistenceSession, idOfOrg);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private DirectiveElement processSyncDirective(long idOfOrg) throws Exception{
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        DirectiveElement directiveElement = new DirectiveElement();
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            directiveElement.process(persistenceSession, idOfOrg);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return directiveElement;
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
            SyncRequest.PaymentRegistry paymentRegistry, List<Long> errorClientIds) throws Exception {
        SyncResponse.ResPaymentRegistry resPaymentRegistry = new SyncResponse.ResPaymentRegistry();
        Enumeration<SyncRequest.PaymentRegistry.Payment> payments = paymentRegistry.getPayments();
        while (payments.hasMoreElements()) {
            SyncRequest.PaymentRegistry.Payment payment = payments.nextElement();
            SyncResponse.ResPaymentRegistry.Item resAcc;
            try {
                resAcc = processSyncPaymentRegistryPayment(idOfSync, idOfOrg, payment, errorClientIds);
                if (resAcc.getResult() != 0) {
                    logger.error("Failure in response payment registry: " + resAcc);
                }
            } catch (Exception e) {
                logger.error(String.format("Failed to process payment == %s", payment), e);
                resAcc = new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 100, "Internal error");
            }
            // TODO: если resAcc.getResult() != 0 записать в журнал ошибок синхры
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
                resAcc = new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null, null,
                        PaymentProcessResult.UNKNOWN_ERROR.getCode(),
                        PaymentProcessResult.UNKNOWN_ERROR.getDescription(), null);
            }
            resPaymentRegistry.addItem(resAcc);
        }
        return resPaymentRegistry;
    }

    private static SyncRequest.PaymentRegistry.Payment.Purchase findPurchase(
            SyncRequest.PaymentRegistry.Payment payment, Long idOfOrderDetail) throws Exception {
        Iterator<SyncRequest.PaymentRegistry.Payment.Purchase> purchases = payment.getPurchases();
        while (purchases.hasNext()) {
            SyncRequest.PaymentRegistry.Payment.Purchase purchase = purchases.next();
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

    private OrgOwnerData processOrgOwnerData(Long idOfOrg) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        OrgOwnerData orgOwnerData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new OrgOwnerProcessor(persistenceSession, idOfOrg);
            orgOwnerData = (OrgOwnerData) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return orgOwnerData;
    }

    private ResTempCardsOperations processTempCardsOperations(TempCardsOperations tempCardsOperations) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ResTempCardsOperations resTempCardsOperations = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new TempCardOperationProcessor(persistenceSession, tempCardsOperations);
            resTempCardsOperations = (ResTempCardsOperations) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return resTempCardsOperations;
    }

    private TempCardOperationData processClientRequestsOperations(Long idOfOrg) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        TempCardOperationData tempCardOperationData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new TempCardRequestProcessor(persistenceSession, idOfOrg);
            tempCardOperationData = (TempCardOperationData) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return tempCardOperationData;
    }

    private ComplexRoles processComplexRoles() throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ComplexRoles complexRoles = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            AbstractProcessor processor = new ComplexRoleProcessor(persistenceSession);
            complexRoles = (ComplexRoles) processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return complexRoles;
    }

    private QuestionaryData processQuestionaryData(Long idOfOrg) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        QuestionaryData questionaryData = new QuestionaryData();
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            questionaryData.process(persistenceSession, idOfOrg);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return questionaryData;
    }

    private GoodsBasicBasketData processGoodsBasicBasketData(Long idOfOrg) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        GoodsBasicBasketData goodsBasicBasketData = new GoodsBasicBasketData();
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            goodsBasicBasketData.process(persistenceSession, idOfOrg);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return goodsBasicBasketData;
    }

    private SyncResponse.ResPaymentRegistry.Item processSyncPaymentRegistryPayment(Long idOfSync, Long idOfOrg,
            SyncRequest.PaymentRegistry.Payment payment, List<Long> errorClientIds) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            //SyncHistory syncHistory = (SyncHistory) persistenceSession.load(SyncHistory.class, idOfSync);
            //Org organization = DAOUtils.findOrg(persistenceSession, idOfOrg);
            Long idOfOrganization = DAOUtils.getIdOfOrg(persistenceSession, idOfOrg);
            if (null == idOfOrganization) {
                return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 130,
                        String.format("Organization no found, IdOfOrg == %s, IdOfOrder == %s", idOfOrg,
                                payment.getIdOfOrder()));
            }
            // Check order existence
            if (DAOUtils.existOrder(persistenceSession, idOfOrg, payment.getIdOfOrder())) {
                Order order = DAOUtils
                        .findOrder(persistenceSession, new CompositeIdOfOrder(idOfOrg, payment.getIdOfOrder()));
                // if order == payment (may be last sync result was not transferred to client)
                Long orderCardNo = order.getCard() == null ? null : order.getCard().getCardNo();
                if ((("" + orderCardNo).equals("" + payment.getCardNo())) && (order.getCreateTime()
                        .equals(payment.getTime())) && (order.getSumByCard().equals(payment.getSumByCard()))) {
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
            // or for one of friendly organizations of specified one
            Set<Long> idOfFriendlyOrgSet = DAOUtils.getIdOfFriendlyOrg(persistenceSession, idOfOrg);
            if (null != client) {
                Org clientOrg = client.getOrg();
                if (!clientOrg.getIdOfOrg().equals(idOfOrg) && !idOfFriendlyOrgSet.contains(clientOrg.getIdOfOrg())) {
                    //
                    errorClientIds.add(idOfClient);
                    //return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 220, String.format(
                    //        "Client isn't registered for the specified organization, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s",
                    //        idOfOrg, payment.getIdOfOrder(), idOfClient));
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
            if (0 != payment.getSumByCard() && card == null) {
                // Check if card is specified
                return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 240, String.format(
                        "Payment has card part but doesn't specify CardNo, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s",
                        idOfOrg, payment.getIdOfOrder(), idOfClient));
            }
            // Create order
            RuntimeContext.getFinancialOpsManager()
                    .createOrderCharge(persistenceSession, payment, idOfOrg, client, card, payment.getConfirmerId());
            long totalPurchaseDiscount = 0;
            long totalPurchaseRSum = 0;
            // Register order details (purchase)
            Iterator<SyncRequest.PaymentRegistry.Payment.Purchase> purchases = payment.getPurchases();
            while (purchases.hasNext()) {
                SyncRequest.PaymentRegistry.Payment.Purchase purchase = purchases.next();
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
                if (purchase.getItemCode() != null) {
                    orderDetail.setItemCode(purchase.getItemCode());
                }
                if ( purchase.getIdOfRule() != null) {
                    orderDetail.setIdOfRule(purchase.getIdOfRule());
                }
                if (purchase.getGuidOfGoods() != null) {
                    Good good = DAOUtils.findGoodByGuid(persistenceSession, purchase.getGuidOfGoods());
                    if (good != null) {
                        orderDetail.setGood(good);
                    }
                }
                persistenceSession.save(orderDetail);
                totalPurchaseDiscount += purchase.getDiscount() * purchase.getQty();
                totalPurchaseRSum += purchase.getRPrice() * purchase.getQty();
            }
            // Check payment sums
            if (totalPurchaseRSum != payment.getRSum() || totalPurchaseDiscount != payment.getSocDiscount() + payment
                    .getTrdDiscount() + payment.getGrant()) {
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

            // !!!!! ОПОВЕЩЕНИЕ ПО СМС !!!!!!!!
            /* в случее если ананимного зака мы не знаем клиента */
            if(client!=null){
                RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                        .sendNotificationAsync(client, EventNotificationService.MESSAGE_PAYMENT,
                                generatePaymentNotificationParams(persistenceSession, client, payment));
            }

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

    private void processContragentAddInfo(Contragent contragent, HashMap<String, String> payAddInfo) {
        if (contragent.getRemarks() != null && contragent.getRemarks().length() > 0) {
            ParameterStringUtils.extractParameters("TSP.", contragent.getRemarks(), payAddInfo);
        }
    }

    private void processSyncClientParamRegistry(SyncHistory syncHistory, Long idOfOrg,
            SyncRequest.ClientParamRegistry clientParamRegistry, List<Long> errorClientIds) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Iterator<SyncRequest.ClientParamRegistry.ClientParamItem> clientParamItems = clientParamRegistry
                    .getPayments().iterator();

            HashMap<Long, HashMap<String, ClientGroup>> orgMap = new HashMap<Long, HashMap<String, ClientGroup>>(0);
            Org org = (Org) persistenceSession.get(Org.class, idOfOrg);
            Set<Org> orgSet = org.getFriendlyOrg();
            /* совместимость организаций которые не имеют дружественных организаций */
            orgSet.add(org);
            for (Org o : orgSet) {
                List clientGroups = DAOUtils.getClientGroupsByIdOfOrg(persistenceSession, o.getIdOfOrg());
                HashMap<String, ClientGroup> nameIdGroupMap = new HashMap<String, ClientGroup>();
                for (Object object : clientGroups) {
                    ClientGroup clientGroup = (ClientGroup) object;
                    nameIdGroupMap.put(clientGroup.getGroupName(), clientGroup);
                    orgMap.put(clientGroup.getCompositeIdOfClientGroup().getIdOfOrg(), nameIdGroupMap);
                }
                orgMap.put(o.getIdOfOrg(), nameIdGroupMap);
            }
            Long version = null;
            if (clientParamItems.hasNext()) {
                version = DAOUtils.updateClientRegistryVersion(persistenceSession);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;

            while (clientParamItems.hasNext()) {
                SyncRequest.ClientParamRegistry.ClientParamItem clientParamItem = clientParamItems.next();
                /*ClientGroup clientGroup = orgMap.get(2L).get(clientParamItem.getGroupName());
                *//* если группы нет то создаем *//*
                if(clientGroup == null){
                    clientGroup = DAOUtils.createClientGroup(persistenceSession, idOfOrg, clientParamItem.getGroupName());
                    *//* заносим в хэш - карту*//*
                    nameIdGroupMap.put(clientGroup.getGroupName(),clientGroup);
                }*/
                try {
                    //processSyncClientParamRegistryItem(idOfSync, idOfOrg, clientParamItem, orgMap, version);
                    processSyncClientParamRegistryItem(syncHistory.getIdOfSync(), clientParamItem, orgMap, version, errorClientIds);
                } catch (Exception e) {
                    String message = String.format("Failed to process clientParamItem == %s", idOfOrg);
                    createSyncHistory(idOfOrg,syncHistory, message);
                    logger.error(message, e);
                }
            }
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

    }

    private void processSyncClientParamRegistryItem(Long idOfSync, //Long idOfOrg,
            SyncRequest.ClientParamRegistry.ClientParamItem clientParamItem,
            HashMap<Long, HashMap<String, ClientGroup>> orgMap, Long version, List<Long> errorClientIds) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client client = DAOUtils.findClient(persistenceSession, clientParamItem.getIdOfClient());
            if (!orgMap.keySet().contains(client.getOrg().getIdOfOrg())) {
                errorClientIds.add(client.getIdOfClient());
                throw new IllegalArgumentException("Client from another organization");
            }
            /*if (!client.getOrg().getIdOfOrg().equals(idOfOrg)) {
                throw new IllegalArgumentException("Client from another organization");
            }*/
            client.setFreePayCount(clientParamItem.getFreePayCount());
            client.setFreePayMaxCount(clientParamItem.getFreePayMaxCount());
            client.setLastFreePayTime(clientParamItem.getLastFreePayTime());
            client.setDiscountMode(clientParamItem.getDiscountMode());
            /**/
            if (clientParamItem.getDiscountMode() == Client.DISCOUNT_MODE_BY_CATEGORY) {
                /* распарсим строку с категориями */
                if (clientParamItem.getCategoriesDiscounts() != null) {
                    String categories = clientParamItem.getCategoriesDiscounts();
                    client.setCategoriesDiscounts(categories);
                    if (!categories.equals("")) {
                        String[] catArray = categories.split(",");
                        List<Long> idOfCategoryDiscount = new ArrayList<Long>();
                        for (String number : catArray) {
                            idOfCategoryDiscount.add(Long.parseLong(number.trim()));
                        }
                        Criteria categoryDiscountCriteria = persistenceSession.createCriteria(CategoryDiscount.class);
                        categoryDiscountCriteria.add(Restrictions.in("idOfCategoryDiscount", idOfCategoryDiscount));
                        Set<CategoryDiscount> categoryDiscountSet = new HashSet<CategoryDiscount>();
                        for (Object object : categoryDiscountCriteria.list()) {
                            categoryDiscountSet.add((CategoryDiscount) object);
                        }
                        client.setCategories(categoryDiscountSet);
                    }
                }
            } else {
                /* Льгота по категориями то ощищаем */
                client.setCategoriesDiscounts("");
                client.setCategories(new HashSet<CategoryDiscount>());
            }
            if (clientParamItem.getAddress() != null) {
                client.setAddress(clientParamItem.getAddress());
            }
            if (clientParamItem.getEmail() != null) {
                client.setEmail(clientParamItem.getEmail());
                if (!StringUtils.isEmpty(clientParamItem.getEmail()) && clientParamItem.getNotifyViaEmail() == null) {
                    client.setNotifyViaEmail(true);
                }
            }
            if (clientParamItem.getMobilePhone() != null) {
                String mobile = Client.checkAndConvertMobile(clientParamItem.getMobilePhone());
                client.setMobile(mobile);
                if (!StringUtils.isEmpty(mobile)
                        && clientParamItem.getNotifyViaSMS() == null) {
                    client.setNotifyViaSMS(true);
                }
            }
            if (clientParamItem.getName() != null) {
                client.getPerson().setFirstName(clientParamItem.getName());
            }
            if (clientParamItem.getPhone() != null) {
                client.setPhone(clientParamItem.getPhone());
            }
            if (clientParamItem.getSecondName() != null) {
                client.getPerson().setSecondName(clientParamItem.getSecondName());
            }
            if (clientParamItem.getSurname() != null) {
                client.getPerson().setSurname(clientParamItem.getSurname());
            }
            if (clientParamItem.getRemarks() != null) {
                client.setRemarks(clientParamItem.getRemarks());
            }
            if (clientParamItem.getNotifyViaEmail() != null) {
                client.setNotifyViaEmail(clientParamItem.getNotifyViaEmail());
            }
            if (clientParamItem.getNotifyViaSMS() != null) {
                client.setNotifyViaSMS(clientParamItem.getNotifyViaSMS());
            }
            /* FAX клиента */
            if (clientParamItem.getFax() != null) {
                client.setFax(clientParamItem.getFax());
            }
            /* разрешает клиенту подтверждать оплату групового питания */
            if (clientParamItem.getCanConfirmGroupPayment() != null) {
                client.setCanConfirmGroupPayment(clientParamItem.getCanConfirmGroupPayment());
            }

            /* заносим клиента в группу */
            if (clientParamItem.getGroupName() != null) {
                ClientGroup clientGroup = orgMap.get(client.getOrg().getIdOfOrg()).get(clientParamItem.getGroupName());
                //если группы нет то создаем
                if (clientGroup == null) {
                    clientGroup = DAOUtils.createClientGroup(persistenceSession, client.getOrg().getIdOfOrg(),
                            clientParamItem.getGroupName());
                    // заносим в хэш - карту
                    orgMap.get(client.getOrg().getIdOfOrg()).put(clientGroup.getGroupName(), clientGroup);
                }

                if (clientGroup != null) {
                    client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                }
            }

            client.setClientRegistryVersion(version);

            persistenceSession.update(client);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private SyncResponse.CorrectingNumbersOrdersRegistry processSyncCorrectingNumbersOrdersRegistry(Long idOfOrg)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria orderCriteria = persistenceSession.createCriteria(Order.class);
            orderCriteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
            orderCriteria.setProjection(Projections.max("compositeIdOfOrder.idOfOrder"));
            List orderMax = orderCriteria.list();
            Criteria orderDetailCriteria = persistenceSession.createCriteria(OrderDetail.class);
            orderDetailCriteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
            orderDetailCriteria.setProjection(Projections.max("compositeIdOfOrderDetail.idOfOrderDetail"));
            List orderDetailMax = orderDetailCriteria.list();

            Criteria enterEventCriteria = persistenceSession.createCriteria(EnterEvent.class);
            enterEventCriteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
            enterEventCriteria.setProjection(Projections.max("compositeIdOfEnterEvent.idOfEnterEvent"));
            List enterEventMax = enterEventCriteria.list();

            persistenceTransaction.commit();
            persistenceTransaction = null;
            Long idOfOrderMax = (Long) orderMax.get(0),
                    idOfOrderDetail = (Long) orderDetailMax.get(0),
                    idOfEnterEvent = (Long) enterEventMax.get(0);
            if (idOfOrderMax == null) {
                idOfOrderMax = 0L;
            }
            if (idOfOrderDetail == null) {
                idOfOrderDetail = 0L;
            }
            if (idOfEnterEvent == null) {
                idOfEnterEvent = 0L;
            }
            return new SyncResponse.CorrectingNumbersOrdersRegistry(idOfOrderMax, idOfOrderDetail, idOfEnterEvent);
            //return null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private SyncResponse.ResOrgStructure processSyncOrgStructure(Long idOfOrg, SyncRequest.OrgStructure reqStructure, SyncHistory syncHistory)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org organization = DAOUtils.findOrg(persistenceSession, idOfOrg);
            // Ищем "лишние" группы
            List<ClientGroup> superfluousClientGroups = new ArrayList<ClientGroup>();
            for (ClientGroup clientGroup : organization.getClientGroups()) {
                if (clientGroup.isTemporaryGroup()) {
                    // добавляем временную группу в список для удаления если она уже есть в постоянных
                    if (findClientGroupByName(clientGroup.getGroupName(), reqStructure)) {
                        superfluousClientGroups.add(clientGroup);
                    }
                } else {
                    if (!findClientGroupById(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup(),
                            reqStructure)) {
                        superfluousClientGroups.add(clientGroup);
                    }
                }
            }
            // Удаляем "лишние" группы
            for (ClientGroup clientGroup : superfluousClientGroups) {
                // Отсоединяем группу от организации
                organization.removeClientGroup(clientGroup);
                // Удаляем из группы всех ее клиентов
                for (Client client : clientGroup.getClients()) {
                    client.setIdOfClientGroup(null);
                    client.setClientGroup(null);
                    client.setUpdateTime(new Date());
                    persistenceSession.update(client);
                }
                // Удаляем группу из БД
                persistenceSession.delete(clientGroup);
            }
            // Добавляем и обновляем группы согласно запроса
            for (SyncRequest.OrgStructure.Group reqGroup : reqStructure.getGroups()) {
                try {
                    processSyncOrgStructureGroup(persistenceSession, organization, reqGroup);
                } catch (Exception e) {
                    String message = String.format("Failed to process: %s", reqGroup);
                    createSyncHistory(idOfOrg,syncHistory, message);
                    logger.error(message, e);
                    return new SyncResponse.ResOrgStructure(2, message);
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

    private static boolean findClientGroupById(long idOfClientGroup, SyncRequest.OrgStructure reqStructure)
            throws Exception {
        for (SyncRequest.OrgStructure.Group group : reqStructure.getGroups()) {
            if (idOfClientGroup == group.getIdOfGroup()) {
                return true;
            }
        }
        return false;
    }

    private static boolean findClientGroupByName(String groupName, SyncRequest.OrgStructure reqStructure)
            throws Exception {
        for (SyncRequest.OrgStructure.Group group : reqStructure.getGroups()) {
            if (groupName.equals(group.getName())) {
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
            if (!StringUtils.equals(reqGroup.getName(), clientGroup.getGroupName())) {
                clientGroup.setGroupName(reqGroup.getName());
                persistenceSession.update(clientGroup);
            }
        } else {
            clientGroup = new ClientGroup(compositeIdOfClientGroup, reqGroup.getName());
            persistenceSession.save(clientGroup);
        }

        /* не нужный код - т.к. список клиентов всегда полный, из за него при регистрации с клиента сбрасывался класс после синхронизации
        // Ищем "лишних" клиентов
        List<Client> superfluousClients = new ArrayList<Client>();
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
        }*/

        // Добавляем в группу клиентов согласно запросу
        for (Long idOfClient : reqGroup.getClients()) {
            Long idOfClientGroup = DAOUtils.getClientGroup(persistenceSession, idOfClient, organization.getIdOfOrg());
            if (idOfClientGroup != null && (idOfClientGroup.longValue() == clientGroup.getCompositeIdOfClientGroup()
                    .getIdOfClientGroup().longValue())) {
                continue;
            }
            ////
            Client client = DAOUtils.findClient(persistenceSession, idOfClient);
            Set<Long> idOfFriendlyOrgSet = DAOUtils
                    .getIdOfFriendlyOrg(persistenceSession, client.getOrg().getIdOfOrg());
            if (null == client) {
                logger.info(String.format("Client with IdOfClient == %s not found", idOfClient));
            } else if (!client.getOrg().getIdOfOrg().equals(organization.getIdOfOrg()) && !idOfFriendlyOrgSet
                    .contains(client.getOrg().getIdOfOrg())) {
                logger.error(String.format(
                        "Client with IdOfClient == %s belongs to other organization. Client: %s, IdOfOrg by request: %s",
                        idOfClient, client, organization.getIdOfOrg()));
            } else if(client.getOrg().getIdOfOrg().equals(organization.getIdOfOrg())) {
                //ClientGroup curClientGroup = client.getClientGroup();
                //if (null == curClientGroup || !curClientGroup.getCompositeIdOfClientGroup()
                //        .equals(clientGroup.getCompositeIdOfClientGroup())) {
                client.setClientGroup(clientGroup);
                client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                client.setUpdateTime(new Date());
                //clientGroup.addClient(client);
                persistenceSession.update(client);
                //persistenceSession.update(clientGroup);
                //}
            }
        }
    }

    private static boolean find(Client client, SyncRequest.OrgStructure.Group reqGroup) throws Exception {
        for (Long aLong : reqGroup.getClients()) {
            if (client.getIdOfClient().equals(aLong)) {
                return true;
            }
        }
        return false;
    }

    private void addClientVersionAndRemoteAddressByOrg(Long idOfOrg, String clientVersion, String remoteAddress) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            DAOUtils.updateClientVersionAndRemoteAddressByOrg(persistenceSession, idOfOrg, clientVersion,
                    remoteAddress);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private SyncHistory addSyncHistory(Long idOfOrg, Long idOfPacket, Date startTime, String clientVersion,
            String remoteAddress) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org organization = DAOUtils.getOrgReference(persistenceSession, idOfOrg);
            SyncHistory syncHistory = new SyncHistory(organization, startTime, idOfPacket, clientVersion,
                    remoteAddress);
            persistenceSession.save(syncHistory);
            //Long idOfSync = syncHistory.getIdOfSync();

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return syncHistory;
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

            Set<Long> idOfOrgSet = new HashSet<Long>();

            Org org = (Org) persistenceSession.get(Org.class, idOfOrg);
            Set<Org> orgSet = org.getFriendlyOrg();
            /* совместимость организаций которые не имеют дружественных организаций */
            for (Org o : orgSet) {
                idOfOrgSet.add(o.getIdOfOrg());
            }
            idOfOrgSet.add(idOfOrg);

            for (Object[] v : DAOUtils.getClientsAndCardsForOrgs(persistenceSession, idOfOrgSet)) {
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
            List<AccountTransaction> accountTransactionList = DAOUtils
                    .getAccountTransactionsForOrgSinceTime(persistenceSession, idOfOrg, fromDateTime, currentDate,
                            AccountTransaction.PAYMENT_SYSTEM_TRANSACTION_SOURCE_TYPE);
            for (AccountTransaction accountTransaction : accountTransactionList) {
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
            SyncRequest.ClientRegistryRequest clientRegistryRequest, List<Long> errorClientIds) throws Exception {
        SyncResponse.ClientRegistry clientRegistry = new SyncResponse.ClientRegistry();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org organization = DAOUtils.getOrgReference(persistenceSession, idOfOrg);
            List clients;
            if (organization.getFriendlyOrg() == null || organization.getFriendlyOrg().isEmpty()) {
                clients = DAOUtils.findNewerClients(persistenceSession, organization, clientRegistryRequest.getCurrentVersion());
            } else {
                List<Org> orgList = new ArrayList<Org>(organization.getFriendlyOrg());
                orgList.add(organization);
                clients = DAOUtils.findNewerClients(persistenceSession, orgList, clientRegistryRequest.getCurrentVersion());
            }

            for (Object object : clients) {
                Client client = (Client) object;
                clientRegistry.addItem(new SyncResponse.ClientRegistry.Item(client));
            }

            if(!errorClientIds.isEmpty()){
                List errorClients = DAOUtils.fetchErrorClientsWithOutFriendlyOrg(persistenceSession, organization.getFriendlyOrg(), errorClientIds);
                ClientGroup clientGroup = DAOUtils.findClientGroupByGroupNameAndIdOfOrg(persistenceSession, organization.getIdOfOrg(), ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
                // Есть возможность отсутсвия даной группы
                if(clientGroup==null){
                    clientGroup = DAOUtils.createClientGroup(persistenceSession, organization.getIdOfOrg(), ClientGroup.Predefined.CLIENT_LEAVING);
                }
                for (Object object : errorClients) {
                    Client client = (Client) object;
                    client.setClientGroup(clientGroup);
                    clientRegistry.addItem(new SyncResponse.ClientRegistry.Item(client));
                }
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

                Org organization = DAOUtils.getOrgReference(persistenceSession, idOfOrg);

                boolean bOrgIsMenuExchangeSource = isOrgMenuExchangeSource(persistenceSession, idOfOrg);

                /// сохраняем секцию Settings
                if (bOrgIsMenuExchangeSource && (reqMenu.getSettingsSectionRawXML() != null)) {
                    persistenceTransaction = persistenceSession.beginTransaction();

                    MenuExchange menuExchangeSettings = new MenuExchange(new Date(0), idOfOrg,
                            reqMenu.getSettingsSectionRawXML(), MenuExchange.FLAG_SETTINGS);
                    persistenceSession.saveOrUpdate(menuExchangeSettings);

                    persistenceSession.flush();
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                }

                Enumeration<SyncRequest.ReqMenu.Item> menuItems = reqMenu.getItems();
                boolean bFirstMenuItem = true;
                while (menuItems.hasMoreElements()) {
                    //  Открываем тразнакцию для каждого дня
                    persistenceTransaction = persistenceSession.beginTransaction();

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
                    HashMap<Long, MenuDetail> localIdsToMenuDetailMap = new HashMap<Long, MenuDetail>();
                    processReqMenuDetails(persistenceSession, menu, item, item.getReqMenuDetails(),
                            localIdsToMenuDetailMap);
                    processReqComplexInfos(persistenceSession, organization, menuDate, menu, item.getReqComplexInfos(),
                            localIdsToMenuDetailMap);
                    bFirstMenuItem = false;


                    //  Подтверждаем транзакцию для каждого дня
                    persistenceSession.flush();
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                }

            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }
    }

    private void processReqComplexInfos(Session persistenceSession, Org organization, Date menuDate, Menu menu,
            List<SyncRequest.ReqMenu.Item.ReqComplexInfo> reqComplexInfos,
            HashMap<Long, MenuDetail> localIdsToMenuDetailMap) throws Exception {
        DAOUtils.deleteComplexInfoForDate(persistenceSession, organization, menuDate);

        for (SyncRequest.ReqMenu.Item.ReqComplexInfo reqComplexInfo : reqComplexInfos) {
            ComplexInfo complexInfo = new ComplexInfo(reqComplexInfo.getComplexId(), organization, menuDate,
                    reqComplexInfo.getModeFree(), reqComplexInfo.getModeGrant(), reqComplexInfo.getModeOfAdd(),
                    reqComplexInfo.getComplexMenuName());
            Integer useTrDiscount = reqComplexInfo.getUseTrDiscount();
            Long currentPrice = reqComplexInfo.getCurrentPrice();
            String goodsGuid = reqComplexInfo.getGoodsGuid();
            if (useTrDiscount != null) {
                complexInfo.setUseTrDiscount(useTrDiscount);
            }
            if (currentPrice != null) {
                complexInfo.setCurrentPrice(currentPrice);
            }
            if (goodsGuid != null) {
                Good good = DAOUtils.findGoodByGuid(persistenceSession, goodsGuid);
                complexInfo.setGood(good);
            }
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail = reqComplexInfo.getReqMenuDetail();
            if (reqMenuDetail != null) {
                MenuDetail menuDetailOptional = DAOUtils.findMenuDetailByLocalId(persistenceSession, menu,
                        reqComplexInfo.getReqMenuDetail().getIdOfMenu());
                if (menuDetailOptional != null) {
                    complexInfo.setMenuDetail(menuDetailOptional);
                }
            }

            SyncRequest.ReqMenu.Item.ReqComplexInfo.ReqComplexInfoDiscountDetail reqComplexInfoDiscountDetail = reqComplexInfo
                    .getComplexInfoDiscountDetail();
            if (reqComplexInfoDiscountDetail != null) {
                double size = reqComplexInfoDiscountDetail.getSize();
                int isAllGroups = reqComplexInfoDiscountDetail.getIsAllGroups();
                Integer maxCount = reqComplexInfoDiscountDetail.getMaxCount();
                Long idOfClientGroup = reqComplexInfoDiscountDetail.getIdOfClientGroup();
                ComplexInfoDiscountDetail complexInfoDiscountDetail = new ComplexInfoDiscountDetail(size, isAllGroups);
                if (idOfClientGroup != null) {
                    CompositeIdOfClientGroup compId = new CompositeIdOfClientGroup(organization.getIdOfOrg(),
                            idOfClientGroup);
                    ClientGroup clientGroup = DAOUtils.findClientGroup(persistenceSession, compId);
                    complexInfoDiscountDetail.setClientGroup(clientGroup);
                    complexInfoDiscountDetail.setOrg(clientGroup.getOrg());
                }
                if (maxCount != null) {
                    complexInfoDiscountDetail.setMaxCount(maxCount);
                }

                persistenceSession.save(complexInfoDiscountDetail);

                complexInfo.setDiscountDetail(complexInfoDiscountDetail);
            }
            persistenceSession.save(complexInfo);

            for (SyncRequest.ReqMenu.Item.ReqComplexInfo.ReqComplexInfoDetail reqComplexInfoDetail : reqComplexInfo
                    .getComplexInfoDetails()) {
                //MenuDetail menuDetail = DAOUtils.findMenuDetailByLocalId(persistenceSession, menu,
                //        reqComplexInfoDetail.getReqMenuDetail().getIdOfMenu());
                MenuDetail menuDetail = localIdsToMenuDetailMap
                        .get(reqComplexInfoDetail.getReqMenuDetail().getIdOfMenu());
                if (menuDetail == null) {
                    throw new Exception(
                            "MenuDetail not found for complex detail with localIdOfMenu=" + reqComplexInfoDetail
                                    .getReqMenuDetail().getIdOfMenu());
                }
                menuDetail = (MenuDetail) persistenceSession.get(MenuDetail.class, menuDetail.getIdOfMenuDetail());
                ComplexInfoDetail complexInfoDetail = new ComplexInfoDetail(complexInfo, menuDetail);
                Long idOfItem = reqComplexInfoDetail.getIdOfItem();
                if (idOfItem != null) {
                    complexInfoDetail.setIdOfItem(idOfItem);
                }
                Integer menuItemCount = reqComplexInfoDetail.getCount();
                if (menuItemCount != null) {
                    complexInfoDetail.setCount(menuItemCount);
                }
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

    private void processReqMenuDetails(Session persistenceSession, Menu menu, SyncRequest.ReqMenu.Item item,
            Enumeration<SyncRequest.ReqMenu.Item.ReqMenuDetail> reqMenuDetails,
            HashMap<Long, MenuDetail> localIdsToMenuDetailMap) throws Exception {
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

        // Добавляем новые элементы из пришедшего меню
        while (reqMenuDetails.hasMoreElements()) {
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail = reqMenuDetails.nextElement();
            boolean exists = false;

            /*if ((bOrgIsMenuExchangeSource && DAOUtils.findMenuDetailByLocalId(persistenceSession, menu, reqMenuDetail.getIdOfMenu()) == null) ||
            (!bOrgIsMenuExchangeSource && DAOUtils.findMenuDetailByPathAndPrice(persistenceSession, menu, reqMenuDetail.getPath(), reqMenuDetail.getPrice()) == null)) {*/
            for (MenuDetail menuDetail : menu.getMenuDetails()) {
                exists = areMenuDetailsEqual(menuDetail, reqMenuDetail);
                if (exists) {
                    localIdsToMenuDetailMap.put(reqMenuDetail.getIdOfMenu(), menuDetail);
                    break;
                }
            }

            if (!exists) {
                MenuDetail newMenuDetail = new MenuDetail(menu, reqMenuDetail.getPath(), reqMenuDetail.getName(),
                        reqMenuDetail.getMenuOrigin(), reqMenuDetail.getAvailableNow(), reqMenuDetail.getFlags());
                newMenuDetail.setLocalIdOfMenu(reqMenuDetail.getIdOfMenu());
                newMenuDetail.setGroupName(reqMenuDetail.getGroup() == null ? ""
                        : reqMenuDetail.getGroup()); // в старых версиях клиента могут быть без группы
                newMenuDetail.setMenuDetailOutput(reqMenuDetail.getOutput());
                newMenuDetail.setPrice(reqMenuDetail.getPrice());
                newMenuDetail.setPriority(reqMenuDetail.getPriority());
                newMenuDetail.setProtein(reqMenuDetail.getProtein());
                newMenuDetail.setFat(reqMenuDetail.getFat());
                newMenuDetail.setCarbohydrates(reqMenuDetail.getCarbohydrates());
                newMenuDetail.setCalories(reqMenuDetail.getCalories());
                newMenuDetail.setVitB1(reqMenuDetail.getVitB1());
                newMenuDetail.setVitC(reqMenuDetail.getVitC());
                newMenuDetail.setVitA(reqMenuDetail.getVitA());
                newMenuDetail.setVitE(reqMenuDetail.getVitE());
                newMenuDetail.setMinCa(reqMenuDetail.getMinCa());
                newMenuDetail.setMinP(reqMenuDetail.getMinP());
                newMenuDetail.setMinMg(reqMenuDetail.getMinMg());
                newMenuDetail.setMinFe(reqMenuDetail.getMinFe());

                persistenceSession.save(newMenuDetail);
                menu.addMenuDetail(newMenuDetail);

                localIdsToMenuDetailMap.put(reqMenuDetail.getIdOfMenu(), newMenuDetail);
            }
        }
    }

    private static boolean areMenuDetailsEqual(MenuDetail menuDetail,
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail) {
        // для организации - источника меню делаем поиска по локальным идентификаторам
        //if (bOrgIsMenuExchangeSource && reqMenuDetail.getIdOfMenu() != null && menuDetail.getLocalIdOfMenu() != null) {
        //    return reqMenuDetail.getIdOfMenu().equals(menuDetail.getLocalIdOfMenu());
        //}
        // для остальных - по путям и ценам, т.к. в клиентах некорректно обновлялось меню, каждый раз перезаписывалось с новыми ид.
        //else {
        return reqMenuDetail.getPath().equals(menuDetail.getMenuPath()) &&
                (reqMenuDetail.getPrice() == null || menuDetail.getPrice() == null
                        || reqMenuDetail.getPrice().longValue() == menuDetail.getPrice().longValue()) && (
                reqMenuDetail.getGroup() == null || menuDetail.getGroupName() == null || reqMenuDetail.getGroup()
                        .equals(menuDetail.getGroupName())) && (reqMenuDetail.getOutput() == null
                || menuDetail.getMenuDetailOutput() == null || reqMenuDetail.getOutput()
                .equals(menuDetail.getMenuDetailOutput()));
        //}
    }

    private boolean isOrgMenuExchangeSource(Session persistenceSession, Long idOfOrg) {
        return DAOUtils.isOrgMenuExchangeSource(persistenceSession, idOfOrg);
    }

    private static boolean find(MenuDetail menuDetail, SyncRequest.ReqMenu.Item menuItem) throws Exception {
        Enumeration<SyncRequest.ReqMenu.Item.ReqMenuDetail> reqMenuDetails = menuItem.getReqMenuDetails();
        while (reqMenuDetails.hasMoreElements()) {
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail = reqMenuDetails.nextElement();
            if (areMenuDetailsEqual(menuDetail, reqMenuDetail)) {
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

        for (SyncRequest.EnterEvents.EnterEvent e : enterEvents.getEvents()) {

            try {
                persistenceSession = persistenceSessionFactory.openSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                // Check enter event existence
                if (DAOUtils.existEnterEvent(persistenceSession, e.getIdOfOrg(), e.getIdOfEnterEvent())) {
                    EnterEvent ee = DAOUtils.findEnterEvent(persistenceSession,
                            new CompositeIdOfEnterEvent(e.getIdOfEnterEvent(), e.getIdOfOrg()));
                    // Если ENTER событие существует (может быть последний результат синхронизации не был передан клиенту)
final boolean checkClient = (ee.getClient() == null && e.getIdOfClient() == null) || (ee.getClient() != null && ee
                                    .getClient().getIdOfClient().equals(e.getIdOfClient()));
final boolean checkTempCard = (ee.getIdOfTempCard() == null && e.getIdOfTempCard() == null) || (ee.getIdOfTempCard() != null && ee.getIdOfTempCard().equals(e.getIdOfTempCard()));

                    if (checkClient && ee.getEvtDateTime().equals(e.getEvtDateTime()) && checkTempCard) {
                        SyncResponse.ResEnterEvents.Item item = new SyncResponse.ResEnterEvents.Item(
                                e.getIdOfEnterEvent(), SyncResponse.ResEnterEvents.Item.RC_OK,
                                "Enter event already registered");
                        resEnterEvents.addItem(item);
                    } else {
                        SyncResponse.ResEnterEvents.Item item = new SyncResponse.ResEnterEvents.Item(
                                e.getIdOfEnterEvent(), SyncResponse.ResEnterEvents.Item.RC_OK, String.format(
                                "Enter event already registered but attributes differ, idOfOrg == %d, idOfEnterEvent == %d",
                                e.getIdOfOrg(), e.getIdOfEnterEvent()));
                        resEnterEvents.addItem(item);
                    }
                } else {
                    // find client by id
                    Client client = null;
                    if (e.getIdOfClient() != null) {
                        client = (Client) persistenceSession.get(Client.class, e.getIdOfClient());
                        if (client == null) {
                            SyncResponse.ResEnterEvents.Item item = new SyncResponse.ResEnterEvents.Item(
                                    e.getIdOfEnterEvent(), SyncResponse.ResEnterEvents.Item.RC_CLIENT_NOT_FOUND,
                                    String.format("Client not found: %d", e.getIdOfClient()));
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


                    SyncResponse.ResEnterEvents.Item item = new SyncResponse.ResEnterEvents.Item(e.getIdOfEnterEvent(),
                            0, null);
                    resEnterEvents.addItem(item);

                    if (isDateToday(e.getEvtDateTime()) &&
                            e.getIdOfClient() != null &&
                            (e.getPassDirection() == EnterEvent.ENTRY || e.getPassDirection() == EnterEvent.EXIT ||
                                    e.getPassDirection() == EnterEvent.RE_ENTRY
                                    || e.getPassDirection() == EnterEvent.RE_EXIT)) {
   final EventNotificationService notificationService = RuntimeContext.getAppContext().getBean(EventNotificationService.class);
   final String[] values = generateNotificationParams(persistenceSession, client, e.getPassDirection(), e.getEvtDateTime());
     notificationService.sendNotificationAsync(client,EventNotificationService.NOTIFICATION_ENTER_EVENT, values);
                    }

                    /// Формирование журнала транзакции
                    if (RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_JOURNAL_TRANSACTIONS) &&
                            (e.getPassDirection() == EnterEvent.ENTRY || e.getPassDirection() == EnterEvent.EXIT ||
                                    e.getPassDirection() == EnterEvent.RE_ENTRY
                                    || e.getPassDirection() == EnterEvent.RE_EXIT) && e.getIdOfCard() != null) {
                        Card card = DAOUtils.findCardByCardNo(persistenceSession, e.getIdOfCard());
                        final CompositeIdOfEnterEvent compositeIdOfEnterEvent = enterEvent.getCompositeIdOfEnterEvent();
                        if (card == null) {
                            final String message = "Не найдена карта по событию прохода: idOfOrg=%d, idOfEnterEvent=%d, idOfCard=%d";
                            logger.error(String.format(message,
                                    compositeIdOfEnterEvent.getIdOfOrg(),
                                    compositeIdOfEnterEvent.getIdOfEnterEvent(), e.getIdOfCard()));
                        }

                        if (card != null && card.getCardType() == Card.TYPE_UEC) {
                            String OGRN = DAOUtils.extraxtORGNFromOrgByIdOfOrg(persistenceSession, e.getIdOfOrg());
                            String transCode;
                            switch (e.getPassDirection()) {
                                case EnterEvent.ENTRY:
                                case EnterEvent.RE_ENTRY:
                                    transCode = "IN";
                                    break;
                                case EnterEvent.EXIT:
                                case EnterEvent.RE_EXIT:
                                    transCode = "OUT";
                                    break;
                                default:
                                    transCode = null;
                            }
                            if (transCode != null) {
                                TransactionJournal transactionJournal = new TransactionJournal(
                                        compositeIdOfEnterEvent.getIdOfOrg(),
                                        compositeIdOfEnterEvent.getIdOfEnterEvent(), new Date(),
                                        OGRN /*org.getOGRN()*/, TransactionJournal.SERVICE_CODE_SCHL_ACC, transCode,
                                        TransactionJournal.CARD_TYPE_CODE_UEC,
                                        TransactionJournal.CARD_TYPE_ID_CODE_MUID, Card.TYPE_NAMES[card.getCardType()],
                                        Long.toHexString(card.getCardNo()), client.getSan(), client.getContractId(),
                                        client.getClientGroupTypeAsString(), e.getEnterName());
                                persistenceSession.save(transactionJournal);
                            }
                        }
                    }

                }
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception ex) {
                logger.error("Save enter event to database error: ", e);
                resEnterEvents = new SyncResponse.ResEnterEvents();
                for (SyncRequest.EnterEvents.EnterEvent ee : enterEvents.getEvents()) {
                    SyncResponse.ResEnterEvents.Item item;
                    item = new SyncResponse.ResEnterEvents.Item(ee.getIdOfEnterEvent(),1, "Save to data base error");
                    resEnterEvents.addItem(item);
                }
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }

        return resEnterEvents;
    }

    private SyncResponse.ResCategoriesDiscountsAndRules processCategoriesDiscountsAndRules(Long idOfOrg) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        SyncResponse.ResCategoriesDiscountsAndRules resCategoriesDiscountsAndRules = new SyncResponse.ResCategoriesDiscountsAndRules();
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria criteriaDiscountRule = persistenceSession.createCriteria(DiscountRule.class);
            Org org = (Org) persistenceSession.load(Org.class, idOfOrg);
            Set<CategoryOrg> categoryOrgSet = org.getCategories();
            if (!categoryOrgSet.isEmpty()) {
                for (Object object : criteriaDiscountRule.list()) {
                    DiscountRule discountRule = (DiscountRule) object;
                    /*
                   * проверяем вхождение одного множества в другое
                   * результат categoryOrgSet.containsAll(discountRule.getCategoryOrgs())
                   * вернет true если все категории организации взятые из таблицы организации
                   * пренадлежат категорий организаций приявязанных к Правилам скидок.
                   *
                   * Если все категории организации содержатся в правиле то выводим
                   * */
                    boolean bIncludeRule = false;
                    if (discountRule.getCategoryOrgs().isEmpty()) {
                        bIncludeRule = true;
                    } else if (categoryOrgSet.containsAll(discountRule.getCategoryOrgs())) {
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
                        SyncResponse.ResCategoriesDiscountsAndRules.DCRI dcri = new SyncResponse.ResCategoriesDiscountsAndRules.DCRI(
                                discountRule);
                        resCategoriesDiscountsAndRules.addDCRI(dcri);
                    }
                }
            }  /* Организация не пренадлежит ни к одной категории*/ else {
                for (Object object : criteriaDiscountRule.list()) {
                    DiscountRule discountRule = (DiscountRule) object;
                    /* если правила не установлены категории организаций то отправляем*/
                    if (discountRule.getCategoryOrgs().isEmpty()) {
                        SyncResponse.ResCategoriesDiscountsAndRules.DCRI dcri = new SyncResponse.ResCategoriesDiscountsAndRules.DCRI(
                                discountRule);
                        resCategoriesDiscountsAndRules.addDCRI(dcri);
                    }
                }
            }

            Criteria criteria = persistenceSession.createCriteria(CategoryDiscount.class);


            List<CategoryDiscount> categoryDiscounts = (List<CategoryDiscount>) criteria.list();
            for (CategoryDiscount categoryDiscount : categoryDiscounts) {
                SyncResponse.ResCategoriesDiscountsAndRules.DCI dci = new SyncResponse.ResCategoriesDiscountsAndRules.DCI(
                        categoryDiscount.getIdOfCategoryDiscount(), categoryDiscount.getCategoryName(),
                        categoryDiscount.getCategoryType().getValue(), categoryDiscount.getDiscountRules());
                resCategoriesDiscountsAndRules.addDCI(dci);
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
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

    private void createTempCard(Session persistenceSession, Long idOfOrg, long cardNo, String cardPrintedNo) throws Exception {
        Org org = DAOUtils.getOrgReference(persistenceSession, idOfOrg);
        if (org == null) {
            throw new Exception(String.format("Организация не найдена: %d", idOfOrg));
        }
        Card c = DAOUtils.findCardByCardNo(persistenceSession, cardNo);
        if (c != null) {
            throw new Exception(
                    String.format("Карта уже зарегистрирована на клиента: %d", c.getClient().getIdOfClient()));
        }
        CardTemp cardTemp = DAOUtils.findCardTempByCardNo(persistenceSession, cardNo);
        if (cardTemp != null) {
            if(cardTemp.getCardPrintedNo()!=null && !cardTemp.getCardPrintedNo().equals(cardPrintedNo)){
                cardTemp.setCardPrintedNo(cardPrintedNo);
            } else {
                throw new Exception("Временная карта уже зарегистрирована на временная: " );
            }
        } else {
            cardTemp = new CardTemp(org,cardNo, cardPrintedNo);

        }
        persistenceSession.save(cardTemp);
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


    private String[] generateNotificationParams(Session session, Client client, int passDirection, Date eventDate)
            throws Exception {
        String eventName = "";
        if (passDirection == EnterEvent.ENTRY) {
            eventName = "Вход в школу";
        } else if (passDirection == EnterEvent.EXIT) {
            eventName = "Выход из школы";
        } else if (passDirection == EnterEvent.RE_ENTRY) {
            eventName = "Вход в школу";
        } else if (passDirection == EnterEvent.RE_EXIT) {
            eventName = "Выход из школы";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(eventDate);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String time = (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
        //String clientName = client.getPerson().getSurname() + " " + client.getPerson().getFirstName();
        return new String[]{
                "balance", CurrencyStringUtils.copecksToRubles(client.getBalance()), "contractId",
                ContractIdFormat.format(client.getContractId()), "surname", client.getPerson().getSurname(),
                "firstName", client.getPerson().getFirstName(), "eventName", eventName, "eventTime", time};
    }

    private String[] generatePaymentNotificationParams(Session session, Client client, SyncRequest.PaymentRegistry.Payment payment) {
        long complexes = 0L;
        long others = 0L;
        Iterator<SyncRequest.PaymentRegistry.Payment.Purchase> purchases = payment.getPurchases();
        while (purchases.hasNext()) {
            SyncRequest.PaymentRegistry.Payment.Purchase purchase = purchases.next();
            if (purchase.getType() >= OrderDetail.TYPE_COMPLEX_MIN && purchase.getType() <= OrderDetail.TYPE_COMPLEX_MAX) {
                complexes += purchase.getSocDiscount() + purchase.getRPrice();
            } else {
                others += purchase.getSocDiscount() + purchase.getRPrice();
            }
        }

        //String date = new SimpleDateFormat("dd.MM.yy HH:mm").format(new Date(System.currentTimeMillis()));
        String date = new SimpleDateFormat("dd.MM.yy HH:mm").format(payment.getTime());
        return new String[] {
                "date", date,
                "contractId", String.valueOf(client.getContractId()),
                "others", CurrencyStringUtils.copecksToRubles(others),
                "complexes", CurrencyStringUtils.copecksToRubles(complexes) };
    }

    private static boolean isDateToday(Date date) {
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        if (today.get(Calendar.DATE) == dateCalendar.get(Calendar.DATE) &&
                today.get(Calendar.MONTH) == dateCalendar.get(Calendar.MONTH) &&
                today.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR)) {
            return true;
        }
        return false;
    }

}