/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.msc;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientSms;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.SentSmsItem;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.events.orders.DiscrepanciesOnOrdersAndAttendanceBuilder;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 04.02.14
 * Time: 11:55
 * To change this template use File | Settings | File Templates.
 */
public class DiscrepanciesOnOrdersAndAttendanceJasperReport extends BasicReportForAllOrgJob {

    private final static Logger logger = LoggerFactory.getLogger(DiscrepanciesOnOrdersAndAttendanceJasperReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {}

    public DiscrepanciesOnOrdersAndAttendanceJasperReport() {
    }


    public DiscrepanciesOnOrdersAndAttendanceJasperReport(Date generateTime, long generateDuration, JasperPrint print,
            Date startTime, Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new DiscrepanciesOnOrdersAndAttendanceJasperReport();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return new DiscrepanciesOnOrdersAndAttendanceBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_MONTH;
    }

}
