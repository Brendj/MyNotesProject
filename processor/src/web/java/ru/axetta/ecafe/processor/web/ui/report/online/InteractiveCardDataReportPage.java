/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;


import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.InteractiveCardDataReport;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.util.GregorianCalendar;


/**
 * Created with IntelliJ IDEA.
 * User: Anvarov
 * Date: 25.03.16
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class InteractiveCardDataReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(InteractiveCardDataReportPage.class);
    private InteractiveCardDataReport report;

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    public String getPageFilename() {
        return "report/online/interactive_card_data_report";
    }

    public InteractiveCardDataReport getReport() {
        return report;
    }

    public void doGenerate() {
        if(idOfOrg == null){
            printError("Выберите организацию");
            return;
        }
        RuntimeContext.getAppContext().getBean(InteractiveCardDataReportPage.class).generate();
    }

    public void doGenerateXLS(ActionEvent actionEvent) {
        if(idOfOrg == null){
            printError("Выберите организацию");
            return;
        }
        RuntimeContext.getAppContext().getBean(InteractiveCardDataReportPage.class).generateXLS();
    }

    @Transactional
    public void generate() {
        Session session;
        try {
            session = (Session) entityManager.getDelegate();
            AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
            String templateFilename =
                    autoReportGenerator.getReportsTemplateFilePath() + InteractiveCardDataReport.class.getSimpleName()
                            + ".jasper";
            generateReport(session, templateFilename);
        } catch (Exception e) {
            logger.error("Failed to load clients data", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    @Transactional
    public void generateXLS() {
        Session session;
        try {
            session = (Session) entityManager.getDelegate();
            generateXLS(session);
        } catch (Exception e) {
            logger.error("Failed to load clients data", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void generateReport(Session session, String templateFile) throws Exception {
        InteractiveCardDataReport.Builder reportBuilder;
        if (templateFile != null) {

            reportBuilder = new InteractiveCardDataReport.Builder(templateFile);

            if (idOfOrg == null) {
                printError("Не указана организация");
            }
            String idOfOrgString = String.valueOf(idOfOrg);
            reportBuilder.getReportProperties().setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOfOrgString);

        } else {
            throw new FileNotFoundException("Файл шаблона не найден: " + templateFile);
        }
        this.report = reportBuilder.build(session, startDate, endDate, new GregorianCalendar());
    }

    public void generateXLS(Session session) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
            String templateFilename =
                    autoReportGenerator.getReportsTemplateFilePath() + InteractiveCardDataReport.class.getSimpleName()
                            + ".jasper";
            generateReport(session, templateFilename);

            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            ServletOutputStream servletOutputStream = response.getOutputStream();

            facesContext.responseComplete();
            response.setContentType("application/xls");
            response.setHeader("Content-disposition", "inline;filename=interactive_report.xls");

            JRXlsExporter xlsExport = new JRXlsExporter();
            xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, this.report.getPrint());
            xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
            xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
            xlsExport.exportReport();

            servletOutputStream.flush();
            servletOutputStream.close();

        } catch (JRException fnfe) {
            String message = (fnfe.getCause() == null ? fnfe.getMessage() : fnfe.getCause().getMessage());
            logAndPrintMessage(String.format("Ошибка при подготовке отчета не найден файл шаблона: %s", message), fnfe);
        } catch (Exception e) {
            getLogger().error("Failed to build transactions report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", e.getMessage()));
        } finally {
        }
    }

}
