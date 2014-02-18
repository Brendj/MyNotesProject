/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.msc.DiscrepanciesDataOnOrdersAndPaymentJasperReport;
import ru.axetta.ecafe.processor.core.report.msc.DiscrepanciesOnOrdersAndAttendanceJasperReport;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.events.orders.DiscrepanciesOnOrdersAndAttendanceBuilder;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.events.orders.DiscrepanciesOnOrdersAndAttendanceReport;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.payment.orders.DiscrepanciesDataOnOrdersAndPaymentBuilder;
import ru.axetta.ecafe.processor.core.report.statistics.payment.preferential.supply.StatisticsPaymentPreferentialSupplyBuilder;
import ru.axetta.ecafe.processor.core.report.statistics.payment.preferential.supply.StatisticsPaymentPreferentialSupplyReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountFilter;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.List;
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
            response.setHeader("Content-disposition", "inline;filename=DiscrepanciesOnOrdersAndAttendanceReport.xls");
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

    public void fill() {}

}
