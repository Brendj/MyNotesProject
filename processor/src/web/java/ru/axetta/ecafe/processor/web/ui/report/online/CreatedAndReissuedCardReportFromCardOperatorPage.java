/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.report.CreatedAndReissuedCardReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Component
@Scope("session")
public class CreatedAndReissuedCardReportFromCardOperatorPage extends OnlineReportPage {
    private CreatedAndReissuedCardReport report = null;
    private final static Logger logger = LoggerFactory.getLogger(CreatedAndReissuedCardReportFromCardOperatorPage.class);
    private String htmlReport = null;

    public CreatedAndReissuedCardReportFromCardOperatorPage(){
        super();
        localCalendar.setTime(this.startDate);
        localCalendar.add(Calendar.DAY_OF_MONTH, 1);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
        this.periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_DAY);
    }

    public Object generateReport() {
        Session session = null;
        Transaction persistenceTransaction = null;
        CreatedAndReissuedCardReport.Builder reportBuilder = new CreatedAndReissuedCardReport.Builder();
        try {
            try {
                session = RuntimeContext.getInstance().createPersistenceSession();
                reportBuilder.setUser(MainPage.getSessionInstance().getCurrentUser());
                persistenceTransaction = session.beginTransaction();
                this.report = reportBuilder.build(session, startDate, endDate, new GregorianCalendar());
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(session, logger);
            }
        } catch(Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        }
            if (this.report != null) {
                try {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    JRHtmlExporter exporter = new JRHtmlExporter();
                    exporter.setParameter(JRExporterParameter.JASPER_PRINT, report.getPrint());
                    exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
                    exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
                    exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
                    exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
                    exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
                    exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                    exporter.exportReport();
                    htmlReport = os.toString("UTF-8");
                    os.close();
                } catch (Exception e) {
                    printError(e.getMessage());
                }
            }
        return null;
    }

    @Override
    public void fill(Session persistenceSession, User currentUser) throws Exception{
        startDate = new Date();
        endDate = new Date();
        htmlReport = null;
    }

    public CreatedAndReissuedCardReport getReport() {
        return report;
    }

    @Override
    public String getPageFilename() {
        return "cardoperator/card_report";
    }

    public void setReport(CreatedAndReissuedCardReport report) {
        this.report = report;
    }

    @Override
    public String getHtmlReport() {
        return htmlReport;
    }

    @Override
    public void setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
    }
}
