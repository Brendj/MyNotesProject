/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: kolpakov
 * Date: 14.03.11
 * Time: 16:05
 * To change this template use File | Settings | File Templates.
 */
public abstract class BasicReportForOrgJob extends BasicReportJob {
    public Class getMyClass() { return getClass(); }
    public abstract BasicReportForOrgJob createInstance();
    public abstract Builder createBuilder(String templateFilename);

    public abstract Logger getLogger();

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
                List<AutoReport> autoReports = new ArrayList<AutoReport>();
                Session session = null;
                org.hibernate.Transaction transaction = null;
                try {
                    session = autoReportBuildTask.sessionFactory.openSession();
                    session.setDefaultReadOnly(true);
                    session.setFlushMode(FlushMode.MANUAL);
                    transaction = BasicReport.createTransaction(session);
                    transaction.begin();
                    Criteria allOrgCriteria = session.createCriteria(Org.class);
                    allOrgCriteria.addOrder(Order.asc("idOfOrg"));
                    allOrgCriteria.setCacheMode(CacheMode.GET);
                    allOrgCriteria.setCacheable(true);
                    List allOrgs = allOrgCriteria.list();

                    for (Object object : allOrgs) {
                        Org org = (Org) object;
                        if (getLogger().isDebugEnabled()) {
                            getLogger().debug(String.format("Building report \"%s\" for org: %s", classPropertyValue, org));
                        }
                        Properties properties = new Properties();
                        ReportPropertiesUtils.addProperties(properties, getMyClass(), autoReportBuildTask);
                        ReportPropertiesUtils.addProperties(session, properties, org, null);
                        BasicReportForOrgJob report = createInstance();
                        report.initialize(autoReportBuildTask.startTime, autoReportBuildTask.endTime,
                                org.getIdOfOrg(), autoReportBuildTask.templateFileName,
                                autoReportBuildTask.sessionFactory, autoReportBuildTask.startCalendar);
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

    private Long idOfOrg;

    public BasicReportForOrgJob(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime);
        this.idOfOrg = idOfOrg;
    }

    // call initialize after this constructor
    protected BasicReportForOrgJob() {
    }

    public void initialize(Date startTime, Date endTime, Long idOfOrg, String templateFilename,
            SessionFactory sessionFactory, Calendar calendar) {
        super.initialize(startTime, endTime, templateFilename, sessionFactory, calendar);
        this.idOfOrg = idOfOrg;
    }
    public void BasicReportForOrgJob(Date startTime, Date endTime, Long idOfOrg, String templateFilename,
            SessionFactory sessionFactory, Calendar calendar) {
        initialize(startTime, endTime, idOfOrg, templateFilename, sessionFactory, calendar);
    }
    @Override
    public String toString() {
        return getMyClass().getCanonicalName()+"{" + "startTime=" + startTime + ", endTime=" + endTime + ", idOfOrg="
                + idOfOrg + ", templateFilename='" + templateFilename + '\'' + ", sessionFactory="
                + sessionFactory + "} " + super.toString();
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    @Override
    public String getReportDistinctText() {
        return Long.toString(idOfOrg);
    }

    protected void prepare() {
        if (!hasPrint() && idOfOrg != null && templateFilename != null && sessionFactory != null) {
            // templateFilename может содержать только имя файла отчета или относительный путь к нему
            // добавляем путь к файлам отчетов, если это необходимо
            templateFilename = AutoReportGenerator.restoreFilename(
                    RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath(), templateFilename);
            Builder builder = createBuilder(templateFilename);
            Session session = null;
            org.hibernate.Transaction transaction = null;
            try {
                session = sessionFactory.openSession();
                transaction = BasicReport.createTransaction(session);
                transaction.begin();
                //Org org = (Org) session.get(Org.class, this.idOfOrg);
                Criteria criteria = session.createCriteria(Org.class);
                criteria.add(Restrictions.eq("idOfOrg", idOfOrg));
                criteria.setProjection(Projections.projectionList()
                        .add(Projections.property("idOfOrg"),"idOfOrg")
                        .add(Projections.property("shortName"),"shortName")
                        .add(Projections.property("officialName"),"officialName")
                );
                criteria.setResultTransformer(Transformers.aliasToBean(OrgShortItem.class));

                builder.setOrg((OrgShortItem) criteria.uniqueResult());
                builder.setReportProperties(getReportProperties());
                BasicReportJob report = builder.build(session, startTime, endTime, calendar);
                setGenerateTime(report.getGenerateTime());
                setGenerateDuration(report.getGenerateDuration());
                setPrint(report.getPrint());
                transaction.commit();
                transaction = null;
            } catch (Exception e) {
                getLogger().error(String.format("Failed at report lazy-build \"%s\"", BasicReportForOrgJob.class),
                        e);
            } finally {
                HibernateUtils.rollback(transaction, getLogger());
                HibernateUtils.close(session, getLogger());
            }
        }
    }

}

