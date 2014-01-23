/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.msc;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.report.BasicReportForContragentJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.statistics.telephone.number.TelephoneNumberCountBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.01.14
 * Time: 11:56
 * To change this template use File | Settings | File Templates.
 */
public class TelephoneNumberCountJasperReport extends BasicReportForContragentJob {

    public TelephoneNumberCountJasperReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfContragent) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfContragent);
    }

    @Override
    protected Integer getContragentSelectClass() {
        return Contragent.TSP;
    }


    public TelephoneNumberCountJasperReport() {}

    public TelephoneNumberCountJasperReport createInstance() {
        return new TelephoneNumberCountJasperReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new TelephoneNumberCountBuilder(templateFilename);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    public int getDefaultReportPeriod (){
        return REPORT_PERIOD_TODAY;
    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {}

    private static final Logger LOGGER = LoggerFactory.getLogger(TelephoneNumberCountJasperReport.class);
}
