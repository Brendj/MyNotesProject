/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 10.09.12
 * Time: 15:28
 * To change this template use File | Settings | File Templates.
 */
public abstract class BasicReportForAllOrgJob extends BasicReportJob {
    public Class getMyClass() { return getClass(); }
    public abstract BasicReportForAllOrgJob createInstance();
    public abstract Builder createBuilder(String templateFilename);

    public abstract Logger getLogger();

    @Override
    public AutoReportRunner getAutoReportRunner() {

        return new AutoReportRunner() {
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

                    Properties properties = new Properties();
                    ReportPropertiesUtils.addProperties(properties, getMyClass(), autoReportBuildTask);
                    BasicReportForAllOrgJob report = createInstance();
                    report.initialize(autoReportBuildTask.startTime, autoReportBuildTask.endTime, autoReportBuildTask.templateFileName,
                            autoReportBuildTask.sessionFactory, autoReportBuildTask.startCalendar);
                    autoReports.add(new AutoReport(report, properties));
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


    public BasicReportForAllOrgJob(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    // call initialize after this constructor
    protected BasicReportForAllOrgJob() {
    }

    public void initialize(Date startTime, Date endTime, String templateFilename,
            SessionFactory sessionFactory, Calendar calendar) {
        super.initialize(startTime, endTime, templateFilename, sessionFactory, calendar);
    }
    public void BasicReportForOrgJob(Date startTime, Date endTime,  String templateFilename,
            SessionFactory sessionFactory, Calendar calendar) {
        initialize(startTime, endTime, templateFilename, sessionFactory, calendar);
    }
    @Override
    public String toString() {
        return getMyClass().getCanonicalName()+"{" + "startTime=" + startTime + ", endTime=" + endTime + ", templateFilename='" + templateFilename + '\'' + ", sessionFactory="
                + sessionFactory + "} " + super.toString();
    }

    @Override
    public String getReportDistinctText() {
        return String.valueOf(new Date().hashCode());
    }

    /*public interface Builder {
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception;
    } */

    protected void prepare() {
        if (!hasPrint() && templateFilename != null && sessionFactory != null) {
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
