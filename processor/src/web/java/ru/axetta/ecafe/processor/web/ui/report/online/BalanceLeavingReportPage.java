/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.BalanceLeavingReport;
import ru.axetta.ecafe.processor.core.report.BalanceLeavingReportBuilder;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Akhmetov
 * Date: 16.02.16
 * Time: 15:13
 * To change this template use File | Settings | File Templates.
 */
public class BalanceLeavingReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(BalanceLeavingReportPage.class);
    private BalanceLeavingReport balanceLeavingReport;
    private String htmlReport;

    @Override
    public String getPageFilename() {
        return "report/online/balance_leaving_report";
    }

    public String exportToHtml() {
        buildReport(startDate, endDate);
        if (balanceLeavingReport == null) return null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JRHtmlExporter exporter = new JRHtmlExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, balanceLeavingReport.getPrint());
        exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
        exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
        exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
        exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
        exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
        exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
        try {
            exporter.exportReport();
            os.flush();
            htmlReport = os.toString("UTF-8");
            os.close();
            printMessage("Отчет построен");
        } catch (Exception ex) {
            printError("Ошибка при построении отчета: " + ex.getMessage());
            logger.error("Failed build report ", ex);
        }
        return null;
    }

    public void exportToXLS(ActionEvent actionEvent) {
        buildReport(startDate, endDate);
            try {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                ServletOutputStream servletOutputStream = response.getOutputStream();
                facesContext.responseComplete();
                response.setContentType("application/xls");
                String filename = "BalanceLeavingReport.xls";
                response.setHeader("Content-disposition", String.format("inline;filename=%s", filename));
                JRXlsExporter xlsExport = new JRXlsExporter();
                xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, balanceLeavingReport.getPrint());
                xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
                xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
                xlsExport.exportReport();
                servletOutputStream.flush();
                servletOutputStream.close();
            } catch (Exception e) {
                logger.error("Failed export report : ", e);
                printError("Ошибка при подготовке отчета: " + e.getMessage());
            }
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public BalanceLeavingReport getBalanceLeavingReport() {
        return balanceLeavingReport;
    }

    private void buildReport(Date fromDate, Date toDate) {
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            String reportTemplateFilePath = getReportsTemplateFilePath(runtimeContext);
            BalanceLeavingReportBuilder reportBuilder = new BalanceLeavingReportBuilder(reportTemplateFilePath);
            balanceLeavingReport = (BalanceLeavingReport)reportBuilder.build(persistenceSession, fromDate, toDate, null);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to build client balance report", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private String getReportsTemplateFilePath(RuntimeContext context) {
        return context.getAutoReportGenerator().getReportsTemplateFilePath();
    }

}
