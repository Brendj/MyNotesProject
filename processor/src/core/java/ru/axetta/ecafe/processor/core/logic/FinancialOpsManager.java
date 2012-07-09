/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.mail.File;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.service.SMSService;
import ru.axetta.ecafe.processor.core.sync.SyncRequest;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.StringReader;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

@Component
@Scope("singleton")
public class FinancialOpsManager {
    @Resource
    EventNotificationService eventNotificationService;

    CurrentPositionsManager getCurrentPositionsManager(Session session) {
        return new CurrentPositionsManager(session);
    }

    public void createClientSmsCharge(Session session, Client client, String idOfSms, String phone, Integer contentsType,
            String textContents, Date serviceSendTime) throws Exception {

        long priceOfSms = client.getOrg().getPriceOfSms();
        //Card card = DAOUtils.findActiveCard(em, client);
        Date currTime = new Date();

        AccountTransaction accountTransaction = null;
        if (priceOfSms != 0) {
            // Register transaction
            //accountTransaction = new AccountTransaction(client, null, -priceOfSms, "",
            //        AccountTransaction.INTERNAL_ORDER_TRANSACTION_SOURCE_TYPE, currTime);
            //session.save(accountTransaction);
            accountTransaction = ClientAccountManager.processAccountTransaction(session, client, null, -priceOfSms, "",
                    AccountTransaction.INTERNAL_ORDER_TRANSACTION_SOURCE_TYPE, currTime);

            Contragent operatorContragent = DAOUtils.findContragentByClass(session, Contragent.OPERATOR);
            Contragent clientContragent = DAOUtils.findContragentByClass(session, Contragent.CLIENT);
            // уменьшаем позицию Оператор - Клиент
            getCurrentPositionsManager(session).changeCurrentPosition(-priceOfSms, operatorContragent, clientContragent);
        }

        ClientSms clientSms = new ClientSms(idOfSms, client, accountTransaction, phone, contentsType, textContents,
                serviceSendTime, priceOfSms);
        session.save(clientSms);
    }

    public void createSubscriptionFeeCharge(Session session, CompositeIdOfSubscriptionFee idOfSubscriptionFee,
            Client client, long subscriptionPrice)
            throws Exception {
        Date currentTime = new Date();
        AccountTransaction accountTransaction = ClientAccountManager.processAccountTransaction(session, client,
                null, -subscriptionPrice, "", AccountTransaction.SUBSCRIPTION_FEE_TRANSACTION_SOURCE_TYPE, currentTime);

        SubscriptionFee subscriptionFee = new SubscriptionFee(idOfSubscriptionFee, accountTransaction,
                subscriptionPrice, currentTime);
        session.save(subscriptionFee);

        Contragent operatorContragent = DAOUtils.findContragentByClass(session, Contragent.OPERATOR);
        Contragent clientContragent = DAOUtils.findContragentByClass(session, Contragent.CLIENT);
        // уменьшаем позицию Оператор - Клиент
        getCurrentPositionsManager(session).changeCurrentPosition(-subscriptionPrice, operatorContragent, clientContragent);
    }

    public void createOrderCharge(Session session, SyncRequest.PaymentRegistry.Payment payment, Long idOfOrg,
            Client client, Card card) throws Exception {
        // By default we have no transaction
        AccountTransaction orderTransaction = null;
        // If "card part" of payment is specified...
        if (0 != payment.getSumByCard()) {
            // Check card balance and overdraft limit to be enough for payment registration
            //if (card.getBalance() + card.getLimit() < payment.getSumByCard()) {
            //    registerLimitOverflow(session, syncHistory, organization, card);
            //    transaction.commit(); transaction = null;
            //    return new SyncResponse.ResPaymentRegistry.Item(payment.getIdOfOrder(), 260, String.format(
            //            "There is not enough sum at the card, IdOfOrg == %s, IdOfOrder == %s, CardNo == %s",
            //            idOfOrg, payment.getIdOfOrder(), payment.getCardNo()));
            //}
            // Update client balance...
            //DAOUtils.changeClientBalance(persistenceSession, client.getIdOfClient(), -payment.getSumByCard());
            //client.addBalance(-payment.getSumByCard());
            //client.setUpdateTime(new Date());
            //persistenceSession.update(client);

            // зарегистрировать транзакцию и провести по балансу
            orderTransaction = ClientAccountManager.processAccountTransaction(session, client, card,
                    -payment.getSumByCard(), ""+idOfOrg+"/"+payment.getIdOfOrder(),
                    AccountTransaction.CLIENT_ORDER_TRANSACTION_SOURCE_TYPE, new Date());
        }

        POS pos = null;
        Contragent supplier;
        if (payment.getIdOfPOS() != null) {
            pos = (POS) session.get(POS.class, payment.getIdOfPOS());
            if (pos == null) {
                throw new Exception("POS with id " + payment.getIdOfPOS() + " not found");
            } else {
                supplier = pos.getContragent();
            }
        } else {
            // Использовать поставщика по умолчанию из организации
            Org org = (Org) session.get(Org.class, idOfOrg);
            supplier = org.getDefaultSupplier();
        }

        Order order = new Order(new CompositeIdOfOrder(idOfOrg, payment.getIdOfOrder()), payment.getIdOfCashier(),
                payment.getSocDiscount(), payment.getTrdDiscount(), payment.getGrant(), payment.getRSum(),
                payment.getTime(), payment.getSumByCard(), payment.getSumByCash(), client, card, orderTransaction, pos,
                supplier);

        Long sumByCard = order.getSumByCard();
        Long budgetSum = order.getSocDiscount() + order.getGrantSum();
        getCurrentPositionsManager(session).changeOrderPosition(sumByCard, budgetSum, supplier);
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

    public void cancelOrder(Session session, Order order) throws Exception {
        if (order.getState()!=Order.STATE_COMMITED) throw new Exception("Заказ не может отменен из статуса: "+order.getStateAsString());
        order.setState(Order.STATE_CANCELED);
        session.save(order);
        for (OrderDetail od : order.getOrderDetails()) {
            od.setState(OrderDetail.STATE_CANCELED);
            session.save(od);
        }
        ClientAccountManager.cancelAccountTransaction(session, order.getTransaction(), new Date());

        Long sumByCard = order.getSumByCard();
        Long budgetSum = order.getSocDiscount() + order.getGrantSum();
        getCurrentPositionsManager(session).changeOrderPosition(-sumByCard, -budgetSum, order.getContragent());
    }


    private static void registerTransactionJournal(String transCode, Org org, Order order, Card card, Client client, Long financialAmount, Session session) {
        TransactionJournal transactionJournal = new TransactionJournal(order.getCompositeIdOfOrder().getIdOfOrg(),
                order.getCompositeIdOfOrder().getIdOfOrder(), new Date(), org.getOGRN(), TransactionJournal.SERVICE_CODE_SCHL_FD,
                transCode,
                TransactionJournal.CARD_TYPE_CODE_UEC, TransactionJournal.CARD_TYPE_ID_CODE_MUID, Card.TYPE_NAMES[card.getCardType()], Long.toHexString(card.getCardNo()),
                client.getSan(), client.getContractId(), client.getClientGroupTypeAsString(), financialAmount, order.getCreateTime());
        session.save(transactionJournal);
    }

    public void createClientPayment(Session session, Client client, Integer paymentMethod, Long paySum, Integer payType,
            Date createTime, String idOfPayment, Contragent contragent, String addPaymentMethod, String addIdOfPayment)
            throws Exception {
        // регистрируем транзакцию и проводим по балансу
        AccountTransaction accountTransaction = ClientAccountManager.processAccountTransaction(session, client,
                null, paySum, idOfPayment,
                AccountTransaction.PAYMENT_SYSTEM_TRANSACTION_SOURCE_TYPE, new Date());
        // регистрируем платеж клиента
        ClientPayment clientPayment = new ClientPayment(accountTransaction, paymentMethod, paySum, payType, createTime,
                idOfPayment, contragent, addPaymentMethod, addIdOfPayment);
        registerClientPayment(session, clientPayment, client);
    }
    public void createClientPaymentWithOrder(Long contragentSum,Session session,
            ClientPaymentOrder clientPaymentOrder,
            Client client,String addIdOfPayment) throws Exception {
        // регистрируем транзакцию и проводим по балансу
       /// AccountTransaction accountTransaction = ClientAccountManager.processAccountTransaction(session, client,
              //  null, clientPaymentOrder.getPaySum(), clientPaymentOrder.getIdOfPayment(),
              //  AccountTransaction.PAYMENT_SYSTEM_TRANSACTION_SOURCE_TYPE, new Date());

        AccountTransaction accountTransaction = ClientAccountManager.processAccountTransaction(session, client,
                null, contragentSum, clientPaymentOrder.getIdOfPayment(),
                AccountTransaction.PAYMENT_SYSTEM_TRANSACTION_SOURCE_TYPE, new Date());


        // регистрируем платеж клиента
        ClientPayment clientPayment= new ClientPayment(contragentSum,accountTransaction, clientPaymentOrder,new Date(),addIdOfPayment);

        registerClientPayment(session, clientPayment, client);
    }

    public void createClientPaymentWithOrder(Long contragentSum,Session session,
            ClientPaymentOrder clientPaymentOrder,
            Client client) throws Exception {
        // регистрируем транзакцию и проводим по балансу
        /// AccountTransaction accountTransaction = ClientAccountManager.processAccountTransaction(session, client,
        //  null, clientPaymentOrder.getPaySum(), clientPaymentOrder.getIdOfPayment(),
        //  AccountTransaction.PAYMENT_SYSTEM_TRANSACTION_SOURCE_TYPE, new Date());

        AccountTransaction accountTransaction = ClientAccountManager.processAccountTransaction(session, client,
                null, contragentSum, clientPaymentOrder.getIdOfPayment(),
                AccountTransaction.PAYMENT_SYSTEM_TRANSACTION_SOURCE_TYPE, new Date());


        // регистрируем платеж клиента
        ClientPayment clientPayment= new ClientPayment(contragentSum,accountTransaction, clientPaymentOrder,
                    new Date());

        registerClientPayment(session, clientPayment, client);
    }

    private void registerClientPayment(Session session,
            ClientPayment clientPayment,
            Client client) throws Exception {
        Long paySum = clientPayment.getPaySum();
        Contragent payAgent = clientPayment.getContragent();

        Contragent objectContragent;
        if (isOperatorScheme(session)) {
            // Оператор
            objectContragent = DAOUtils.findContragentByClass(session, Contragent.OPERATOR);
        }
        else {
            objectContragent = client.getOrg().getDefaultSupplier();
        }

        getCurrentPositionsManager(session).changeClientPaymentPosition(payAgent, paySum, objectContragent);
        session.save(clientPayment);

        eventNotificationService.sendNotificationAsync(client, EventNotificationService.NOTIFICATION_BALANCE_TOPUP, new String[]{
                "paySum",CurrencyStringUtils.copecksToRubles(paySum),
                "balance", CurrencyStringUtils.copecksToRubles(client.getBalance()),
                "contractId",String.valueOf(client.getContractId()),
                "surname",client.getPerson().getSurname(),
                "firstName",client.getPerson().getFirstName()
            });
    }

    static Boolean useOperatorScheme = null;
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


}
