/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientSms;

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
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 04.02.14
 * Time: 11:55
 * To change this template use File | Settings | File Templates.
 */
public class SentSmsReport extends BasicReportForAllOrgJob {
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
    public static final String REPORT_NAME = "Статистика отправки сообщений информирования";
    public static final String[] TEMPLATE_FILE_NAMES = {"SentSmsReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};


    private List<String> cols;
    public static final long MILLIS_IN_DAY = 86400000L;
    private static final String ORG_NUM = "Организация";
    private static final String STR_YEAR_DATE_FORMAT = "dd.MM.yyyy EE";
    private static final DateFormat YEAR_DATE_FORMAT = new SimpleDateFormat(STR_YEAR_DATE_FORMAT, new Locale("ru"));
    private static final List<ReportColumn> DEFAULT_COLUMNS = new ArrayList<ReportColumn>();

    static {
        DEFAULT_COLUMNS.add(new ReportColumn(ORG_NUM));
    }

    private final static Logger logger = LoggerFactory.getLogger(SentSmsReport.class);

    private List<SentSmsItem> items;
    private Date startDate;
    private Date endDate;
    private String htmlReport;

    private static List<Long> idOfOrgList;

    public List<SentSmsItem> getItems() {
        return items;
    }


    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }


    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;
        private boolean exportToHTML = false;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath()
                    + SentSmsReport.class.getSimpleName() + ".jasper";
            exportToHTML = true;
        }

        @Override
        public SentSmsReport build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();


            /* Строим параметры для передачи в jasper */
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);


            Date generateEndTime = new Date();
            List<SentSmsItem> items = findSentSms(session, startTime, endTime);
            JasperPrint jasperPrint = JasperFillManager
                    .fillReport(templateFilename, parameterMap, createDataSource(items));
            //  Если имя шаблона присутствует, значит строится для джаспера
            if (!exportToHTML) {
                return new SentSmsReport(generateTime, generateEndTime.getTime() - generateTime.getTime(), jasperPrint,
                        startTime, endTime, null);
            } else {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                JRHtmlExporter exporter = new JRHtmlExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
                exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
                exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();
                return new SentSmsReport(generateTime, generateEndTime.getTime() - generateTime.getTime(), startTime,
                        endTime, items).setHtmlReport(os.toString("UTF-8"));
            }
        }

        private JRDataSource createDataSource(List<SentSmsItem> items) throws Exception {
            return new JRBeanCollectionDataSource(items);
        }

        public List<SentSmsItem> findSentSms(Session session, Date start, Date end) {
            String orgRestrict = "";
            String orgIds = "";
            if (idOfOrgList.size() > 0) {
                orgRestrict = " and cf_orgs.idoforg in (";
                for (int i = 0; i < idOfOrgList.size() - 1; i++) {
                    orgIds = orgIds + idOfOrgList.get(i) + ", ";
                }
                orgIds = orgIds + idOfOrgList.get(idOfOrgList.size() - 1);
                orgRestrict = orgRestrict + orgIds + ")";
            }
            String sql = "select sms_data.org, substring(sms_data.org from '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)'), "
                    + "     EXTRACT(EPOCH FROM sms_data.d) * 1000 + 3600000, count(sms) " + "from ("
                    + "select IdOfSms as sms, date_trunc('day', to_timestamp(servicesenddate/1000)) as d, cf_orgs.shortname as org "
                    + "from CF_ClientSms " + "join cf_clients on CF_ClientSms.IdOfClient=cf_clients.idofclient "
                    + "join cf_orgs on cf_clients.idoforg=cf_orgs.idoforg " + "where servicesenddate >= :startDate and "
                    + "      servicesenddate <= :endDate and "
                    + "      DeliveryStatus in (:sentStatus, :sendStatus, :deliveredStatus) " + orgRestrict
                    + ") as sms_data " + "group by sms_data.d, sms_data.org order by 1";
            Query query = session.createSQLQuery(sql);
            query.setLong("startDate", start.getTime());
            query.setLong("endDate", end.getTime());
            query.setInteger("sentStatus", ClientSms.SENT_TO_SERVICE);
            query.setInteger("sendStatus", ClientSms.SEND_TO_RECIPENT);
            query.setInteger("deliveredStatus", ClientSms.DELIVERED_TO_RECIPENT);
            List res = query.list();

            Date target = new Date();
            Calendar cal = new GregorianCalendar();

            List<String> orgList = new ArrayList<>();
            List<Long> dateList = new ArrayList<>();

            for (Object entry : res) {
                Object[] e = (Object[]) entry;
                orgList.add((String) e[0]);
                dateList.add(((Double) e[2]).longValue());
            }
            dateList.sort(null);
            List<SentSmsItem> itemList = orgList.stream().map(SentSmsItem::new).distinct().collect(Collectors.toList());

            itemList.forEach(o -> {
                List<SentSmsValue> valueList = new ArrayList<>();
                dateList.forEach(dates -> {
                    boolean checkAddDate = false;
                    cal.setTimeInMillis(dates);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    target.setTime(cal.getTimeInMillis());

                    for (Object entry : res) {
                        Object e[] = (Object[]) entry;
                        String orgName = (String) e[0];
                        long date = ((Double) e[2]).longValue();
                        if (orgName.equals(o.getOrgName()) && dates.equals(date)) {
                            int value = ((BigInteger) e[3]).intValue();
                            valueList.add(new SentSmsValue(YEAR_DATE_FORMAT.format(target), String.valueOf(value)));
                            checkAddDate = true;
                        }
                    }
                    if(!checkAddDate)
                        valueList.add(new SentSmsValue(YEAR_DATE_FORMAT.format(target), ""));
                });
                o.setValue(valueList);
            });

            /*Date d = new Date(1391544000000L);
            result.add(new SentSmsItem(1L, 1L, "1234", YEAR_DATE_FORMAT.format(d), "100"));
            result.add(new SentSmsItem(2L, 1L, "5678", YEAR_DATE_FORMAT.format(d), "200"));
            result.add(new SentSmsItem(3L, 1L, "1111", YEAR_DATE_FORMAT.format(d), "300"));

            d = new Date(1391630400000L);
            result.add(new SentSmsItem(1L, 2L, "1234", YEAR_DATE_FORMAT.format(d), "1"));

            d = new Date(1391716800000L);
            result.add(new SentSmsItem(2L, 3L, "5678", YEAR_DATE_FORMAT.format(d), "3"));
            result.add(new SentSmsItem(3L, 3L, "1111", YEAR_DATE_FORMAT.format(d), "55"));

            d = new Date(1391803200000L);
            result.add(new SentSmsItem(1L, 4L, "1234", YEAR_DATE_FORMAT.format(d), "10"));
            result.add(new SentSmsItem(3L, 4L, "1111", YEAR_DATE_FORMAT.format(d), "30"));*/

            /*d = new Date(1391889600000L);
            result.add(new SentSmsItem(3L, 5L, "1111", YEAR_DATE_FORMAT.format(d), "99"));*/
            return itemList;
        }
    }

    public SentSmsReport() {
    }


    public SentSmsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime, Date endTime,
                         List<SentSmsItem> items) {
        super(generateTime, generateDuration, print, startTime, endTime);
        this.items = items;
    }

    public SentSmsReport(Date generateTime, long generateDuration, Date startTime, Date endTime,
                         List<SentSmsItem> items) {
        this.items = items;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public SentSmsReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }

    public Object[] getColumnNames() {
        if (cols != null) {
            return cols.toArray();
        } else {
            cols = new ArrayList<String>();
        }
        for (ReportColumn c : DEFAULT_COLUMNS) {
            cols.add(c.getName());
        }


        if (items == null || items.size() < 1) {
            return cols.toArray();
        }
        Set<Date> dates = new TreeSet<Date>();
        long ts = startDate.getTime();
        Date target = new Date();
        for (; ts <= endDate.getTime(); ) {
            target.setTime(ts);
            cols.add(YEAR_DATE_FORMAT.format(target));
            ts += MILLIS_IN_DAY;
        }
        return cols.toArray();
    }


    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new SentSmsReport();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_MONTH;
    }

    public static class ReportColumn {

        private String name;

        public ReportColumn(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public void setIdOfOrgList(List<Long> idOfOrgList) {
        this.idOfOrgList = idOfOrgList;
    }

    public List<Long> getIdOfOrgList() {
        return idOfOrgList;
    }
}
