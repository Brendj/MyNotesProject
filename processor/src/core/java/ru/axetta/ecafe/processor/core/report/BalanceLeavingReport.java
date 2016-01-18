package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperPrint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: T800
 * Date: 18.01.16
 * Time: 13:22
 * To change this template use File | Settings | File Templates.
 */
public class BalanceLeavingReport extends BasicReportForContragentJob {

    /* Логгер для отчета BalanceLeavingReport */
    private static final Logger Logger = LoggerFactory.getLogger(BalanceLeavingReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public BalanceLeavingReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfContragent) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfContragent);
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
    public Logger getLogger() {
        return Logger;
    }
}
