/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.meal;

import generated.spb.meal.PushResponse;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.AccountTransaction;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 10.04.17
 * Time: 10:37
 */
@Component
@Scope("prototype")
public class MealManager {
    private final static String EXPENSE = "expense";
    private final static String INCOME = "income";

    private static final Logger logger = LoggerFactory.getLogger(MealManager.class);

    private final static boolean isOn = isOn();
    public final static boolean isSendToExternal = isSendToExternal();

    private static MealService mealService = RuntimeContext.getAppContext().getBean(MealService.class);

    private static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().getProperty("ecafe.processor.mealmanager.node", "");
        return !(StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim()
                .equals(reqInstance.trim()));
    }

    private static boolean isSendToExternal() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String res = runtimeContext.getConfigProperties().getProperty("ecafe.processor.mealmanager.sendtoexternal", "false");
        return "true".equals(res);
    }

    /**
     * Обертка для запуска по расписанию
     */
    public void sendData() throws Exception {
        if(isOn){
            sendToExternal();
        }
    }

    @SuppressWarnings("unchecked")
    private void sendToExternal() throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            sendOrdersToExternal(persistenceSession);
            sendTransactionsToExternal(persistenceSession);

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @SuppressWarnings("unchecked")
    private void sendOrdersToExternal(Session session) throws Exception {
        List<TransactionDataItem> sendOrders = new ArrayList<TransactionDataItem>();
        List<TransactionDataItem> list = findOrders(session, sendOrders);
        int i = 0;
        for(TransactionDataItem item : list) {
            TransactionItem trItem = new TransactionItem(item.getTransactionId(), item.getTransactionDate(), item.getBalance(), item.getAmount(),
                    item.getCardName(), item.getFoodName(), item.getFoodAmount(), EXPENSE);
            MealDataItem mItem = new MealDataItem(item.getOrganizationUid(), item.getStudentUid(), item.getUserToken(), item.getCardUid(), trItem);
            try {
                PushResponse response = mealService.sendEvent(mItem);
                if(response.isResult()) {
                    sendOrders.add(item);
                    i++;
                } else {
                    logger.warn(String.format(
                            "Не удалось доставить данные о покупках клиента studentUid = %s, transactionId = %s, result false",
                            mItem.getStudentUid(), mItem.getTransactionItems().get(0)));
                }
            } catch (Exception e) {
                logger.error(String.format(
                        "Не удалось доставить данные о покупках клиента studentUid = %s, transactionId = %s",
                        mItem.getStudentUid(), mItem.getTransactionItems().get(0).getTransactionId()), e);
            }
        }
        updateOrders(sendOrders, session);
        logger.info("MealManager: " + i + " orders sent to external system.");
    }

    @SuppressWarnings("unchecked")
    private void sendTransactionsToExternal(Session session) throws Exception {
        List<TransactionDataItem> sendTransactions = new ArrayList<TransactionDataItem>();
        List<TransactionDataItem> list = findTransactions(session, sendTransactions);
        int i = 0;
        for(TransactionDataItem item : list) {
            TransactionItem trItem = new TransactionItem(item.getTransactionId(), item.getTransactionDate(), item.getBalance(), Math.abs(item.getAmount()),
                    item.getCardName(), item.getFoodName(), item.getFoodAmount(), item.getAmount() > 0 ? INCOME : EXPENSE);
            MealDataItem mItem = new MealDataItem(item.getOrganizationUid(), item.getStudentUid(), item.getUserToken(), item.getCardUid(), trItem);
            try {
                PushResponse response = mealService.sendEvent(mItem);
                if(response.isResult()) {
                    sendTransactions.add(item);
                    i++;
                } else {
                    logger.warn(String.format(
                            "Не удалось доставить данные о транзакциях клиента studentUid = %s, transactionId = %s, result false",
                            mItem.getStudentUid(), mItem.getTransactionItems().get(0)));
                }
            } catch (Exception e) {
                logger.error(String.format(
                        "Не удалось доставить данные о транзакциях клиента studentUid = %s, transactionId = %s",
                        mItem.getStudentUid(), mItem.getTransactionItems().get(0).getTransactionId()), e);
            }
        }
        updateTransactions(sendTransactions, session);
        logger.info("MealManager: " + i + " transactions sent to external system.");
    }

    @SuppressWarnings("unchecked")
    private List<TransactionDataItem> findOrders(Session session, List<TransactionDataItem> sendOrders) throws Exception {
        List<TransactionDataItem> result = new ArrayList<TransactionDataItem>();
        Criteria criteria = session.createCriteria(OrderDetail.class);
        criteria.add(Restrictions.eq("sendToExternal", Boolean.FALSE));
        List<OrderDetail> list = criteria.list();
        for(OrderDetail od : list) {
            if(od.getOrder().getClient() != null) {
                Card card = od.getOrder().getCard();
                if(card == null) {
                    //card = od.getOrder().getClient().findActiveCard(session, null);
                }
                result.add(new TransactionDataItem(od.getCompositeIdOfOrderDetail().getIdOfOrderDetail(), od.getOrg().getIdOfOrg(),
                        od.getOrg().getOGRN(), od.getOrder().getClient().getClientGUID(),
                        card == null ? null : card.getCardPrintedNo(), card == null ? null : card.getCardType(),
                        od.getOrder().getTransaction() == null ? null : od.getOrder().getTransaction().getIdOfTransaction(),
                        od.getOrder().getTransaction() == null ? od.getOrder().getCreateTime() : od.getOrder().getTransaction().getTransactionTime(),
                        od.getOrder().getTransaction() == null ? null : od.getOrder().getTransaction().getBalanceAfterTransaction(),
                        od.getMenuDetailName(), od.getQty(), od.getRPrice()));
            } else {
                sendOrders.add(new TransactionDataItem(od.getCompositeIdOfOrderDetail().getIdOfOrderDetail(), od.getCompositeIdOfOrderDetail().getIdOfOrg()));
            }
        }
        return result;
    }

    private void updateOrders(List<TransactionDataItem> sendOrders, Session session) {
        if(sendOrders.size() < 1) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("update cf_orderdetails set sendtoexternal = 1 where (idoforderdetail, idoforg) in (");
        for(TransactionDataItem item : sendOrders) {
            sb.append("(")
            .append(item.getIdOfOrderDetail())
            .append(",")
            .append(item.getIdOfOrg())
            .append("), ");
        }
        Query query = session.createSQLQuery(sb.substring(0, sb.length() - 2) + ")");
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    private List<TransactionDataItem> findTransactions(Session session, List<TransactionDataItem> sendTransactions) throws Exception {
        List<TransactionDataItem> result = new ArrayList<TransactionDataItem>();
        Criteria criteria = session.createCriteria(AccountTransaction.class);
        criteria.add(Restrictions.eq("sendToExternal", Boolean.FALSE));
        List<AccountTransaction> list = criteria.list();
        for(AccountTransaction tr : list) {
            if(tr.getOrg() != null && tr.getClient() != null) {
                Card card = tr.getCard();
                if(card == null) {
                    card = tr.getClient().findActiveCard(session, null);
                }
                result.add(new TransactionDataItem(tr.getOrg().getIdOfOrg(), tr.getOrg().getOGRN(), tr.getClient().getClientGUID(),
                        card == null ? null : card.getCardPrintedNo(),
                        card == null ? null : card.getCardType(), tr.getIdOfTransaction(), tr.getTransactionTime(),
                        tr.getBalanceAfterTransaction(), tr.getTransactionSum(), tr.getSourceType()));
            } else {
                sendTransactions.add(new TransactionDataItem(tr.getIdOfTransaction().toString()));
            }
        }
        return result;
    }

    private void updateTransactions(List<TransactionDataItem> sendOrders, Session session) {
        if(sendOrders.size() < 1) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("update cf_transactions set sendtoexternal = 1 where idoftransaction in (");
        for(TransactionDataItem item : sendOrders) {
            sb.append(item.getTransactionId())
                    .append(", ");
        }
        Query query = session.createSQLQuery(sb.substring(0, sb.length() - 2) + ")");
        query.executeUpdate();
    }

}
