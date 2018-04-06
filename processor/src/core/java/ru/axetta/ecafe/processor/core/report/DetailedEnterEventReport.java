/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperPrint;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by anvarov on 06.04.18.
 */
public class DetailedEnterEventReport extends BasicReportForAllOrgJob {
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
    public static final String REPORT_NAME = "Детализированного отчета по проходам";
    public static final String[] TEMPLATE_FILE_NAMES = {".jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{-46, -47, -48};

    private final static Logger logger = LoggerFactory.getLogger(DetailedEnterEventReport.class);

    public DetailedEnterEventReport(Date generateTime, long generateDuration, JasperPrint jasperPrint, Date startTime) {
        super(generateTime, generateDuration, jasperPrint, startTime, null);
    }

    public DetailedEnterEventReport() {

    }



    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new DetailedEnterEventReport();
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
        private List<Long> orgsList;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            return null;
        }

        public void setOrgsList(List<Long> orgsList) {
            this.orgsList = orgsList;
        }
    }
}
