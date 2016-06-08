package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperPrint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 18.01.16
 * Time: 13:22
 * To change this template use File | Settings | File Templates.
 */
public class BalanceLeavingReport extends BasicReportForListOrgsJob {
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
    public static final String REPORT_NAME = "Отчет ухода баланса в минус/большой плюс";
    public static final String[] TEMPLATE_FILE_NAMES = {"BalanceLeavingReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{};

    /* Логгер для отчета BalanceLeavingReport */
    private static final Logger Logger = LoggerFactory.getLogger(BalanceLeavingReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public BalanceLeavingReport(Date generateTime, long generateDuration, JasperPrint print,
            Date startTime, Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public BalanceLeavingReport() {
    }

    @Override
    public BalanceLeavingReport createInstance() {
        return new BalanceLeavingReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new BalanceLeavingReportBuilder(templateFilename);
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_DAY;
    }

    @Override
    public Logger getLogger() {
        return Logger;
    }
}
