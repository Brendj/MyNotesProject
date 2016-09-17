/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.enterevents.EnterEventsMonitoringReportBuilder;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class EnterEventsMonitoringReportPage extends OnlineReportPage {
    private final static Logger logger = LoggerFactory.getLogger(EnterEventsMonitoringReportPage.class);

    private String htmlReport = null;
    private Boolean applyUserSettings = false;
    private Boolean showElectionAreaOnly = true;
    private Boolean showStatus1 = true;
    private Boolean showStatus2 = true;
    private Boolean showStatus3 = true;
    private Boolean showStatus4 = true;
    private Boolean showStatus5 = true;
    private Boolean showLastSync1 = true;
    private Boolean showLastSync2 = true;
    private Boolean showLastSync3 = true;
    private Boolean showLastSync4 = true;
    private Boolean showLastEvent1 = true;
    private Boolean showLastEvent2 = true;
    private Boolean showLastEvent3 = true;
    private Boolean showLastEvent4 = true;
    private String UIKfilter;
    private String idOfOrgFilter;
    private String addressFilter;
    private String orgNameFilter;

    public EnterEventsMonitoringReportPage() {
        super();
    }

    public Object buildReportHTML() {
        htmlReport = null;
        EnterEventsMonitoringReportBuilder builder = new EnterEventsMonitoringReportBuilder();
        builder.setReportProperties(buildProperties());
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            logger.info("Starting to build EnterEventsMonitoringReport");
            report =  builder.build(persistenceSession, null, null, null);
            logger.info("EnterEventsMonitoringReport build finished");
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
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
                exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.FALSE);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();
                htmlReport = os.toString("UTF-8");
                os.close();
            } catch (Exception e) {
                printError("Ошибка при построении отчета: " + e.getMessage());
                logger.error("Failed build report ",e);
            }
        }
        return null;
    }

    public void exportToXLS(ActionEvent actionEvent){
        Date generateTime = new Date();
        EnterEventsMonitoringReportBuilder builder = new EnterEventsMonitoringReportBuilder();
        builder.setReportProperties(buildProperties());
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            logger.info("Starting to build EnterEventsMonitoringReport");
            report =  builder.build(persistenceSession, null, null, null);
            logger.info("EnterEventsMonitoringReport build finished");
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        if(report!=null){
            try {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

                ServletOutputStream servletOutputStream = response.getOutputStream();

                facesContext.responseComplete();
                response.setContentType("application/xls");
                String filename = buildFileName(generateTime, report);
                response.setHeader("Content-disposition", String.format("inline;filename=%s.xls", filename));

                JRXlsExporter xlsExport = new JRXlsExporter();
                xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, report.getPrint());
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
    }

    private String buildFileName(Date generateTime, BasicReportJob basicReportJob) {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        String format = timeFormat.format(generateTime);
        return String.format("%s-%s", "ElectionsMonitoring", format);
    }

    private Properties buildProperties() {
        Properties properties = new Properties();
        properties.setProperty("showElectionAreaOnly", Boolean.toString(showElectionAreaOnly));
        properties.setProperty("showStatus1", Boolean.toString(showStatus1));
        properties.setProperty("showStatus2", Boolean.toString(showStatus2));
        properties.setProperty("showStatus3", Boolean.toString(showStatus3));
        properties.setProperty("showStatus4", Boolean.toString(showStatus4));
        properties.setProperty("showStatus5", Boolean.toString(showStatus5));
        properties.setProperty("showLastSync1", Boolean.toString(showLastSync1));
        properties.setProperty("showLastSync2", Boolean.toString(showLastSync2));
        properties.setProperty("showLastSync3", Boolean.toString(showLastSync3));
        properties.setProperty("showLastSync4", Boolean.toString(showLastSync4));
        properties.setProperty("showLastEvent1", Boolean.toString(showLastEvent1));
        properties.setProperty("showLastEvent2", Boolean.toString(showLastEvent2));
        properties.setProperty("showLastEvent3", Boolean.toString(showLastEvent3));
        properties.setProperty("showLastEvent4", Boolean.toString(showLastEvent4));
        properties.setProperty("UIKfilter", UIKfilter);
        properties.setProperty("idOfOrgFilter", idOfOrgFilter);
        properties.setProperty("addressFilter", addressFilter);
        properties.setProperty("orgNameFilter", orgNameFilter);
        return properties;
    }

    public void clear() {
        UIKfilter = null;
        idOfOrgFilter = null;
        addressFilter = null;
        orgNameFilter = null;
        showStatus1 = true;
        showStatus2 = true;
        showStatus3 = true;
        showStatus4 = true;
        showStatus5 = true;
        showLastSync1 = true;
        showLastSync2 = true;
        showLastSync3 = true;
        showLastSync4 = true;
        showLastEvent1 = true;
        showLastEvent2 = true;
        showLastEvent3 = true;
        showLastEvent4 = true;

    }

    @Override
    public String getPageFilename() {
        return "report/online/enter_events_report";
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public Boolean getApplyUserSettings() {
        return applyUserSettings;
    }

    public void setApplyUserSettings(Boolean applyUserSettings) {
        this.applyUserSettings = applyUserSettings;
    }

    public Boolean getShowElectionAreaOnly() {
        return showElectionAreaOnly;
    }

    public void setShowElectionAreaOnly(Boolean showElectionAreaOnly) {
        this.showElectionAreaOnly = showElectionAreaOnly;
    }

    public String getUIKfilter() {
        return UIKfilter;
    }

    public void setUIKfilter(String UIKfilter) {
        this.UIKfilter = UIKfilter;
    }

    public String getIdOfOrgFilter() {
        return idOfOrgFilter;
    }

    public void setIdOfOrgFilter(String idOfOrgFilter) {
        this.idOfOrgFilter = idOfOrgFilter;
    }

    public String getAddressFilter() {
        return addressFilter;
    }

    public void setAddressFilter(String addressFilter) {
        this.addressFilter = addressFilter;
    }

    public String getOrgNameFilter() {
        return orgNameFilter;
    }

    public void setOrgNameFilter(String orgNameFilter) {
        this.orgNameFilter = orgNameFilter;
    }

    public Boolean getShowStatus1() {
        return showStatus1;
    }

    public void setShowStatus1(Boolean showStatus1) {
        this.showStatus1 = showStatus1;
    }

    public Boolean getShowStatus2() {
        return showStatus2;
    }

    public void setShowStatus2(Boolean showStatus2) {
        this.showStatus2 = showStatus2;
    }

    public Boolean getShowStatus3() {
        return showStatus3;
    }

    public void setShowStatus3(Boolean showStatus3) {
        this.showStatus3 = showStatus3;
    }

    public Boolean getShowStatus4() {
        return showStatus4;
    }

    public void setShowStatus4(Boolean showStatus4) {
        this.showStatus4 = showStatus4;
    }

    public Boolean getShowStatus5() {
        return showStatus5;
    }

    public void setShowStatus5(Boolean showStatus5) {
        this.showStatus5 = showStatus5;
    }

    public Boolean getShowLastSync1() {
        return showLastSync1;
    }

    public void setShowLastSync1(Boolean showLastSync1) {
        this.showLastSync1 = showLastSync1;
    }

    public Boolean getShowLastSync2() {
        return showLastSync2;
    }

    public void setShowLastSync2(Boolean showLastSync2) {
        this.showLastSync2 = showLastSync2;
    }

    public Boolean getShowLastSync3() {
        return showLastSync3;
    }

    public void setShowLastSync3(Boolean showLastSync3) {
        this.showLastSync3 = showLastSync3;
    }

    public Boolean getShowLastSync4() {
        return showLastSync4;
    }

    public void setShowLastSync4(Boolean showLastSync4) {
        this.showLastSync4 = showLastSync4;
    }

    public Boolean getShowLastEvent1() {
        return showLastEvent1;
    }

    public void setShowLastEvent1(Boolean showLastEvent1) {
        this.showLastEvent1 = showLastEvent1;
    }

    public Boolean getShowLastEvent2() {
        return showLastEvent2;
    }

    public void setShowLastEvent2(Boolean showLastEvent2) {
        this.showLastEvent2 = showLastEvent2;
    }

    public Boolean getShowLastEvent3() {
        return showLastEvent3;
    }

    public void setShowLastEvent3(Boolean showLastEvent3) {
        this.showLastEvent3 = showLastEvent3;
    }

    public Boolean getShowLastEvent4() {
        return showLastEvent4;
    }

    public void setShowLastEvent4(Boolean showLastEvent4) {
        this.showLastEvent4 = showLastEvent4;
    }
}
