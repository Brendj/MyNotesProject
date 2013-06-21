/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 10.09.12
 * Time: 15:28
 * To change this template use File | Settings | File Templates.
 */
public abstract class BasicReportForContragentJob extends BasicReportJob {

    protected Class getMyClass() { return getClass(); }
    public abstract BasicReportForContragentJob createInstance();
    //protected abstract Builder createBuilder(String templateFilename);
    protected abstract Logger getLogger();
    protected Integer getContragentSelectClass() {
        return null;
    }

    @Override
    public AutoReportRunner getAutoReportRunner() {
        return new AutoReportRunner(){
            @Override
            public void run(AutoReportBuildTask autoReportBuildTask) {
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
                    Criteria allContragentCriteria = session.createCriteria(Contragent.class);
                    if (getContragentSelectClass()!=null) {
                        allContragentCriteria.add(Restrictions.eq("classId", getContragentSelectClass()));
                    }
                    List allContragents = allContragentCriteria.list();
                    for (Object object: allContragents){
                        Contragent contragent = (Contragent) object;
                        if (getLogger().isDebugEnabled()) {
                             getLogger().debug(String.format("Building report \"%s\" for contragent: %s", classPropertyValue, contragent));
                        }
                        Properties properties = new Properties();
                        ReportPropertiesUtils.addProperties(properties,getMyClass(), autoReportBuildTask);
                        ReportPropertiesUtils.addProperties(properties, contragent, null);
                        BasicReportForContragentJob report = createInstance();
                        report.initialize(autoReportBuildTask.startTime, autoReportBuildTask.endTime,
                                contragent.getIdOfContragent(),autoReportBuildTask.templateFileName,
                                autoReportBuildTask.sessionFactory, autoReportBuildTask.startCalendar);
                        autoReports.add(new AutoReport(report, properties));
                    }
                    transaction.commit();
                    transaction = null;
                    autoReportBuildTask.executorService.execute(
                            new AutoReportProcessor.ProcessTask(autoReportBuildTask.autoReportProcessor, autoReports,
                                    autoReportBuildTask.documentBuilders));
                } catch (Exception e) {
                    getLogger().error(String.format("Failed at building auto reports \"%s\"", classPropertyValue), e);
                } finally {
                    HibernateUtils.rollback(transaction, getLogger());
                    HibernateUtils.close(session, getLogger());
                }
            }
        };
    }

    public BasicReportForContragentJob(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfContragent) {
        super(generateTime, generateDuration, print, startTime, endTime);
        this.idOfContragent = idOfContragent;
    }

    public BasicReportForContragentJob(){}

    @Override
    public String getReportDistinctText() {
        return Long.toString(idOfContragent);
    }

    public void initialize(Date startTime, Date endTime, Long idOfContragent, String templateFilename,
            SessionFactory sessionFactory, Calendar calendar) {
        super.initialize(startTime, endTime, templateFilename, sessionFactory, calendar);
        this.idOfContragent = idOfContragent;
    }

    @Override
    public String toString() {
        return getMyClass().getCanonicalName()+"{" + "startTime=" + startTime + ", endTime=" + endTime + ", idOfContragent="
                + idOfContragent + ", templateFilename='" + templateFilename + '\'' + ", sessionFactory="
                + sessionFactory + "} " + super.toString();
    }

    protected Long idOfContragent;

    protected void prepare() {
        if (!hasPrint() && idOfContragent != null && templateFilename != null && sessionFactory != null) {
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
                Contragent contragent = (Contragent) session.get(Contragent.class, this.idOfContragent);
                builder.setReportProperties(getReportProperties());
                builder.setContragent(contragent);
                BasicReportJob report = builder.build(session, startTime, endTime, calendar);
                setGenerateTime(report.getGenerateTime());
                setGenerateDuration(report.getGenerateDuration());
                setPrint(report.getPrint());
                transaction.commit();
                transaction = null;
            } catch (Exception e) {
                getLogger().error(String.format("Failed at report lazy-build \"%s\"", BasicReportForContragentJob.class),
                        e);
            } finally {
                HibernateUtils.rollback(transaction, getLogger());
                HibernateUtils.close(session, getLogger());
            }
        }
    }
}
