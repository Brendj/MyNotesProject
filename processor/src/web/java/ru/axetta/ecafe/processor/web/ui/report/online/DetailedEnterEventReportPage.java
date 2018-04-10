/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.DetailedEnterEventReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by anvarov on 06.04.18.
 */
public class DetailedEnterEventReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(DetailedEnterEventReportPage.class);

    private String htmlReport = null;

    public String getPageFilename() {
        return "report/online/detailed_enter_event";
    }

    private Boolean allFriendlyOrgs;

    public Object buildReportHTML() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile();
        if (templateFilename == null) {
            return null;
        }
        DetailedEnterEventReport.Builder builder = new DetailedEnterEventReport.Builder(templateFilename);

        if (idOfOrg == null) {
            printError(String.format("Выберите организацию "));
            return null;
        }
        builder.setIdOfOrg(idOfOrg);


        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                report = builder.build(persistenceSession, startDate, endDate, localCalendar);
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        }

        if (report != null) {
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
                printError("Ошибка при построении отчета: " + e.getMessage());
                logger.error("Failed build report ", e);
            }
        }
        return null;
    }

    public void generateXLS(ActionEvent event) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile();
        if (templateFilename == null) {
        } else {
            DetailedEnterEventReport.Builder builder = new DetailedEnterEventReport.Builder(templateFilename);
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            BasicReportJob report = null;
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                if (idOfOrg == null) {
                    printError(String.format("Выберите организацию "));
                }

                builder.setIdOfOrg(idOfOrg);
                report = builder.build(persistenceSession, startDate, endDate, localCalendar);
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception e) {
                logger.error("Failed export report : ", e);
                printError("Ошибка при подготовке отчета: " + e.getMessage());
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }

            FacesContext facesContext = FacesContext.getCurrentInstance();
            try {
                if (report != null) {
                    HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext()
                            .getResponse();
                    ServletOutputStream servletOutputStream = response.getOutputStream();
                    facesContext.getResponseComplete();
                    facesContext.responseComplete();
                    response.setContentType("application/xls");
                    response.setHeader("Content-disposition", "inline;filename=typesOfCardReport.xls");
                    JRXlsExporter xlsExporter = new JRXlsExporter();
                    xlsExporter.setParameter(JRCsvExporterParameter.JASPER_PRINT, report.getPrint());
                    xlsExporter.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
                    xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                    xlsExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                    xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                    xlsExporter.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
                    xlsExporter.exportReport();
                    servletOutputStream.close();
                }
            } catch (Exception e) {
                logAndPrintMessage("Ошибка при выгрузке отчета:", e);
            }
        }
    }

    private String checkIsExistFile() {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFilename = "AutoEnterEventV2Report.jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFilename;
        if (!(new File(templateFilename)).exists()) {
            printError(String.format("Не найден файл шаблона '%s'", templateFilename));
            return null;
        }
        return templateFilename;
    }

    public Boolean getAllFriendlyOrgs() {
        return allFriendlyOrgs;
    }

    public void setAllFriendlyOrgs(Boolean allFriendlyOrgs) {
        this.allFriendlyOrgs = allFriendlyOrgs;
    }
}
