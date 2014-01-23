/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.msc;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.report.*;
import ru.axetta.ecafe.processor.core.report.statistics.payment.preferential.supply.StatisticsPaymentPreferentialSupplyBuilder;
import ru.axetta.ecafe.processor.core.report.statistics.payment.preferential.supply.StatisticsPaymentPreferentialSupplyReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.01.14
 * Time: 11:56
 * To change this template use File | Settings | File Templates.
 */
public class StatisticsPaymentPreferentialSupplyJasperReport extends BasicReportForContragentJob {

    public StatisticsPaymentPreferentialSupplyJasperReport(Date generateTime, long generateDuration, JasperPrint print,
            Date startTime, Date endTime, Long idOfContragent) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfContragent);
    }

    @Override
    protected Integer getContragentSelectClass() {
        return Contragent.TSP;
    }

    public StatisticsPaymentPreferentialSupplyJasperReport() {}

    public StatisticsPaymentPreferentialSupplyJasperReport createInstance() {
        return new StatisticsPaymentPreferentialSupplyJasperReport();
    }

    @Override
    public BasicReportForContragentJob.Builder createBuilder(String templateFilename) {
        return new StatisticsPaymentPreferentialSupplyBuilder(templateFilename);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    public int getDefaultReportPeriod (){
        return REPORT_PERIOD_LAST_WEEK;
    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {}

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsPaymentPreferentialSupplyJasperReport.class);
}
