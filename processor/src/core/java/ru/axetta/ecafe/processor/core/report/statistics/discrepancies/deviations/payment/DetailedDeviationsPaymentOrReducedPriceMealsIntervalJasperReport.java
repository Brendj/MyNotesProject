package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 02.10.14
 * Time: 16:41
 */

public class DetailedDeviationsPaymentOrReducedPriceMealsIntervalJasperReport extends BasicReportForAllOrgJob {

    /*Логгер для отчета DetailedDeviationsPaymentOrReducedPriceMealsJasperReport*/
    private static final Logger logger = LoggerFactory
            .getLogger(DetailedDeviationsPaymentOrReducedPriceMealsIntervalJasperReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public DetailedDeviationsPaymentOrReducedPriceMealsIntervalJasperReport(Date generateTime, long generateDuration,
            JasperPrint print, Date startTime, Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public DetailedDeviationsPaymentOrReducedPriceMealsIntervalJasperReport() {
    }

    public DetailedDeviationsPaymentOrReducedPriceMealsIntervalJasperReport createInstance() {
        return new DetailedDeviationsPaymentOrReducedPriceMealsIntervalJasperReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new DetailedDeviationsPaymentOrReducedPriceMealsBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
