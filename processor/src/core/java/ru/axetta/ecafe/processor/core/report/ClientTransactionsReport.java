/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by anvarov on 09.06.2017.
 */
public class ClientTransactionsReport extends BasicReportForAllOrgJob {


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
    public static final String REPORT_NAME = "Транзакции клиента";
    public static final String[] TEMPLATE_FILE_NAMES = {"ClientTransactionsReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};

    public static final String REPORT_NAME_FOR_MENU = "Транзакции клиента";

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public static class Builder extends BasicReportJob.Builder {

        private String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            String reportTemplateFilePath = RuntimeContext.getInstance().getAutoReportGenerator()
                    .getReportsTemplateFilePath();

            templateFilename = reportTemplateFilePath + ClientTransactionsReport.class.getSimpleName() + ".jasper";

            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("reportName", REPORT_NAME);

            String idOfOrgString = StringUtils.trimToEmpty(reportProperties.getProperty("idOfOrgList"));
            String[] idOfOrgStringList = idOfOrgString.split(",");

            List<Long> idOfOrgList = new ArrayList<Long>();

            for (String id : idOfOrgStringList) {
                idOfOrgList.add(Long.valueOf(id));
            }

            String operationTypeString = null;

            int operationType = Integer
                    .parseInt(StringUtils.trimToEmpty(reportProperties.getProperty("operationType")));


            if (operationType == 0) {
                operationTypeString = "Все";
            } else if (operationType == 1) {
                operationTypeString = "Поподнение";
            } else if (operationType == 2) {
                operationTypeString = "Списание";
            }


            if (idOfOrgList.size() == 1) {
                Org org = (Org) session.load(Org.class, idOfOrgList.get(0));
                parameterMap.put("officialName", org.getOfficialName());
                parameterMap.put("address", org.getAddress());
                parameterMap.put("operationType", operationTypeString);
            }

            String clientListString = StringUtils.trimToEmpty(reportProperties.getProperty("clientList"));

            if (!clientListString.isEmpty() && clientListString != null) {

            }

            Boolean showAllBuildings = Boolean
                    .valueOf(StringUtils.trimToEmpty(reportProperties.getProperty("showAllBuildings")));

            if (showAllBuildings) {

            }

            JRDataSource dataSource = createDataSource(session, startTime, endTime, idOfOrgList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            long generateDuration = generateEndTime.getTime() - generateTime.getTime();
            return new ClientTransactionsReport(generateTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, List<Long> idOfOrgList)
                throws Exception {
            ClientTransactionsReportService service = new ClientTransactionsReportService();

            return new JRBeanCollectionDataSource(service.buildReportItems(session, startTime, endTime, idOfOrgList));
        }
    }

    public ClientTransactionsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    private static final Logger logger = LoggerFactory.getLogger(ClientTransactionsReport.class);

    public ClientTransactionsReport() {
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new ClientTransactionsReport();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_PREV_PREV_DAY;
    }
}
