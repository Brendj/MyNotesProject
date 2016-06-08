/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 28.10.14
 * Time: 13:23
 * To change this template use File | Settings | File Templates.
 */
public class PaymentTotalsReport extends BasicReportForAllOrgJob {
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
    public static final String REPORT_NAME = "Отчет по итоговым показателям";
    public static final String[] TEMPLATE_FILE_NAMES = {"PaymentTotalsReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{20, 3, 35};


    final public static String P_CONTRAGENT = "contragent";
    final public static String P_ORG_LIST = "orgList";
    final public static String P_HIDE_NULL_ROWS = "hideNullRows";
    final private static Logger logger = LoggerFactory.getLogger(PaymentTotalsReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {}

    public PaymentTotalsReport() {}

    public PaymentTotalsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new PaymentTotalsReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            String reportsTemplateFilePath = RuntimeContext.getInstance().getAutoReportGenerator()
                    .getReportsTemplateFilePath();
            templateFilename = reportsTemplateFilePath + PaymentTotalsReport.class.getSimpleName() + ".jasper";
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {

            Date generateTime = new Date();

            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            JRDataSource dataSource = createDataSource(session, startTime, endTime);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

            Date generateEndTime = new Date();

            long generateDuration = generateEndTime.getTime() - generateTime.getTime();

            return new PaymentTotalsReport(generateTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime) throws Exception {

            String idOfContragentString = reportProperties.getProperty(P_CONTRAGENT);
            Long idOfContragent = idOfContragentString == "" ? null : Long.parseLong(idOfContragentString);

            String idOfOrgsString = StringUtils.trimToEmpty(reportProperties.getProperty(P_ORG_LIST));
            List<Long> idOfOrgList = new ArrayList<Long>();
            if (idOfOrgsString.length() > 0) {
                List<String> idOfOrgListString = Arrays.asList(StringUtils.split(idOfOrgsString, ','));
                for (String idOfOrg : idOfOrgListString)
                    if (!idOfOrg.equals(""))
                        idOfOrgList.add(Long.parseLong(idOfOrg));
            }

            boolean hideNullRows = Boolean.parseBoolean(reportProperties.getProperty(P_HIDE_NULL_ROWS, "false"));

            PaymentTotalsReportService service = new PaymentTotalsReportService(session);


            List<PaymentTotalsReportService.Item> reportItems = service.buildReportItems(idOfContragent, idOfOrgList,
                    startTime, endTime, hideNullRows);

            return new JRBeanCollectionDataSource(reportItems);
        }
    }
}
