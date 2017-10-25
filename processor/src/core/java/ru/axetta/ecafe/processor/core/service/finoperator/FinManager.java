/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.finoperator;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.AccountTransaction;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by i.semenov on 24.10.2017.
 */
@Component
@Scope("singleton")
public class FinManager {
    private static final Logger logger = LoggerFactory.getLogger(FinManager.class);
    public static final String SENDTOEXTERNAL_OPTION = "ecafe.processor.finmanager.sendtoexternal";

    public static final String SUPPLIERS_OPTION = "ecafe.processor.finmanager.suppliers";
    public static List<Long> ORG_LIST;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManagerRW;

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManagerRO;

    public static FinManager getInstance() {
        return RuntimeContext.getAppContext().getBean(FinManager.class);
    }

    @PostConstruct
    private void getOrgs() {
        String suppliers = RuntimeContext.getInstance().getConfigProperties().getProperty(SUPPLIERS_OPTION, "");
        if (StringUtils.isEmpty(suppliers) || !sendToExternal()) {
            ORG_LIST = null;
            return;
        }
        try {
            String[] ids = suppliers.split(",");
            List<Long> supp = new ArrayList<Long>();
            for (String s : ids) {
                supp.add(new Long(s));
            }
            Query query = entityManagerRO.createQuery("select o.idOfOrg from Org o where o.defaultSupplier.idOfContragent in :suppliers");
            query.setParameter("suppliers", supp);
            ORG_LIST = query.getResultList();
        } catch(Exception e) {
            logger.error("Error getting orgs from supplier list ecafe.processor.finmanager.suppliers:", e);
            ORG_LIST = null;
        }
    }

    public boolean sendToExternal() {
        String res = RuntimeContext.getInstance().getConfigProperties().getProperty(SENDTOEXTERNAL_OPTION, "false");
        return "true".equals(res);
    }


    @Transactional
    public void run(Date startDate, Date endDate, String filename) throws RuntimeException {
        logger.info("Start make summary transactions for finoperator");
        Long beg_time = System.currentTimeMillis();
        try {
            String str_query = "select o.idOfContragent, o.idOfOrg, t.idOfTransaction, t.transactionSum, t.sourceType, t.transactionDate, "
                    + "t.balanceBefore, t.balanceAfter, t.sendToExternal, o.idOfOrder, c.contractId "
                    + "from cf_transactions t inner join cf_orders o on t.idOfTransaction = o.idOfTransaction "
                    + "inner join cf_clients c on c.idOfClient = t.idOfClient "
                    + "where t.transactionDate between :startDate and :endDate and (t.sourceType = :sourceTypeOrder or t.sourceType = :sourceTypeCancelOrder) "
                    + "order by t.transactionDate";
            Query query = entityManagerRW.createNativeQuery(str_query);
            query.setParameter("sourceTypeOrder", AccountTransaction.CLIENT_ORDER_TRANSACTION_SOURCE_TYPE);
            query.setParameter("sourceTypeCancelOrder", AccountTransaction.CANCEL_TRANSACTION_SOURCE_TYPE);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            List list = query.getResultList();
            List<String> result = new ArrayList<String>();
            result.add("idOfContragent;idOfOrg;idOfTransaction;transactionSum;sourceType;transactionDate;balanceBefore;balanceAfter;sentToExternal;idOfOrder;contractId");
            for (Object o : list) {
                Object[] row = (Object[]) o;
                Long idOfContragent = ((BigInteger) row[0]).longValue();
                Long idOfOrg = ((BigInteger) row[1]).longValue();
                Long idOfTransaction = ((BigInteger) row[2]).longValue();
                Long transactionSum = ((BigInteger) row[3]).longValue();
                Integer sourceType = (Integer) row[4];
                String transactionDate = CalendarUtils.toStringFullDateTimeWithLocalTimeZone(new Date(((BigInteger) row[5]).longValue()));
                Long balanceBefore = ((BigInteger) row[6]).longValue();
                Long balanceAfter = ((BigInteger) row[7]).longValue();
                Integer sendToExternal = (Integer) row[8];
                Long idOfOrder = ((BigInteger) row[9]).longValue();
                Long contractId = ((BigInteger) row[10]).longValue();
                StringBuilder sb = new StringBuilder();
                sb.append(idOfContragent).append(";")
                        .append(idOfOrg).append(";")
                        .append(idOfTransaction).append(";")
                        .append(transactionSum).append(";")
                        .append(sourceType).append(";")
                        .append(transactionDate).append(";")
                        .append(balanceBefore).append(";")
                        .append(balanceAfter).append(";")
                        .append(sendToExternal).append(";")
                        .append(idOfOrder).append(";")
                        .append(contractId);
                result.add(sb.toString());
            }
            File file = new File(filename);
            FileUtils.writeLines(file, result);

            Query query2 = entityManagerRW.createNativeQuery("update cf_transactions set sendToExternal = 1 "
                    + "where transactionDate between :startDate and :endDate and sendToExternal = 0");
            query2.setParameter("startDate", startDate.getTime());
            query2.setParameter("endDate", endDate.getTime());
            query2.executeUpdate();
            logger.info(String.format("End make summary transactions for finoperator. Time taken - %s ms", System.currentTimeMillis() - beg_time));
        } catch (Exception e) {
            logger.error("Error in make summary file for finoperator: ", e);
        }
    }

    @Transactional
    public void markTransactionsAsSentToExternal(List<Long> ids) {
        if (ids == null || ids.size() == 0) return;
        try {
            int counter = 0;
            String str_query = "update cf_transactions set sendtoexternal = 1 where idoftransaction in (:transactions) and sendtoexternal = 0";
            Query query = entityManagerRW.createNativeQuery(str_query);
            List<Long> transactions = new ArrayList<Long>();
            for (Long id : ids) {
                counter++;
                transactions.add(id);
                if (counter % 10 == 0) {
                    query.setParameter("transactions", transactions);
                    query.executeUpdate();
                    transactions.clear();
                }
            }
            if (transactions.size() > 0) {
                query.setParameter("transactions", transactions);
                query.executeUpdate();
            }
        } catch(Exception e) {
            logger.error("Error in FinManager.markTransactionsAsSentToExternal: ", e);
        }
    }

    public List getOrdersAndTransactions() {
        String str_query = "select t.idOfTransaction, t.transactionSum, t.sourceType, t.transactionDate, "
                + "t.balanceBefore, t.balanceAfter, o.idOfOrder, c.contractId "
                + "from cf_transactions t inner join cf_orders o on t.idOfTransaction = o.idOfTransaction "
                + "inner join cf_clients c on c.idOfClient = t.idOfClient "
                + "where t.sendToExternal = 0 and (t.sourceType = :sourceTypeOrder or t.sourceType = :sourceTypeCancelOrder)";
        Query query = entityManagerRO.createNativeQuery(str_query);
        query.setParameter("sourceTypeOrder", AccountTransaction.CLIENT_ORDER_TRANSACTION_SOURCE_TYPE);
        query.setParameter("sourceTypeCancelOrder", AccountTransaction.CANCEL_TRANSACTION_SOURCE_TYPE);
        return query.getResultList();
    }
}
