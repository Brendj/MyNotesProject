/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.LatePaymentByOneDayCountType;
import ru.axetta.ecafe.processor.core.persistence.LatePaymentDaysCountType;
import ru.axetta.ecafe.processor.core.persistence.OrganizationTypeModify;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.financialControlReports.LatePaymentReport;
import ru.axetta.ecafe.processor.core.report.statistics.sfk.latepayment.LatePaymentReportBuilder;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.finansional.settings.LatePaymentByOneDayCountTypeMenu;
import ru.axetta.ecafe.processor.web.ui.finansional.settings.LatePaymentDaysCountTypeMenu;
import ru.axetta.ecafe.processor.web.ui.org.OrganizationTypeModifyMenu;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 26.08.15
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */
public class LatePaymentReportPage extends OnlineReportPage {

    private static final Logger logger = LoggerFactory.getLogger(LatePaymentReportPage.class);

    private String htmlReport = null;

    public String getHtmlReport() {
        return htmlReport;
    }

    private PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
    }

    public void onReportPeriodChanged() {
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

    public void onEndDateSpecified() {
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

    // тип организации
    private OrganizationTypeModify organizationTypeModify;
    private final OrganizationTypeModifyMenu organizationTypeModifyMenu = new OrganizationTypeModifyMenu();

    // количество несвоевременной оплаты за 1 день
    private LatePaymentByOneDayCountType latePaymentByOneDayCountType;
    private final LatePaymentByOneDayCountTypeMenu latePaymentByOneDayCountTypeMenu = new LatePaymentByOneDayCountTypeMenu();

    // количество дней несвоевременной оплаты
    private LatePaymentDaysCountType latePaymentDaysCountType;
    private final LatePaymentDaysCountTypeMenu latePaymentDaysCountTypeMenu = new LatePaymentDaysCountTypeMenu();

    public OrganizationTypeModify getOrganizationTypeModify() {
        return organizationTypeModify;
    }

    public void setOrganizationTypeModify(OrganizationTypeModify organizationTypeModify) {
        this.organizationTypeModify = organizationTypeModify;
    }

    public OrganizationTypeModifyMenu getOrganizationTypeModifyMenu() {
        return organizationTypeModifyMenu;
    }

    public LatePaymentByOneDayCountType getLatePaymentByOneDayCountType() {
        return latePaymentByOneDayCountType;
    }

    public void setLatePaymentByOneDayCountType(LatePaymentByOneDayCountType latePaymentByOneDayCountType) {
        this.latePaymentByOneDayCountType = latePaymentByOneDayCountType;
    }

    public LatePaymentByOneDayCountTypeMenu getLatePaymentByOneDayCountTypeMenu() {
        return latePaymentByOneDayCountTypeMenu;
    }

    public LatePaymentDaysCountType getLatePaymentDaysCountType() {
        return latePaymentDaysCountType;
    }

    public void setLatePaymentDaysCountType(LatePaymentDaysCountType latePaymentDaysCountType) {
        this.latePaymentDaysCountType = latePaymentDaysCountType;
    }

    public LatePaymentDaysCountTypeMenu getLatePaymentDaysCountTypeMenu() {
        return latePaymentDaysCountTypeMenu;
    }

    private LatePaymentReport report;

    public String getPageFilename() {
        return "report/online/late_payment_report";
    }

    public LatePaymentReport getReport() {
        return report;
    }

    public void fill() throws Exception {
    }

    private LatePaymentReport buildReport() {

        if (idOfOrg == null) {
            printError("Не указана организация");
            return null;
        }

        if (CalendarUtils.getDifferenceInDays(startDate, endDate) > 31) {
            printError("Интервал выборки для отчета не может превышать 31 день");
        }

        BasicReportJob report = null;
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + "LatePaymentReport.jasper";
        LatePaymentReportBuilder builder = new LatePaymentReportBuilder(templateFilename);
        builder.getReportProperties().setProperty(LatePaymentReport.LATE_PAYMENT_DAYS_COUNT_TYPE, latePaymentDaysCountType.toString());
        builder.getReportProperties().setProperty(LatePaymentReport.LATE_PAYMENT_BY_ONE_DAY_COUNT_TYPE, latePaymentByOneDayCountType.toString());
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();
            List<Long> org_ids = DAOUtils.findFriendlyOrgIds(session, idOfOrg);
            if (org_ids.size() == 0) org_ids.add(idOfOrg);
            String idOfOrgString = StringUtils.join(org_ids.iterator(), ",");
            builder.getReportProperties().setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOfOrgString);
            report = builder.build(session, startDate, endDate, localCalendar);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            getLogger().error("Filed build LatePaymentReport", e);
            printError("Ошибка при построении отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            HibernateUtils.close(session, getLogger());
        }
        return (LatePaymentReport) report;
    }

    // Генерировать отчет
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

    // Выгрузить в Excel
    public void generateXLS(ActionEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            BasicReportJob report = buildReport();
            if (report != null) {
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                ServletOutputStream servletOutputStream = response.getOutputStream();
                facesContext.responseComplete();
                response.setContentType("application/xls");
                response.setHeader("Content-disposition", "inline;filename=latePaymentReport.xls");
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
        periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);
        htmlReport = null;
        setLatePaymentByOneDayCountType(LatePaymentByOneDayCountType.EMPTY);
        setLatePaymentDaysCountType(LatePaymentDaysCountType.EMPTY);
        return null;
    }

    public Object showOrgListSelectPage() {
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }
}
