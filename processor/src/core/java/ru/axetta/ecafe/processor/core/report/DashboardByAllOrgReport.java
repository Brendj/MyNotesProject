/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.dashboard.DashboardServiceBean;
import ru.axetta.ecafe.processor.dashboard.data.DashboardResponse;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.09.12
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */

public class DashboardByAllOrgReport extends BasicReportForAllOrgJob {
    /*
    * Параметры отчета для добавления в правила и шаблоны
    *
    * При создании любого отчета необходимо добавить параметры:
    * REPORT_NAME - название отчета на русском
    * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
    * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
    * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
    * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
    *
    * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
    */
    public static final String REPORT_NAME = "Мониторинг активности";
    public static final String[] TEMPLATE_FILE_NAMES = {"DashboardByAllOrgReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{39};


    final public static String P_ORG_STATE = "orgState";
    final private static Logger logger = LoggerFactory.getLogger(DashboardByAllOrgReport.class);

    public class AutoReportBuildJob extends BasicReportForAllOrgJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder{

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {

            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", new Date(endTime.getTime()+1000));

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new DashboardByAllOrgReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {

            int orgFilter = Integer.parseInt(reportProperties.getProperty(P_ORG_STATE, "1"));

            DashboardServiceBean dashboardService = RuntimeContext.getAppContext().getBean(DashboardServiceBean.class);
            //2 parameter startTime, endTime was endTime
            DashboardResponse.OrgBasicStats orgBasicStats = dashboardService.getOrgBasicStats(startTime, endTime, null, orgFilter);
            return new JRBeanCollectionDataSource(orgBasicStats.getOrgBasicStatItems());
        }

    }

    public DashboardByAllOrgReport() {}

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new DashboardByAllOrgReport();
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public DashboardByAllOrgReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_TODAY;
    }

}
