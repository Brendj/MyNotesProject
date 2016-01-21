package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import java.io.File;
import java.math.BigInteger;
import java.util.*;

/*
 * Copyright (c) 2016. Axetta LLC. All Rights/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 18.01.16
 * Time: 13:56
 * To change this template use File | Settings | File Templates.
 */
public class BalanceLeavingReportBuilder extends BasicReportForAllOrgJob.Builder {

    private final String templateFilename;

    public BalanceLeavingReportBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();

        String templateFilename = autoReportGenerator.getReportsTemplateFilePath()
                + "BalanceLeavingReport.jasper";

        if (!(new File(templateFilename)).exists()) {
            throw new Exception(String.format("Не найден файл шаблона '%s'", templateFilename));
        }

        Date generateBeginTime = new Date();

        /* Параметры для передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("endDate", CalendarUtils.dateToString(endTime));
        parameterMap.put("startDate", CalendarUtils.dateToString(startTime));

        JRDataSource dataSource = buildDataSource(session, startTime, endTime);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

        Date generateEndTime = new Date();
        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();

        return new BalanceLeavingReport(generateBeginTime, generateDuration, jasperPrint, startTime, endTime);
    }

    private JRDataSource buildDataSource(Session session, Date startTime, Date endTime) {

        //Результирующий лист по которому строиться отчет
        List<BalanceLeavingItem> balanceLeavingItemList = new ArrayList<BalanceLeavingItem>();

        Query query = session.createSQLQuery(
                "select idOfTransaction, idOfClient, transactionSum, balanceBefore, balanceAfter, transactionDate "
                        + "from cf_transactions where balanceAfter < 0 and transactionDate between :startTime and :endTime");
        query.setParameter("startTime", startTime.getTime());
        query.setParameter("endTime", endTime.getTime());

        List result = query.list();

        for (Object resultTransaction: result) {
            Object[] object = (Object[]) resultTransaction;

            BalanceLeavingItem balanceLeavingItem = new BalanceLeavingItem(((BigInteger) object[0]).longValue(),
                    ((BigInteger) object[1]).longValue(),
                    ((BigInteger) object[2]).longValue(),
                    ((BigInteger) object[3]).longValue(),
                    ((BigInteger) object[4]).longValue(),
                    CalendarUtils.truncateToDayOfMonth(new Date(((BigInteger) object[5]).longValue())));
            balanceLeavingItemList.add(balanceLeavingItem);
        }

        return new JRBeanCollectionDataSource(balanceLeavingItemList);
    }

    public class BalanceLeavingItem {

        private Long idOfTransaction;
        private Long idOfClient;
        private Long transactionSum;
        private Long balanceBeforeTransaction;
        private Long balanceAfterTransaction;
        private Date transactionTime;

        public BalanceLeavingItem() {
        }

        public BalanceLeavingItem(Long idOfTransaction, Long idOfClient, Long transactionSum,
                Long balanceBeforeTransaction, Long balanceAfterTransaction, Date transactionTime) {
            this.idOfTransaction = idOfTransaction;
            this.idOfClient = idOfClient;
            this.transactionSum = transactionSum;
            this.balanceBeforeTransaction = balanceBeforeTransaction;
            this.balanceAfterTransaction = balanceAfterTransaction;
            this.transactionTime = transactionTime;
        }

        public Long getIdOfTransaction() {
            return idOfTransaction;
        }

        public void setIdOfTransaction(Long idOfTransaction) {
            this.idOfTransaction = idOfTransaction;
        }

        public Long getIdOfClient() {
            return idOfClient;
        }

        public void setIdOfClient(Long idOfClient) {
            this.idOfClient = idOfClient;
        }

        public Long getTransactionSum() {
            return transactionSum;
        }

        public void setTransactionSum(Long transactionSum) {
            this.transactionSum = transactionSum;
        }

        public Long getBalanceBeforeTransaction() {
            return balanceBeforeTransaction;
        }

        public void setBalanceBeforeTransaction(Long balanceBeforeTransaction) {
            this.balanceBeforeTransaction = balanceBeforeTransaction;
        }

        public Long getBalanceAfterTransaction() {
            return balanceAfterTransaction;
        }

        public void setBalanceAfterTransaction(Long balanceAfterTransaction) {
            this.balanceAfterTransaction = balanceAfterTransaction;
        }

        public Date getTransactionTime() {
            return transactionTime;
        }

        public void setTransactionTime(Date transactionTime) {
            this.transactionTime = transactionTime;
        }

        public String getTransactionTimeShortFormat() {
            return CalendarUtils.dateShortToStringFullYear(transactionTime);
        }
    }
}
