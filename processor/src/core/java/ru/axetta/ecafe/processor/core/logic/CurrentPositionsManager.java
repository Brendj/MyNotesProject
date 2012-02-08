/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.sync.SyncRequest;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 21.11.11
 * Time: 10:01
 * To change this template use File | Settings | File Templates.
 */
public class CurrentPositionsManager {

    public static void createOrder(Session session, SyncRequest.PaymentRegistry.Payment payment, Long idOfOrg,
            Client client, Card card, AccountTransaction orderTransaction) throws Exception {
        POS pos = null;
        Contragent supplier = null;
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

        Long rSum = order.getRSum();
        Long priviledge = order.getSocDiscount() + order.getGrantSum();
        changeOrderPosition(session, rSum, priviledge, supplier, null, null, null, null, null);
        session.save(order);

        /// Формирование журнала транзакции
        if (RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_JOURNAL_TRANSACTIONS)) {
            if (card!=null && card.getCardType()==Card.TYPE_UEC) {
                Criteria orgCriteria = session.createCriteria(Org.class);
                orgCriteria.add(Restrictions.eq("idOfOrg",idOfOrg));
                Org org = (Org) orgCriteria.uniqueResult();
                if(payment.getSocDiscount()>0){
                    registerTransactionJournal(TransactionJournal.TRANS_CODE_FD_BEN, org, order, card, client, payment.getSocDiscount(), session);
                }
                if (payment.getSumByCard()>0) {
                    registerTransactionJournal(TransactionJournal.TRANS_CODE_DEBIT, org, order, card, client, payment.getSumByCard(), session);
                }
            }
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

    public static void createClientPayment(Session session, ClientPayment clientPayment, Client client,
            List<CurrentPositionItem> currentPositionList) throws Exception {
        Long paySum = clientPayment.getPaySum();
        Contragent payAgent = clientPayment.getContragent();

        Criteria optionCriteria = session.createCriteria(Option.class);
        optionCriteria.add(Restrictions.eq("idOfOption", 2L));
        Option option = (Option) optionCriteria.uniqueResult();
        boolean withOperator = option.getOptionText().equals("1");

        Criteria criteria = null;
        Contragent objectContragent = null;

        if (withOperator) {
            // Оператор
            criteria = session.createCriteria(Contragent.class);
            criteria.add(Restrictions.eq("classId", Contragent.OPERATOR));
            objectContragent = (Contragent) criteria.uniqueResult();
        }
        else
            objectContragent = client.getOrg().getDefaultSupplier();

        changeClientPaymentPosition(session, payAgent, paySum, currentPositionList, objectContragent);
        session.save(clientPayment);
    }

    public static void createSettlement(Session session, Settlement settlement) throws Exception {
        changeSettlementPosition(session, settlement.getSumma(),
                settlement.getIdOfContragentPayer().getClassId(),
                settlement.getIdOfContragentReceiver().getClassId(),
                settlement.getIdOfContragentPayer(),
                settlement.getIdOfContragentReceiver(), null, null, null);
        session.save(settlement);
    }

    public static void editSettlement(Session session, Settlement settlement, Long preSumma) throws Exception {
        changeSettlementPosition(session, preSumma,
                settlement.getIdOfContragentPayer().getClassId(),
                settlement.getIdOfContragentReceiver().getClassId(),
                settlement.getIdOfContragentPayer(),
                settlement.getIdOfContragentReceiver(), null, null, null);
        session.update(settlement);
    }

    public static void deleteSettlement(Session session, Settlement settlement) throws Exception {
        changeSettlementPosition(session, -settlement.getSumma(),
                settlement.getIdOfContragentPayer().getClassId(),
                settlement.getIdOfContragentReceiver().getClassId(),
                settlement.getIdOfContragentPayer(),
                settlement.getIdOfContragentReceiver(), null, null, null);
        session.delete(settlement);
    }

    // пополнение лицевого счета
    public static void changeClientPaymentPosition(Session session, Contragent payAgent, Long paySum,
            List<CurrentPositionItem> currentPositionList, Contragent objectContragent)
            throws Exception {

        // увеличиваем позицию Платежный агент – ТСП/Оператор
        changeCurrentPosition(session, paySum, payAgent, objectContragent, currentPositionList);
    }

    // Регистрация заказа с точки продажи
    public static void changeOrderPosition(Session session, Long rSum, Long priviledge, Contragent supplier,
            List<CurrentPositionItem> currentPositionList, Boolean withOperator,
            Contragent operatorContragent, Contragent clientContragent, Contragent budgetContragent) throws Exception {
        if (currentPositionList == null) {
            Criteria optionCriteria = session.createCriteria(Option.class);
            optionCriteria.add(Restrictions.eq("idOfOption", 2L));
            Option option = (Option) optionCriteria.uniqueResult();
            withOperator = option.getOptionText().equals("1");

            Criteria criteria = null;

            if (withOperator) {
                criteria = session.createCriteria(Contragent.class);
                criteria.add(Restrictions.eq("classId", Contragent.OPERATOR));
                operatorContragent = (Contragent) criteria.uniqueResult();
            }

            // Клиент
            criteria = session.createCriteria(Contragent.class);
            criteria.add(Restrictions.eq("classId", Contragent.CLIENT));
            clientContragent = (Contragent) criteria.uniqueResult();

            // Бюджет
            criteria = session.createCriteria(Contragent.class);
            criteria.add(Restrictions.eq("classId", Contragent.BUDGET));
            budgetContragent = (Contragent) criteria.uniqueResult();
        }

        // увеличиваем позицию Оператор – ТСП
        if (withOperator)
            changeCurrentPosition(session, rSum, operatorContragent, supplier, currentPositionList);

        Contragent objectContragent = null;
        if (withOperator)
            objectContragent = operatorContragent;
        else
            objectContragent = supplier;

        // уменьшаем позицию ТСП/Оператор – Клиенты
        changeCurrentPosition(session, -rSum, objectContragent, clientContragent, currentPositionList);

        // увеличиваем позицию Бюджет – ТСП/Оператор на размер льготы
        changeCurrentPosition(session, priviledge, budgetContragent, objectContragent, currentPositionList);
    }

    // Регистрация выплаты
    public static void changeSettlementPosition(Session session, Long summa,
            Integer payerClassId, Integer receiverClassId, Contragent contragentPayer, Contragent contragentReceiver,
            List<CurrentPositionItem> currentPositionList, Boolean withOperator, Contragent clientContragent) throws Exception {
        if (currentPositionList == null) {
            Criteria optionCriteria = session.createCriteria(Option.class);
            optionCriteria.add(Restrictions.eq("idOfOption", 2L));
            Option option = (Option) optionCriteria.uniqueResult();
            withOperator = option.getOptionText().equals("1");

            Criteria criteria = null;

            criteria = session.createCriteria(Contragent.class);
            criteria.add(Restrictions.eq("classId", Contragent.CLIENT));
            clientContragent = (Contragent) criteria.uniqueResult();
        }

        // Регистрация выплаты от платежного агента
        if (payerClassId.equals(Contragent.PAY_AGENT)) {
            if ((withOperator && receiverClassId.equals(Contragent.OPERATOR)) ||
                (!withOperator && receiverClassId.equals(Contragent.TSP))) {
                // уменьшаем позицию Платежный агент – ТСП/Оператор
                changeCurrentPosition(session, -summa, contragentPayer,
                    contragentReceiver, currentPositionList);

                // увеличиваем позицию ТСП/Оператор – Клиенты
                changeCurrentPosition(session, summa, contragentReceiver, clientContragent,
                        currentPositionList);
            }
        }

        // Регистрация выплаты от Оператора в ТСП
        else if (payerClassId.equals(Contragent.OPERATOR)) {
            if (withOperator) {
                // уменьшаем позицию Оператор – ТСП
                changeCurrentPosition(session, -summa, contragentPayer,
                        contragentReceiver, currentPositionList);
            }
        }

        // Регистрация выплаты от ТСП за обслуживание
        else if (payerClassId.equals(Contragent.TSP)) {
            if (withOperator) {
                // уменьшаем позицию ТСП – Оператор
                changeCurrentPosition(session, -summa, contragentPayer,
                        contragentReceiver, currentPositionList);
            }
        }

        // Регистрация выплаты компенсации ТСП из бюджета
        else if (payerClassId.equals(Contragent.BUDGET)) {
            // уменьшаем позицию Бюджет – ТСП
            changeCurrentPosition(session, -summa, contragentPayer,
                    contragentReceiver, currentPositionList);
        }
    }

    public static class CurrentPositionItem {
        Long idOfContragentDebtor;
        Long idOfContragentCreditor;
        Long summa;

        public CurrentPositionItem(Long idOfContragentDebtor, Long idOfContragentCreditor, Long summa) {
            this.idOfContragentDebtor = idOfContragentDebtor;
            this.idOfContragentCreditor = idOfContragentCreditor;
            this.summa = summa;
        }

        public Long getIdOfContragentDebtor() {
            return idOfContragentDebtor;
        }

        public Long getIdOfContragentCreditor() {
            return idOfContragentCreditor;
        }

        public Long getSumma() {
            return summa;
        }

        public void setSumma(Long summa) {
            this.summa = summa;
        }
    }

    private static void changeCurrentPosition(Session session, Long summa, Contragent debtor, Contragent creditor,
            List<CurrentPositionItem> currentPositionList) {
        if (currentPositionList != null) {
            CurrentPositionItem currentPosition = null;
            for (CurrentPositionItem currentPositionItem : currentPositionList) {
                if ((currentPositionItem.getIdOfContragentDebtor().equals(debtor.getIdOfContragent()) &&
                    currentPositionItem.getIdOfContragentCreditor().equals(creditor.getIdOfContragent())) ||
                    (currentPositionItem.getIdOfContragentDebtor().equals(creditor.getIdOfContragent()) &&
                    currentPositionItem.getIdOfContragentCreditor().equals(debtor.getIdOfContragent()))) {
                    currentPosition = currentPositionItem;
                    if (currentPositionItem.getIdOfContragentDebtor().equals(debtor.getIdOfContragent()))
                        currentPositionItem.setSumma(currentPositionItem.getSumma() + summa);
                    else
                        currentPositionItem.setSumma(currentPositionItem.getSumma() - summa);
                    break;
                }
            }
            if (currentPosition == null) {
                currentPosition = new CurrentPositionItem(debtor.getIdOfContragent(),
                        creditor.getIdOfContragent(), summa);
                currentPositionList.add(currentPosition);
            }
        } else {
            Criteria criteria;
            CurrentPosition currentPosition;
            criteria = session.createCriteria(CurrentPosition.class);
            LogicalExpression directRestr = Restrictions.and(Restrictions.eq("idOfContragentDebtor", debtor),
                    Restrictions.eq("idOfContragentCreditor", creditor));
            LogicalExpression reverseRestr = Restrictions.and(Restrictions.eq("idOfContragentDebtor", creditor),
                    Restrictions.eq("idOfContragentCreditor", debtor));
            criteria.add(Restrictions.or(directRestr, reverseRestr));
            currentPosition = (CurrentPosition) criteria.uniqueResult();
            if (currentPosition != null) {
                if (currentPosition.getIdOfContragentDebtor().equals(debtor))
                    currentPosition.setSumma(currentPosition.getSumma() + summa);
                else
                    currentPosition.setSumma(currentPosition.getSumma() - summa);
                session.save(currentPosition);
            } else {
                currentPosition = new CurrentPosition();
                currentPosition.setIdOfContragentDebtor(debtor);
                currentPosition.setIdOfContragentCreditor(creditor);
                currentPosition.setSumma(summa);
                session.persist(currentPosition);
            }
        }
    }

    public static void createAddPayment(Session session, AddPayment addPayment) throws Exception {
        changeAddPaymentPosition(session, addPayment, addPayment.getSumma(), null, null);
        session.save(addPayment);
    }

    public static void updateAddPayment(Session session, AddPayment addPayment, Long preSumma) throws Exception {
        changeAddPaymentPosition(session, addPayment, preSumma, null, null);
        session.update(addPayment);
    }

    public static void deleteAddPayment(Session session, AddPayment addPayment) throws Exception {
        changeAddPaymentPosition(session, addPayment, -addPayment.getSumma(), null, null);
        session.delete(addPayment);
    }

    // Регистрация начисления выплаты
    public static void changeAddPaymentPosition(Session session, AddPayment addPayment, Long summa,
            List<CurrentPositionItem> currentPositionList, Boolean withOperator) throws Exception {
        if (currentPositionList == null) {
            Criteria optionCriteria = session.createCriteria(Option.class);
            optionCriteria.add(Restrictions.eq("idOfOption", 2L));
            Option option = (Option) optionCriteria.uniqueResult();
            withOperator = option.getOptionText().equals("1");
        }

        if (withOperator) {
            changeCurrentPosition(session, summa, addPayment.getContragentPayer(),
                    addPayment.getContragentReceiver(), currentPositionList);
        }
    }
}
