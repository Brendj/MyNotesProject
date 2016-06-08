/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.without.corps;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportForContragentJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 03.03.15
 * Time: 12:07
 */

public class DetailedDeviationsWithoutCorpsNewJasperReport extends BasicReportForAllOrgJob {
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
    public static final String REPORT_NAME = "Детализированный отчет отклонений оплаты льготного питания на дату";
    public static final String[] TEMPLATE_FILE_NAMES = {"DetailedDeviationsWithoutCorpsNewJasperReport.jasper",
                                                        "DetailedDeviationsWithoutCorpsNewJasperReport_Subreport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{0,3};


    /* Логгер для отчета  DetailedDeviationsWithoutCorpsNewJasperReport*/
    private static final Logger logger = LoggerFactory.getLogger(DetailedDeviationsWithoutCorpsNewJasperReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public DetailedDeviationsWithoutCorpsNewJasperReport(Date generateTime, long generateDuration, JasperPrint print,
            Date startTime, Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public DetailedDeviationsWithoutCorpsNewJasperReport() {
    }

    @Override
    public DetailedDeviationsWithoutCorpsNewJasperReport createInstance() {
        return new DetailedDeviationsWithoutCorpsNewJasperReport();
    }

    @Override
    public BasicReportForContragentJob.Builder createBuilder(String templateFilename) {
        return new DetailedDeviationsWithoutCorpsNewBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}