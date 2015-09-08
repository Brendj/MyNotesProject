/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.persistence.OrganizationTypeModify;
import ru.axetta.ecafe.processor.core.report.financialControlReports.LatePaymentDetailedReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.org.OrganizationTypeModifyMenu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.event.ActionEvent;
import java.util.Collections;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 07.09.15
 * Time: 11:25
 */

public class LatePaymentDetailedReportPage extends OnlineReportPage {
    private static final Logger logger = LoggerFactory.getLogger(LatePaymentDetailedReportPage.class);

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

    // тип организации
    private OrganizationTypeModify organizationTypeModify;
    private final OrganizationTypeModifyMenu organizationTypeModifyMenu = new OrganizationTypeModifyMenu();



    private LatePaymentDetailedReport report;

    public String getPageFilename() {
        return "report/online/late_payment_detailed_report";
    }

    public LatePaymentDetailedReport getReport() {
        return report;
    }

    public void fill() throws Exception {
    }

    // Генерировать отчет
    public Object buildReportHTML() {
        return null;
    }

    // Выгрузить в Excel
    public void generateXLS(ActionEvent event) {

    }

    // Очистить
    public Object clear() {
        idOfOrgList = Collections.EMPTY_LIST;
        filter = "Не выбрано";
        periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);
        showReserve = false;
        htmlReport = null;
        return null;
    }

    public Object showOrgListSelectPage() {
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }

}
