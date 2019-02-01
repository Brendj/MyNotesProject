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

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
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
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final Integer FOR_ONE_DAY = 0;
    public static final Integer FOR_MONTH = 1;
    public static final int[] PARAM_HINTS = new int[]{3};
    public static final String[] TEMPLATE_FILE_NAMES = {
            "MonitoringOfReportForOneDay.jasper", "MonitoringOfReportForMonth.jasper", "MonitoringOfReport.jasper"
    };

    public static final String REPORT_NAME_FOR_MENU = "Мониторинг";

    final private static Logger logger = LoggerFactory.getLogger(MonitoringOfReport.class);

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private String templateFilename;
        private String subReportDir;
        private Integer selectedPeriod = FOR_ONE_DAY;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {

        }

        public BasicReportJob buildInternal(Date startTime, Date endTime, Calendar calendar) throws Exception {
            String reportsTemplateFilePath = RuntimeContext.getInstance().getAutoReportGenerator()
                    .getReportsTemplateFilePath();

            subReportDir = reportsTemplateFilePath;

            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("reportName", REPORT_NAME);
            parameterMap.put("SUBREPORT_DIR", subReportDir);

            templateFilename = subReportDir + TEMPLATE_FILE_NAMES[selectedPeriod];

            if(selectedPeriod.equals(FOR_ONE_DAY)) {
                parameterMap.put("reportDate", CalendarUtils.dateShortToStringFullYear(startTime));
            } else if(selectedPeriod.equals(FOR_MONTH)){
                String month = new SimpleDateFormat("MMMM", new Locale("ru")).format(startTime);
                parameterMap.put("currentMonth", month);
                parameterMap.put("startDate", CalendarUtils.dateShortToStringFullYear(startTime));
                parameterMap.put("endDate", CalendarUtils.dateShortToStringFullYear(endTime));
            }

            String idOfOrgs = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
            for (String idOfOrg : stringOrgList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }

            JRDataSource dataSource = createDataSource(startTime, endTime, idOfOrgList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            long generateDuration = generateEndTime.getTime() - generateTime.getTime();
            return new MonitoringOfReport(generateTime, generateDuration, jasperPrint, startTime, null);
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date date = CalendarUtils.addDays(CalendarUtils.startOfDay(new Date()), -1);//вызов из построения по расписанию
            int day = CalendarUtils.getDayOfWeek(date);
            if (day == 1 && selectedPeriod.equals(FOR_ONE_DAY)) {
                return null; //на Вск не генерируем
            }
            return buildInternal(date, endTime, calendar);
        }

        private JRDataSource createDataSource(Date startTime, Date endTime, List<Long> idOfOrgList)
                throws Exception {
            MonitoringOfReportService service = new MonitoringOfReportService();
            return new JRBeanCollectionDataSource(service.buildReportItems(startTime, endTime, idOfOrgList));
        }

        public Integer getSelectedPeriod() {
            return selectedPeriod;
        }

        public void setSelectedPeriod(Integer selectedPeriod) {
            this.selectedPeriod = selectedPeriod;
        }
    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public MonitoringOfReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    @Override
    protected void prepare() {
        if (!hasPrint() && templateFilename != null && sessionFactory != null) {
            templateFilename = AutoReportGenerator.restoreFilename(
                    RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath(), templateFilename);
            Builder builder = createBuilder(templateFilename);
            try {
                builder.setReportProperties(getReportProperties());
                BasicReportJob report = builder.build(null, startTime, endTime, calendar);
                setGenerateTime(report.getGenerateTime());
                setGenerateDuration(report.getGenerateDuration());
                setPrint(report.getPrint());
            } catch (Exception e) {
                getLogger().error(String.format("Failed at report lazy-build \"%s\"", MonitoringOfReport.class), e);
            }
        }
    }

    @Override
    public MonitoringOfReport createInstance() {
        return new MonitoringOfReport();
    }

    public MonitoringOfReport() {
        startTime = new Date();
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
