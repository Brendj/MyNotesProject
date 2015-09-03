/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.LatePaymentByOneDayCountType;
import ru.axetta.ecafe.processor.core.persistence.LatePaymentDaysCountType;
import ru.axetta.ecafe.processor.core.persistence.OrganizationTypeModify;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.financialControlReports.LatePaymentReport;
import ru.axetta.ecafe.processor.core.report.statistics.sfk.LatePaymentReportBuilder;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.finansional.settings.LatePaymentByOneDayCountTypeMenu;
import ru.axetta.ecafe.processor.web.ui.finansional.settings.LatePaymentDaysCountTypeMenu;
import ru.axetta.ecafe.processor.web.ui.org.OrganizationTypeModifyMenu;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 26.08.15
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */
public class LatePaymentReportPage extends OnlineReportPage {

    private static final Logger logger = LoggerFactory.getLogger(LatePaymentReportPage.class);

    private String htmlReport;

    public String getHtmlReport() {
        return htmlReport;
    }

    private PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
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

    private LatePaymentReport buildReport() {
        if (idOfOrgList.size() < 0 && idOfOrgList != null) {
            printError("Не выбраны организации");
            return null;
        }
        BasicReportJob report = null;
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath()
                + "LatePaymentReport.jasper";
        LatePaymentReportBuilder builder = new LatePaymentReportBuilder(templateFilename);
        //builder.getReportProperties().setProperty(ReportPropertiesUtils.P_ID_LIST_OF_ORG, idOfOrgList);
        Session session = null;
        Transaction persistenceTransaction = null;
        try {

        } catch (Exception e) {
            getLogger().error("Filed build LatePaymentReport", e);
            printError("Ошибка при построении отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            HibernateUtils.close(session, getLogger());
        }
        return (LatePaymentReport) report;
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

    // Генерировать отчет
    public Object buildReportHTML() {

        Session session = null;
        Transaction persistenceTransaction = null;
        try {
//            BasicReportJob report = buildReport();
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();

//            LatePaymentReportService.getCountOfBeneficiariesByOrg(session, 99L);

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            getLogger().error("Filed build DetailedPaymentWithoutCorpsJasperReport", e);
            printError("Ошибка при построении отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            HibernateUtils.close(session, getLogger());
        }

        return null;
    }

    // Выгрузить в Excel
    public void generateXLS(ActionEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
    }

    // Очистить
    public Object clear(){
        return null;
    }

}
