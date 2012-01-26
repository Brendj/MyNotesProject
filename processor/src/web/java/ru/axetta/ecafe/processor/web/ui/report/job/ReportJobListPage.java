/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.job;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.core.persistence.SchedulerJob;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ReportJobListPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(ReportJobListPage.class);

    public static class Item {

        private final Long idOfSchedulerJob;
        private final String jobName;
        private final String reportType;
        private final String cronExpression;
        private final boolean enabled;

        public Item(SchedulerJob schedulerJob) throws Exception {
            this.idOfSchedulerJob = schedulerJob.getIdOfSchedulerJob();
            this.jobName = schedulerJob.getJobName();
            this.reportType = cutReportType(AutoReportGenerator.getReportType(schedulerJob.getJobClass()));
            this.cronExpression = schedulerJob.getCronExpression();
            this.enabled = schedulerJob.isEnabled();
        }

        public Long getIdOfSchedulerJob() {
            return idOfSchedulerJob;
        }

        public String getJobName() {
            return jobName;
        }

        public String getReportType() {
            return reportType;
        }

        public String getCronExpression() {
            return cronExpression;
        }

        public boolean isEnabled() {
            return enabled;
        }

        private static String cutReportType(String reportType) {
            if (reportType.startsWith(RuleCondition.REPORT_TYPE_BASE_PART)) {
                return ReportJobConstants.ELIDE_FILL + reportType
                        .substring(RuleCondition.REPORT_TYPE_BASE_PART.length());
            }
            return reportType;
        }
    }

    private List<Item> items = Collections.emptyList();

    public String getPageFilename() {
        return "report/job/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<Item> getItems() {
        return items;
    }

    public void fill(Session session) throws Exception {
        List<Item> newItems = new LinkedList<Item>();
        Criteria jobsCriteria = AutoReportGenerator.createEnabledReportJobsCriteria(session);
        List rules = jobsCriteria.list();
        for (Object object : rules) {
            SchedulerJob schedulerJob = (SchedulerJob) object;
            newItems.add(new Item(schedulerJob));
        }
        this.items = newItems;
    }

    public void removeReportJob(Long idOfReportJob) throws Exception {
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();

            runtimeContext.getAutoReportGenerator().removeJob(idOfReportJob);

            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            fill(persistenceSession);

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

}