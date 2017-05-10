/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.utils.MigrantsUtils;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.MigrantsReport;
import ru.axetta.ecafe.processor.core.report.MonitoringOfReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.event.ActionEvent;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;


/**
 * Created by almaz anvarov on 03.05.2017.
 */
public class MonitoringOfReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(MonitoringOfReportPage.class);
    private final String reportName = MonitoringOfReport.REPORT_NAME;
    private final String reportNameForMenu = MonitoringOfReport.REPORT_NAME_FOR_MENU;

    private String htmlReport = null;

    private PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_DAY);

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
    }

    public MonitoringOfReportPage() throws RuntimeContext.NotInitializedException {
        super();
        localCalendar.setTime(this.startDate);
        localCalendar.add(Calendar.DATE, 1);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
    }

    public void showOrgListSelectPage() {
        MainPage.getSessionInstance().showOrgListSelectPage();
    }

    public void onReportPeriodChanged(javax.faces.event.ActionEvent event) {
        htmlReport = null;
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

    public void onEndDateSpecified(javax.faces.event.ActionEvent event) {
        htmlReport = null;
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

    public Object buildReportHTML() {
        htmlReport = null;
        return null;
    }

    public void exportToXLS(ActionEvent actionEvent) {

    }

    private String checkIsExistFile(String suffix) {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFileName = MonitoringOfReport.class.getSimpleName() + suffix;
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if (!(new File(templateFilename)).exists()) {
            printError(String.format("Не найден файл шаблона '%s'", templateShortFileName));
            return null;
        }
        return templateFilename;
    }

    private Properties buildProperties() {
        Properties properties = new Properties();
        String idOfOrgString = "";
        if(idOfOrgList != null) {
            idOfOrgString = StringUtils.join(idOfOrgList.iterator(), ",");
        }
        properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOfOrgString);
        return properties;
    }

    private String buildFileName(Date generateTime, BasicReportJob basicReportJob) {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        String reportDistinctText = basicReportJob.getReportDistinctText();
        String format = timeFormat.format(generateTime);
        return String.format("%s-%s-%s", "MonitoringOfReport", reportDistinctText, format);
    }

    @Override
    public String getPageFilename() {
        return "report/online/monitoring_of_report";
    }

    public String getReportName() {
        return reportName;
    }

    public String getReportNameForMenu() {
        return reportNameForMenu;
    }

    public String getHtmlReport() {
        return htmlReport;
    }
}
