/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperPrint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 21.01.16
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public class OutOfSynchronizationReport extends BasicReportForListOrgsJob {

    /* Логгер для отчета  DetailedDeviationsWithoutCorpsNewJasperReport*/
    private static final Logger logger = LoggerFactory.getLogger(OutOfSynchronizationReport.class);

   /* public AutoReportRunner getAutoReportRunner() {

        return new AutoReportRunner() {
            public void run(AutoReportBuildTask autoReportBuildTask) {

                String jobId = autoReportBuildTask.jobId;
                Long idOfSchedulerJob = Long.valueOf(jobId);

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(String.format("Building auto reports \"%s\"",
                            getMyClass().getCanonicalName()));
                }
                String classPropertyValue = getMyClass().getCanonicalName();
                List<AutoReport> autoReports = new ArrayList<AutoReport>();
                Session session = null;
                org.hibernate.Transaction transaction = null;
                try {
                    session = autoReportBuildTask.sessionFactory.openSession();
                    transaction = BasicReport.createTransaction(session);
                    transaction.begin();

                    List<RuleProcessor.Rule> thisReportRulesList = getThisReportRulesList(session, idOfSchedulerJob);
                    for (RuleProcessor.Rule rule : thisReportRulesList) {
                        String pre_orgs = rule.getExpressionValue(ReportPropertiesUtils.P_ID_OF_ORG);
                            Properties properties = new Properties();
                            ReportPropertiesUtils.addProperties(properties, getMyClass(), autoReportBuildTask);
                            properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, pre_orgs);

                            BasicReportForAllOrgJob report = createInstance();
                            report.setReportProperties(properties);
                            report.initialize(autoReportBuildTask.startTime, autoReportBuildTask.endTime,
                                    autoReportBuildTask.templateFileName, autoReportBuildTask.sessionFactory,
                                    autoReportBuildTask.startCalendar);

                            autoReports.add(new AutoReport(report, properties));
                    }

                    List<Long> reportHandleRuleIdsList = getRulesIdsByJobRules(session, idOfSchedulerJob);

                    transaction.commit();
                    transaction = null;
                    autoReportBuildTask.executorService.execute(
                            new AutoReportProcessor.ProcessTask(autoReportBuildTask.autoReportProcessor, autoReports,
                                    autoReportBuildTask.documentBuilders, reportHandleRuleIdsList));
                } catch (Exception e) {
                    getLogger().error(String.format("Failed at building auto reports \"%s\"", classPropertyValue), e);
                } finally {
                    HibernateUtils.rollback(transaction, getLogger());
                    HibernateUtils.close(session, getLogger());
                }

            }
        };
    }  */

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public OutOfSynchronizationReport(Date generateTime, long generateDuration, JasperPrint jasperPrint,
            Date startTime, Date endTime) {
        super(generateTime, generateDuration, jasperPrint, startTime, endTime);
    }

    public OutOfSynchronizationReport() {
    }

    @Override
    public OutOfSynchronizationReport createInstance() {
        return new OutOfSynchronizationReport();
    }

    @Override
    public OutOfSynchronizationReport.Builder createBuilder(String templateFilename) {
        return new OutOfSynchronizationReportBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
