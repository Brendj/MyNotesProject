/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: kolpakov
 * Date: 14.03.11
 * Time: 16:05
 * To change this template use File | Settings | File Templates.
 */
public abstract class BasicReportForListOrgsJob extends BasicReportForAllOrgJob {

    public AutoReportRunner getAutoReportRunner() {

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
                        properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, pre_orgs != null ? pre_orgs : "0"); //0  -организация не предусмотрена параметром

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
    }

    // call initialize after this constructor
    protected BasicReportForListOrgsJob() {
    }

    public BasicReportForListOrgsJob(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

}

