/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.contragent.job.reports;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.report.*;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    protected abstract Builder createBuilder(String templateFilename);
    protected abstract Logger getLogger();

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
                List<AutoReport> autoReports = new LinkedList<AutoReport>();
                Session session = null;
                org.hibernate.Transaction transaction = null;
                try {
                    session = autoReportBuildTask.sessionFactory.openSession();
                    transaction = BasicReport.createTransaction(session);
                    transaction.begin();
                    Criteria allContragentCriteria = session.createCriteria(Contragent.class);
                    List allContragents = allContragentCriteria.list();
                    for (Object object: allContragents){
                        Contragent contragent = (Contragent) object;
                        if (getLogger().isDebugEnabled()) {
                             getLogger().debug(String.format("Building report \"%s\" for contragent: %s", classPropertyValue, contragent));
                        }
                        Properties properties = new Properties();
                        ReportPropertiesUtils.addProperties(properties,getMyClass());
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

    public interface Builder {
        public BasicReportJob build(Session session, Contragent contragent, Date startTime, Date endTime, Calendar calendar)
                throws Exception;
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

    private Long idOfContragent;
}
