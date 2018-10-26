/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.meal;

import generated.spb.meal.PushResponse;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 10.04.17
 * Time: 10:37
 */
@Component
@Scope("singleton")
public class MealManager {
    private final static String EXPENSE = "expense";
    private final static String INCOME = "income";
    private final static String CARDNAME = "Счёт питания";
    private final String updateInitialStatement = "update cf_orderdetails set sendtoexternal = 1 where (idoforderdetail, idoforg) in (";
    private final static Integer LIMIT_RECORDS = 1000;
    private final static String MEAL_ENDPOINT_ADDRESS = getMainEndPointAddress();

    private static String getMainEndPointAddress() {
        Properties properties = RuntimeContext.getInstance().getConfigProperties();
        return properties
                .getProperty("ecafe.processor.mealmanager.mainendpointaddress", "http://10.146.136.36/service/webservice/meal");
    }

    private static final Logger logger = LoggerFactory.getLogger(MealManager.class);

    private final static boolean isOn = isOn();
    public final static boolean isSendToExternal = isSendToExternal();

    private static MealService mealService = RuntimeContext.getAppContext().getBean(MealService.class);

    public static boolean isOn() {
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
            sendToExternal(LIMIT_RECORDS);
        }
    }

    @SuppressWarnings("unchecked")
    public void sendToExternal(Integer limit) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            sendOrdersToExternal(persistenceSession, limit);
            sendTransactionsToExternal(persistenceSession, limit);

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Error sending data to meal service: ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @SuppressWarnings("unchecked")
    private void sendOrdersToExternal(Session session, Integer limit) throws Exception {
        List<TransactionDataItem> sendOrders = new ArrayList<TransactionDataItem>();
        List<TransactionDataItem> list = findOrders(session, sendOrders, limit);
        int i = 0;
        logger.info("MealManager: start sending orders to external system.");
        for(TransactionDataItem item : list) {
            TransactionItem trItem = new TransactionItem(item.getTransactionId(), item.getTransactionDate(), item.getBalance(), item.getAmount(),
                    CARDNAME, item.getFoodName(), item.getFoodAmount(), EXPENSE);
            MealDataItem mItem = new MealDataItem(item.getOrganizationUid(), item.getStudentUid(), item.getUserToken(), item.getCardUid(), trItem);
            try {
                HashMap<String, PushResponse> responses = mealService.sendEvent(mItem);
                PushResponse response = responses.get(MEAL_ENDPOINT_ADDRESS);
                if(response == null){
                    throw new Exception("Main receiver ( " + MEAL_ENDPOINT_ADDRESS + " ) didn't get the packages");
                }
                if(response.isResult()) {
                    sendOrders.add(item);
                    i++;
                    logger.info(String.format(
                            "Успешно отправлена транзакция о покупках клиента studentUid = %s, transactionId = %s, result false",
                            mItem.getStudentUid(), mItem.getTransactionItems().get(0)));
                } else {
                    logger.error(String.format(
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
    private void sendTransactionsToExternal(Session session, Integer limit) throws Exception {
        List<TransactionDataItem> sendTransactions = new ArrayList<TransactionDataItem>();
        List<TransactionDataItem> list = findTransactions(session, sendTransactions, limit);
        int i = 0;
        logger.info("MealManager: start sending Transactions to external system.");
        for(TransactionDataItem item : list) {
            TransactionItem trItem = new TransactionItem(item.getTransactionId(), item.getTransactionDate(), item.getBalance(), Math.abs(item.getAmount()),
                    CARDNAME, item.getFoodName(), item.getFoodAmount(), item.getAmount() > 0 ? INCOME : EXPENSE);
            MealDataItem mItem = new MealDataItem(item.getOrganizationUid(), item.getStudentUid(), item.getUserToken(), item.getCardUid(), trItem);
            try {
                HashMap<String, PushResponse> responses = mealService.sendEvent(mItem);
                PushResponse response = responses.get(MEAL_ENDPOINT_ADDRESS);
                if(response == null){
                    throw new Exception("Main receiver ( " + MEAL_ENDPOINT_ADDRESS + " ) didn't get the packages");
                }
                if(response.isResult()) {
                    sendTransactions.add(item);
                    i++;
                    logger.info(String.format(
                            "Успешно отправились данные о транзакциях клиента studentUid = %s, transactionId = %s, result false",
                            mItem.getStudentUid(), mItem.getTransactionItems().get(0)));
                } else {
                    logger.error(String.format(
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
    private List<TransactionDataItem> findOrders(Session session, List<TransactionDataItem> sendOrders, Integer limit) throws Exception {
        List<TransactionDataItem> result = new ArrayList<TransactionDataItem>();
        Criteria criteria = session.createCriteria(OrderDetail.class);
        criteria.add(Restrictions.eq("sendToExternal", Boolean.FALSE));
        criteria.addOrder(Order.asc("idOfOrder"));
        criteria.setMaxResults(limit);
        List<OrderDetail> list = criteria.list();
        for(OrderDetail od : list) {
            if(sendOrderDetail(od)) {
                Card card = od.getOrder().getCard();
                result.add(new TransactionDataItem(od.getCompositeIdOfOrderDetail().getIdOfOrderDetail(), od.getOrg().getIdOfOrg(),
                        od.getOrg().getOGRN(), od.getOrder().getClient().getClientGUID(),
                        card == null ? null : card.getCardPrintedNo(), card == null ? null : card.getCardType(),
                        od.getOrder().getTransaction().getIdOfTransaction(),
                        od.getOrder().getTransaction().getTransactionTime(),
                        od.getOrder().getTransaction().getBalanceAfterTransaction(),
                        od.getMenuDetailName(), od.getQty(), od.getRPrice()));
            } else {
                sendOrders.add(new TransactionDataItem(od.getCompositeIdOfOrderDetail().getIdOfOrderDetail(), od.getCompositeIdOfOrderDetail().getIdOfOrg()));
            }
        }
        return result;
    }

    private boolean sendOrderDetail(OrderDetail od) {
        return od.getOrder().getClient() != null
                && !StringUtils.isEmpty(od.getOrder().getOrg().getOGRN())
                && !StringUtils.isEmpty(od.getOrder().getClient().getClientGUID())
                && od.getOrder().getTransaction() != null;
    }

    private void updateOrders(List<TransactionDataItem> sendOrders, Session session) {
        if(sendOrders.size() < 1) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(updateInitialStatement);
        Integer divider = 100;
        for(TransactionDataItem item : sendOrders) {
            sb.append("(")
            .append(item.getIdOfOrderDetail())
            .append(",")
            .append(item.getIdOfOrg())
            .append("), ");
            divider++;
            if (divider % 100 == 0) {
                Query query = session.createSQLQuery(sb.substring(0, sb.length() - 2) + ")");
                query.executeUpdate();
                sb.setLength(0);
                sb.append(updateInitialStatement);
            }
        }
        if (sb.length() > updateInitialStatement.length()) {
            Query query = session.createSQLQuery(sb.substring(0, sb.length() - 2) + ")");
            query.executeUpdate();
        }
    }

    @SuppressWarnings("unchecked")
    private List<TransactionDataItem> findTransactions(Session session, List<TransactionDataItem> sendTransactions, Integer limit) throws Exception {
        List<TransactionDataItem> result = new ArrayList<TransactionDataItem>();
        /*Criteria criteria = session.createCriteria(AccountTransaction.class);
        criteria.add(Restrictions.eq("sendToExternal", Boolean.FALSE));
        criteria.addOrder(Order.asc("idOfTransaction"));
        criteria.setMaxResults(1000);
        List<AccountTransaction> list = criteria.list();*/
        Query query = session.createSQLQuery("SELECT t.idoforg, o.ogrn, c.clientguid, c.contractid, coalesce(card.cardprintedno, -1) as cardprintedno, "
                + "coalesce(card.cardtype, -1) as cardtype, "
                + "t.idoftransaction, t.transactiondate, t.balanceafter, t.transactionsum, t.sourcetype "
                + "FROM cf_transactions t LEFT JOIN cf_orgs o ON t.idoforg = o.idoforg LEFT JOIN cf_clients c ON t.idofclient = c.idofclient "
                + "LEFT JOIN cf_cards card ON c.idofclient = card.idofclient AND card.state = :cardState "
                + "WHERE t.sendtoexternal = 0 and (o.OGRN is not null and o.OGRN <> '') and (c.ClientGUID is not null and c.ClientGUID <> '') order by t.idoftransaction limit :limit_rec");
        query.setParameter("cardState", Card.ACTIVE_STATE);
        query.setParameter("limit_rec", limit);
        List list = query.list();
        for (Object res : list) {
            Object[] row = (Object[]) res;
            Long idOfOrg = ((BigInteger) row[0]).longValue();
            String ogrn = (String) row[1];
            Long clientGuidLong = ((BigInteger) row[3]).longValue();
            Long cardPrintedNo = ((BigInteger) row[4]).longValue();
            if (cardPrintedNo.equals(-1)) cardPrintedNo = null;
            Integer cardType = (Integer) row[5];
            if (cardType.equals(-1)) cardType = null;
            Long idOfTransaction = ((BigInteger) row[6]).longValue();
            Long transactionTime = ((BigInteger) row[7]).longValue();
            Long balanceAfter = ((BigInteger) row[8]).longValue();
            Long transactionSum = ((BigInteger) row[9]).longValue();
            Integer sourceType = (Integer) row[10];
            if (idOfOrg != null) {
                result.add(new TransactionDataItem(idOfOrg, ogrn, clientGuidLong.toString(), cardPrintedNo, cardType, idOfTransaction,
                        new Date(transactionTime), balanceAfter, transactionSum, sourceType));
            } else {
                sendTransactions.add(new TransactionDataItem(idOfTransaction.toString()));
            }
        }
        /*for(AccountTransaction tr : list) {
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
        } */
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
