/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperPrint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 06.05.13
 * Time: 13:37
 * Онлайн отчеты -> Льготное питание -> Отчет по предоставленным услугам
 */
public class DeliveredServicesReport extends BasicReportForMainBuildingOrgJob {
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
    public static final String REPORT_NAME = "Сводный отчет по услугам (предварительный)";
    public static final String[] TEMPLATE_FILE_NAMES = {"DeliveredServicesReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{3, 32};


    private final static Logger logger = LoggerFactory.getLogger(DeliveredServicesReport.class);

    protected List<DeliveredServicesItem.DeliveredServicesData> data;
    private Date startDate;
    private Date endDate;
    protected String htmlReport;

    protected static final String ORG_NUM = "Номер ОУ";
    protected static final String ORG_NAME = "Наименование ОУ";
    protected static final String GOOD_NAME = "Товар";
    protected static final List<String> DEFAULT_COLUMNS = new ArrayList<String>();

    static {
        DEFAULT_COLUMNS.add(ORG_NUM);
        DEFAULT_COLUMNS.add(ORG_NAME);
        DEFAULT_COLUMNS.add(GOOD_NAME);
    }


    public List<DeliveredServicesItem.DeliveredServicesData> getData() {
        return data;
    }


    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }





    public DeliveredServicesReport() {
    }


    public DeliveredServicesReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, List<DeliveredServicesItem.DeliveredServicesData> data, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfOrg);
        this.data = data;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public DeliveredServicesReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }

    public DeliveredServicesReport(Date generateTime, long generateDuration, Date startTime, Date endTime,
            List<DeliveredServicesItem.DeliveredServicesData> data) {
        this.data = data;
    }


    @Override
    public BasicReportForOrgJob createInstance() {
        return new DeliveredServicesReport();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return new DeliveredServicesReportBuilder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_MONTH;
    }

    public class JasperStringOutputStream extends OutputStream {

        private StringBuilder string = new StringBuilder();

        @Override
        public void write(int b) throws IOException {
            this.string.append((char) b);
        }

        //Netbeans IDE automatically overrides this toString()
        public String toString() {
            return this.string.toString();
        }
    }
}
