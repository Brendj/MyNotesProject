/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.service.SMSSubscriptionFeeService;
import ru.axetta.ecafe.processor.core.sync.handlers.payment.registry.Payment;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import static ru.axetta.ecafe.processor.core.logic.ClientManager.findGuardiansByClient;

@Component
@Scope("singleton")
public class FinancialOpsManager {

    static Boolean useOperatorScheme = null;

    @PersistenceContext(unitName = "processorPU")
    EntityManager em;

    @Autowired
    private RuntimeContext runtimeContext;

    @Resource
    EventNotificationService eventNotificationService;

    CurrentPositionsManager getCurrentPositionsManager(Session session) {
        return new CurrentPositionsManager(session);
    }

    @Transactional
    public ClientSms createClientFailedSmsCharge(Client client, String idOfSms, String phone, Long contentsId, Integer contentsType,
            String textContents, Date serviceSendTime) throws Exception {

        Session session = em.unwrap(Session.class);

        if(textContents.length() >= 70) {
            textContents = textContents.substring(0, 69);
        }
        long priceOfSms = client.getOrg().getPriceOfSms();
        ClientSms clientSms = new ClientSms(idOfSms, client, null, phone, contentsId, contentsType, textContents,
                serviceSendTime, priceOfSms, serviceSendTime, client.getOrg().getIdOfOrg());
        clientSms.setDeliveryStatus(ClientSms.NOT_DELIVERED_TO_RECIPENT);
        session.save(clientSms);
        return clientSms;
    }

    @Transactional
    public ClientSms createClientSmsCharge(Client client, String idOfSms, String phone, Long contentsId,Integer contentsType,
            String textContents, Date serviceSendTime, Date eventTime) throws Exception {
        return createClientSmsCharge(client, idOfSms, phone, contentsId, contentsType, textContents, serviceSendTime, eventTime, false, null);
    }

    @Transactional
    public ClientSms createClientSmsCharge(Client client, String idOfSms, String phone, Long contentsId,Integer contentsType,
            String textContents, Date serviceSendTime, Date eventTime, boolean isDelivered, Long idOfSourceOrg) throws Exception {

        Session session = em.unwrap(Session.class);
        long priceOfSms = client.getOrg().getPriceOfSms();
        int paymentType = runtimeContext.getOptionValueInt(Option.OPTION_SMS_PAYMENT_TYPE);
        //Card card = DAOUtils.findActiveCard(em, client);
        Date currTime = new Date();

        AccountTransaction accountTransaction = null;
        if (priceOfSms != 0 && paymentType == SMSSubscriptionFeeService.SMS_PAYMENT_BY_THE_PIECE) {
            // Register transaction
            //accountTransaction = new AccountTransaction(client, null, -priceOfSms, "",
            //        AccountTransaction.INTERNAL_ORDER_TRANSACTION_SOURCE_TYPE, currTime);
            //session.save(accountTransaction);
            accountTransaction = ClientAccountManager.processAccountTransaction(session, client, null, -priceOfSms, "",
                    AccountTransaction.INTERNAL_ORDER_TRANSACTION_SOURCE_TYPE, null, currTime);

            Contragent operatorContragent = DAOUtils.findContragentByClass(session, Contragent.OPERATOR);
            Contragent clientContragent = DAOUtils.findContragentByClass(session, Contragent.CLIENT);
            // уменьшаем позицию Оператор - Клиент
            getCurrentPositionsManager(session)
                    .changeCurrentPosition(-priceOfSms, operatorContragent, clientContragent);
        }

        textContents = textContents.substring(0, Math.min(textContents.length(), 70));
        Long orgId = idOfSourceOrg == null ? client.getOrg().getIdOfOrg() : idOfSourceOrg;
        ClientSms clientSms = new ClientSms(idOfSms, client, accountTransaction, phone, contentsId, contentsType, textContents,
                serviceSendTime, priceOfSms, eventTime, orgId);
        clientSms.setSendTime(serviceSendTime);
        clientSms.setContentsId(contentsId);
        if(isDelivered) {
            clientSms.setDeliveryTime(serviceSendTime);
            clientSms.setDeliveryStatus(ClientSms.DELIVERED_TO_RECIPENT);
        }
        session.save(clientSms);
        //session.close();
        return clientSms;
    }

    public SubscriptionFee createSubscriptionFeeCharge(Session session, Client client, long subscriptionPrice,
            int subscriptionYear, int periodNo, int type) throws Exception {
        Date currentTime = new Date();
        AccountTransaction accountTransaction = ClientAccountManager
                .processAccountTransaction(session, client, null, -subscriptionPrice, "",
                        AccountTransaction.SUBSCRIPTION_FEE_TRANSACTION_SOURCE_TYPE, null, currentTime);

        SubscriptionFee subscriptionFee = new SubscriptionFee(subscriptionYear, periodNo, accountTransaction,
                subscriptionPrice, currentTime, type);
        session.save(subscriptionFee);

        Contragent operatorContragent = DAOUtils.findContragentByClass(session, Contragent.OPERATOR);
        Contragent clientContragent = DAOUtils.findContragentByClass(session, Contragent.CLIENT);
        // уменьшаем позицию Оператор - Клиент
        getCurrentPositionsManager(session)
                .changeCurrentPosition(-subscriptionPrice, operatorContragent, clientContragent);
        return subscriptionFee;
    }

    public void createOrderCharge(Session session, Payment payment, Long idOfOrg,
            Client client, Card card, Long confirmerId, boolean isFromFriendlyOrg) throws Exception {
        // By default we have no transaction
        AccountTransaction orderTransaction = null;
        // If "card part" of payment is specified...
        if (0 != payment.getSumByCard()) {
            if(payment.getOrderType()==null){
                // поддержка старых версий
                orderTransaction = ClientAccountManager.processAccountTransaction(session, client, card,
                      -payment.getSumByCard(), ""+idOfOrg+"/"+payment.getIdOfOrder(),
                      AccountTransaction.CLIENT_ORDER_TRANSACTION_SOURCE_TYPE, idOfOrg, new Date());
            } else {
                if(payment.getOrderType()==OrderTypeEnumType.PAY_PLAN ||
                      payment.getOrderType()==OrderTypeEnumType.SUBSCRIPTION_FEEDING ){
                    orderTransaction = ClientAccountManager.checkBalanceAndProcessAccountTransaction(session,
                          client, card, -payment.getSumByCard(), "" + idOfOrg + "/" + payment.getIdOfOrder(),
                          AccountTransaction.CLIENT_ORDER_TRANSACTION_SOURCE_TYPE, new Date(), idOfOrg);
                } else {
                    orderTransaction = ClientAccountManager.processAccountTransaction(session, client, card,
                          -payment.getSumByCard(), ""+idOfOrg+"/"+payment.getIdOfOrder(),
                          AccountTransaction.CLIENT_ORDER_TRANSACTION_SOURCE_TYPE, idOfOrg, new Date());
                }
            }
        }

        POS pos = null;
        Contragent supplier;
        if (payment.getIdOfPOS() != null) {
            pos = (POS) session.get(POS.class, payment.getIdOfPOS());
            if (pos == null) {
                //throw new Exception("POS with id " + payment.getIdOfPOS() + " not found");
                supplier = getDefaultSupplier(session, idOfOrg);
            } else {
                supplier = pos.getContragent();
            }
        } else {
            // Использовать поставщика по умолчанию из организации
            supplier = getDefaultSupplier(session, idOfOrg);
        }

        //idOfOrg = DAOService.getInstance().receiveIdOfOrgByAccessory(idOfOrg, Accessory.BANK_ACCESSORY_TYPE, "" + payment.getIdOfPOS());
        Order order = new Order(new CompositeIdOfOrder(idOfOrg, payment.getIdOfOrder()), payment.getIdOfCashier(),
                payment.getSocDiscount(), payment.getTrdDiscount(), payment.getGrant(), payment.getRSum(),
                payment.getTime(),payment.getOrderDate(), payment.getSumByCard(), payment.getSumByCash(),payment.getComments(), client, card, orderTransaction, pos,
                supplier, payment.getOrderType(), payment.getIdOfPayForClient());
        order.setIsFromFriendlyOrg(isFromFriendlyOrg);
        if(client != null){
            Long idOfClientGroup = DAOService.getInstance().getClientGroupByClientId(client.getIdOfClient());    //
            order.setIdOfClientGroup(idOfClientGroup);
        }

        Long sumByCard = order.getSumByCard();
        Long budgetSum = order.getSocDiscount() + order.getGrantSum();
        getCurrentPositionsManager(session).changeOrderPosition(sumByCard, budgetSum, supplier);
        if(confirmerId!=null){
            order.setConfirmerId(confirmerId);
        }
        session.save(order);

        /// Формирование журнала транзакции
        if (RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_JOURNAL_TRANSACTIONS)) {
            if (card!=null && card.getCardType()==Card.TYPE_UEC) {
                Criteria orgCriteria = session.createCriteria(Org.class);
                orgCriteria.add(Restrictions.eq("idOfOrg", idOfOrg));
                Org org = (Org) orgCriteria.uniqueResult();
                if(budgetSum>0){
                    registerTransactionJournal(TransactionJournal.TRANS_CODE_FD_BEN, org, order, card, client, budgetSum, session);
                }
                if (payment.getSumByCard()>0) {
                    registerTransactionJournal(TransactionJournal.TRANS_CODE_DEBIT, org, order, card, client, payment.getSumByCard(), session);
                }
            }
        }
    }

    protected Contragent getDefaultSupplier(Session session, long idoforg) {
        Org org = (Org) session.get(Org.class, idoforg);
        Contragent supplier = org.getDefaultSupplier();
        return supplier;
    }

    public void cancelOrder(Session session, Order order) throws Exception {
        if (order.getState() != Order.STATE_COMMITED) {
        } else {
            order.setState(Order.STATE_CANCELED);
            session.save(order);
            for (OrderDetail od : order.getOrderDetails()) {
                od.setState(OrderDetail.STATE_CANCELED);
                session.save(od);
            }

            CanceledOrder canceledOrder = new CanceledOrder(order, order.getOrg());

            if (0 != order.getSumByCard()) {
                AccountTransaction transaction = order.getTransaction();
                canceledOrder.setIdOfTransaction(transaction.getIdOfTransaction());
                ClientAccountManager.cancelAccountTransaction(session, transaction, new Date());
            }

            session.save(canceledOrder);

            Long sumByCard = order.getSumByCard();
            Long budgetSum = order.getSocDiscount() + order.getGrantSum();
            getCurrentPositionsManager(session).changeOrderPosition(-sumByCard, -budgetSum, order.getContragent());
        }
    }


    private static void registerTransactionJournal(String transCode, Org org, Order order, Card card, Client client, Long financialAmount, Session session) {
        TransactionJournal transactionJournal = new TransactionJournal(order.getCompositeIdOfOrder().getIdOfOrg(),
                order.getCompositeIdOfOrder().getIdOfOrder(), new Date(), org.getOGRN(), TransactionJournal.SERVICE_CODE_SCHL_FD,
                transCode,
                TransactionJournal.CARD_TYPE_CODE_UEC, TransactionJournal.CARD_TYPE_ID_CODE_MUID, Card.TYPE_NAMES[card.getCardType()], Long.toHexString(card.getCardNo()),
                client.getSan(), client.getContractId(), client.getClientGroupTypeAsString(), financialAmount, order.getCreateTime());
        session.save(transactionJournal);
    }

    public ClientPayment createClientPayment(Session session, Client client, Contragent contragent,
            Integer paymentMethod, Long paySum, Date createTime, String idOfPayment, String addPaymentMethod,
            String addIdOfPayment, Integer subScribeNum)
            throws Exception {
        ClientPayment clientPayment;

        Integer payType = null;
        if (idOfPayment.matches(".*" + ClientPayment.CANCEL_SUBSTRING + ".*")) {
            payType = ClientPayment.CANCELLED_PAYMENT;
        }

        // регистрируем транзакцию и проводим по балансу
        if(subScribeNum==null || subScribeNum.equals(0)){
            AccountTransaction accountTransaction;
            if(paymentMethod != null && paymentMethod == ClientPayment.CASHIER_PAYMENT_METHOD){
                accountTransaction = ClientAccountManager.processAccountTransaction(session, client,
                        null, paySum, idOfPayment,
                        AccountTransaction.CASHBOX_TRANSACTION_SOURCE_TYPE, null, new Date());
            }else {
                accountTransaction = ClientAccountManager.processAccountTransaction(session, client,
                        null, paySum, idOfPayment,
                        AccountTransaction.PAYMENT_SYSTEM_TRANSACTION_SOURCE_TYPE, null, new Date());
            }
            // регистрируем платеж клиента
            int pType = (payType == null ? ClientPayment.CLIENT_TO_ACCOUNT_PAYMENT : payType);
            clientPayment = new ClientPayment(accountTransaction, paymentMethod, paySum, pType, createTime,
                    idOfPayment, contragent, getContragentReceiverForPayments(session, client), addPaymentMethod, addIdOfPayment);
            registerClientPayment(session, clientPayment, client);
        } else {
            // TODO: логика по субсчетам
            AccountTransaction accountTransaction;
            if(paymentMethod != null && paymentMethod == ClientPayment.CASHIER_PAYMENT_METHOD){
                accountTransaction = ClientAccountManager.processAccountTransaction(session, client, null, paySum, idOfPayment,
                        AccountTransaction.CASHBOX_TRANSACTION_SOURCE_TYPE, new Date(), subScribeNum);
            }else {
                accountTransaction = ClientAccountManager.processAccountTransaction(session, client, null, paySum, idOfPayment,
                    AccountTransaction.PAYMENT_SYSTEM_TRANSACTION_SOURCE_TYPE, new Date(), subScribeNum);
            }
            // регистрируем платеж клиента
            int pType = (payType == null ? ClientPayment.CLIENT_TO_SUB_ACCOUNT_PAYMENT : payType);
            clientPayment = new ClientPayment(accountTransaction, paymentMethod, paySum, pType, createTime,
                    idOfPayment, contragent, getContragentReceiverForPayments(session, client), addPaymentMethod, addIdOfPayment);
            registerSubBalance1ClientPayment(session, clientPayment, client);
        }

        return clientPayment;
    }

    private Contragent getContragentReceiverForPayments(Session session, Client client) {
        if (isOperatorScheme(session)) {
            // Оператор
            return DAOUtils.findContragentByClass(session, Contragent.OPERATOR);
        }
        else {
            return client.getOrg().getDefaultSupplier();
        }
    }

    public void createClientPaymentWithOrder(Session session, ClientPaymentOrder clientPaymentOrder,
            Client client,String addIdOfPayment) throws Exception {
        // регистрируем транзакцию и проводим по балансу
        AccountTransaction accountTransaction = ClientAccountManager.processAccountTransaction(session, client,
                null, clientPaymentOrder.getPaySum(), clientPaymentOrder.getIdOfPayment(),
                AccountTransaction.PAYMENT_SYSTEM_TRANSACTION_SOURCE_TYPE, null, new Date());

        // регистрируем платеж клиента
        ClientPayment clientPayment = new ClientPayment(accountTransaction, clientPaymentOrder,
                getContragentReceiverForPayments(session, client),new Date(),addIdOfPayment);

        registerClientPayment(session, clientPayment, client);
    }

    private void registerClientPayment(Session session,
            ClientPayment clientPayment,
            Client client) throws Exception {
        Long paySum = clientPayment.getPaySum();
        Contragent payAgent = clientPayment.getContragent();

        getCurrentPositionsManager(session).changeClientPaymentPosition(payAgent, paySum, clientPayment.getContragentReceiver());
        session.save(clientPayment);

        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        String empTime = df.format(clientPayment.getCreateTime());

        String[] values = new String[]{
                "paySum",CurrencyStringUtils.copecksToRubles(paySum),
                "balance", CurrencyStringUtils.copecksToRubles(client.getBalance()),
                "contractId",String.valueOf(client.getContractId()),
                "surname",client.getPerson().getSurname(),
                "firstName",client.getPerson().getFirstName(),
                "empTime", empTime
        };
        values = EventNotificationService.attachTargetIdToValues(clientPayment.getIdOfClientPayment(), values);

        eventNotificationService.sendNotificationAsync(client, null, EventNotificationService.NOTIFICATION_BALANCE_TOPUP, values, clientPayment.getCreateTime());

        List<Client> guardians = findGuardiansByClient(session, client.getIdOfClient(), null);

        if (!(guardians == null || guardians.isEmpty())) {
            for (Client destGuardian : guardians) {
                if (ClientManager.allowedGuardianshipNotification(destGuardian.getIdOfClient(),
                        client.getIdOfClient(), ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_REFILLS.getValue())) {
                    eventNotificationService.sendNotificationAsync(destGuardian, client, EventNotificationService.NOTIFICATION_BALANCE_TOPUP, values, clientPayment.getCreateTime());
                }
            }
        }
    }

    private void registerSubBalance1ClientPayment(Session session,
            ClientPayment clientPayment,
            Client client) throws Exception {
        Long paySum = clientPayment.getPaySum();
        Contragent payAgent = clientPayment.getContragent();

        getCurrentPositionsManager(session).changeClientPaymentPosition(payAgent, paySum, clientPayment.getContragentReceiver());
        session.save(clientPayment);
        final String contractId = String.valueOf(client.getContractId())+"01";
        final Long balance = client.getSubBalance1()==null?0L:client.getSubBalance1();

        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        String empTime = df.format(clientPayment.getCreateTime());

        String[] values = new String[]{
                "paySum",CurrencyStringUtils.copecksToRubles(paySum),
                "balance", CurrencyStringUtils.copecksToRubles(balance),
                "contractId", contractId,
                "surname",client.getPerson().getSurname(),
                "firstName",client.getPerson().getFirstName(),
                "empTime", empTime
        };
        values = EventNotificationService.attachTargetIdToValues(clientPayment.getIdOfClientPayment(), values);
        eventNotificationService.sendNotificationAsync(client, null, EventNotificationService.NOTIFICATION_BALANCE_TOPUP, values, clientPayment.getCreateTime());

        List<Client> guardians = findGuardiansByClient(session, client.getIdOfClient(), null);

        if (!(guardians == null || guardians.isEmpty())) {
            for (Client destGuardian : guardians) {
                if (ClientManager.allowedGuardianshipNotification(destGuardian.getIdOfClient(),
                        client.getIdOfClient(), ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_REFILLS.getValue())) {
                    eventNotificationService.sendNotificationAsync(destGuardian, client, EventNotificationService.NOTIFICATION_BALANCE_TOPUP, values, clientPayment.getCreateTime());
                }
            }
        }
    }


    private boolean isOperatorScheme(Session session) {
        if (useOperatorScheme!=null) return useOperatorScheme;
        return useOperatorScheme = DAOUtils.getOptionValueBool(session, Option.OPTION_WITH_OPERATOR, false);
    }

    public void createSettlement(Session session, Settlement settlement) throws Exception {
        getCurrentPositionsManager(session).changeSettlementPosition(settlement.getSumma(),
                settlement.getIdOfContragentPayer().getClassId(),
                settlement.getIdOfContragentReceiver().getClassId(),
                settlement.getIdOfContragentPayer(),
                settlement.getIdOfContragentReceiver());
        session.save(settlement);
    }

    public void editSettlement(Session session, Settlement settlement, Long preSumma) throws Exception {
        getCurrentPositionsManager(session).changeSettlementPosition(preSumma,
                settlement.getIdOfContragentPayer().getClassId(),
                settlement.getIdOfContragentReceiver().getClassId(),
                settlement.getIdOfContragentPayer(),
                settlement.getIdOfContragentReceiver());
        session.update(settlement);
    }

    public void deleteSettlement(Session session, Settlement settlement) throws Exception {
        getCurrentPositionsManager(session).changeSettlementPosition(-settlement.getSumma(),
                settlement.getIdOfContragentPayer().getClassId(),
                settlement.getIdOfContragentReceiver().getClassId(),
                settlement.getIdOfContragentPayer(),
                settlement.getIdOfContragentReceiver());
        session.delete(settlement);
    }


    public void createAddPayment(Session session, AddPayment addPayment) throws Exception {
        getCurrentPositionsManager(session).changeAddPaymentPosition(addPayment, addPayment.getSumma());
        session.save(addPayment);
    }

    public void updateAddPayment(Session session, AddPayment addPayment, Long preSumma) throws Exception {
        getCurrentPositionsManager(session).changeAddPaymentPosition(addPayment, preSumma);
        session.update(addPayment);
    }

    public void deleteAddPayment(Session session, AddPayment addPayment) throws Exception {
        getCurrentPositionsManager(session).changeAddPaymentPosition(addPayment, -addPayment.getSumma());
        session.delete(addPayment);
    }

    //TODO: добавить изменение текущих позиций
    @Transactional
    public void createAccountTransfer(Client benefactor, Client beneficiary, Long sum, String reason, User createdBy) throws Exception {
        SecurityJournalBalance journalFrom = SecurityJournalBalance.getSecurityJournalBalanceFromOperations(
                null, benefactor, SJBalanceTypeEnum.SJBALANCE_TYPE_ORDER, SJBalanceSourceEnum.SJBALANCE_SOURCE_BALANCE_TRANSFER);
        SecurityJournalBalance journalTo = SecurityJournalBalance.getSecurityJournalBalanceFromOperations(
                null, beneficiary, SJBalanceTypeEnum.SJBALANCE_TYPE_PAYMENT, SJBalanceSourceEnum.SJBALANCE_SOURCE_BALANCE_TRANSFER);
        Session session = (Session)em.getDelegate();
        String mess = null;
        if (sum<=0) {
            mess = "Сумма перевода должна быть больше нуля";
        }
        if (benefactor.getBalance()<sum) {
            mess = "Недостаточно средств на лицевом счете ("+CurrencyStringUtils.copecksToRubles(beneficiary.getBalance())+") для перевода";
        }
        if (mess != null) {
            SecurityJournalBalance.saveSecurityJournalBalance(journalFrom, false, mess);
            SecurityJournalBalance.saveSecurityJournalBalance(journalTo, false, mess);
            throw new Exception(mess);
        }
        Date dt = new Date();
        // регистрируем транзакцию на плательщике
        AccountTransaction accountTransactionOnBenefactor = ClientAccountManager.processAccountTransaction(session, benefactor,
                null, -sum, "",
                AccountTransaction.ACCOUNT_TRANSFER_TRANSACTION_SOURCE_TYPE, null, dt);
        // регистрируем транзакцию на получателе
        AccountTransaction accountTransactionOnBeneficiary = ClientAccountManager.processAccountTransaction(session, beneficiary,
                null, sum, "",
                AccountTransaction.ACCOUNT_TRANSFER_TRANSACTION_SOURCE_TYPE, null, dt);
        AccountTransfer accountTransfer = new AccountTransfer(dt, benefactor,  beneficiary, reason, createdBy, 
                accountTransactionOnBenefactor, accountTransactionOnBeneficiary, sum);
        session.save(accountTransactionOnBenefactor);
        session.save(accountTransactionOnBeneficiary);
        session.save(accountTransfer);
        session.flush();
        accountTransactionOnBenefactor.updateSource(accountTransfer.getIdOfAccountTransfer() + "");
        accountTransactionOnBeneficiary.updateSource(accountTransfer.getIdOfAccountTransfer() + "");
        session.update(accountTransactionOnBenefactor);
        session.update(accountTransactionOnBeneficiary);

        SecurityJournalBalance.saveSecurityJournalBalanceWithTransaction(journalFrom, true, "OK", accountTransactionOnBenefactor);
        SecurityJournalBalance.saveSecurityJournalBalanceWithTransaction(journalTo, true, "OK", accountTransactionOnBeneficiary);
    }

    @Transactional
    public boolean defaultSupplierEqual(Client from, Client to) {
        Session session = (Session) em.getDelegate();
        Org orgFrom = (Org) session.load(Org.class, from.getOrg().getIdOfOrg());
        Org orgTo = (Org) session.load(Org.class, to.getOrg().getIdOfOrg());

        Contragent contragentFrom = (Contragent) session.load(Contragent.class, orgFrom.getDefaultSupplier().getIdOfContragent());
        Contragent contragentTo = (Contragent) session.load(Contragent.class, orgTo.getDefaultSupplier().getIdOfContragent());

        return contragentFrom.getIdOfContragent().equals(contragentTo.getIdOfContragent());
    }

    //TODO: добавить изменение текущих позиций
    @Transactional
    public void createAccountRefund(Client client, Long sum, String reason, User createdBy) throws Exception {
        SecurityJournalBalance journal = SecurityJournalBalance.getSecurityJournalBalanceFromOperations(
                null, client, SJBalanceTypeEnum.SJBALANCE_TYPE_ORDER, SJBalanceSourceEnum.SJBALANCE_SOURCE_REFUND);
        Session session = (Session)em.getDelegate();
        String mess = null;
        if (sum<=0) {
            mess = "Сумма возврата должна быть больше нуля";
            //throw new Exception("Сумма возврата должна быть больше нуля");
        }
        if (client.getBalance()<sum) {
            mess = "Недостаточно средств на лицевом счете ("+CurrencyStringUtils.copecksToRubles(client.getBalance())+") для возврата";
            //throw new Exception("Недостаточно средств на лицевом счете ("+CurrencyStringUtils.copecksToRubles(client.getBalance())+") для возврата");
        }
        if (mess != null) {
            SecurityJournalBalance.saveSecurityJournalBalance(journal, false, mess);
            throw new Exception(mess);
        }
        Date dt = new Date();
        // регистрируем транзакцию
        AccountTransaction accountTransaction = ClientAccountManager.processAccountTransaction(session, client,
                null, -sum, "",
                AccountTransaction.ACCOUNT_REFUND_TRANSACTION_SOURCE_TYPE, null, dt);
        AccountRefund accountRefund = new AccountRefund(dt, client, reason, createdBy, accountTransaction, sum);
        session.save(accountTransaction);
        session.save(accountRefund);
        session.flush();
        accountTransaction.updateSource(accountRefund.getIdOfAccountRefund() + "");
        session.update(accountTransaction);
        SecurityJournalBalance.saveSecurityJournalBalanceWithTransaction(journal, true, "OK", accountTransaction);
    }

    @Transactional
    public void createSubAccountTransfer(Client client, Integer fromBalance, Integer toBalance, Long sum) throws Exception{
        Session session = (Session)em.getDelegate();
        if (sum<=0) throw new AccountTransactionException("Сумма перевода должна быть больше нуля");
        Long fromBalanceLong = client.getContractId() * 100 + fromBalance;
        Long toBalanceLong = client.getContractId() * 100 + toBalance;
        final Long fromSubBalance;
        /* проверим существует ли данные субсчета */
        try {
            fromSubBalance = client.getSubBalance(fromBalance);
        } catch (NullPointerException e){
            throw new AccountTransactionException(e.getMessage());
        }
        try {
            Long toSubBalance = client.getSubBalance(toBalance);
        } catch (NullPointerException e){
            throw new AccountTransactionException(e.getMessage());
        }
        checkBalance(fromBalanceLong, fromSubBalance, sum);
        Date dt = new Date();

        // регистрируем транзакцию на исходящем счете клиента
        AccountTransaction accountTransactionOnBenefactor = ClientAccountManager.processAccountTransaction(session, client,
                null, -sum, "",
                AccountTransaction.ACCOUNT_TRANSFER_TRANSACTION_SOURCE_TYPE, dt, fromBalance);
        // регистрируем транзакцию на получателе счета клиента
        AccountTransaction accountTransactionOnBeneficiary = ClientAccountManager.processAccountTransaction(session, client,
                null, sum, "",
                AccountTransaction.ACCOUNT_TRANSFER_TRANSACTION_SOURCE_TYPE, dt, toBalance);

        SubAccountTransfer subAccountTransfer = new SubAccountTransfer(dt, client, fromBalanceLong, toBalanceLong, "",
                accountTransactionOnBenefactor, accountTransactionOnBeneficiary, sum);

        session.save(accountTransactionOnBenefactor);
        session.save(accountTransactionOnBeneficiary);
        session.save(subAccountTransfer);
        session.flush();
        accountTransactionOnBenefactor.updateSource(subAccountTransfer.getIdOfSubAccountTransfer() + "");
        accountTransactionOnBeneficiary.updateSource(subAccountTransfer.getIdOfSubAccountTransfer() + "");
        session.update(accountTransactionOnBenefactor);
        session.update(accountTransactionOnBeneficiary);
    }

    private static void checkBalance(Long contractId, Long balance, Long sum) throws AccountTransactionException {
        if (balance<sum) {
            final String s = CurrencyStringUtils.copecksToRubles(balance);
            final String message = String.format("Недостаточно средств на лицевом счете (%s)", contractId);
            throw new AccountTransactionException(message);
        }
    }

    public static class AccountTransactionException extends Exception{

        public AccountTransactionException(String message) {
            super(message);
        }
    }

}
