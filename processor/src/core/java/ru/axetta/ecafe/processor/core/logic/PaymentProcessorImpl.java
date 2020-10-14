/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.AccessDiniedException;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdGenerator;
import ru.axetta.ecafe.processor.core.event.EventNotificator;
import ru.axetta.ecafe.processor.core.event.PaymentProcessEvent;
import ru.axetta.ecafe.processor.core.payment.PaymentProcessor;
import ru.axetta.ecafe.processor.core.payment.PaymentRequest;
import ru.axetta.ecafe.processor.core.payment.PaymentResponse;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadExternalsService;
import ru.axetta.ecafe.processor.core.service.geoplaner.GeoplanerManager;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ParameterStringUtils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

import static ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils.findUser;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 07.07.16
 * Time: 10:37
 */
public class PaymentProcessorImpl implements PaymentProcessor {
    private static final Logger logger = LoggerFactory.getLogger(Processor.class);
    private final SessionFactory persistenceSessionFactory;
    private final EventNotificator eventNotificator;

    public PaymentProcessorImpl(SessionFactory persistenceSessionFactory, EventNotificator eventNotificator) {
        this.persistenceSessionFactory = persistenceSessionFactory;
        this.eventNotificator = eventNotificator;
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

    @Override
    public PaymentResponse.ResPaymentRegistry.Item processPayPaymentRegistryPayment(Long idOfContragent,
            PaymentRequest.PaymentRegistry.Payment payment) throws Exception {

        Client client = DAOReadExternalsService.getInstance().findClient(null, payment.getContractId()); //payment.getIdOfClient - это контрактИд клиента
        SecurityJournalBalance journal = SecurityJournalBalance.getSecurityJournalBalanceDataFromPayment(payment, client);

        Contragent contragent = DAOReadExternalsService.getInstance().findContragent(idOfContragent);
        if (null == contragent) {
            SecurityJournalBalance.saveSecurityJournalBalanceFromPayment(journal, false, "Не найден контрагент",
                    null);
            return new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null, null, null,
                    PaymentProcessResult.CONTRAGENT_NOT_FOUND.getCode(),
                    String.format("%s. IdOfContragent == %s, ContractId == %s",
                            PaymentProcessResult.CONTRAGENT_NOT_FOUND.getDescription(), idOfContragent,
                            payment.getContractId()), null);
        }
        Long idOfClientPayment = DAOReadExternalsService.getInstance().existingClientPayment(contragent, payment);
        if (idOfClientPayment != null) {
            SecurityJournalBalance.saveSecurityJournalBalanceFromPayment(journal, false,
                    "Платеж с такими атрибутами уже зарегистрирован", null);
            logger.warn(
                    String.format("Payment request with duplicated attributes IdOfContragent == %s, payment == %s",
                            idOfContragent, payment.toString()));
            PaymentResponse.ResPaymentRegistry.Item result =
                new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null, null, null,
                    PaymentProcessResult.PAYMENT_ALREADY_REGISTERED.getCode(),
                    String.format("%s. IdOfContragent == %s, IdOfPayment == %s",
                            PaymentProcessResult.PAYMENT_ALREADY_REGISTERED.getDescription(), idOfContragent,
                            payment.getIdOfPayment()), null);
            result.setIdOfClientPayment(idOfClientPayment);
            return result;
        }

        Integer subBalanceNum = null;
        Long contractId = null;

        boolean enableSubBalanceOperation = RuntimeContext.getInstance()
                .getOptionValueBool(Option.OPTION_ENABLE_SUB_BALANCE_OPERATION);
        if (enableSubBalanceOperation) {
            if (payment.getContractId() != null) {
                String contractIdstr = String.valueOf(payment.getContractId());
                if (ContractIdGenerator.luhnTest(contractIdstr) || RuntimeContext.RegistryType.isSpb()) {
                    subBalanceNum = 0;
                    contractId = payment.getContractId();
                } else {
                    int len = contractIdstr.length();
                    if (len > 2 && ContractIdGenerator.luhnTest(contractIdstr.substring(0, len - 2))) {
                        subBalanceNum = Integer.parseInt(contractIdstr.substring(len - 2));
                        contractId = Long.parseLong(contractIdstr.substring(0, len - 2));
                    }
                }
            }
        } else {
            contractId = payment.getContractId();
        }

        if (contractId == null) {
            SecurityJournalBalance.saveSecurityJournalBalanceFromPayment(journal, false,
                    "ContractId клиента не найден", null);
            return new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null, null, null,
                    PaymentProcessResult.CLIENT_NOT_FOUND.getCode(),
                    String.format("%s. IdOfContragent == %s, ContractId == %s, ClientId == %s",
                            PaymentProcessResult.CLIENT_NOT_FOUND.getDescription(), idOfContragent,
                            payment.getContractId(), payment.getClientId()), null);
        }

        if (client == null) {
            client = findPaymentClient(contragent, contractId, payment.getClientId());
        }

        if (null == client) {
            SecurityJournalBalance.saveSecurityJournalBalanceFromPayment(journal, false, "Клиент не найден", null);
            return new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null, null, null,
                    PaymentProcessResult.CLIENT_NOT_FOUND.getCode(),
                    String.format("%s. IdOfContragent == %s, ContractId == %s, ClientId == %s",
                            PaymentProcessResult.CLIENT_NOT_FOUND.getDescription(), idOfContragent,
                            payment.getContractId(), payment.getClientId()), null);
        }

        Long idOfClient = client.getIdOfClient();
        Long paymentTspContragentId = null;
        HashMap<String, String> payAddInfo = new HashMap<String, String>();
        Contragent defaultTsp = DAOReadExternalsService.getInstance().getOrgDefaultSupplier(
                client.getOrg().getIdOfOrg());
        boolean tspError = false;
        if (payment.getTspContragentId() != null) {
            // если явно указан контрагент ТСП получатель, проверяем что он соответствует организации клиента
            if (defaultTsp == null || !defaultTsp.getIdOfContragent().equals(payment.getTspContragentId())) {
                tspError = true;
            }
        }
        if (payment.getAllowedTSPIds() != null) {
            //Проверка на вхождение поставщика клиента в список разрешенных поставщиков
            if (!payment.getAllowedTSPIds().contains(defaultTsp.getIdOfContragent())) {
                tspError = true;
            }
        }
        if (tspError) {
            SecurityJournalBalance.saveSecurityJournalBalanceFromPayment(journal, false,
                    "Merchant (TSP) contragent is prohibited for this client", null);
            return new PaymentResponse.ResPaymentRegistry.Item(payment, null, null, null, null, null, null,
                    PaymentProcessResult.TSP_CONTRAGENT_IS_PROHIBITED.getCode(),
                    String.format("%s. IdOfTspContragent == %s, ContractId == %s, ClientId == %s",
                            PaymentProcessResult.TSP_CONTRAGENT_IS_PROHIBITED.getDescription(),
                            payment.getTspContragentId(), payment.getContractId(), payment.getClientId()),
                    null);
        }
        if (defaultTsp != null) {
            paymentTspContragentId = defaultTsp.getIdOfContragent();
            processContragentAddInfo(defaultTsp, payAddInfo);
        }
        ClientPayment clientPayment = null;
        if (!payment.isCheckOnly()) {
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = persistenceSessionFactory.openSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                long paymentSum = payment.getSum();
                if (payment.isResetBalance()) {
                    paymentSum -= client.getBalance();
                    logger.info("Processing payment with balance reset: " + client + "; current balance=" + client
                            .getBalance() + "; set balance=" + paymentSum);
                }

                final FinancialOpsManager financialOpsManager = RuntimeContext.getFinancialOpsManager();
                clientPayment = financialOpsManager
                    .createClientPayment(persistenceSession, client, contragent, payment.getPaymentMethod(),
                            paymentSum, payment.getPayTime(), payment.getIdOfPayment(),
                            payment.getAddPaymentMethod(), payment.getAddIdOfPayment());

                persistenceSession.flush();
                persistenceTransaction.commit();
                persistenceTransaction = null;
                RuntimeContext.getAppContext().getBean(PaymentNotificator.class).sendNotification(clientPayment, client, subBalanceNum);
                if(GeoplanerManager.isOn() && client.clientHasActiveSmartWatch()){
                    try {
                        GeoplanerManager manager = RuntimeContext.getAppContext().getBean(GeoplanerManager.class);
                        manager.sendPaymentInfoToGeoplaner(clientPayment, client);
                    } catch (Exception exc){
                        logger.error("Can't send to Geoplaner JSON with Payments: ", exc);
                    }
                }
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }

        PaymentResponse.ResPaymentRegistry.Item result = new PaymentResponse.ResPaymentRegistry.Item(payment,
                idOfClient, client.getContractId(), paymentTspContragentId, null, client.getBalance(),
                PaymentProcessResult.OK.getCode(), PaymentProcessResult.OK.getDescription(), client,
                client.getSubBalance1(), payAddInfo, defaultTsp.getInn(), defaultTsp.getContragentName(), defaultTsp.getBic(),
                defaultTsp.getAccount(), defaultTsp.getBank(), defaultTsp.getCorrAccount(), defaultTsp.getKpp());
        if (clientPayment != null) {
            result.setIdOfClientPayment(clientPayment.getIdOfClientPayment());
        }

        SecurityJournalBalance.saveSecurityJournalBalanceFromPayment(journal, true, "OK", clientPayment);

        return result;

    }

    private static Client findPaymentClient(Contragent contragent, Long contractId,
            Long clientId) throws Exception {
        if (clientId != null) {
            return DAOReadExternalsService.getInstance().findClient(clientId, null);
        }
        // Извлекаем из модели данных клиента, на карту которого необходимо перевести платеж
        // Если необходимо преобразовать номер счета, то делаем это
        if (contragent.getNeedAccountTranslate()) {
            ContragentClientAccount contragentClientAccount = DAOReadExternalsService.getInstance()
                    .findContragentClientAccount(
                            new CompositeIdOfContragentClientAccount(contragent.getIdOfContragent(), contractId));
            if (null != contragentClientAccount) {
                return contragentClientAccount.getClient();
            }
        }
        return DAOReadExternalsService.getInstance().findClient(null, contractId);
    }

    private void processContragentAddInfo(Contragent contragent, HashMap<String, String> payAddInfo) {
        if (contragent.getRemarks() != null && contragent.getRemarks().length() > 0) {
            ParameterStringUtils.extractParameters("TSP.", contragent.getRemarks(), payAddInfo);
        }
    }
}
