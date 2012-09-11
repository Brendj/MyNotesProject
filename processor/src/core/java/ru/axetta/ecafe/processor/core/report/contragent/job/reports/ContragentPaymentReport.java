/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.contragent.job.reports;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 10.09.12
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
public class ContragentPaymentReport extends BasicReportForContragentJob {

    public static class Builder implements BasicReportForContragentJob.Builder{

        public static class TransactionItem {

            private final Date transactionTime;

            public Date getTransactionTime() {
                return transactionTime;
            }

            public TransactionItem(AccountTransaction accountTransaction) {
                this.transactionTime = accountTransaction.getTransactionTime();
            }
        }

        public static class ContragentPaymentItem {
            private final TransactionItem transaction;
            private final long paySum;
            private final Date createTime;

            public TransactionItem getTransaction() {
                return transaction;
            }

            public long getPaySum() {
                return paySum;
            }

            public Date getCreateTime() {
                return createTime;
            }

            public ContragentPaymentItem(ContragentPayment contragentPayment) {
                AccountTransaction accountTransaction = contragentPayment.getTransaction();
                this.transaction = new TransactionItem(accountTransaction);
                this.paySum = contragentPayment.getPaySum();
                this.createTime = contragentPayment.getCreateTime();
            }
        }

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Contragent contragent, Date startTime, Date endTime,
                Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<Object, Object> parameterMap = new HashMap<Object, Object>();
            parameterMap.put("idOfContragent", contragent.getIdOfContragent());
            parameterMap.put("contragentName", contragent.getContragentName());
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, contragent, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new ContragentPaymentReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, contragent.getIdOfContragent());
        }

        private JRDataSource createDataSource(Session session, Contragent contragent, Date startTime, Date endTime,
                Calendar clone, Map<Object, Object> parameterMap) {
            List<ContragentPaymentItem> contragentInfoList = new LinkedList<ContragentPaymentItem>();
            Criteria criteria = session.createCriteria(ContragentPayment.class);
            criteria.add(Restrictions.eq("contragent",contragent));
            criteria.add(Restrictions.between("createTime",startTime,endTime));
            List list = criteria.list();
            for (Object  object: list){
                ContragentPayment contragentPayment = (ContragentPayment) object;
                ContragentPaymentItem contragentPaymentItem = new ContragentPaymentItem(contragentPayment);
            }
            return new JRBeanCollectionDataSource(contragentInfoList);
        }
    }

    @Override
    protected Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    @Override
    public BasicReportForContragentJob createInstance() {
        return new ContragentPaymentReport();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    public ContragentPaymentReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfContragent) {
         super(generateTime,generateDuration,print,startTime,endTime, idOfContragent);
    }

    public ContragentPaymentReport() {}

    private static final Logger logger = LoggerFactory.getLogger(ContragentPaymentReport.class);
}
