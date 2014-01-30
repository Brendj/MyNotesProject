package ru.axetta.ecafe.processor.core.report.msc;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.report.BasicReportForContragentJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.payment.orders.DiscrepanciesDataOnOrdersAndPaymentBuilder;
import ru.axetta.ecafe.processor.core.report.statistics.payment.preferential.supply.StatisticsPaymentPreferentialSupplyBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 30.01.14
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public class DiscrepanciesDataOnOrdersAndPaymentJasperReport extends BasicReportForContragentJob{

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {}

    public DiscrepanciesDataOnOrdersAndPaymentJasperReport(Date generateTime, long generateDuration, JasperPrint print,
            Date startTime, Date endTime, Long idOfContragent) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfContragent);
    }

    @Override
    protected Integer getContragentSelectClass() {
        return Contragent.TSP;
    }

    public DiscrepanciesDataOnOrdersAndPaymentJasperReport() {}

    public DiscrepanciesDataOnOrdersAndPaymentJasperReport createInstance() {
        return new DiscrepanciesDataOnOrdersAndPaymentJasperReport();
    }

    @Override
    public BasicReportForContragentJob.Builder createBuilder(String templateFilename) {
        return new DiscrepanciesDataOnOrdersAndPaymentBuilder(templateFilename);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    public int getDefaultReportPeriod (){
        return REPORT_PERIOD_LAST_WEEK;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscrepanciesDataOnOrdersAndPaymentJasperReport.class);

}
