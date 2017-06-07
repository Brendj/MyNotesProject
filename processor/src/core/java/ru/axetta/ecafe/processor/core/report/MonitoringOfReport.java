/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by almaz anvarov on 04.05.2017.
 */
public class MonitoringOfReport extends BasicReportForListOrgsJob {

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
    public static final String REPORT_NAME = "Мониторинг";
    public static final String[] TEMPLATE_FILE_NAMES = {
            "MonitoringOfReportMonday.jasper", "MonitoringOfReportTuesday.jasper", "MonitoringOfReportWednesday.jasper",
            "MonitoringOfReportThursday.jasper", "MonitoringOfReportFriday.jasper", "MonitoringOfReportSaturday.jasper", "MonitoringOfSubReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};

    public static final String REPORT_NAME_FOR_MENU = "Мониторинг";

    final private static Logger logger = LoggerFactory.getLogger(MonitoringOfReport.class);

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private String templateFilename;
        private String subReportDir;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {

        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            String reportsTemplateFilePath = RuntimeContext.getInstance().getAutoReportGenerator()
                    .getReportsTemplateFilePath();

            subReportDir = reportsTemplateFilePath;

            int dayOfWeek = CalendarUtils.getDayOfWeek(startTime);

            Date dateMonday = new Date();
            Date dateTuesday = new Date();
            Date dateWednesday = new Date();
            Date dateThursday = new Date();
            Date dateFriday = new Date();
            Date dateSaturday = new Date();

            if (dayOfWeek == 2) {
                templateFilename = reportsTemplateFilePath + "MonitoringOfReportMonday" + ".jasper";
                dateMonday = startTime;
            } else if (dayOfWeek == 3) {
                templateFilename = reportsTemplateFilePath + "MonitoringOfReportTuesday" + ".jasper";
                dateMonday = CalendarUtils.addDays(startTime, -1);
                dateTuesday = startTime;
            } else if (dayOfWeek == 4) {
                templateFilename = reportsTemplateFilePath + "MonitoringOfReportWednesday" + ".jasper";
                dateMonday = CalendarUtils.addDays(startTime, -2);
                dateTuesday = CalendarUtils.addDays(startTime, -1);
                dateWednesday = startTime;
            } else if (dayOfWeek == 5) {
                templateFilename = reportsTemplateFilePath + "MonitoringOfReportThursday" + ".jasper";
                dateMonday = CalendarUtils.addDays(startTime, -3);
                dateTuesday = CalendarUtils.addDays(startTime, -2);
                dateWednesday = CalendarUtils.addDays(startTime, -1);
                dateThursday = startTime;
            } else if (dayOfWeek == 6) {
                templateFilename = reportsTemplateFilePath + "MonitoringOfReportFriday" + ".jasper";
                dateMonday = CalendarUtils.addDays(startTime, -4);
                dateTuesday = CalendarUtils.addDays(startTime, -3);
                dateWednesday = CalendarUtils.addDays(startTime, -2);
                dateThursday = CalendarUtils.addDays(startTime, -1);
                dateFriday = startTime;
            } else if (dayOfWeek == 7) {
                templateFilename = reportsTemplateFilePath + "MonitoringOfReportSaturday" + ".jasper";
                dateMonday = CalendarUtils.addDays(startTime, -5);
                dateTuesday = CalendarUtils.addDays(startTime, -4);
                dateWednesday = CalendarUtils.addDays(startTime, -3);
                dateThursday = CalendarUtils.addDays(startTime, -2);
                dateFriday = CalendarUtils.addDays(startTime, -1);
                dateSaturday = startTime;
            }

            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("dateMonday", CalendarUtils.dateShortToStringFullYear(dateMonday));
            parameterMap.put("dateTuesday", CalendarUtils.dateShortToStringFullYear(dateTuesday));
            parameterMap.put("dateWednesday", CalendarUtils.dateShortToStringFullYear(dateWednesday));
            parameterMap.put("dateThursday", CalendarUtils.dateShortToStringFullYear(dateThursday));
            parameterMap.put("dateFriday", CalendarUtils.dateShortToStringFullYear(dateFriday));
            parameterMap.put("dateSaturday", CalendarUtils.dateShortToStringFullYear(dateSaturday));
            parameterMap.put("reportName", REPORT_NAME);
            parameterMap.put("SUBREPORT_DIR", subReportDir);

            String idOfOrgs = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
            for (String idOfOrg : stringOrgList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }

            JRDataSource dataSource = createDataSource(session, startTime, idOfOrgList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            long generateDuration = generateEndTime.getTime() - generateTime.getTime();
            return new MonitoringOfReport(generateTime, generateDuration, jasperPrint, startTime, null);
        }

        private JRDataSource createDataSource(Session session, Date startTime, List<Long> idOfOrgList)
                throws Exception {

            MonitoringOfReportService service = new MonitoringOfReportService();

            return new JRBeanCollectionDataSource(service.buildReportItems(session, startTime, idOfOrgList));
        }
    }

    public MonitoringOfReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new MonitoringOfReport();
    }

    public MonitoringOfReport() {
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
