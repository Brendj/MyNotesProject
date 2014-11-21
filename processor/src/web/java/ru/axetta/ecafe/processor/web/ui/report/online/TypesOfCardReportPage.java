/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.TypesOfCardReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.client.ClientListPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 03.11.14
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class TypesOfCardReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(TypesOfCardReportPage.class);

    private TypesOfCardReport report;

    private final ClientListPage clientListPage = new ClientListPage();

    private final boolean includeSummaryByDistrict = false;

    private String htmlReport = null;

    public String getPageFilename() {
        return "report/online/types_of_card_report";
    }

    public TypesOfCardReport getReport() {
        return report;
    }

    public Object buildReportHTML() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFileName = checkIsExistFile();
        if (StringUtils.isEmpty(templateFileName)) {
            return null;
        }
        String subReportDir =  RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
        TypesOfCardReport.Builder builder = new TypesOfCardReport.Builder(templateFileName, subReportDir);
        builder.setReportProperties(buildProperties());
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                report = builder.build(persistenceSession, startDate, localCalendar);
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
                printError("Ошибка при построении отчета: "+e.getMessage());
                logger.error("Failed build report ",e);
            }
        }
        return null;
    }

    public void generateXLS(ActionEvent event) {

        try {
            TypesOfCardReport report = buildReport();
            if (report != null) {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                ServletOutputStream servletOutputStream = response.getOutputStream();
                facesContext.getResponseComplete();
                facesContext.responseComplete();
                response.setContentType("application/xls");
                response.setHeader("Content-disposition", "inline;filename=typesOfCardReport.xls");
            }
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при выгрузке отчета:", e);
        }
    }

    private TypesOfCardReport buildReport() {
        return null;
    }


    public ClientListPage getClientListPage() {
        return clientListPage;
    }

    public Date getStartDate() {
        return new Date();
    }

    public boolean isIncludeSummaryByDistrict() {
        return includeSummaryByDistrict;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public void onShow() throws Exception {
    }

    private String checkIsExistFile() {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFileName = TypesOfCardReport.class.getSimpleName() + ".jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if(!(new File(templateFilename)).exists()){
            printError(String.format("Не найден файл шаблона '%s'", templateShortFileName));
            return null;
        }
        return templateFilename;
    }

    private Properties buildProperties() {
        Properties properties = new Properties();
        return properties;
    }
}
