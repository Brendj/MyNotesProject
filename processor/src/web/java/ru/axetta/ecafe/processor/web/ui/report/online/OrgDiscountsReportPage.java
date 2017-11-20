/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.statistics.discounts.OrgDiscountsBuilder;
import ru.axetta.ecafe.processor.core.report.statistics.discounts.OrgDiscountsReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage.CompleteHandler;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 11.02.12
 */
public class OrgDiscountsReportPage extends OnlineReportPage implements CompleteHandler {

    private String htmlReport;
    private Boolean showReserve = false;
    private Boolean showPayComplex = false;
    private Boolean showDSZN = false;
    private String orgFilter;

    public String getHtmlReport() {
        return htmlReport;
    }

    private OrgDiscountsReport orgDiscountsReport;

    public String getPageFilename() {
        return "report/online/org_discounts_report";
    }

    public OrgDiscountsReport getOrgDiscountsReport() {
        return orgDiscountsReport;
    }

    public OrgDiscountsReport buildReport() {
        if (idOfOrg == null) {
            printError("Не указана организация");
            return null;
        }
        BasicReportJob report = null;
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + "OrgDiscountsReport.jasper";
        OrgDiscountsBuilder builder = new OrgDiscountsBuilder(templateFilename);
        String idOfOrgString = String.valueOf(idOfOrg);
        builder.getReportProperties().setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOfOrgString);
        builder.getReportProperties().setProperty("showReserve", Boolean.toString(showReserve));
        builder.getReportProperties().setProperty("showPayComplex", Boolean.toString(showPayComplex));
        builder.getReportProperties().setProperty("orgFilter", orgFilter);
        builder.getReportProperties().setProperty("showDSZN", Boolean.toString(showDSZN));
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();
            report = builder.build(session, startDate, endDate, localCalendar);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            getLogger().error("Failed build OrgDiscountsReport", e);
            printError("Ошибка при построении отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            HibernateUtils.close(session, getLogger());
        }
        return (OrgDiscountsReport) report;
    }

    public Object buildReportHTML() {
        try {
            BasicReportJob report = buildReport();
            if (report != null) {
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
            }
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при построении отчета:", e);
        }
        return null;
    }

    public void generateXLS(ActionEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            BasicReportJob report = buildReport();
            if (report != null) {
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                ServletOutputStream servletOutputStream = response.getOutputStream();
                facesContext.responseComplete();
                response.setContentType("application/xls");
                response.setHeader("Content-disposition", "inline;filename=OrgDiscountsReport.xls");
                JRXlsExporter xlsExport = new JRXlsExporter();
                xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, report.getPrint());
                xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
                xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
                xlsExport.exportReport();
                servletOutputStream.close();
            }
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при выгрузке отчета:", e);
        }
    }

    public Properties addOrgFilterProperty(Properties props, String orgFilter) {
        if (props == null) {
            props = new Properties();
        }
        if (orgFilter != null && orgFilter.trim().length() > 0) {
            props.put("orgFilter", orgFilter);
        }
        return props;
    }

    public List<SelectItem> getOrgFilters() {
        List<SelectItem> filters = new ArrayList<SelectItem>();
        filters.add(new SelectItem(""));
        filters.add(new SelectItem("Все корпуса"));
        filters.add(new SelectItem("Корпуса СОШ"));
        filters.add(new SelectItem("Корпуса ДОУ"));
        return filters;
    }

    public Boolean getShowReserve() {
        return showReserve;
    }

    public void setShowReserve(Boolean showReserve) {
        this.showReserve = showReserve;
    }

    public String getOrgFilter() {
        return orgFilter;
    }

    public void setOrgFilter(String orgFilter) {
        this.orgFilter = orgFilter;
    }

    public Boolean getShowPayComplex() {
        return showPayComplex;
    }

    public void setShowPayComplex(Boolean showPayComplex) {
        this.showPayComplex = showPayComplex;
    }

    public Boolean getShowDSZN() {
        return showDSZN;
    }

    public void setShowDSZN(Boolean showDSZN) {
        this.showDSZN = showDSZN;
    }
}
