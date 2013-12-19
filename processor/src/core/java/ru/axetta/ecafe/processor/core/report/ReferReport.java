/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
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

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 16.12.13
 * Time: 17:32
 * To change this template use File | Settings | File Templates.
 */
public class ReferReport extends BasicReportForAllOrgJob {

    private final static Logger logger = LoggerFactory.getLogger(ReferReport.class);

    private List<ReferReportItem> items;
    private Date startDate;
    private Date endDate;
    private String htmlReport;
    public static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public static DateFormat dailyItemsFormat = new SimpleDateFormat("dd.MM.yyyy");


    public List<ReferReportItem> getItems() {
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
            String templateName = ReferReport.class.getSimpleName();
            templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + templateName + ".jasper";
            exportToHTML = true;
        }

        @Override
        public ReferReport build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            /*Long idOfContragent = null;
            Long idOfContract = null;
            if (reportProperties.getProperty("idOfContract") != null &&
                    reportProperties.getProperty("idOfContract").length() > 0) {
                try {
                    idOfContract = Long.parseLong(reportProperties.getProperty("idOfContract"));
                } catch (Exception e) {
                    idOfContract = null;
                }
            }
            if (contragent != null) {
                idOfContragent = contragent.getIdOfContragent();
            }*/

            return doBuild(session, startTime, endTime, calendar);
        }

        public ReferReport doBuild(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();


            /* Строим параметры для передачи в jasper */
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            /*parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));*/
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);
            parameterMap.put("orgName", org.getShortName());


            Date generateEndTime = new Date();
            List<ReferReportItem> items = findReferItems(session, startTime, endTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, startTime, endTime, (Calendar) calendar.clone(), parameterMap, items));
            //  Если имя шаблона присутствует, значит строится для джаспера
            if (!exportToHTML) {
                return new ReferReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        jasperPrint, startTime, endTime, null);
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
                return new ReferReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        startTime, endTime, items).setHtmlReport(os.toString("UTF-8"));
            }
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Calendar calendar,
                Map<String, Object> parameterMap, List<ReferReportItem> items) throws Exception {
            return new JRBeanCollectionDataSource(items);
        }
        
        private List<ReferReportItem> findReferItems(Session session, Date startTime, Date endTime) {
            List<ReferReportItem> result = new ArrayList<ReferReportItem>();
            String sql =
                      "select subcategory, "
                      + "       count(distinct children) as children, "
                      + "       count(total) as total, "
                      + "       count(total) * price as summary "
                      + "from ("
                      + "      select cf_discountrules.subcategory, "
                      + "       cf_orders.idofclient as children, "
                      + "       cf_orders.idoforder as total, "
                      + "       cast(cf_orderdetails.rprice + cf_orderdetails.socdiscount as decimal) / 100 as price "
                      + "from cf_orgs "
                      + "left join cf_orders on cf_orgs.idoforg=cf_orders.idoforg "
                      + "join cf_orderdetails on cf_orders.idoforder=cf_orderdetails.idoforder and cf_orders.idoforg=cf_orderdetails.idoforg "
                      + "join cf_discountrules on cf_discountrules.idofrule=cf_orderdetails.idofrule "
                      + "where cf_orderdetails.socdiscount<>0 and cf_orgs.idoforg=:idoforg and "
                      + "           cf_orders.createddate between :start and :end and "
                      + "           cf_discountrules.subcategory <> '') as data "
                      + "group by subcategory, price";
            Query query = session.createSQLQuery(sql);
            query.setLong("idoforg", org.getIdOfOrg());
            query.setLong("start", startTime.getTime());
            query.setLong("end", endTime.getTime());
            List res = query.list();
            for (Object entry : res) {
                Object e[]            = (Object[]) entry;
                String name           = (String) e[0];
                long children         = ((BigInteger) e[1]).longValue();
                long total            = ((BigInteger) e[2]).longValue();
                BigDecimal summaryObj = e[3] == null ? new BigDecimal(0D) : (BigDecimal) e[3];
                summaryObj            = summaryObj.setScale(2, BigDecimal.ROUND_HALF_DOWN);
                ReferReportItem item = new ReferReportItem(name, children, total, summaryObj.doubleValue());
                result.add(item);
            }
            return result;
        }
    }


    public ReferReport() {
    }


    public ReferReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, List<ReferReportItem> items) {
        super(generateTime, generateDuration, print, startTime, endTime);
        this.items = items;
    }

    public ReferReport(Date generateTime, long generateDuration, Date startTime, Date endTime,
            List<ReferReportItem> items) {
        this.items = items;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public ReferReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }


    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new ReferReport();  //To change body of implemented methods use File | Settings | File Templates.
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

    public static class DailyReferReportItem extends ReferReportItem {
        private String day;         //  день
        private String group1;      //  наименование правила
        private String group2;      //  завтрак / обед
        private double price;       //  цена
        
        public DailyReferReportItem() {

        }

        public DailyReferReportItem(long ts, String name, String goodname, long children, double price, double summary) {
            day = dailyItemsFormat.format(new Date(ts));
            this.group1 = name.substring(0, name.indexOf("("));
            if (goodname.toLowerCase().indexOf("завтрак") > -1) {
                this.group2 = "ЗАВТРАК";
            } else if (goodname.toLowerCase().indexOf("обед") > -1) {
                this.group2 = "ОБЕД";
            }
            this.children = children;
            this.price = price;
            this.summary = summary;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getGroup1() {
            return group1;
        }

        public void setGroup1(String group1) {
            this.group1 = group1;
        }

        public String getGroup2() {
            return group2;
        }

        public void setGroup2(String group2) {
            this.group2 = group2;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }


    public static class ReferReportItem {
        protected String name;            //  наименование правила
        protected long children;          //  количество детей
        protected long total;             //  дето/дни
        protected double summary;         //  сумма
        protected int value;              //  поле для группировки, всегда = 1

        public ReferReportItem() {
            name = "";
            children = 0L;
            total = 0L;
            summary = 0D;
            value = 1;

        }

        public ReferReportItem(String name, long children, long total, double summary) {
            value = 1;
            this.name = name;
            this.children = children;
            this.total = total;
            this.summary = summary;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value =
                    value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getChildren() {
            return children;
        }

        public void setChildren(long children) {
            this.children = children;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public double getSummary() {
            return summary;
        }

        public void setSummary(double summary) {
            this.summary = summary;
        }
    }
}
