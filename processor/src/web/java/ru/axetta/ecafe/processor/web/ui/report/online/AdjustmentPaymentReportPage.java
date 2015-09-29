/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.financialControlReports.AdjustmentPaymentReport;
import ru.axetta.ecafe.processor.core.report.statistics.sfk.adjustmentpayment.AdjustmentPaymentReportBuilder;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 28.09.15
 * Time: 11:15
 */

public class AdjustmentPaymentReportPage extends OnlineReportPage {

    private static final Logger logger = LoggerFactory.getLogger(AdjustmentPaymentReportPage.class);

    private Boolean showReserve = false;

    public Boolean getShowReserve() {
        return showReserve;
    }

    public void setShowReserve(Boolean showReserve) {
        this.showReserve = showReserve;
    }

    private String htmlReport = null;

    public String getHtmlReport() {
        return htmlReport;
    }

    public void setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
    }

    private PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK);

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
    }

    public AdjustmentPaymentReportPage() throws RuntimeContext.NotInitializedException {
        super();
        localCalendar.setTime(this.startDate);
        localCalendar.add(Calendar.DATE, 7);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
    }

    public void onReportPeriodChanged(ActionEvent event) {
        switch (periodTypeMenu.getPeriodType()) {
            case ONE_DAY: {
                setEndDate(startDate);
            }
            break;
            case ONE_WEEK: {
                setEndDate(CalendarUtils.addDays(startDate, 6));
            }
            break;
            case TWO_WEEK: {
                setEndDate(CalendarUtils.addDays(startDate, 13));
            }
            break;
            case ONE_MONTH: {
                setEndDate(CalendarUtils.addDays(CalendarUtils.addMonth(startDate, 1), -1));
            }
            break;
        }
    }

    public void onEndDateSpecified(ActionEvent event) {
        Date end = CalendarUtils.truncateToDayOfMonth(endDate);
        if (CalendarUtils.addMonth(CalendarUtils.addOneDay(end), -1).equals(startDate)) {
            periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);
        } else {
            long diff = end.getTime() - startDate.getTime();
            int noOfDays = (int) (diff / (24 * 60 * 60 * 1000));
            switch (noOfDays) {
                case 0:
                    periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_DAY);
                    break;
                case 6:
                    periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK);
                    break;
                case 13:
                    periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.TWO_WEEK);
                    break;
                default:
                    periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.FIXED_DAY);
                    break;
            }
        }
        if (startDate.after(endDate)) {
            printError("Дата выборки от меньше дата выборки до");
        }
    }

    private AdjustmentPaymentReport report;

    public String getPageFilename() {
        return "report/online/adjustment_payment_report";
    }

    public AdjustmentPaymentReport getReport() {
        return report;
    }

    public void fill() throws Exception {
    }

    private AdjustmentPaymentReport buildReport() {
        if (idOfOrg == null) {
            printError("Не указана организация");
            return null;
        }
        BasicReportForAllOrgJob report = null;
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + "AdjustmentPaymentReport.jasper";
        AdjustmentPaymentReportBuilder builder = new AdjustmentPaymentReportBuilder(templateFilename);
        String idOfOrgString = String.valueOf(idOfOrg);
        builder.getReportProperties().setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOfOrgString);
        builder.getReportProperties().setProperty("showReserve", showReserve.toString());
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = session.beginTransaction();
            report = builder.build(session, startDate, endDate, localCalendar);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            getLogger().error("Filed build AdjustmentPaymentJasperReport", e);
            printError("Ошибка при построении отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            HibernateUtils.close(session, getLogger());
        }

        return (AdjustmentPaymentReport) report;
    }

    // Генерировать отчет
    public Object buildReportHTML() {
        try {
            BasicReportForAllOrgJob report = buildReport();
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

    // Выгрузить в Excel
    public void generateXLS(ActionEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            BasicReportForAllOrgJob report = buildReport();
            if (report != null) {
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                ServletOutputStream servletOutputStream = response.getOutputStream();
                facesContext.responseComplete();
                response.setContentType("application/xls");
                response.setHeader("Content-disposition", "inline;filename=adjustmentPaymentReport.xls");
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

    // Очистить
    public Object clear() {
        idOfOrgList = Collections.EMPTY_LIST;
        filter = "Не выбрано";
        periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK);
        showReserve = false;
        htmlReport = null;
        return null;
    }

    public Object showOrgListSelectPage() {
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }

}
