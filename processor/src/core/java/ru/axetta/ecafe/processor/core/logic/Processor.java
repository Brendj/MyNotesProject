/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.AccessDiniedException;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.client.ContractIdGenerator;
import ru.axetta.ecafe.processor.core.event.EventNotificator;
import ru.axetta.ecafe.processor.core.event.PaymentProcessEvent;
import ru.axetta.ecafe.processor.core.event.SyncEvent;
import ru.axetta.ecafe.processor.core.order.OrderCancelProcessor;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.ClientPaymentOrderProcessor;
import ru.axetta.ecafe.processor.core.payment.PaymentProcessor;
import ru.axetta.ecafe.processor.core.payment.PaymentRequest;
import ru.axetta.ecafe.processor.core.payment.PaymentResponse;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgSyncWritableRepository;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.bk.BKRegularPaymentSubscriptionService;
import ru.axetta.ecafe.processor.core.sync.*;
import ru.axetta.ecafe.processor.core.sync.handlers.client.request.TempCardOperationData;
import ru.axetta.ecafe.processor.core.sync.handlers.client.request.TempCardRequestProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.complex.roles.ComplexRoleProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.complex.roles.ComplexRoles;
import ru.axetta.ecafe.processor.core.sync.handlers.org.owners.OrgOwnerData;
import ru.axetta.ecafe.processor.core.sync.handlers.org.owners.OrgOwnerProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.*;
import ru.axetta.ecafe.processor.core.sync.handlers.registry.accounts.AccountsRegistryHandler;
import ru.axetta.ecafe.processor.core.sync.handlers.registry.cards.CardsOperationsRegistryHandler;
import ru.axetta.ecafe.processor.core.sync.handlers.registry.operations.account.AccountOperationsRegistryHandler;
import ru.axetta.ecafe.processor.core.sync.handlers.registry.operations.account.ResAccountOperationsRegistry;
import ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations.ResTempCardsOperations;
import ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations.TempCardOperationProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations.TempCardsOperations;
import ru.axetta.ecafe.processor.core.sync.manager.Manager;
import ru.axetta.ecafe.processor.core.sync.process.ClientGuardianDataProcessor;
import ru.axetta.ecafe.processor.core.sync.request.*;
import ru.axetta.ecafe.processor.core.sync.response.*;
import ru.axetta.ecafe.processor.core.sync.response.registry.ResCardsOperationsRegistry;
import ru.axetta.ecafe.processor.core.sync.response.registry.accounts.AccountsRegistry;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ParameterStringUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrTokenizer;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.*;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.axetta.ecafe.processor.core.logic.ClientManager.findGuardiansByClient;
import static ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils.*;

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
        OrderCancelProcessor {

    private static final Logger logger = LoggerFactory.getLogger(Processor.class);
    private static final int RESPONSE_MENU_PERIOD_IN_DAYS = 7;
    private final SessionFactory persistenceSessionFactory;
    private final EventNotificator eventNotificator;

    public Processor(SessionFactory persistenceSessionFactory, EventNotificator eventNotificator) {
        this.persistenceSessionFactory = persistenceSessionFactory;
        this.eventNotificator = eventNotificator;
    }

    public PaymentResponse.ResPaymentRegistry.Item processPayPaymentRegistryPayment(Long idOfContragent,
            PaymentRequest.PaymentRegistry.Payment payment) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Contragent contragent = findContragent(persistenceSession, idOfContragent);
            if (null == contragent) {
                return new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null, null, null,
                        PaymentProcessResult.CONTRAGENT_NOT_FOUND.getCode(),
                        String.format("%s. IdOfContragent == %s, ContractId == %s",
                                PaymentProcessResult.CONTRAGENT_NOT_FOUND.getDescription(), idOfContragent,
                                payment.getContractId()), null);
            }
            if (existClientPayment(persistenceSession, contragent, payment.getIdOfPayment())) {
                    logger.warn(String.format("Payment request with duplicated attributes IdOfContragent == %s, payment == %s",
                            idOfContragent,
                            payment.toString()));
                return new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null, null, null,
                        PaymentProcessResult.PAYMENT_ALREADY_REGISTERED.getCode(),
                        String.format("%s. IdOfContragent == %s, IdOfPayment == %s",
                                PaymentProcessResult.PAYMENT_ALREADY_REGISTERED.getDescription(), idOfContragent,
                                payment.getIdOfPayment()), null);
            }

            Integer subBalanceNum = null;
            Long  contractId=null;

            Boolean enableSubBalanceOperation = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_ENABLE_SUB_BALANCE_OPERATION);
            if(enableSubBalanceOperation){
                if(payment.getContractId()!=null){
                    String contractIdstr = String.valueOf(payment.getContractId());
                    if(ContractIdGenerator.luhnTest(contractIdstr)){
                        subBalanceNum = 0;
                        contractId = payment.getContractId();
                    } else {
                        int len = contractIdstr.length();
                        if(len>2 && ContractIdGenerator.luhnTest(contractIdstr.substring(0, len - 2))){
                            subBalanceNum = Integer.parseInt(contractIdstr.substring(len-2));
                            contractId = Long.parseLong(contractIdstr.substring(0, len-2));
                        }
                    }
                }
            } else {
                contractId = payment.getContractId();
            }

            if(contractId==null) {
                return new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null, null, null,
                        PaymentProcessResult.CLIENT_NOT_FOUND.getCode(),
                        String.format("%s. IdOfContragent == %s, ContractId == %s, ClientId == %s",
                                PaymentProcessResult.CLIENT_NOT_FOUND.getDescription(), idOfContragent,
                                payment.getContractId(), payment.getClientId()), null);
            }

            //Client client = findPaymentClient(persistenceSession, contragent, payment.getContractId(), payment.getClientId());
            Client client = findPaymentClient(persistenceSession, contragent, contractId,  payment.getClientId());

            if (null == client) {
                return new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null, null, null,
                        PaymentProcessResult.CLIENT_NOT_FOUND.getCode(),
                        String.format("%s. IdOfContragent == %s, ContractId == %s, ClientId == %s",
                                PaymentProcessResult.CLIENT_NOT_FOUND.getDescription(), idOfContragent,
                                payment.getContractId(), payment.getClientId()), null);
            }

            if(subBalanceNum!=null && subBalanceNum>1 && enableSubBalanceOperation) {
                return new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null, null, null,
                        PaymentProcessResult.SUB_BALANCE_NOT_FOUND.getCode(),
                        String.format("%s. IdOfContragent == %s, ContractId == %s, ClientId == %s",
                                PaymentProcessResult.SUB_BALANCE_NOT_FOUND.getDescription(), idOfContragent,
                                payment.getContractId(), payment.getClientId()), null);
            }

            Long idOfClient = client.getIdOfClient();
            Long paymentTspContragentId = null;
            HashMap<String, String> payAddInfo = new HashMap<String, String>();
            Contragent defaultTsp = client.getOrg().getDefaultSupplier();
            if (payment.getTspContragentId() != null) {
                // если явно указан контрагент ТСП получатель, проверяем что он соответствует организации клиента
                if (defaultTsp == null || !defaultTsp.getIdOfContragent().equals(payment.getTspContragentId())) {
                    return new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null, null, null,
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
            ClientPayment clientPayment = null;
            if (!payment.isCheckOnly()) {
                long paymentSum = payment.getSum();
                if (payment.isResetBalance()) {
                    paymentSum -= client.getBalance();
                    logger.info("Processing payment with balance reset: " + client + "; current balance=" + client
                            .getBalance() + "; set balance=" + paymentSum);
                }
                //RuntimeContext.getFinancialOpsManager()
                //        .createClientPayment(persistenceSession, client, payment.getPaymentMethod(), paymentSum,
                //                ClientPayment.CLIENT_TO_ACCOUNT_PAYMENT, payment.getPayTime(), payment.getIdOfPayment(),
                //                contragent, payment.getAddPaymentMethod(), payment.getAddIdOfPayment());

                final FinancialOpsManager financialOpsManager = RuntimeContext.getFinancialOpsManager();
                 clientPayment = financialOpsManager
                        .createClientPayment(persistenceSession, client, contragent, payment.getPaymentMethod(),
                                paymentSum, payment.getPayTime(), payment.getIdOfPayment(),
                                payment.getAddPaymentMethod(), payment.getAddIdOfPayment(), subBalanceNum);

                persistenceSession.flush();
            }
            PaymentResponse.ResPaymentRegistry.Item result = new PaymentResponse.ResPaymentRegistry.Item(payment,
                    idOfClient, client.getContractId(), paymentTspContragentId, null, client.getBalance(),
                    PaymentProcessResult.OK.getCode(), PaymentProcessResult.OK.getDescription(), client, client.getSubBalance1(),
                    payAddInfo);
            if(clientPayment != null){
                result.setIdOfClientPayment(clientPayment.getIdOfClientPayment());
            }

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

            ClientPaymentOrder clientPaymentOrder = getClientPaymentOrderReference(persistenceSession,
                  idOfClientPaymentOrder);
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
                case TYPE_GET_GET_ACC_REGISGTRY_UPDATE:{
                    // обработка синхронизации параметров клиента
                    response = buildAccRegisgtryUpdate(request);
                    break;
                }
                case TYPE_COMMODITY_ACCOUNTING:{
                    // обработка синхронизации параметров клиента
                    response = buildCommodityAccountingSyncResponse(request);
                    break;
                }
            }

        } catch (Exception e) {
            logger.error(String.format("Failed to perform synchronization, IdOfOrg == %s", request.getIdOfOrg()), e);
            syncResult = 1;
        }

        // Build and return response
        if (request.getSyncType() == SyncType.TYPE_FULL) {
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
        //Long idOfSync = createSyncHistory(request.getIdOfOrg(), idOfPacket, syncStartTime);

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

            Contragent contragent = findContragent(persistenceSession, idOfContragent);
            Client client = getClientReference(persistenceSession, idOfClient);
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
            throw new IllegalArgumentException("Unacceptable orderStatus");
        }
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            ClientPaymentOrder clientPaymentOrder = getClientPaymentOrderReference(persistenceSession,
                  idOfClientPaymentOrder);
            Client client = getClientReference(persistenceSession, idOfClient);
            if (!client.getIdOfClient().equals(clientPaymentOrder.getClient().getIdOfClient())) {
                throw new IllegalArgumentException("Client doesn't own this order");
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

        logger.debug("check valid date");
        if(validTime.after(CalendarUtils.AFTER_DATE)) {
            throw new Exception("Не верно введена дата");
        }

        logger.debug("check issue date");
        if(issueTime!=null && validTime.before(issueTime)) {
            throw new Exception("Не верно введена дата");
        }

        logger.debug("check exist client");
        Client client = getClientReference(persistenceSession, idOfClient);
        if (client == null) {
            throw new Exception("Клиент не найден: " + idOfClient);
        }
        logger.debug("check exist card");
        Card c = findCardByCardNo(persistenceSession, cardNo);
        if (c != null && c.getClient()!=null) {
            throw new Exception("Карта уже зарегистрирована на клиента: " + c.getClient().getIdOfClient());
        }
        logger.debug("check exist temp card");
        CardTemp ct = findCardTempByCardNo(persistenceSession, cardNo);
        if (ct != null) {
            if(ct.getClient()!=null){
                throw new Exception(String.format(
                        "Карта с таким номером уже зарегистрирована как временная на клиента: %s. Статус карты - %s.",
                        ct.getClient().getIdOfClient(), ct.getCardStation()));
            }
            if(ct.getVisitor()!=null){
                throw new Exception(String.format(
                        "Карта с таким номером уже зарегистрирована как временная на посетителя: %s. Статус карты - %s.",
                        ct.getVisitor().getIdOfVisitor(), ct.getCardStation()));
            }
        }

        if (state == CardState.ISSUED.getValue()){
            boolean haveActiveCard = false;
            for (Card card : client.getCards()) {
                if (CardState.ISSUED.getValue() == card.getState()){
                    haveActiveCard = true;
                }
            }
            if(haveActiveCard && client.getOrg().getOneActiveCard()){
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

    public void disableClientCardsIfChangeOrg(Client client, Set<Org> oldOrgs, long newIdOfOrg) throws Exception {
        if (client == null) {
            return;
        }
        Boolean isReplaceOrg = !client.getOrg().getIdOfOrg().equals(newIdOfOrg); //сравниваем старую организацию клиента с новой
        for (Org o : oldOrgs) {
            if (o.getIdOfOrg().equals(newIdOfOrg)) {                             //и с дружественными организациями
                isReplaceOrg = false;
                break;
            }
        }
        //Если новая организация не совпадает ни со старой, ни с дружественными старой, то блокируем карты клиента
        if (isReplaceOrg) {
            Set<Card> cards = client.getCards();
            for (Card card : cards) {
                if (card.getState().equals(CardState.BLOCKED.getValue())) {     //если карта уже заблокирована, ее пропускаем
                    continue;
                }
                updateCard(client.getIdOfClient(),
                        card.getIdOfCard(),
                        card.getCardType(),
                        CardState.BLOCKED.getValue(), //статус = Заблокировано
                        card.getValidTime(),
                        card.getLifeState(),
                        card.getLockReason(),
                        card.getIssueTime(),
                        card.getExternalId()
                );
            }
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

            Client newCardOwner = getClientReference(persistenceSession, idOfClient);
            Card updatedCard = getCardReference(persistenceSession, idOfCard);

            if (state == Card.ACTIVE_STATE) {
                Set<Card> clientCards = new HashSet<Card>(newCardOwner.getCards());
                clientCards.remove(updatedCard);
                lockActiveCards(persistenceSession, clientCards);
            }

            final long oldClient = (updatedCard.getClient()!= null)?updatedCard.getClient().getIdOfClient():-1;
            final long newClient = newCardOwner.getIdOfClient();

            //История карты при обновлении информации
            if (oldClient != newClient) {
                HistoryCard historyCard = new HistoryCard();
                historyCard.setCard(updatedCard);
                historyCard.setUpDatetime(new Date());
                historyCard.setInformationAboutCard("Передача карты №: " + updatedCard.getCardNo() + " другому владельцу");
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
    public void changeCardOwner(Long idOfClient, Long cardNo, Date changeTime, Date validTime)
            throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            logger.debug("check valid date");
            if(validTime.after(CalendarUtils.AFTER_DATE)) {
                throw new Exception("Не верно введена дата");
            }

            logger.debug("check issue date");
            if(validTime.before(changeTime)) {
                throw new Exception("Не верно введена дата");
            }

            Client newCardOwner = getClientReference(persistenceSession, idOfClient);
            if(newCardOwner == null) {
                throw new Exception("Клиент не найден: "+idOfClient);
            }

            CardTemp ct = findCardTempByCardNo(persistenceSession, cardNo);
            if(ct!=null){
                if(ct.getClient()!=null){
                    final String format = "Карта с таким номером уже зарегистрирована как временная на клиента: %d";
                    final String message = String.format(format,ct.getClient().getIdOfClient());
                    throw new Exception(message);
                }
                if(ct.getVisitor()!=null){
                    final String format = "Карта с таким номером уже зарегистрирована как временная на посетителя: %d";
                    final String message = String.format(format,ct.getVisitor().getIdOfVisitor());
                    throw new Exception(message);
                }
                if(ct.getVisitor()==null && ct.getClient()==null){
                    final String format = "Карта с таким номером уже зарегистрирована как временная, но не имеет владельца: %d";
                    final String message = String.format(format, cardNo);
                    throw new Exception(message);
                }
            }

            //Card updatedCard = DAOUtils.getCardReference(persistenceSession, idOfCard);
            Card updatedCard = findCardByCardNo(persistenceSession, cardNo);
            if(updatedCard==null){
                throw new Exception("Неизвестная карта: "+cardNo);
            }

            //if (state == Card.ACTIVE_STATE) {
            //    Set<Card> clientCards = new HashSet<Card>(newCardOwner.getCards());
            //    clientCards.remove(updatedCard);
            //    lockActiveCards(persistenceSession, clientCards);
            //}

            lockActiveCards(persistenceSession, newCardOwner.getCards());

            final long oldClient = (updatedCard.getClient()!= null)?updatedCard.getClient().getIdOfClient():-1;
            final long newClient = newCardOwner.getIdOfClient();

            //История карты при смене владельца
            if (oldClient != newClient) {
                HistoryCard historyCard = new HistoryCard();
                historyCard.setCard(updatedCard);
                historyCard.setUpDatetime(new Date());
                historyCard.setInformationAboutCard("Передача карты №: " + updatedCard.getCardNo() + " другому владельцу");
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
    public void cancelOrder(CompositeIdOfOrder compositeIdOfOrder) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Order order = findOrder(persistenceSession, compositeIdOfOrder);
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

    public Client getClientInfo(long idOfContract) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        Client client = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            client = findClientByContractId(persistenceSession, idOfContract);

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

        ResPaymentRegistry resPaymentRegistry = null;
        ResAccountOperationsRegistry resAccountOperationsRegistry = null;
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
        ResultClientGuardian resultClientGuardian = null;
        ClientGuardianData clientGuardianData = null;
        AccRegistryUpdate accRegistryUpdate = null;
        ProhibitionsMenu prohibitionsMenu = null;
        ResCardsOperationsRegistry resCardsOperationsRegistry = null;
        AccountsRegistry accountsRegistry = null;

        boolean bError = false;

        idOfPacket = generateIdOfPacket(request.getIdOfOrg());
        // Register sync history
        syncHistory = createSyncHistory(request.getIdOfOrg(), idOfPacket, syncStartTime, request.getClientVersion(),
                request.getRemoteAddr());
        addClientVersionAndRemoteAddressByOrg(request.getIdOfOrg(), request.getClientVersion(),
                request.getRemoteAddr());


        try {
            if (request.getAccountOperationsRegistry()!= null){
                AccountOperationsRegistryHandler accountOperationsRegistryHandler = new AccountOperationsRegistryHandler();
                resAccountOperationsRegistry = accountOperationsRegistryHandler.process(request);
            }
        }catch (Exception e){
            logger.error("Ошибка при обработке AccountOperationsRegistry: ",e);
        }


        // Process paymentRegistry
        try {
            if (request.getPaymentRegistry().getPayments().hasNext()) {
                if (!RuntimeContext.getInstance().isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_P)) {
                    createSyncHistoryException(request.getIdOfOrg(), syncHistory, "no license slots available");
                    throw new Exception("no license slots available");
                }
            }
            resPaymentRegistry = processSyncPaymentRegistry(syncHistory.getIdOfSync(), request.getIdOfOrg(),
                    request.getPaymentRegistry(), errorClientIds);
        } catch (Exception e) {
            String message = String.format("Failed to process PaymentRegistry, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
            bError = true;
        }

        // Process ClientParamRegistry
        try {
            processSyncClientParamRegistry(syncHistory, request.getIdOfOrg(), request.getClientParamRegistry(), errorClientIds);
        } catch (Exception e) {
            String message = String.format("Failed to process ClientParamRegistry, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        // Process ClientGuardianRequest
        try {
            ClientGuardianRequest clientGuardianRequest = request.getClientGuardianRequest();
            if(clientGuardianRequest!=null){
                final List<ClientGuardianItem> clientGuardianResponseElement
                      = clientGuardianRequest.getClientGuardianResponseElement();
                if(clientGuardianResponseElement !=null) {
                    resultClientGuardian = processClientGuardian(clientGuardianResponseElement, request.getIdOfOrg(),
                          syncHistory);
                }
                final Long responseClientGuardian = clientGuardianRequest.getMaxVersion();
                if(responseClientGuardian!=null) {
                    clientGuardianData = processClientGuardianData(request.getIdOfOrg(), syncHistory,
                          responseClientGuardian);
                }
            }
        } catch (Exception e) {
            String message = String.format("Failed to process ClientGuardianRequest, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }


        // Process OrgStructure
        try {
            if (request.getOrgStructure() != null) {
                resOrgStructure = processSyncOrgStructure(request.getIdOfOrg(), request.getOrgStructure(), syncHistory);
            }
            if(resOrgStructure!=null && resOrgStructure.getResult()>0)
                createSyncHistoryException(request.getIdOfOrg(), syncHistory, resOrgStructure.getError());
        } catch (Exception e) {
            resOrgStructure = new SyncResponse.ResOrgStructure(1, "Unexpected error");
            String message = String.format("Failed to process OrgStructure, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        // Build client registry
        try {
            clientRegistry = processSyncClientRegistry(request.getIdOfOrg(), request.getClientRegistryRequest(), errorClientIds);
        } catch (Exception e) {
            String message = String.format("Failed to build ClientRegistry, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        // Process menu from Org
        try {
            processSyncMenu(request.getIdOfOrg(), request.getReqMenu());
        } catch (Exception e) {
            String message = String.format("Failed to process menu, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
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
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        // Process prohibitions menu from Org
        try {
            final ProhibitionMenuRequest prohibitionMenuRequest = request.getProhibitionMenuRequest();
            if(prohibitionMenuRequest !=null){
                prohibitionsMenu = getProhibitionsMenuData(request.getOrg(), prohibitionMenuRequest.getMaxVersion());
            }
        } catch (Exception e){
            String message = String.format("Failed to build prohibitions menu, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
            prohibitionsMenu = new ProhibitionsMenu(100, String.format("Internal error: %s", e.getMessage()));
            logger.error(message, e);
        }

        // Build AccRegistry
        try {
            accRegistry = getAccRegistry(request.getIdOfOrg(), null, request.getClientVersion());
        } catch (Exception e) {
            accRegistry = new SyncResponse.AccRegistry();
            String message = String.format("Failed to build AccRegistry, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);

        }

        try {
            resCardsOperationsRegistry= new CardsOperationsRegistryHandler().handler(request, request.getIdOfOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build ResCardsOperationsRegistry, IdOfOrg == %s", request.getIdOfOrg()),e);
        }

        // Process ReqDiary
        try {
            if(request.getReqDiary()!=null){
                resDiary = processSyncDiary(request.getIdOfOrg(), request.getReqDiary());
            }
        } catch (Exception e) {
            resDiary = new SyncResponse.ResDiary(1, "Unexpected error");
            String message = "SyncResponse.ResDiary: Unexpected error";
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        // Process enterEvents
        try {
            if (request.getEnterEvents() != null) {
                if (request.getEnterEvents().getEvents().size() > 0) {
                    if (!RuntimeContext.getInstance()
                            .isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_S)) {
                        createSyncHistoryException(request.getIdOfOrg(), syncHistory, "no license slots available");
                        throw new Exception("no license slots available");
                    }
                }
                resEnterEvents = processSyncEnterEvents(request.getEnterEvents(), request.getOrg());
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to process enter events, IdOfOrg == %s", request.getIdOfOrg()),e);
            bError = true;
        }

        try {
            if(request.getTempCardsOperations()!=null){
                resTempCardsOperations = processTempCardsOperations(request.getTempCardsOperations());
            }
        } catch (Exception e) {
            String message = String.format("processTempCardsOperations: %s", e.getMessage());
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
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
            String message = String.format("processClientRequestsOperations: %s", e.getMessage());
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        // Process ResCategoriesDiscountsAndRules
        try {
            resCategoriesDiscountsAndRules = processCategoriesDiscountsAndRules(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String.format("Failed to process categories and rules, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
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
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            orgOwnerData = processOrgOwnerData(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String.format("Failed to process org owner data, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            questionaryData = processQuestionaryData(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String.format("Failed to process questionary data, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            goodsBasicBasketData = processGoodsBasicBasketData(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String.format("Failed to process goods basic basket data , IdOfOrg == %s",request.getIdOfOrg());
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            if (request.getManager() != null) {
                manager = request.getManager();
                manager.setSyncHistory(syncHistory);
                manager.process(persistenceSessionFactory);
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to process of Distribution Manager, IdOfOrg == %s",
                    request.getIdOfOrg()), e);
        }

        if (bError) {
            DAOService.getInstance().updateLastUnsuccessfulBalanceSync(request.getIdOfOrg());
        } else {
            DAOService.getInstance().updateLastSuccessfulBalanceSync(request.getIdOfOrg());
        }

        try {
            directiveElement = processFullSyncDirective(request.getOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build Directive, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
        }

        Date syncEndTime = new Date();
        updateSyncHistory(syncHistory.getIdOfSync(), syncResult, syncEndTime);
        updateFullSyncParam(request.getIdOfOrg());

        if(RuntimeContext.getInstance().isMainNode() && RuntimeContext.getInstance().getSettingsConfig().isEcafeAutopaymentBkEnabled()){
            runRegularPayments(request);
        }

        String fullName = DAOService.getInstance().getPersonNameByOrg(request.getOrg());

        try {
        accountsRegistry = new AccountsRegistryHandler().handlerFull(request,request.getIdOfOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build AccountsRegistry, IdOfOrg == %s", request.getIdOfOrg()),e);
        }




        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), fullName, idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure, resMenuExchange, resDiary, "",
                resEnterEvents, resTempCardsOperations, tempCardOperationData, resCategoriesDiscountsAndRules, complexRoles,
                correctingNumbersOrdersRegistry, manager, orgOwnerData, questionaryData, goodsBasicBasketData,
                directiveElement, resultClientGuardian, clientGuardianData, accRegistryUpdate, prohibitionsMenu,
                accountsRegistry, resCardsOperationsRegistry);
    }
    /*
    * Запуск авто пополнения
    * */
    @Async
     private void runRegularPayments(SyncRequest request) {
        try {
            long time = System.currentTimeMillis();
            logger.info("runRegularPayments run");
            BKRegularPaymentSubscriptionService regularPaymentSubscriptionService = (BKRegularPaymentSubscriptionService) RuntimeContext
                    .getInstance().getRegularPaymentSubscriptionService();
            regularPaymentSubscriptionService.checkClientBalances(request.getIdOfOrg());
            logger.info("runRegularPayments stop" + (System.currentTimeMillis() - time));
        } catch (Exception e) {
            logger.warn("catch BKRegularPaymentSubscriptionService exc");
        }
    }

    /* Do process full synchronization */
    private SyncResponse buildCommodityAccountingSyncResponse(SyncRequest request) throws Exception {

        Long idOfPacket = null;
        SyncHistory syncHistory = null; // регистируются и заполняются только для полной синхронизации

        ResPaymentRegistry resPaymentRegistry = null;
        ResAccountOperationsRegistry resAccountOperationsRegistry = null;
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
        ResultClientGuardian resultClientGuardian = null;
        ClientGuardianData clientGuardianData = null;
        AccRegistryUpdate accRegistryUpdate = null;
        ProhibitionsMenu prohibitionsMenu = null;
        ResCardsOperationsRegistry resCardsOperationsRegistry = null;
        AccountsRegistry accountsRegistry = null;

        boolean bError = false;

        idOfPacket = generateIdOfPacket(request.getIdOfOrg());

        try {
            orgOwnerData = processOrgOwnerData(request.getIdOfOrg());
        } catch (Exception e) {
            String message = String.format("Failed to process org owner data, IdOfOrg == %s", request.getIdOfOrg());
            createSyncHistoryException(request.getIdOfOrg(), syncHistory, message);
            logger.error(message, e);
        }

        try {
            if (request.getManager() != null) {
                manager = request.getManager();
                manager.setSyncHistory(syncHistory);
                manager.process(persistenceSessionFactory);
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to process of Distribution Manager, IdOfOrg == %s",
                    request.getIdOfOrg()), e);
        }

        try {
            directiveElement = processFullSyncDirective(request.getOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build Directive, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
        }

        Date syncEndTime = new Date();

        String fullName = DAOService.getInstance().getPersonNameByOrg(request.getOrg());

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), fullName, idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure, resMenuExchange, resDiary, "",
                resEnterEvents, resTempCardsOperations, tempCardOperationData, resCategoriesDiscountsAndRules, complexRoles,
                correctingNumbersOrdersRegistry, manager, orgOwnerData, questionaryData, goodsBasicBasketData,
                directiveElement, resultClientGuardian, clientGuardianData, accRegistryUpdate, prohibitionsMenu,
                accountsRegistry, resCardsOperationsRegistry);
    }

    /* Do process short synchronization for update Client parameters */
    private SyncResponse buildClientsParamsSyncResponse(SyncRequest request) {

        Long idOfPacket = null; // регистируются и заполняются только для полной синхронизации
        SyncHistory idOfSync = null;

        ResPaymentRegistry resPaymentRegistry = null;
        ResAccountOperationsRegistry resAccountOperationsRegistry = null;
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
        AccRegistryUpdateRequest accRegistryUpdateRequest = null;
        List<Long> errorClientIds = new ArrayList<Long>();
        ResultClientGuardian resultClientGuardian = null;
        ClientGuardianData clientGuardianData = null;
        AccRegistryUpdate accRegistryUpdate = null;
        ProhibitionsMenu prohibitionsMenu = null;
        ResCardsOperationsRegistry resCardsOperationsRegistry = null;
        AccountsRegistry accountsRegistry = null;

        boolean bError = false;

        //Process AccountOperationsRegistry
        try {
            if (request.getAccountOperationsRegistry()!= null){
                AccountOperationsRegistryHandler accountOperationsRegistryHandler = new AccountOperationsRegistryHandler();
                resAccountOperationsRegistry = accountOperationsRegistryHandler.process(request);
            }
        }catch (Exception e){
            logger.error("Ошибка при обработке AccountOperationsRegistry: ",e);
        }


        // Process paymentRegistry
        try {
            if (request.getPaymentRegistry() != null) {
                if (request.getPaymentRegistry().getPayments() != null) {
                    if (request.getPaymentRegistry().getPayments().hasNext()) {
                        if (!RuntimeContext.getInstance()
                                .isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_P)) {
                            String clientVersion = (request.getClientVersion()==null?"":request.getClientVersion());
                            Long packet = (idOfPacket==null?-1L:idOfPacket);
                            SyncHistory syncHistory = createSyncHistory(request.getIdOfOrg(), packet, new Date(),
                                    clientVersion, request.getRemoteAddr());
                            final String s = String.format("Failed to process PaymentRegistry, IdOfOrg == %s, no license slots available", request.getIdOfOrg());
                            createSyncHistoryException(request.getIdOfOrg(), syncHistory, s);
                            throw new Exception("no license slots available");
                        }
                    }
                    resPaymentRegistry = processSyncPaymentRegistry(null, request.getIdOfOrg(),
                            request.getPaymentRegistry(), errorClientIds);
                }
            }
        } catch (Exception e) {
            logger.error(
                    String.format("Failed to process Payment Registry, IdOfOrg == %s", request.getIdOfOrg()), e);
            bError = true;
        }

        // Build AccRegistryUpdateRequest
        try {
            accRegistryUpdateRequest = request.getAccRegistryUpdateRequest();
        } catch (Exception e) {
            accRegistry = new SyncResponse.AccRegistry();
            String message = String.format("Failed to build AccRegistry, IdOfOrg == %s", request.getIdOfOrg());
            logger.error(message, e);
        }

        // Build AccRegistry
        try {
            if(accRegistryUpdateRequest!=null){
                accRegistry = getAccRegistry(request.getIdOfOrg(), accRegistryUpdateRequest.getClientIds(), request.getClientVersion());
            } else {
                accRegistry = getAccRegistry(request.getIdOfOrg(), null, request.getClientVersion());
            }
        } catch (Exception e) {
            accRegistry = new SyncResponse.AccRegistry();
            String message = String.format("Failed to build AccRegistry, IdOfOrg == %s", request.getIdOfOrg());
            logger.error(message, e);

        }

        try {
            processSyncClientParamRegistry(idOfSync, request.getIdOfOrg(), request.getClientParamRegistry(),errorClientIds);
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
            if(request.getTempCardsOperations()!=null){
                resTempCardsOperations = processTempCardsOperations(request.getTempCardsOperations());
            }
        } catch (Exception e) {
            String message = String.format("processTempCardsOperations: %s", e.getMessage());
            logger.error(message, e);
        }

        try {
            directiveElement = processSyncDirective(request.getOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build Directive, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
        }

        try {
            resCardsOperationsRegistry= new CardsOperationsRegistryHandler().handler(request, request.getIdOfOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build ResCardsOperationsRegistry, IdOfOrg == %s", request.getIdOfOrg()),e);
        }


        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(),request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure, resMenuExchange, resDiary, "",
                resEnterEvents, resTempCardsOperations, tempCardOperationData, resCategoriesDiscountsAndRules, complexRoles,
                correctingNumbersOrdersRegistry, manager, orgOwnerData, questionaryData, goodsBasicBasketData,
                directiveElement, resultClientGuardian, clientGuardianData, accRegistryUpdate, prohibitionsMenu,
                accountsRegistry, resCardsOperationsRegistry);
    }

    /* Do process short synchronization for update AccRegisgtryUpdate parameters */
    private SyncResponse buildAccRegisgtryUpdate(SyncRequest request) {

        Long idOfPacket = null; // регистируются и заполняются только для полной синхронизации
        SyncHistory idOfSync = null;

        ResPaymentRegistry resPaymentRegistry = null;
        ResAccountOperationsRegistry resAccountOperationsRegistry = null;
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
        AccRegistryUpdateRequest accRegistryUpdateRequest = null;
        List<Long> errorClientIds = new ArrayList<Long>();
        ResultClientGuardian resultClientGuardian = null;
        ClientGuardianData clientGuardianData = null;
        AccRegistryUpdate accRegistryUpdate = null;
        ProhibitionsMenu prohibitionsMenu = null;
        ResCardsOperationsRegistry resCardsOperationsRegistry = null;
        AccountsRegistry accountsRegistry = null;

        //Process AccountOperationsRegistry
        try {
            if (request.getAccountOperationsRegistry()!= null){
                AccountOperationsRegistryHandler accountOperationsRegistryHandler = new AccountOperationsRegistryHandler();
                resAccountOperationsRegistry = accountOperationsRegistryHandler.process(request);
            }
        }catch (Exception e){
            logger.error("Ошибка при обработке AccountOperationsRegistry: ",e);
        }


        // Process paymentRegistry
        try {
            if (request.getPaymentRegistry() != null) {
                if (request.getPaymentRegistry().getPayments() != null) {
                    if (request.getPaymentRegistry().getPayments().hasNext()) {
                        if (!RuntimeContext.getInstance()
                                .isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_P)) {
                            String clientVersion = (request.getClientVersion()==null?"":request.getClientVersion());
                            Long packet = (idOfPacket==null?-1L:idOfPacket);
                            SyncHistory syncHistory = createSyncHistory(request.getIdOfOrg(), packet, new Date(),
                                    clientVersion, request.getRemoteAddr());
                            final String s = String.format("Failed to process PaymentRegistry, IdOfOrg == %s, no license slots available", request.getIdOfOrg());
                            createSyncHistoryException(request.getIdOfOrg(), syncHistory, s);
                            throw new Exception("no license slots available");
                        }
                    }
                    resPaymentRegistry = processSyncPaymentRegistry(null, request.getIdOfOrg(),
                            request.getPaymentRegistry(), errorClientIds);
                }
            }
        } catch (Exception e) {
            logger.error(
                    String.format("Failed to process Payment Registry, IdOfOrg == %s", request.getIdOfOrg()), e);
        }

        // Build AccRegistryUpdateRequest
        try {
            accRegistryUpdateRequest = request.getAccRegistryUpdateRequest();
        } catch (Exception e) {
            accRegistry = new SyncResponse.AccRegistry();
            String message = String.format("Failed to build AccRegistry, IdOfOrg == %s", request.getIdOfOrg());
            logger.error(message, e);

        }
        // Build AccRegistry
        try {
            if(accRegistryUpdateRequest!=null){
                accRegistry = getAccRegistry(request.getIdOfOrg(), accRegistryUpdateRequest.getClientIds(), request.getClientVersion());
            } else {
                accRegistry = getAccRegistry(request.getIdOfOrg(), null, request.getClientVersion());
            }
        } catch (Exception e) {
            accRegistry = new SyncResponse.AccRegistry();
            String message = String.format("Failed to build AccRegistry, IdOfOrg == %s", request.getIdOfOrg());
            logger.error(message, e);

        }

        try {
            if(request.getTempCardsOperations()!=null){
                resTempCardsOperations = processTempCardsOperations(request.getTempCardsOperations());
            }
        } catch (Exception e) {
            String message = String.format("processTempCardsOperations: %s", e.getMessage());
            logger.error(message, e);
        }

        try {
            directiveElement = processSyncDirective(request.getOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build Directive, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
        }

        try {
            resCardsOperationsRegistry= new CardsOperationsRegistryHandler().handler(request, request.getIdOfOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build ResCardsOperationsRegistry, IdOfOrg == %s", request.getIdOfOrg()),e);
        }

        try {
            accountsRegistry= new AccountsRegistryHandler().accRegisgtryUpdateHandler(request);
        } catch (Exception e) {
            logger.error(String.format("Failed to build AccountsRegistry, IdOfOrg == %s", request.getIdOfOrg()),e);
        }


        Date syncEndTime = new Date();

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure, resMenuExchange, resDiary, "",
                resEnterEvents, resTempCardsOperations, tempCardOperationData, resCategoriesDiscountsAndRules, complexRoles,
                correctingNumbersOrdersRegistry, manager, orgOwnerData, questionaryData, goodsBasicBasketData,
                directiveElement, resultClientGuardian, clientGuardianData, accRegistryUpdate, prohibitionsMenu,
                accountsRegistry, resCardsOperationsRegistry);
    }

    /* Do process short synchronization for update payment register and account inc register */
    private SyncResponse buildAccIncSyncResponse(SyncRequest request) {

        Long idOfPacket = null, idOfSync = null; // регистируются и заполняются только для полной синхронизации
        ResAccountOperationsRegistry resAccountOperationsRegistry = null;
        ResPaymentRegistry resPaymentRegistry = null;
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
        ResultClientGuardian resultClientGuardian = null;
        ClientGuardianData clientGuardianData = null;
        AccRegistryUpdate accRegistryUpdate = null;
        ProhibitionsMenu prohibitionsMenu = null;
        ResCardsOperationsRegistry resCardsOperationsRegistry = null;
        AccountsRegistry accountsRegistry = null;

        boolean bError = false;

        try {
            if (request.getAccountOperationsRegistry()!= null){
                AccountOperationsRegistryHandler accountOperationsRegistryHandler = new AccountOperationsRegistryHandler();
                resAccountOperationsRegistry = accountOperationsRegistryHandler.process(request);
            }
        }catch (Exception e){
            logger.error("Ошибка при обработке AccountOperationsRegistry: ",e);
            bError = true;
        }

        // Process paymentRegistry
        try {
            if (request.getPaymentRegistry() != null) {
                if (request.getPaymentRegistry().getPayments() != null) {
                    if (request.getPaymentRegistry().getPayments().hasNext()) {
                        if (!RuntimeContext.getInstance()
                                .isPermitted(request.getIdOfOrg(), RuntimeContext.TYPE_P)) {
                            String clientVersion = (request.getClientVersion()==null?"":request.getClientVersion());
                            Long packet = (idOfPacket==null?-1L:idOfPacket);
                            SyncHistory syncHistory = createSyncHistory(request.getIdOfOrg(), packet, new Date(),
                                    clientVersion, request.getRemoteAddr());
                            final String s = String.format("Failed to process PaymentRegistry, IdOfOrg == %s, no license slots available", request.getIdOfOrg());
                            createSyncHistoryException(request.getIdOfOrg(), syncHistory, s);
                            throw new Exception("no license slots available");
                        }
                    }
                    resPaymentRegistry = processSyncPaymentRegistry(idOfSync, request.getIdOfOrg(),
                            request.getPaymentRegistry(), errorClientIds);
                }
            }
        } catch (Exception e) {
            logger.error(
                    String.format("Failed to process Payment Registry, IdOfOrg == %s", request.getIdOfOrg()), e);
            bError = true;
        }

        try {
            if(request.getProtoVersion()<6){
                accIncRegistry = getAccIncRegistry(request.getOrg(), request.getAccIncRegistryRequest().dateTime);
            } else {
                accRegistryUpdate = getAccRegistryUpdate(request.getOrg(), request.getAccIncRegistryRequest().dateTime);
            }

        } catch (Exception e) {
            logger.error(String.format("Failed to build AccIncRegistry, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
            if(request.getProtoVersion()<6){
                accIncRegistry = new SyncResponse.AccIncRegistry();
                accIncRegistry.setDate(request.getAccIncRegistryRequest().dateTime);
            } else {
                accRegistryUpdate = new AccRegistryUpdate();
            }
            bError = true;
        }

        try {
            directiveElement = processSyncDirective(request.getOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build Directive, IdOfOrg == %s", request.getIdOfOrg()),
                    e);
        }

        // Process enterEvents
        try {
            if (request.getEnterEvents() != null) {
                resEnterEvents = processSyncEnterEvents(request.getEnterEvents(), request.getOrg());
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to process Enter Events, IdOfOrg == %s", request.getIdOfOrg()),
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

        if(RuntimeContext.getInstance().isMainNode() && RuntimeContext.getInstance().getSettingsConfig().isEcafeAutopaymentBkEnabled()){
            runRegularPayments(request);
        }

        try {
            resCardsOperationsRegistry= new CardsOperationsRegistryHandler().handler(request, request.getIdOfOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build ResCardsOperationsRegistry, IdOfOrg == %s", request.getIdOfOrg()),e);
        }

        try {
            accountsRegistry = new AccountsRegistryHandler().accRegistryHandler(request,request.getIdOfOrg());
        } catch (Exception e) {
            logger.error(String.format("Failed to build AccountsRegistry, IdOfOrg == %s", request.getIdOfOrg()),e);
        }

        updateOrgSyncDate(request.getIdOfOrg());

        return new SyncResponse(request.getSyncType(), request.getIdOfOrg(), request.getOrg().getShortName(),
                request.getOrg().getType(), "", idOfPacket, request.getProtoVersion(), syncEndTime, "", accRegistry,
                resPaymentRegistry, resAccountOperationsRegistry, accIncRegistry, clientRegistry, resOrgStructure, resMenuExchange, resDiary, "",
                resEnterEvents, resTempCardsOperations, tempCardOperationData, resCategoriesDiscountsAndRules, complexRoles,
                correctingNumbersOrdersRegistry, manager, orgOwnerData, questionaryData, goodsBasicBasketData,
                directiveElement, resultClientGuardian, clientGuardianData, accRegistryUpdate, prohibitionsMenu,
                accountsRegistry, resCardsOperationsRegistry);
    }

    private void updateOrgSyncDate(long idOfOrg) {
        try{
        OrgSyncWritableRepository orgSyncWritableRepository = OrgSyncWritableRepository.getInstance();
        orgSyncWritableRepository.updateAccRegistryDate(idOfOrg);
        }catch (Exception e){
            logger.error("Не удалось обновить время синхронизации, idOfOrg: "+ idOfOrg);
        }
    }

    private void createSyncHistoryException(long idOfOrg, SyncHistory syncHistory, String s) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            DAOUtils.createSyncHistoryException(persistenceSession, idOfOrg, syncHistory, s);
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("createSyncHistoryException exception: ",e);
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
            falseFullSyncByOrg(persistenceSession, idOfOrg);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private DirectiveElement processSyncDirective(Org org) throws Exception{
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        DirectiveElement directiveElement = new DirectiveElement();
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            directiveElement.process(persistenceSession, org);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return directiveElement;
    }

    private DirectiveElement processFullSyncDirective(Org org) throws Exception{
        DirectiveElement directiveElement = new DirectiveElement();
        directiveElement.processForFullSync(org);
        return directiveElement;
    }

    private void checkUserPaymentProcessRights(Long idOfUser) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            User user = findUser(persistenceSession, idOfUser);
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

    private ResPaymentRegistry processSyncPaymentRegistry(Long idOfSync, Long idOfOrg,
            PaymentRegistry paymentRegistry, List<Long> errorClientIds) throws Exception {
        ResPaymentRegistry resPaymentRegistry = new ResPaymentRegistry();
        Iterator<Payment> payments = paymentRegistry.getPayments();
        while (payments.hasNext()) {
            Payment Payment = payments.next();
            ResPaymentRegistryItem resAcc;
            try {
                resAcc = processSyncPaymentRegistryPayment(idOfSync, idOfOrg, Payment, errorClientIds);
                if (resAcc.getResult() != 0) {
                    logger.error("Failure in response payment registry: " + resAcc);
                }
            } catch (Exception e) {
                logger.error(String.format("Failed to process payment == %s", Payment), e);
                resAcc = new ResPaymentRegistryItem(Payment.getIdOfOrder(), 100, "Internal error");
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
                resAcc = new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null, null, null,
                        PaymentProcessResult.UNKNOWN_ERROR.getCode(),
                        PaymentProcessResult.UNKNOWN_ERROR.getDescription(), null);
            }
            resPaymentRegistry.addItem(resAcc);
        }
        return resPaymentRegistry;
    }

    private static Purchase findPurchase(Payment payment, Long idOfOrderDetail) throws Exception {
        Iterator<Purchase> purchases = payment.getPurchases().iterator();
        while (purchases.hasNext()) {
            Purchase Purchase = purchases.next();
            if (idOfOrderDetail.equals(Purchase.getIdOfOrderDetail())) {
                return Purchase;
            }
        }
        return null;
    }

    private static void updateOrderDetails(Session session, Order order, Payment payment)
            throws Exception {
        Set<OrderDetail> orderDetails = order.getOrderDetails();
        for (OrderDetail orderDetail : orderDetails) {
            if (StringUtils.isEmpty(orderDetail.getRootMenu())) {
                Purchase Purchase = findPurchase(payment,
                        orderDetail.getCompositeIdOfOrderDetail().getIdOfOrderDetail());
                if (null != Purchase) {
                    String rootMenu = Purchase.getRootMenu();
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

    private ClientGuardianData processClientGuardianData(Long idOfOrg,SyncHistory syncHistory, Long maxVersion){
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ClientGuardianData clientGuardianData = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            //persistenceSession = RuntimeContext.reportsSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ClientGuardianDataProcessor processor = new ClientGuardianDataProcessor(persistenceSession, idOfOrg, maxVersion);
            clientGuardianData = processor.process();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception ex) {
            String message = String.format("Load Client Guardian to database error, IdOfOrg == %s :",idOfOrg);
            logger.error(message, ex);
            clientGuardianData = new ClientGuardianData(new ResultOperation(100, ex.getMessage()));
            createSyncHistoryException(idOfOrg, syncHistory, message);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return clientGuardianData;
    }

    private ResultClientGuardian processClientGuardian(List<ClientGuardianItem> items, Long idOfOrg, SyncHistory syncHistory){
        ResultClientGuardian resultClientGuardian = new ResultClientGuardian();
        for (ClientGuardianItem item: items){
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            ClientGuardian clientGuardian = new ClientGuardian(item.getIdOfChildren(), item.getIdOfGuardian());
            if(item.getDeleteState()==0){
                try {
                    persistenceSession = persistenceSessionFactory.openSession();
                    //persistenceSession = RuntimeContext.reportsSessionFactory.openSession();
                    persistenceTransaction = persistenceSession.beginTransaction();
                    Criteria criteria = persistenceSession.createCriteria(ClientGuardian.class);
                    criteria.add(Example.create(clientGuardian));
                    ClientGuardian dbClientGuardian = (ClientGuardian) criteria.uniqueResult();
                    if(dbClientGuardian==null){
                        clientGuardian = (ClientGuardian) persistenceSession.merge(clientGuardian);
                    }
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                    if(dbClientGuardian==null){
                        resultClientGuardian.addItem(clientGuardian, 0, null);
                    } else {
                        resultClientGuardian.addItem(dbClientGuardian, 0, "Client guardian exist");
                    }
                } catch (Exception ex) {
                    String message = String.format(
                            "Save Client Guardian to database error, idOfChildren == %s, idOfGuardian == %s",
                            clientGuardian.getIdOfChildren(), clientGuardian.getIdOfGuardian());
                    logger.error(message, ex);
                    resultClientGuardian.addItem(clientGuardian, 100, ex.getMessage());
                    createSyncHistoryException(idOfOrg, syncHistory, message);
                } finally {
                    HibernateUtils.rollback(persistenceTransaction, logger);
                    HibernateUtils.close(persistenceSession, logger);
                }
            } else {
                try {
                    persistenceSession = persistenceSessionFactory.openSession();
                    //persistenceSession = RuntimeContext.reportsSessionFactory.openSession();
                    persistenceTransaction = persistenceSession.beginTransaction();
                    Criteria criteria = persistenceSession.createCriteria(ClientGuardian.class);
                    criteria.add(Example.create(clientGuardian));
                    ClientGuardian dbClientGuardian = (ClientGuardian) criteria.uniqueResult();
                    if(dbClientGuardian!=null){
                        persistenceSession.delete(dbClientGuardian);
                    }
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                    final String resultMessage = (dbClientGuardian==null?"Client guardian is removed":null);
                    resultClientGuardian.addItem(item, 0, resultMessage);
                } catch (Exception ex) {
                    String message = String.format(
                            "Delete Client Guardian to database error, idOfChildren == %s, idOfGuardian == %s",
                            clientGuardian.getIdOfChildren(), clientGuardian.getIdOfGuardian());
                    logger.error(message, ex);
                    resultClientGuardian.addItem(clientGuardian, 100, ex.getMessage());
                    createSyncHistoryException(idOfOrg, syncHistory, message);
                } finally {
                    HibernateUtils.rollback(persistenceTransaction, logger);
                    HibernateUtils.close(persistenceSession, logger);
                }
            }
        }
        return resultClientGuardian;
    }

    private ComplexRoles processComplexRoles() throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ComplexRoles complexRoles = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ComplexRoleProcessor processor = new ComplexRoleProcessor(persistenceSession);
            complexRoles = processor.process();
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

    public ResPaymentRegistryItem processSyncPaymentRegistryPayment(Long idOfSync, Long idOfOrg,
            Payment payment, List<Long> errorClientIds) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            //  Применяем фильтр оборудования
            idOfOrg = DAOService.getInstance().receiveIdOfOrgByAccessory(idOfOrg, Accessory.BANK_ACCESSORY_TYPE, payment.getIdOfPOS());

            //SyncHistory syncHistory = (SyncHistory) persistenceSession.load(SyncHistory.class, idOfSync);
            //Org organization = DAOUtils.findOrg(persistenceSession, idOfOrg);
            Long idOfOrganization = getIdOfOrg(persistenceSession, idOfOrg);
            if (null == idOfOrganization) {
                return new ResPaymentRegistryItem(payment.getIdOfOrder(), 130,
                        String.format("Organization no found, IdOfOrg == %s, IdOfOrder == %s", idOfOrg,
                                payment.getIdOfOrder()));
            }

            CompositeIdOfOrder compositeIdOfOrder = new CompositeIdOfOrder(idOfOrg, payment.getIdOfOrder());
            Order order = findOrder(persistenceSession, compositeIdOfOrder);

            if(payment.isCommit()){

                // Check order existence
                //if (DAOUtils.existOrder(persistenceSession, idOfOrg, payment.getIdOfOrder())) {
                if (order!=null) {
                    // if order == payment (may be last sync result was not transferred to client)
                    Long orderCardNo = order.getCard() == null ? null : order.getCard().getCardNo();
                    if ((("" + orderCardNo).equals("" + payment.getCardNo())) && (order.getCreateTime()
                            .equals(payment.getTime())) && (order.getSumByCard().equals(payment.getSumByCard()))) {
                        return new ResPaymentRegistryItem(payment.getIdOfOrder(), 0,
                                "Order is already registered");
                    } else {
                        return new ResPaymentRegistryItem(payment.getIdOfOrder(), 110, String.format(
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
                    card = findCardByCardNo(persistenceSession, cardNo);
                    if (null == card) {
                        return new ResPaymentRegistryItem(payment.getIdOfOrder(), 200,
                                String.format("Unknown card, IdOfOrg == %s, IdOfOrder == %s, CardNo == %s", idOfOrg,
                                        payment.getIdOfOrder(), cardNo));
                    }
                }
                // If client specified - load client from data model
                Client client = null;
                Long idOfClient = payment.getIdOfClient();
                if (null != idOfClient) {
                    client = findClient(persistenceSession, idOfClient);
                    // Check client existance

                    if (null == client) {
                        return new ResPaymentRegistryItem(payment.getIdOfOrder(), 210,
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
                        return new ResPaymentRegistryItem(payment.getIdOfOrder(), 230, String.format(
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
                                        String.format(
                                                "Specified card is inactive. Client: %s, Card: %s. Will use card: %s",
                                                "" + client.getIdOfClient(), card.getIdOfCard(), newCard.getIdOfCard()));
                            }
                        }
                        card = newCard;
                    }
                }
                // If client is specified - check if client is registered for the specified organization
                // or for one of friendly organizations of specified one
                Set<Long> idOfFriendlyOrgSet = getIdOfFriendlyOrg(persistenceSession, idOfOrg);
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
                    return new ResPaymentRegistryItem(payment.getIdOfOrder(), 250,
                            String.format("Negative sum(s) are specified, IdOfOrg == %s, IdOfOrder == %s", idOfOrg,
                                    payment.getIdOfOrder()));
                }
                if (0 != payment.getSumByCard() && card == null) {
                    // Check if card is specified
                    return new ResPaymentRegistryItem(payment.getIdOfOrder(), 240, String.format(
                            "Payment has card part but doesn't specify CardNo, IdOfOrg == %s, IdOfOrder == %s, IdOfClient == %s",
                            idOfOrg, payment.getIdOfOrder(), idOfClient));
                }
                // Create order
                RuntimeContext.getFinancialOpsManager()
                        .createOrderCharge(persistenceSession, payment, idOfOrg, client, card, payment.getConfirmerId());
                long totalPurchaseDiscount = 0;
                long totalPurchaseRSum = 0;
                // Register order details (purchase)
                for (Purchase purchase : payment.getPurchases()) {
                    if (null != findOrderDetail(persistenceSession,
                          new CompositeIdOfOrderDetail(idOfOrg, purchase.getIdOfOrderDetail()))) {
                        return new ResPaymentRegistryItem(payment.getIdOfOrder(), 120, String.format(
                                "Order detail is already registered, IdOfOrg == %s, IdOfOrder == %s, IdOfOrderDetail == %s",
                                idOfOrg, payment.getIdOfOrder(), purchase.getIdOfOrderDetail()));
                    }
                    if (purchase.getDiscount() < 0 || purchase.getrPrice() < 0 /*|| purchase.getQty() < 0*/) {
                        return new ResPaymentRegistryItem(payment.getIdOfOrder(), 250, String.format(
                                "Negative Discount or rPrice are specified, IdOfOrg == %s, IdOfOrder == %s, Discount == %s, Discount == %s",
                                idOfOrg, payment.getIdOfOrder(), purchase.getDiscount(), purchase.getrPrice()));
                    }
                    OrderDetail orderDetail = new OrderDetail(
                            new CompositeIdOfOrderDetail(idOfOrg, purchase.getIdOfOrderDetail()), payment.getIdOfOrder(),
                            purchase.getQty(), purchase.getDiscount(), purchase.getSocDiscount(), purchase.getrPrice(),
                            purchase.getName(), purchase.getRootMenu(), purchase.getMenuGroup(), purchase.getMenuOrigin(),
                            purchase.getMenuOutput(), purchase.getType(), purchase.getIdOfMenu());
                    if (purchase.getItemCode() != null) {
                        orderDetail.setItemCode(purchase.getItemCode());
                    }
                    if (purchase.getIdOfRule() != null) {
                        orderDetail.setIdOfRule(purchase.getIdOfRule());
                    }
                    if (purchase.getGuidOfGoods() != null) {
                        Good good = findGoodByGuid(persistenceSession, purchase.getGuidOfGoods());
                        if (good != null) {
                            orderDetail.setGood(good);
                        }
                    }
                    persistenceSession.save(orderDetail);
                    totalPurchaseDiscount += purchase.getDiscount() * Math.abs(purchase.getQty());
                    totalPurchaseRSum += purchase.getrPrice() * Math.abs(purchase.getQty());
                }
                // Check payment sums
                if (totalPurchaseRSum != payment.getRSum() || totalPurchaseDiscount != payment.getSocDiscount() + payment
                        .getTrdDiscount() + payment.getGrant()) {
                    return new ResPaymentRegistryItem(payment.getIdOfOrder(), 300,
                            String.format("Invalid total sum by order, IdOfOrg == %s, IdOfOrder == %s", idOfOrg,
                                    payment.getIdOfOrder()));
                }
                if (payment.getRSum() != payment.getSumByCard() + payment.getSumByCash()) {
                    return new ResPaymentRegistryItem(payment.getIdOfOrder(), 310,
                            String.format("Invalid sum of order card and cash payments, IdOfOrg == %s, IdOfOrder == %s",
                                    idOfOrg, payment.getIdOfOrder()));
                }

                // Commit data model transaction
                persistenceSession.flush();
                persistenceTransaction.commit();
                persistenceTransaction = null;

                // !!!!! ОПОВЕЩЕНИЕ ПО СМС !!!!!!!!
                /* в случее если анонимного заказа мы не знаем клиента */
                /* не оповещаем в случае пробития корректировачных заказов */
                if(client!=null && !payment.getOrderType().equals(OrderTypeEnumType.CORRECTION_TYPE)){
                    String[] values = generatePaymentNotificationParams(persistenceSession, client, payment);
                    values = EventNotificationService.attachTargetIdToValues(payment.getIdOfOrder(), values);
                    RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                            .sendNotificationAsync(client, null, EventNotificationService.MESSAGE_PAYMENT,values);
                }
            } else {
                // TODO: есть ли необходимость оповещать клиента о сторне?
                // отмена заказа
                if (null != order) {
                    // Update client balance
                    RuntimeContext.getFinancialOpsManager().cancelOrder(persistenceSession, order);
                    persistenceSession.flush();
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                } else {
                    return new ResPaymentRegistryItem(payment.getIdOfOrder(), 0,
                            String.format("Unknown order, IdOfOrg == %s, IdOfOrder == %s", idOfOrg,
                                    payment.getIdOfOrder()));
                }
            }

            // Return no errors
            return new ResPaymentRegistryItem(payment.getIdOfOrder(), 0, null);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private static Client findPaymentClient(Session persistenceSession, Contragent contragent, Long contractId,
            Long clientId) throws Exception {
        if (clientId != null) {
            return findClient(persistenceSession, clientId);
        }
        // Извлекаем из модели данных клиента, на карту которого необходимо перевести платеж
        // Если необходимо преобразовать номер счета, то делаем это
        if (contragent.getNeedAccountTranslate()) {
            ContragentClientAccount contragentClientAccount = findContragentClientAccount(persistenceSession,
                  new CompositeIdOfContragentClientAccount(contragent.getIdOfContragent(), contractId));
            if (null != contragentClientAccount) {
                return contragentClientAccount.getClient();
                //return DAOUtils.findClientByContractId(persistenceSession, contractId);
                //return null;
            }
        } //else {
        return findClientByContractId(persistenceSession, contractId);
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

            HashMap<Long, HashMap<String, ClientGroup>> orgMap = new HashMap<Long, HashMap<String, ClientGroup>>();
            Org org = (Org) persistenceSession.get(Org.class, idOfOrg);
            Set<Org> orgSet = org.getFriendlyOrg();
            /* совместимость организаций которые не имеют дружественных организаций */
            orgSet.add(org);
            for (Org o : orgSet) {
                List clientGroups = getClientGroupsByIdOfOrg(persistenceSession, o.getIdOfOrg());
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
                version = updateClientRegistryVersion(persistenceSession);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;

            while (clientParamItems.hasNext()) {
                SyncRequest.ClientParamRegistry.ClientParamItem clientParamItem = clientParamItems.next();

                //Проверяем, если у клиента меняется организация, то блокируем ему карты в старой организации
                Client client = DAOUtils.findClient(persistenceSession, clientParamItem.getIdOfClient());
                disableClientCardsIfChangeOrg(client, orgSet, idOfOrg);

                /*ClientGroup clientGroup = orgMap.get(2L).get(clientParamItem.getGroupName());
                *//* если группы нет то создаем *//*
                if(clientGroup == null){
                    clientGroup = DAOUtils.createClientGroup(persistenceSession, idOfOrg, clientParamItem.getGroupName());
                    *//* заносим в хэш - карту*//*
                    nameIdGroupMap.put(clientGroup.getGroupName(),clientGroup);
                }*/
                try {
                    //processSyncClientParamRegistryItem(idOfSync, idOfOrg, clientParamItem, orgMap, version);
                    processSyncClientParamRegistryItem(clientParamItem, orgMap, version, errorClientIds);
                } catch (Exception e) {
                    String message = String.format("Failed to process clientParamItem == %s", idOfOrg);
                    if(syncHistory!=null) {
                        createSyncHistoryException(idOfOrg, syncHistory, message);
                    }
                    logger.error(message, e);
                }
            }
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

    }

    private void processSyncClientParamRegistryItem(SyncRequest.ClientParamRegistry.ClientParamItem clientParamItem,
            HashMap<Long, HashMap<String, ClientGroup>> orgMap, Long version, List<Long> errorClientIds) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client client = findClient(persistenceSession, clientParamItem.getIdOfClient());
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
            if(clientParamItem.getExpenditureLimit()!=null){
                client.setExpenditureLimit(clientParamItem.getExpenditureLimit());
            }
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
                String email = clientParamItem.getEmail();
                //  если у клиента есть емайл и он не совпадает с новым, то сбрсываем ССОИД для ЕМП
                if(client != null && client.getEmail() != null && !client.getEmail().equals(email)) {
                    client.setSsoid("");
                }
                client.setEmail(email);
                if (!StringUtils.isEmpty(clientParamItem.getEmail()) && clientParamItem.getNotifyViaEmail() == null) {
                    client.setNotifyViaEmail(true);
                }
            }
            if (clientParamItem.getMobilePhone() != null) {
                String mobile = Client.checkAndConvertMobile(clientParamItem.getMobilePhone());
                //  если у клиента есть мобильный и он не совпадает с новым, то сбрсываем ССОИД для ЕМП
                if(client != null && client.getMobile() != null && !client.getMobile().equals(mobile)) {
                    client.setSsoid("");
                }
                client.setMobile(mobile);
                if (!StringUtils.isEmpty(mobile)) {
                    if (clientParamItem.getNotifyViaSMS() == null) {
                        client.setNotifyViaSMS(true);
                    }
                    //if (clientParamItem.getNotifyViaPUSH() == null) {
                    //    client.setNotifyViaPUSH(false);
                    //}
                }
            }
            if (clientParamItem.getMiddleGroup() != null) {
                client.setMiddleGroup(clientParamItem.getMiddleGroup());
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
            //if (clientParamItem.getNotifyViaPUSH() != null) {
            //    client.setNotifyViaPUSH(clientParamItem.getNotifyViaPUSH());
            //}
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
                    clientGroup = createClientGroup(persistenceSession, client.getOrg().getIdOfOrg(),
                          clientParamItem.getGroupName());
                    // заносим в хэш - карту
                    orgMap.get(client.getOrg().getIdOfOrg()).put(clientGroup.getGroupName(), clientGroup);
                }

                if ((clientGroup != null )&&(client.getClientGroup() != null)&&(clientGroup.getCompositeIdOfClientGroup() != null)) {
                    if(!clientGroup.getCompositeIdOfClientGroup().equals(client.getClientGroup().getCompositeIdOfClientGroup())){
                        ClientGroupMigrationHistory migrationHistory = new ClientGroupMigrationHistory(client.getOrg(), client);
                        migrationHistory.setComment(ClientGroupMigrationHistory.MODIFY_IN_ARM);
                        migrationHistory.setOldGroupId(client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup());
                        migrationHistory.setOldGroupName(client.getClientGroup().getGroupName());

                        migrationHistory.setNewGroupId(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                        migrationHistory.setNewGroupName(clientGroup.getGroupName());

                        persistenceSession.save(migrationHistory);
                    }

                }
                client.setClientGroup(clientGroup);
                client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
            }

            client.setClientRegistryVersion(version);

            persistenceSession.update(client);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }finally {
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

            Org organization = findOrg(persistenceSession, idOfOrg);
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
        ClientGroup clientGroup = findClientGroup(persistenceSession, compositeIdOfClientGroup);
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
            Long idOfClientGroup = getClientGroup(persistenceSession, idOfClient, organization.getIdOfOrg());
            if (idOfClientGroup != null && (idOfClientGroup.longValue() == clientGroup.getCompositeIdOfClientGroup()
                    .getIdOfClientGroup().longValue())) {
                continue;
            }
            ////
            Client client = findClient(persistenceSession, idOfClient);
            Set<Long> idOfFriendlyOrgSet = getIdOfFriendlyOrg(persistenceSession, client.getOrg().getIdOfOrg());
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
            updateClientVersionAndRemoteAddressByOrg(persistenceSession, idOfOrg, clientVersion, remoteAddress);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public SyncHistory createSyncHistory(Long idOfOrg, Long idOfPacket, Date startTime, String clientVersion,
            String remoteAddress) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org organization = getOrgReference(persistenceSession, idOfOrg);
            SyncHistory syncHistory = new SyncHistory(organization, startTime, idOfPacket, clientVersion,remoteAddress);
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

            SyncHistory syncHistory = getSyncHistoryReference(persistenceSession, idOfSync);
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

    private SyncResponse.AccRegistry getAccRegistry(Long idOfOrg, List<Long> clientIds, String clientVerision) throws Exception {
        if (SyncRequest.versionIsAfter(clientVerision, "2.7")){
            return null;
        }
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
            List<Card> cards = getClientsAndCardsForOrgs(persistenceSession, idOfOrgSet, clientIds);
            for (Card card : cards) {
                Client client = card.getClient();
                accRegistry.addItem(new SyncResponse.AccRegistry.Item(card));
            }
						 // Добавляем карты перемещенных клиентов.
            if(clientIds==null || clientIds.isEmpty()){
                List<Client> allocClients = ClientManager.findAllAllocatedClients(persistenceSession, org);
                for (Client client : allocClients) {
                    for (Card card : client.getCards()) {
                        accRegistry.addItem(new SyncResponse.AccRegistry.Item(client, card));
                    }
                }
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return accRegistry;
    }

    private AccRegistryUpdate getAccRegistryUpdate(Org org, Date fromDateTime) throws Exception {
        AccRegistryUpdate accRegistryUpdate = new AccRegistryUpdate();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            final Date currentDate = new Date();
            persistenceSession.refresh(org);
            List<AccountTransaction> accountTransactionList;
            accountTransactionList = getAccountTransactionsForOrgSinceTime(persistenceSession, org, fromDateTime,
                  currentDate);
            for (AccountTransaction accountTransaction : accountTransactionList) {
                accRegistryUpdate.addAccountTransactionInfo(accountTransaction);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return accRegistryUpdate;
    }

    private SyncResponse.AccIncRegistry getAccIncRegistry(Org org, Date fromDateTime) throws Exception {
        SyncResponse.AccIncRegistry accIncRegistry = new SyncResponse.AccIncRegistry();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Date currentDate = new Date();
            List<Integer> transactionSourceTypes = Arrays.asList(
                    AccountTransaction.PAYMENT_SYSTEM_TRANSACTION_SOURCE_TYPE,
                    AccountTransaction.CASHBOX_TRANSACTION_SOURCE_TYPE,
                    AccountTransaction.ACCOUNT_TRANSFER_TRANSACTION_SOURCE_TYPE,
                    AccountTransaction.CANCEL_TRANSACTION_SOURCE_TYPE);
            persistenceSession.refresh(org);
            List<AccountTransaction> accountTransactionList = getAccountTransactionsForOrgSinceTime(persistenceSession,
                  org, fromDateTime, currentDate, transactionSourceTypes);
            for (AccountTransaction accountTransaction : accountTransactionList) {
                SyncResponse.AccIncRegistry.Item accIncItem = new SyncResponse.AccIncRegistry.Item(
                        accountTransaction.getIdOfTransaction(), accountTransaction.getClient().getIdOfClient(),
                        accountTransaction.getTransactionTime(), accountTransaction.getTransactionSum(), accountTransaction.getTransactionSubBalance1Sum());
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

            Org organization = getOrgReference(persistenceSession, idOfOrg);
            List<Org> orgList = new ArrayList<Org>(organization.getFriendlyOrg());
            orgList.add(organization);
            List<Client> clients = findNewerClients(persistenceSession, orgList,
                  clientRegistryRequest.getCurrentVersion());
            for (Client client : clients) {
                if(client.getOrg().getIdOfOrg().equals(idOfOrg)){
                    clientRegistry.addItem(new SyncResponse.ClientRegistry.Item(client, 0));
                } else {
                    clientRegistry.addItem(new SyncResponse.ClientRegistry.Item(client, 1));
                }
            }
            List<Long> activeClientsId = findActiveClientsId(persistenceSession, orgList);
            // Получаем чужих клиентов.
            Map<String, Set<Client>> alienClients = ClientManager.findAllocatedClients(persistenceSession, organization);
            for (Map.Entry<String, Set<Client>> entry : alienClients.entrySet()) {
                boolean isTempClient = entry.getKey().equals("TemporaryClients");
                for (Client cl : entry.getValue()) {
                    if (cl.getClientRegistryVersion() > clientRegistryRequest.getCurrentVersion()) {
                        clientRegistry.addItem(new SyncResponse.ClientRegistry.Item(cl, 2, isTempClient));
                    }
                    activeClientsId.add(cl.getIdOfClient());
                }
            }
            // "при отличии количества активных клиентов в базе админки от клиентов, которые должны быть у данной организации
            // с учетом дружественных и правил - выдаем список идентификаторов всех клиентов в отдельном теге"
            if (clientRegistryRequest.getCurrentCount() != null && activeClientsId.size() != clientRegistryRequest
                    .getCurrentCount()) {
                for (Long id : activeClientsId) {
                    clientRegistry.addActiveClientId(id);
                }
            }
            if(!errorClientIds.isEmpty()){
                List errorClients = fetchErrorClientsWithOutFriendlyOrg(persistenceSession,
                      organization.getFriendlyOrg(), errorClientIds);
                ClientGroup clientGroup = findClientGroupByGroupNameAndIdOfOrg(persistenceSession,
                      organization.getIdOfOrg(), ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
                // Есть возможность отсутсвия даной группы
                if(clientGroup==null){
                    clientGroup = createClientGroup(persistenceSession, organization.getIdOfOrg(),
                          ClientGroup.Predefined.CLIENT_LEAVING);
                }
                for (Object object : errorClients) {
                    Client client = (Client) object;
                    client.setClientGroup(clientGroup);
                    if(client.getOrg().getIdOfOrg().equals(idOfOrg)){
                        clientRegistry.addItem(new SyncResponse.ClientRegistry.Item(client, 0));
                    } else {
                        clientRegistry.addItem(new SyncResponse.ClientRegistry.Item(client, 1));
                    }
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

    private ProhibitionsMenu getProhibitionsMenuData(Org org, long version) throws Exception{
        ProhibitionsMenu prohibitionsMenu = new ProhibitionsMenu();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = persistenceSessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            persistenceSession.refresh(org);
            List<ProhibitionMenu> prohibitionMenuList;
            prohibitionMenuList = getProhibitionMenuForOrgSinceVersion(persistenceSession, org, version);
            for (ProhibitionMenu prohibitionMenu: prohibitionMenuList){
                prohibitionsMenu.addProhibitionMenuInfo(prohibitionMenu);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return prohibitionsMenu;
    }

    private void processSyncMenu(Long idOfOrg, SyncRequest.ReqMenu reqMenu) throws Exception {
        if (null != reqMenu) {
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = persistenceSessionFactory.openSession();

                Org organization = getOrgReference(persistenceSession, idOfOrg);

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

                Iterator<SyncRequest.ReqMenu.Item> menuItems = reqMenu.getItems();
                boolean bFirstMenuItem = true;
                while (menuItems.hasNext()) {
                    //  Открываем тразнакцию для каждого дня
                    persistenceTransaction = persistenceSession.beginTransaction();

                    SyncRequest.ReqMenu.Item item = menuItems.next();
                    /// сохраняем данные меню для распространения
                    if (bOrgIsMenuExchangeSource) {
                        MenuExchange menuExchange = new MenuExchange(item.getDate(), idOfOrg, item.getRawXmlText(),
                                bFirstMenuItem ? MenuExchange.FLAG_ANCHOR_MENU : MenuExchange.FLAG_NONE);
                        persistenceSession.saveOrUpdate(menuExchange);
                    }
                    ///
                    Date menuDate = item.getDate();
                    ////
                    Menu menu = findMenu(persistenceSession, organization, Menu.ORG_MENU_SOURCE, menuDate);
                    Integer detailsHashCode = null;
                    // Подсчитываем хеш-код входных данных
                    final int detailsHashCode1 = item.hashCode();
                    if (null == menu) {
                        // Если меню не найдено то создаем
                        menu = new Menu(organization, menuDate, new Date(), Menu.ORG_MENU_SOURCE,
                                bFirstMenuItem ? Menu.FLAG_ANCHOR_MENU : Menu.FLAG_NONE, detailsHashCode1);
                        persistenceSession.save(menu);
                    } else {
                        // если меню найдено смотрим его хеш-код
                        detailsHashCode = menu.getDetailsHashCode();
                        // обновляем занчение хеша в случае если оно пусто (меню возможно уже есть но не имееет хеша)
                        // или в случае если меню изменилось

                        if(detailsHashCode==null || !detailsHashCode.equals(detailsHashCode1)){
                            //menu.setDetailsHashCode(detailsHashCode1);
                            //persistenceSession.persist(menu);
                            String sql = "update Menu set detailsHashCode=:detailsHashCode where org=:org and menuSource=:menuSource and menuDate=:menuDate";
                            Query query = persistenceSession.createQuery(sql);
                            query.setParameter("detailsHashCode", detailsHashCode1);
                            query.setParameter("org", organization);
                            query.setParameter("menuSource", Menu.ORG_MENU_SOURCE);
                            query.setParameter("menuDate", menuDate);
                            query.executeUpdate();
                        }
                    }
                    // проверяем спомощью хеш-кода изменилось ли меню, в случае если изменилось то перезаписываем
                    if(detailsHashCode==null || !detailsHashCode.equals(detailsHashCode1)){

                        processReqAssortment(persistenceSession, organization, menuDate, item.getReqAssortments());
                        HashMap<Long, MenuDetail> localIdsToMenuDetailMap = new HashMap<Long, MenuDetail>();
                        processReqMenuDetails(persistenceSession, menu, item, item.getReqMenuDetails(),
                                localIdsToMenuDetailMap);
                        processReqComplexInfos(persistenceSession, organization, menuDate, menu, item.getReqComplexInfos(),
                                localIdsToMenuDetailMap);

                    }

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
        deleteComplexInfoForDate(persistenceSession, organization, menuDate);

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
                Good good = findGoodByGuid(persistenceSession, goodsGuid);
                complexInfo.setGood(good);
            }
            Integer usedSubscriptionFeeding = reqComplexInfo.getUsedSubscriptionFeeding();
            if (usedSubscriptionFeeding != null) {
                complexInfo.setUsedSubscriptionFeeding(usedSubscriptionFeeding);
            }
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail = reqComplexInfo.getReqMenuDetail();
            if (reqMenuDetail != null) {
                MenuDetail menuDetailOptional = findMenuDetailByLocalId(persistenceSession, menu,
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
                    ClientGroup clientGroup = findClientGroup(persistenceSession, compId);
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
        deleteAssortmentForDate(persistenceSession, organization, menuDate);
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

    private void processLocalIdsToMenuDetailMap(Menu menu, SyncRequest.ReqMenu.Item item,
            Iterator<SyncRequest.ReqMenu.Item.ReqMenuDetail> reqMenuDetails,
            HashMap<Long, MenuDetail> localIdsToMenuDetailMap) throws Exception {
        while (reqMenuDetails.hasNext()) {
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail = reqMenuDetails.next();
            for (MenuDetail menuDetail : menu.getMenuDetails()) {
                if (areMenuDetailsEqual(menuDetail, reqMenuDetail)) {
                    localIdsToMenuDetailMap.put(reqMenuDetail.getIdOfMenu(), menuDetail);
                    break;
                }
            }
        }
    }

    private void processReqMenuDetails(Session persistenceSession, Menu menu, SyncRequest.ReqMenu.Item item,
            Iterator<SyncRequest.ReqMenu.Item.ReqMenuDetail> reqMenuDetails,
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

            String deldetSql = "delete ComplexInfoDetail  where menuDetail=:md";
            Query query = persistenceSession.createQuery(deldetSql);
            query.setParameter("md", menuDetail);
            query.executeUpdate();

            String delSql = "delete ComplexInfo  where menuDetail=:md";
            query = persistenceSession.createQuery(delSql);
            query.setParameter("md", menuDetail);
            query.executeUpdate();

            persistenceSession.delete(menuDetail);
        }

        // Добавляем новые элементы из пришедшего меню
        while (reqMenuDetails.hasNext()) {
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail = reqMenuDetails.next();
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
                newMenuDetail.setVitB2(reqMenuDetail.getVitB2());
                newMenuDetail.setVitPp(reqMenuDetail.getVitPp());
                newMenuDetail.setIdOfMenuFromSync(reqMenuDetail.getIdOfMenu());

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
        Iterator<SyncRequest.ReqMenu.Item.ReqMenuDetail> reqMenuDetails = menuItem.getReqMenuDetails();
        while (reqMenuDetails.hasNext()) {
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail = reqMenuDetails.next();
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

            Long idOfSourceOrg = findMenuExchangeSourceOrg(persistenceSession, idOfOrg);

            if (idOfSourceOrg != null) {
                List<MenuExchange> menuExchangeList = findMenuExchangeDataBetweenDatesIncludingSettings(
                      persistenceSession, idOfSourceOrg, toDate(startDate), toDate(endDate));
                boolean hasAnchorMenu = false;
                for (MenuExchange menuExchange : menuExchangeList) {
                    if ((menuExchange.getFlags() & MenuExchange.FLAG_ANCHOR_MENU) != 0) {
                        hasAnchorMenu = true;
                        break;
                    }
                }
                /// если в период выборки не попало корневое меню, то ищем его на предыдущие даты
                if (!hasAnchorMenu) {
                    MenuExchange anchorMenu = findMenuExchangeBeforeDateByEqFlag(persistenceSession, idOfSourceOrg,
                          toDate(startDate), MenuExchange.FLAG_ANCHOR_MENU);
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

            Org organization = getOrgReference(persistenceSession, idOfOrg);
            List menus = findMenusBetweenDates(persistenceSession, organization, Menu.CONTRAGENT_MENU_SOURCE, startDate,
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

            Org organization = getOrgReference(persistenceSession, idOfOrg);
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

    private SyncResponse.ResEnterEvents processSyncEnterEvents(EnterEvents enterEvents, Org org) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        SyncResponse.ResEnterEvents resEnterEvents = new SyncResponse.ResEnterEvents();
        Long idOfOrg;
        Map<String, Long> accessories = new HashMap<String, Long>();
        for (EnterEventItem e : enterEvents.getEvents()) {

            try {
                persistenceSession = persistenceSessionFactory.openSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                // Check enter event existence
                final Long idOfClient = e.getIdOfClient();



                //  Применяем фильтр оборудования
                idOfOrg = accessories.get(e.getTurnstileAddr());
                if(idOfOrg == null){
                    idOfOrg = org.getIdOfOrg();
                    idOfOrg =DAOService.getInstance().receiveIdOfOrgByAccessory(idOfOrg, Accessory.GATE_ACCESSORY_TYPE, e.getTurnstileAddr());
                    accessories.put(e.getTurnstileAddr(), idOfOrg);
                }


                if (existEnterEvent(persistenceSession, idOfOrg, e.getIdOfEnterEvent())) {
                    EnterEvent ee = findEnterEvent(persistenceSession,
                          new CompositeIdOfEnterEvent(e.getIdOfEnterEvent(), idOfOrg));
                    // Если ENTER событие существует (может быть последний результат синхронизации не был передан клиенту)
final boolean checkClient = (ee.getClient() == null && idOfClient == null) || (ee.getClient() != null && ee
                                    .getClient().getIdOfClient().equals(idOfClient));
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
                                idOfOrg, e.getIdOfEnterEvent()));
                        resEnterEvents.addItem(item);
                    }
                } else {
                    // find client by id
                    Client clientFromEnterEvent = null;
                    if (idOfClient != null) {
                        clientFromEnterEvent = (Client) persistenceSession.get(Client.class, idOfClient);
                        if (clientFromEnterEvent == null) {
                            SyncResponse.ResEnterEvents.Item item = new SyncResponse.ResEnterEvents.Item(
                                    e.getIdOfEnterEvent(), SyncResponse.ResEnterEvents.Item.RC_CLIENT_NOT_FOUND,
                                    String.format("Client not found: %d", idOfClient));
                            resEnterEvents.addItem(item);
                            continue;
                        }
                    }

                    EnterEvent enterEvent = new EnterEvent();
                    enterEvent.setCompositeIdOfEnterEvent(
                            new CompositeIdOfEnterEvent(e.getIdOfEnterEvent(), idOfOrg));
                    enterEvent.setEnterName(e.getEnterName());
                    enterEvent.setTurnstileAddr(e.getTurnstileAddr());
                    enterEvent.setPassDirection(e.getPassDirection());
                    enterEvent.setEventCode(e.getEventCode());
                    enterEvent.setIdOfCard(e.getIdOfCard());
                    enterEvent.setClient(clientFromEnterEvent);
                    enterEvent.setIdOfTempCard(e.getIdOfTempCard());
                    enterEvent.setEvtDateTime(e.getEvtDateTime());
                    enterEvent.setIdOfVisitor(e.getIdOfVisitor());
                    enterEvent.setVisitorFullName(e.getVisitorFullName());
                    enterEvent.setDocType(e.getDocType());
                    enterEvent.setDocSerialNum(e.getDocSerialNum());
                    enterEvent.setIssueDocDate(e.getIssueDocDate());
                    enterEvent.setVisitDateTime(e.getVisitDateTime());
                    final Long guardianId = e.getGuardianId();
                    enterEvent.setGuardianId(guardianId);
                    enterEvent.setChildPassChecker(e.getChildPassChecker());
                    enterEvent.setChildPassCheckerId(e.getChildPassCheckerId());
                    enterEvent.setIdOfClientGroup(e.getIdOfClientGroup());
                    persistenceSession.save(enterEvent);


                    SyncResponse.ResEnterEvents.Item item = new SyncResponse.ResEnterEvents.Item(e.getIdOfEnterEvent(),
                            0, null);
                    resEnterEvents.addItem(item);

                    if (isDateToday(e.getEvtDateTime()) &&
                            idOfClient != null &&
                            (e.getPassDirection() == EnterEvent.ENTRY || e.getPassDirection() == EnterEvent.EXIT ||
                                    e.getPassDirection() == EnterEvent.RE_ENTRY
                                    || e.getPassDirection() == EnterEvent.RE_EXIT)) {
                        final EventNotificationService notificationService = RuntimeContext.getAppContext()
                                .getBean(EventNotificationService.class);
                        //final String[] values = generateNotificationParams(persistenceSession, client,
                        //        e.getPassDirection(), e.getEvtDateTime(), guardianId);
                        String[] values = generateNotificationParams(persistenceSession, clientFromEnterEvent, e);
                        values = EventNotificationService.attachTargetIdToValues(e.getIdOfEnterEvent(), values);
                        switch (org.getType()){
                            case PROFESSIONAL:
                            case SCHOOL: {
                                values = EventNotificationService.attachEventDirectionToValues(e.getPassDirection(), values);

                                if (guardianId != null) {
                                    List<Client> guardians = findGuardiansByClient(persistenceSession, idOfClient, null);
                                    Client guardianFromEnterEvent = DAOService.getInstance().findClientById(guardianId);

                                    if(!(guardians==null || guardians.isEmpty())){
                                        for (Client destGuardian : guardians){
                                            if(guardians.size() > 1 && destGuardian.getIdOfClient().equals(
                                                    guardianFromEnterEvent.getIdOfClient())) {
                                                continue;
                                            }
                                            notificationService.sendNotificationAsync(destGuardian, clientFromEnterEvent, EventNotificationService.NOTIFICATION_ENTER_EVENT, values, e.getPassDirection(), guardianFromEnterEvent);
                                        }
                                    }
                                }

                                notificationService.sendNotificationAsync(clientFromEnterEvent, null, EventNotificationService.NOTIFICATION_ENTER_EVENT, values, e.getPassDirection());
                            } break;
                            case KINDERGARTEN: {
                                if(guardianId!=null){
                                    List<Client> guardians = findGuardiansByClient(persistenceSession, idOfClient, null);//guardianId);
                                    Client guardianFromEnterEvent = DAOService.getInstance().findClientById(guardianId);
                                    values = EventNotificationService.attachGuardianIdToValues(guardianFromEnterEvent.getIdOfClient(), values);
                                    values = EventNotificationService.attachEventDirectionToValues(e.getPassDirection(), values);
                                    if(!(guardians==null || guardians.isEmpty())){
                                        for (Client destGuardian : guardians){
                                            if(guardians.size() > 1 && destGuardian.getIdOfClient().equals(
                                                    guardianFromEnterEvent.getIdOfClient())) {
                                                continue;
                                            }
                                            notificationService.sendNotificationAsync(destGuardian, clientFromEnterEvent,
                                                    EventNotificationService.NOTIFICATION_PASS_WITH_GUARDIAN, values, e.getPassDirection(), guardianFromEnterEvent);
                                        }
                                    } else {
                                        notificationService.sendNotificationAsync(clientFromEnterEvent, null,
                                                EventNotificationService.NOTIFICATION_PASS_WITH_GUARDIAN, values, e.getPassDirection(), guardianFromEnterEvent);
                                    }
                                }
                            } break;
                        }
                    }

                    /// Формирование журнала транзакции
                    if (RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_JOURNAL_TRANSACTIONS) &&
                            (e.getPassDirection() == EnterEvent.ENTRY || e.getPassDirection() == EnterEvent.EXIT ||
                                    e.getPassDirection() == EnterEvent.RE_ENTRY
                                    || e.getPassDirection() == EnterEvent.RE_EXIT) && e.getIdOfCard() != null) {
                        Card card = findCardByCardNo(persistenceSession, e.getIdOfCard());
                        final CompositeIdOfEnterEvent compositeIdOfEnterEvent = enterEvent.getCompositeIdOfEnterEvent();
                        if (card == null) {
                            final String message = "Не найдена карта по событию прохода: idOfOrg=%d, idOfEnterEvent=%d, idOfCard=%d";
                            logger.error(String.format(message,
                                    compositeIdOfEnterEvent.getIdOfOrg(),
                                    compositeIdOfEnterEvent.getIdOfEnterEvent(), e.getIdOfCard()));
                        }

                        if (card != null && card.getCardType() == Card.TYPE_UEC) {
                            String OGRN = extraxtORGNFromOrgByIdOfOrg(persistenceSession, idOfOrg);
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
                                        Long.toHexString(card.getCardNo()), clientFromEnterEvent.getSan(), clientFromEnterEvent.getContractId(),
                                        clientFromEnterEvent.getClientGroupTypeAsString(), e.getEnterName());
                                persistenceSession.save(transactionJournal);
                            }
                        }
                    }

                }
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception ex) {
                logger.error("Save enter event to database error: ", ex);
                resEnterEvents = new SyncResponse.ResEnterEvents();
                for (EnterEventItem ee : enterEvents.getEvents()) {
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
        DiaryClass diaryClass = findDiaryClass(persistenceSession, compositeIdOfDiaryClass);
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
        DiaryTimesheet diaryTimesheet = findDiaryTimesheet(persistenceSession, compositeIdOfDiaryTimesheet);
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
        DiaryValue diaryValue = findDiaryValue(persistenceSession, compositeIdOfDiaryValue);
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
        DiaryValue diaryValue = findDiaryValue(persistenceSession, compositeIdOfDiaryValue);
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
        DiaryValue diaryValue = findDiaryValue(persistenceSession, compositeIdOfDiaryValue);
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
                card.setState(CardState.BLOCKED.getValue());
                card.setLockReason("Выпуск новой карты");
                persistenceSession.update(card);
            }
        }
    }

    private void createTempCard(Session persistenceSession, Long idOfOrg, long cardNo, String cardPrintedNo) throws Exception {
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
            if(cardTemp.getOrg().getIdOfOrg().equals(idOfOrg)){
                cardTemp.setCardPrintedNo(cardPrintedNo);
            } else {
                String orgInfo = org.getIdOfOrg()+":"+org.getOfficialName()+" '"+org.getAddress()+"'";
                throw new Exception(
                        String.format("Временная карта уже зарегистрирована в другой организации: %s. Статус карты - %s.",
                                orgInfo, cardTemp.getCardStation()));
            }
        } else {
            cardTemp = new CardTemp(org, cardNo, cardPrintedNo);
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
            Org organization = getOrgReference(persistenceSession, idOfOrg);
            Long result = organization.getOrgSync().getIdOfPacket();
            organization.getOrgSync().setIdOfPacket(++result);
            //organization.setIdOfPacket(result + 1);
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

    private String[] generateNotificationParams(Session session, Client client, EnterEventItem event) throws Exception {
        final int passDirection = event.getPassDirection();
        final Long guardianId = event.getGuardianId();
        final Integer childPassChecker  = event.getChildPassChecker();
        final Long childPassCheckerId = event.getChildPassCheckerId();
        final Date eventDate = event.getEvtDateTime();
        final String enterEvent = "Вход";
        final String exitEvent = "Выход";
        String eventName = "";
        if (passDirection == EnterEvent.ENTRY) {
            eventName = enterEvent;
        } else if (passDirection == EnterEvent.EXIT) {
            eventName = exitEvent;
        } else if (passDirection == EnterEvent.RE_ENTRY) {
            eventName = enterEvent;
        } else if (passDirection == EnterEvent.RE_EXIT) {
            eventName = exitEvent;
        }
        // Если представитель не пуст, то значит вход/выход в детский сад. Иначе - в школу.
        eventName = eventName + (guardianId == null ? (eventName.equals(enterEvent) ? " в школу"
                : eventName.equals(exitEvent) ? " из школы" : "") : "");
        String guardianName = "";
        if (guardianId != null) {
            Person guardPerson = ((Client) session.load(Client.class, guardianId)).getPerson();
            guardianName = StringUtils.join(new Object[]{guardPerson.getSurname(), guardPerson.getFirstName()}, ' ');
        }
        String childPassCheckerMark = "";
        if (childPassChecker != null) {
            if (childPassChecker.longValue() == 0) {
                childPassCheckerMark = "восп.";
            } else {
                childPassCheckerMark = "охр.";
            }
        }
        String childPassCheckerName = "";
        if (childPassCheckerId != null) {
            Person childPassCheckerPerson = ((Client) session.load(Client.class, childPassCheckerId)).getPerson();
            childPassCheckerName = StringUtils.join(new Object[]{childPassCheckerPerson.getSurname(), childPassCheckerPerson.getFirstName()}, ' ');
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(RuntimeContext.getInstance().getDefaultLocalTimeZone(null));
        calendar.setTime(eventDate);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String time = (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        String empTime = df.format(eventDate);
        //String clientName = client.getPerson().getSurname() + " " + client.getPerson().getFirstName();
        return new String[]{
                "balance", CurrencyStringUtils.copecksToRubles(client.getBalance()), "contractId",
                ContractIdFormat.format(client.getContractId()), "surname", client.getPerson().getSurname(),
                "firstName", client.getPerson().getFirstName(), "eventName", eventName, "eventTime", time, "guardian",
                guardianName, "empTime", empTime, "childPassCheckerMark", childPassCheckerMark, "childPassCheckerName", childPassCheckerName};
    }

    private String[] generatePaymentNotificationParams(Session session, Client client, Payment payment) {
        long complexes = 0L;
        long others = 0L;
        Iterator<Purchase> purchases = payment.getPurchases().iterator();
        while (purchases.hasNext()) {
            Purchase purchase = purchases.next();
            if (purchase.getType() >= OrderDetail.TYPE_COMPLEX_MIN && purchase.getType() <= OrderDetail.TYPE_COMPLEX_MAX) {
                complexes += purchase.getSocDiscount() + purchase.getrPrice();
            } else {
                others += purchase.getSocDiscount() + purchase.getrPrice();
            }
        }

        //String date = new SimpleDateFormat("dd.MM.yy HH:mm").format(new Date(System.currentTimeMillis()));
        String date = new SimpleDateFormat("dd.MM.yy HH:mm").format(payment.getTime());
        String contractId = String.valueOf(client.getContractId());
        if(payment.getOrderType().equals(OrderTypeEnumType.SUBSCRIPTION_FEEDING)){
           contractId=contractId+"01";
        }
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        String empTime = df.format(payment.getTime());
        return new String[] {
                "date", date,
                "contractId", contractId,
                "others", CurrencyStringUtils.copecksToRubles(others),
                "complexes", CurrencyStringUtils.copecksToRubles(complexes),
                "empTime", empTime};
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