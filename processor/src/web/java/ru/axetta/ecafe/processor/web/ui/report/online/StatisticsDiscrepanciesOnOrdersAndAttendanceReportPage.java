/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.events.orders.DiscrepanciesOnOrdersAndAttendanceBuilder;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.events.orders.DiscrepanciesOnOrdersAndAttendanceReport;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 20.12.11
 * Time: 12:56
 * To change this template use File | Settings | File Templates.
 */
public class StatisticsDiscrepanciesOnOrdersAndAttendanceReportPage extends OnlineReportWithContragentPage{

    private DiscrepanciesOnOrdersAndAttendanceReport report;

    public String getPageFilename() {
        return "report/online/statistics_discrepancies_on_orders_and_attendance_report";
    }

    public DiscrepanciesOnOrdersAndAttendanceReport getReport() {
        return report;
    }

    public void buildReport(Session session) throws Exception{
        if(idOfContragentOrgList==null || idOfContragentOrgList.isEmpty()){
            throw new Exception("Выберите список поставщиков");
        }
        DiscrepanciesOnOrdersAndAttendanceBuilder builder = new DiscrepanciesOnOrdersAndAttendanceBuilder();
        this.report = builder.build(session, idOfContragentOrgList, idOfOrgList, localCalendar, startDate, endDate);
    }

    public void export(Session session) throws Exception{
        if(idOfContragentOrgList==null || idOfContragentOrgList.isEmpty()){
            throw new Exception("Выберите список поставщиков");
        }
        Date generateTime = new Date();
        DiscrepanciesOnOrdersAndAttendanceBuilder builder = new DiscrepanciesOnOrdersAndAttendanceBuilder();
        builder.setReportProperties(new Properties());
        String sourceMenuOrgId = StringUtils.join(idOfContragentOrgList.iterator(), ",");
        builder.getReportProperties().setProperty("idOfMenuSourceOrg", sourceMenuOrgId);
        String idOfOrgString = StringUtils.join(idOfOrgList.iterator(), ",");
        builder.getReportProperties().setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOfOrgString);
        BasicReportJob report = builder.build(session, startDate, endDate, localCalendar);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (report != null) {
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            ServletOutputStream servletOutputStream = response.getOutputStream();
            facesContext.responseComplete();
            response.setContentType("application/xls");
            String filename = buildFileName(generateTime, report);
            response.setHeader("Content-disposition", String.format("inline;filename=%s.xls", filename));
            //response.setHeader("Content-disposition", "inline;filename=DiscrepanciesOnOrdersAndAttendanceReport.xls");
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
    }

    public void fill() throws Exception{}

    private String buildFileName(Date generateTime, BasicReportJob report) {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        String reportDistinctText = report.getReportDistinctText();
        String format = timeFormat.format(generateTime);
        return String.format("%s-%s-%s", "DiscrepanciesOnOrdersAndAttendanceReport", reportDistinctText, format);
    }
}
