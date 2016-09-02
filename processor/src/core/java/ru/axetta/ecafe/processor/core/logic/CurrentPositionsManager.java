/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.persistence.AddPayment;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Session;

import java.util.List;

public class CurrentPositionsManager {
    Session session;
    Boolean isOperatorScheme;
    Contragent operatorCt, budgetCt, clientCt;
    List<CurrentPositionItem> currentPositionList;

    public CurrentPositionsManager(Session session) {
        this.session = session;
    }
    boolean isOperatorScheme() {
        if (isOperatorScheme==null) isOperatorScheme = DAOUtils.getOptionValueBool(session, Option.OPTION_WITH_OPERATOR, false);
        return isOperatorScheme;
    }
    Contragent getOperatorContragent() {
        if (session!=null) return DAOUtils.findContragentByClass(session, Contragent.OPERATOR);
        return operatorCt;
    }
    Contragent getBudgetContragent() {
        if (session!=null) return DAOUtils.findContragentByClass(session, Contragent.BUDGET);
        return budgetCt;
    }
    Contragent getClientContragent() {
        if (session!=null) return DAOUtils.findContragentByClass(session, Contragent.CLIENT);
        return clientCt;
    }

    public CurrentPositionsManager(boolean operatorScheme, Contragent operatorContragent, Contragent budgetContragent,
            Contragent clientContragent, List<CurrentPositionItem> currentPositionList) {
        isOperatorScheme = operatorScheme;
        this.operatorCt = operatorContragent;
        this.budgetCt = budgetContragent;
        this.clientCt = clientContragent;
        this.currentPositionList = currentPositionList;
    }

    // пополнение лицевого счета
    public void changeClientPaymentPosition(Contragent payAgent, Long paySum, Contragent objectContragent)
            throws Exception {
        // увеличиваем позицию Платежный агент – ТСП/Оператор
        changeCurrentPosition(paySum, payAgent, objectContragent);
    }

    // Регистрация заказа с точки продажи
    public void changeOrderPosition(Long sumByCard, Long budgetSum, Contragent supplier) throws Exception {
        // увеличиваем позицию Оператор – ТСП
        if (isOperatorScheme())
            changeCurrentPosition(sumByCard, getOperatorContragent(), supplier);

        Contragent objectContragent;
        if (isOperatorScheme()) objectContragent = getOperatorContragent();
        else objectContragent = supplier;

        // уменьшаем позицию ТСП/Оператор – Клиенты
        changeCurrentPosition(-sumByCard, objectContragent, getClientContragent());

        // увеличиваем позицию Бюджет – ТСП/Оператор на размер льготы
        changeCurrentPosition(budgetSum, getBudgetContragent(), objectContragent);
    }

    // Регистрация выплаты
    public void changeSettlementPosition(Long sum,
            Integer payerClassId, Integer receiverClassId, Contragent contragentPayer, Contragent contragentReceiver) throws Exception {
        boolean withOperator = isOperatorScheme();

        // Регистрация выплаты от платежного агента
        if (payerClassId.equals(Contragent.PAY_AGENT)) {
            if ((withOperator && receiverClassId.equals(Contragent.OPERATOR)) ||
                (!withOperator && receiverClassId.equals(Contragent.TSP))) {
                // уменьшаем позицию Платежный агент – ТСП/Оператор
                changeCurrentPosition(-sum, contragentPayer, contragentReceiver);

                // увеличиваем позицию ТСП/Оператор – Клиенты
                changeCurrentPosition(sum, contragentReceiver, getClientContragent());
            }
        }

        // Регистрация выплаты от Оператора в ТСП
        else if (payerClassId.equals(Contragent.OPERATOR)) {
            if (withOperator) {
                // уменьшаем позицию Оператор – ТСП
                changeCurrentPosition(-sum, contragentPayer, contragentReceiver);
            }
        }

        // Регистрация выплаты от ТСП за обслуживание
        else if (payerClassId.equals(Contragent.TSP)) {
            if (withOperator) {
                // уменьшаем позицию ТСП – Оператор
                changeCurrentPosition(-sum, contragentPayer, contragentReceiver);
            }
        }

        // Регистрация выплаты компенсации ТСП из бюджета
        else if (payerClassId.equals(Contragent.BUDGET)) {
            // уменьшаем позицию Бюджет – ТСП
            changeCurrentPosition(-sum, contragentPayer, contragentReceiver);
        }
    }

    // Регистрация начисления выплаты
    public void changeAddPaymentPosition(AddPayment addPayment, Long sum) throws Exception {
        changeCurrentPosition(sum, addPayment.getContragentPayer(), addPayment.getContragentReceiver());
    }

    public void changeCurrentPosition(Long sum, Contragent debtor, Contragent creditor) {
        return;
        /*if (currentPositionList != null) {
            CurrentPositionItem currentPosition = null;
            for (CurrentPositionItem currentPositionItem : currentPositionList) {
                if ((currentPositionItem.getIdOfContragentDebtor().equals(debtor.getIdOfContragent()) &&
                    currentPositionItem.getIdOfContragentCreditor().equals(creditor.getIdOfContragent())) ||
                    (currentPositionItem.getIdOfContragentDebtor().equals(creditor.getIdOfContragent()) &&
                    currentPositionItem.getIdOfContragentCreditor().equals(debtor.getIdOfContragent()))) {
                    currentPosition = currentPositionItem;
                    if (currentPositionItem.getIdOfContragentDebtor().equals(debtor.getIdOfContragent()))
                        currentPositionItem.setSum(currentPositionItem.getSum() + sum);
                    else
                        currentPositionItem.setSum(currentPositionItem.getSum() - sum);
                    break;
                }
            }
            if (currentPosition == null) {
                currentPosition = new CurrentPositionItem(debtor.getIdOfContragent(),
                        creditor.getIdOfContragent(), sum);
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
                    currentPosition.setSumma(currentPosition.getSumma() + sum);
                else
                    currentPosition.setSumma(currentPosition.getSumma() - sum);
                session.save(currentPosition);
            } else {
                currentPosition = new CurrentPosition();
                currentPosition.setIdOfContragentDebtor(debtor);
                currentPosition.setIdOfContragentCreditor(creditor);
                currentPosition.setSumma(sum);
                session.persist(currentPosition);
            }
        }*/
    }


    public static class CurrentPositionItem {
        Long idOfContragentDebtor;
        Long idOfContragentCreditor;
        Long sum;

        public CurrentPositionItem(Long idOfContragentDebtor, Long idOfContragentCreditor, Long summa) {
            this.idOfContragentDebtor = idOfContragentDebtor;
            this.idOfContragentCreditor = idOfContragentCreditor;
            this.sum = summa;
        }

        public Long getIdOfContragentDebtor() {
            return idOfContragentDebtor;
        }

        public Long getIdOfContragentCreditor() {
            return idOfContragentCreditor;
        }

        public Long getSum() {
            return sum;
        }

        public void setSum(Long sum) {
            this.sum = sum;
        }
    }

}
