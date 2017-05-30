/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.meal;

import generated.spb.meal.PushResponse;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
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

    private final static String GET_MEAL_TRANSACTIONS = "SELECT od.idoforderdetail, od.idoforg, org.guid, c.clientguid, cr.cardprintedno, cr.cardtype, "
            + " t.idoftransaction, t.transactiondate, t.balanceafter, od.menudetailname, od.qty, od.rprice"
            + "  FROM cf_orderdetails od inner join cf_orders o on od.idoforg = o.idoforg and od.idoforder = o.idoforder"
            + "  inner join cf_orgs org on od.idoforg = org.idoforg "
            + "  inner join cf_transactions t on o.idoftransaction = t.idoftransaction"
            + "  inner join cf_clients c on o.idofclient = c.idofclient "
            + "  inner join cf_cards cr on o.idofcard = cr.idofcard"
            + "  where c.clientguid is not null and od.sendtoexternal = false and o.idoftransaction is not null and t.sourceType = 8";

    private final static String GET_TRANSACTIONS = "SELECT t.idoforg, org.guid, c.clientguid, cr.cardprintedno, cr.cardtype, "
            + " t.idoftransaction, t.transactiondate, t.balanceafter, t.transactionsum, t.sourcetype"
            + "  FROM cf_transactions t "
            + "  inner join cf_orgs org on t.idoforg = org.idoforg "
            + "  inner join cf_clients c on c.idofclient = c.idofclient "
            + "  inner join cf_cards cr on t.idofcard = cr.idofcard"
            + "  where c.clientguid is not null and t.sendtoexternal = false";

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
            logger.info("MealManager: start sending transactions to external system.");
            sendToExternal();
            logger.info("MealManager: end sending transactions to external system.");
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
        List<TransactionDataItem> list = findOrders(session);
        for(TransactionDataItem item : list) {
            TransactionItem trItem = new TransactionItem(item.getTransactionId(), item.getTransactionDate(), item.getBalance(), item.getAmount(),
                    item.getCardName(), item.getFoodName(), item.getFoodAmount(), EXPENSE);
            MealDataItem mItem = new MealDataItem(item.getOrganizationUid(), item.getStudentUid(), item.getUserToken(), item.getCardUid(), trItem);

            List<TransactionDataItem> sendOrders = new ArrayList<TransactionDataItem>();
            try {
                PushResponse response = mealService.sendEvent(mItem);
                System.out.println(mItem);
                if(response.isResult()) {
                    sendOrders.add(item);
                }
            } catch (Exception e) {
                logger.error(String.format(
                        "Не удалось доставить данные о покупках клиента studentUid = %s, transactionId = %s",
                        mItem.getStudentUid(), mItem.getTransactionItems().get(0)), e);
            }

            updateOrders(sendOrders, session);

        }
    }

    @SuppressWarnings("unchecked")
    private void sendTransactionsToExternal(Session session) throws Exception {
        List<TransactionDataItem> list = findTransactions(session);
        for(TransactionDataItem item : list) {
            TransactionItem trItem = new TransactionItem(item.getTransactionId(), item.getTransactionDate(), item.getBalance(), Math.abs(item.getAmount()),
                    item.getCardName(), item.getFoodName(), item.getFoodAmount(), item.getAmount() > 0 ? INCOME : EXPENSE);
            MealDataItem mItem = new MealDataItem(item.getOrganizationUid(), item.getStudentUid(), item.getUserToken(), item.getCardUid(), trItem);

            List<TransactionDataItem> sendTransactions = new ArrayList<TransactionDataItem>();
            try {
                PushResponse response = mealService.sendEvent(mItem);
                System.out.println(mItem);
                if(response.isResult()) {
                    sendTransactions.add(item);
                }
            } catch (Exception e) {
                logger.error(String.format(
                        "Не удалось доставить данные о транзакциях клиента studentUid = %s, transactionId = %s",
                        mItem.getStudentUid(), mItem.getTransactionItems().get(0)), e);
            }

            updateTransactions(sendTransactions, session);

        }
    }

    private List<TransactionDataItem> findOrders(Session session){
        Query query = session.createSQLQuery(GET_MEAL_TRANSACTIONS);
        List list = query.list();
        List<TransactionDataItem> result = new ArrayList<TransactionDataItem>();
        for(Object o : list) {
            Object vals[] = (Object[]) o;
            Long idOfOrderDetail = Long.parseLong(vals[0].toString());
            Long idOfOrg = Long.parseLong(vals[1].toString());
            String organizationUid = (String)vals[2];
            String studentUid = (String)vals[3];
            Long cardPrintedNo = Long.parseLong(vals[4].toString());
            Integer cardType = Integer.parseInt(vals[5].toString());
            Long transactionId =  Long.parseLong(vals[6].toString());
            Date transactionDate = new Date(((BigInteger) vals[7]).longValue());
            Long balance = Long.parseLong(vals[8].toString());
            String foodName = (String)vals[9];
            Long qty = Long.parseLong(vals[10].toString());
            Long rprice = Long.parseLong(vals[11].toString());
            result.add(new TransactionDataItem(idOfOrderDetail, idOfOrg, organizationUid, studentUid, cardPrintedNo, cardType,
                    transactionId, transactionDate, balance, foodName, qty, rprice));
        }
        return result;
    }

    private void updateOrders(List<TransactionDataItem> sendOrders, Session session) {
        StringBuilder sb = new StringBuilder();
        sb.append("update cf_orderdetails od set od.sendtoexternal = true where (od.idoforderdetail, od.idoforg) in (");
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

    private List<TransactionDataItem> findTransactions(Session session){
        Query query = session.createSQLQuery(GET_TRANSACTIONS);
        List list = query.list();
        List<TransactionDataItem> result = new ArrayList<TransactionDataItem>();
        for(Object o : list) {
            Object vals[] = (Object[]) o;
            Long idOfOrg = Long.parseLong(vals[0].toString());
            String organizationUid = (String)vals[1];
            String studentUid = (String)vals[2];
            Long cardPrintedNo = Long.parseLong(vals[3].toString());
            Integer cardType = Integer.parseInt(vals[4].toString());
            Long transactionId =  Long.parseLong(vals[5].toString());
            Date transactionDate = new Date(((BigInteger) vals[6]).longValue());
            Long balance = Long.parseLong(vals[7].toString());
            Long amount = Long.parseLong(vals[8].toString());
            Integer sourceType = Integer.parseInt(vals[9].toString());
            result.add(new TransactionDataItem(idOfOrg, organizationUid, studentUid, cardPrintedNo, cardType,
                    transactionId, transactionDate, balance, amount, sourceType));
        }
        return result;
    }

    private void updateTransactions(List<TransactionDataItem> sendOrders, Session session) {
        StringBuilder sb = new StringBuilder();
        sb.append("update cf_transactions t set е.sendtoexternal = true where idoftransaction in (");
        for(TransactionDataItem item : sendOrders) {
            sb.append(item.getTransactionId())
                    .append("), ");
        }
        Query query = session.createSQLQuery(sb.substring(0, sb.length() - 2) + ")");
        query.executeUpdate();
    }

}
