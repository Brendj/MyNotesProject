/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: kolpakov
 * Date: 14.03.11
 * Time: 16:05
 * To change this template use File | Settings | File Templates.
 */
public abstract class BasicReportForMainBuildingOrgJob extends BasicReportForOrgJob {
    private final static Object runExecutorLock = new Object();

    @Override
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
                Session session = null;
                org.hibernate.Transaction transaction = null;
                try {
                    session = autoReportBuildTask.sessionFactory.openSession();
                    transaction = BasicReport.createTransaction(session);
                    transaction.begin();
                    Criteria allOrgCriteria = session.createCriteria(Org.class);
                    allOrgCriteria.addOrder(Order.asc("idOfOrg"));
                    allOrgCriteria.setCacheMode(CacheMode.GET);
                    allOrgCriteria.setCacheable(true);

                    List<RuleProcessor.Rule> thisReportRulesList = getThisReportRulesList(session, idOfSchedulerJob);

                    for (RuleProcessor.Rule rule : thisReportRulesList) {
                        String pre_orgs = rule.getExpressionValue(ReportPropertiesUtils.P_ID_OF_ORG);
                        if (pre_orgs == null) {
                            pre_orgs = getAllOrgs(session);
                        }
                        String[] idOfOrgs = pre_orgs.split(",");
                        List<AutoReport> autoReports = new ArrayList<AutoReport>();
                        for (String id : idOfOrgs) {
                            Org org = (Org)session.load(Org.class, Long.parseLong(id));
                            if (!doReportByOrgCondition(session, org)) {
                                continue;
                            }
                            Properties properties = new Properties();
                            ReportPropertiesUtils.addProperties(properties, getMyClass(), autoReportBuildTask);
                            properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, id == null ? "" : id);
                            properties.setProperty(ReportPropertiesUtils.P_ORG_NUMBER_IN_NAME, org.getOrgNumberInName());

                            BasicReportForOrgJob report = createInstance();
                            report.setReportProperties(properties);

                            report.initialize(autoReportBuildTask.startTime, autoReportBuildTask.endTime, Long.parseLong(id),
                                    autoReportBuildTask.templateFileName, autoReportBuildTask.sessionFactory,
                                    autoReportBuildTask.startCalendar);

                            autoReports.add(new AutoReport(report, properties));
                        }
                        List<Long> ids = Arrays.asList(rule.getRuleId());
                        synchronized (runExecutorLock) {
                            autoReportBuildTask.executorService.execute(
                                new AutoReportProcessor.ProcessTask(autoReportBuildTask.autoReportProcessor, autoReports,
                                    autoReportBuildTask.documentBuilders, ids));
                        }
                    }

                    transaction.commit();
                    transaction = null;
                } catch (Exception e) {
                    getLogger().error(String.format("Failed at building auto reports \"%s\"", classPropertyValue), e);
                } finally {
                    HibernateUtils.rollback(transaction, getLogger());
                    HibernateUtils.close(session, getLogger());
                }

            }
        };
    }

    private String getAllOrgs(Session session) {
        Query query = session.createSQLQuery("SELECT string_agg(CAST(idOfOrg as varchar), ',') FROM cf_orgs");
        return (String)query.uniqueResult();
    }

    private boolean doReportByOrgCondition(Session session, Org org) {
        Set<Org> fOrgs = org.getFriendlyOrg();
        if (fOrgs.size() <= 1) {
            return true;
        } else {
            boolean mainBuildingExists = false;
            for (Org o : fOrgs) {
                if (o.isMainBuilding()) {
                    mainBuildingExists = true;
                    break;
                }
            }
            if (mainBuildingExists && org.isMainBuilding()) {
                return true;
            } else {
                return false;
            }
        }
    }

    // call initialize after this constructor
    protected BasicReportForMainBuildingOrgJob() {
    }

    public BasicReportForMainBuildingOrgJob(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfOrg);
    }

}

