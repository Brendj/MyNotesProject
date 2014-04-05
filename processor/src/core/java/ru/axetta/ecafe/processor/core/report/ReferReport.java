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
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

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

    private List<List<ReferReportItem>> items;
    private Date startDate;
    private Date endDate;
    private String htmlReport;
    public static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public static DateFormat dailyItemsFormat = new SimpleDateFormat("dd.MM.yyyy");
    private static final long MILLIS_IN_DAY = 86400000L;
    public static final String BREAKFAST = "ЗАВТРАК";
    public static final String LUNCH = "ОБЕД";
    public static final String SNACK = "ПОЛДНИК";


    public List<List<ReferReportItem>> getItems() {
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
            if(org == null) {
                throw new IllegalArgumentException("Не указана организация");
            }
            if(startTime == null || endTime == null) {
                throw new IllegalArgumentException("Не задан период");
            }

            Date generateTime = new Date();

            /* Строим параметры для передачи в jasper */
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            /*parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));*/
            parameterMap.put("startDate", DailyReferReport.dailyItemsFormat.format(startTime));
            parameterMap.put("endDate", DailyReferReport.dailyItemsFormat.format(endTime));
            parameterMap.put("orgName", org.getShortName());
            parameterMap.put("SUBREPORT_DIR", RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath());


            //  Получение рабочих и выходных дней
            PeriodResult periodResult = new PeriodResult();
            Date generateEndTime = new Date();
            int counts [] = getDaysCount(session, org.getIdOfOrg(), startTime, endTime);
            int workDaysCount = counts [0];
            int weekendsCount = counts [1];
            //  Загрузка данных из БД
            List<DailyReferReportItem> items = findReferItems(session, startTime, endTime);
            List<String> categories = DAOUtils.getDiscountRuleSubcategories(session);                   //  Данные по дням
            DailyReferReportItem samples [] = Builder.getSampleItems(session, org, startTime, endTime);    //  Хранится 2 объекта с данными по пробе
            //  Соединение всего вместе
            List<List<ReferReportItem>> total = getTotalItems(workDaysCount, weekendsCount, items, categories,
                    samples[0], samples[1], periodResult);
            //  Добавляем массив как параметр отчета
            parameterMap.put("reports", total);
            parameterMap.put("periodChildren", periodResult.getPeriodChildren());
            parameterMap.put("periodTotal", periodResult.getPeriodTotal());
            parameterMap.put("periodSummary", periodResult.getPeriodSummary());
            parameterMap.put("workdaysCount", workDaysCount);
            parameterMap.put("weekendsCount", weekendsCount);
            //
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, startTime, endTime, (Calendar) calendar.clone(), parameterMap,
                            //total));
                            Collections.EMPTY_LIST));
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
                        startTime, endTime, total).setHtmlReport(os.toString("UTF-8"));
            }
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Calendar calendar,
                Map<String, Object> parameterMap, List<List<ReferReportItem>> items) throws Exception {
            return new JRBeanCollectionDataSource(items);
        }

        public static DailyReferReportItem[] getSampleItems(Session session, OrgShortItem org,
                                                            Date startTime, Date endTime,
                                                            Set<String> groups) {
            Map<String, DailyReferReportItem> items = new HashMap<String, DailyReferReportItem>();
            for(String g : groups) {
                DailyReferReportItem i = new DailyReferReportItem(g);
                i.setGoodName(g);
                items.put(g, i);
            }
            List res = executeSampleItemsQuery(session, org, startTime, endTime);
            Calendar cal = new GregorianCalendar();
            for (Object entry : res) {
                Object e[] = (Object[]) entry;
                BigDecimal priceObj   = e[0] == null ? new BigDecimal(0D) : (BigDecimal) e[0];
                priceObj              = priceObj.setScale(2, BigDecimal.ROUND_HALF_DOWN);
                long ts               = ((BigInteger) e[1]).longValue();
                String good           = ((String) e[2]).trim();
                cal.setTimeInMillis(ts);
                String groupName = "";
                if(good.toLowerCase().indexOf(BREAKFAST.toLowerCase()) >= 0) {
                    groupName = BREAKFAST;
                }
                if(good.toLowerCase().indexOf(SNACK.toLowerCase()) >= 0) {
                    groupName = SNACK;
                }
                if(good.toLowerCase().indexOf(LUNCH.toLowerCase()) >= 0) {
                    groupName = LUNCH;
                }
                DailyReferReportItem it = items.get(groupName);
                if(it == null) {
                    continue;
                }
                it.setPrice(priceObj.doubleValue());
                it.setChildren(it.getChildren() + 1);
                it.setSummary(it.getChildren() * it.getPrice());
            }

            DailyReferReportItem result[] = new DailyReferReportItem[items.size()];
            int i = 0;
            for(String k : items.keySet()) {
                result[i] = items.get(k);
                i++;
            }
            return result;
        }

        public static DailyReferReportItem[] getSampleItems(Session session, OrgShortItem org,
                                                            Date startTime, Date endTime) {
            DailyReferReportItem result [] = new DailyReferReportItem [] { new DailyReferReportItem("БУДНИЕ"),
                                                   new DailyReferReportItem("ВЫХОДНЫЕ") };
            List res = executeSampleItemsQuery(session, org, startTime, endTime);
            Calendar cal = new GregorianCalendar();
            for (Object entry : res) {
                Object e[] = (Object[]) entry;
                BigDecimal priceObj   = e[0] == null ? new BigDecimal(0D) : (BigDecimal) e[0];
                priceObj              = priceObj.setScale(2, BigDecimal.ROUND_HALF_DOWN);
                long ts               = ((BigInteger) e[1]).longValue();
                String good           = ((String) e[2]).trim();
                cal.setTimeInMillis(ts);
                //  Заносим изменения в соответствующий объект
                int index = cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                            cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY ? 0 : 1;
                result[index].setTotal(result[index].getTotal() + 1);
                result[index].setPrice(priceObj.doubleValue());
                result[index].setGoodName(good);
            }
            return result;
        }

        protected static List executeSampleItemsQuery(Session session, OrgShortItem org,
                                                Date startTime, Date endTime) {
            Query query = session.createSQLQuery(
                    "select cast (cf_orderdetails.socdiscount as decimal) / 100 as price, cf_orders.createddate, cf_goods.nameofgood "
                            + "from cf_orders "
                            + "join cf_orderdetails on cf_orders.idoforder=cf_orderdetails.idoforder and cf_orders.idoforg=cf_orderdetails.idoforg "
                            + "join cf_goods on cf_orderdetails.idofgood=cf_goods.idofgood "
                            + "where cf_orders.socdiscount<>0 and cf_orders.idoforg=:idoforg and "
                            + "      cf_orders.createddate between :start and :end "
                            + "      and cf_orders.ordertype=:ordertype ");
            query.setLong("idoforg", org.getIdOfOrg());
            query.setLong("start", startTime.getTime());
            query.setLong("end", endTime.getTime());
            query.setInteger("ordertype", OrderTypeEnumType.DAILY_SAMPLE.ordinal());
            List res = query.list();
            return res;
        }
        
        private List<DailyReferReportItem> findReferItems(Session session, Date startTime, Date endTime) {
            List<DailyReferReportItem> result = new ArrayList<DailyReferReportItem>();
            List res = DailyReferReport.getReportData(session, org.getIdOfOrg(), startTime.getTime(), endTime.getTime(),
                                                      " and cf_discountrules.subcategory <> ''");
                        //"and cf_discountrules.subcategory = 'Многодетные 5-11 кл.(завтрак+обед)' and nameofgood='Обед 5-11' ");
            for (Object entry : res) {
                Object e[]            = (Object[]) entry;
                String name           = (String) e[0];
                String goodname       = (String) e[1];
                long ts               = ((BigInteger) e[2]).longValue();
                BigDecimal priceObj   = e[3] == null ? new BigDecimal(0D) : (BigDecimal) e[3];
                priceObj              = priceObj.setScale(2, BigDecimal.ROUND_HALF_DOWN);
                long children         = ((BigInteger) e[4]).longValue();
                BigDecimal summaryObj = e[5] == null ? new BigDecimal(0D) : (BigDecimal) e[5];
                summaryObj            = summaryObj.setScale(2, BigDecimal.ROUND_HALF_DOWN);
                DailyReferReportItem item = new DailyReferReportItem(ts, name, goodname, children,
                                                                     priceObj.doubleValue(), summaryObj.doubleValue());
                result.add(item);
            }

            return result;
        }
    }

    private static final List<List<ReferReportItem>> getTotalItems(int workDaysCount,
                                                             int weekendsCount,
                                                             List<DailyReferReportItem> items,
                                                             List<String> categories,
                                                             DailyReferReportItem workdaysSample,
                                                             DailyReferReportItem weekendsSample,
                                                             PeriodResult periodResult) {
        Calendar tmp = new GregorianCalendar();
        List<ReferReportItem> workdays = new ArrayList<ReferReportItem>();
        List<ReferReportItem> weekends = new ArrayList<ReferReportItem>();
        int id = 0;
        Set<Double> prices = new TreeSet<Double>();
        for (String cat : categories) {
            prices.clear();
            //  Поиск итогового объекта
            ReferReportItem workdayItem = new ReferReportItem();
            workdayItem.setLineId(id);
            workdayItem.setName(cat);
            workdayItem.setValue(1);
            workdays.add(workdayItem);
            ReferReportItem weekendItem = new ReferReportItem();
            weekendItem.setLineId(id);
            weekendItem.setName(cat);
            weekendItem.setValue(1);
            weekends.add(weekendItem);

            //  Листаем значения
            for (DailyReferReportItem i : items) {
                if (!i.getName().equals(cat)) {
                    continue;
                }
                if(i.getGroup2() == null) {
                    continue;
                }
                if(i.getGroup2().equals(LUNCH) || cat.indexOf("(завтрак)") > 0 ||
                   i.getGroup2().equals(BREAKFAST) || i.getGroup2().equals(SNACK)) {
                    prices.add(i.getPrice());
                }
                if (i.getGroup2() != null &&
                    (!i.getGroup2().equals(LUNCH) && !i.getGroup2().equals(SNACK) && cat.indexOf("(завтрак)") < 1)) {
                    continue;
                }
                /*if (i.getGroup2() != null && !i.getGroup2().equals(LUNCH)) {
                    continue;
                }*/

                tmp.setTimeInMillis(i.getTs());
                //  Если запись относится к рабочему дню, то обновляем итог по рабочим
                if (tmp.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                    tmp.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                    workdayItem.setChildren(workdayItem.getChildren() + i.getChildren());
                    workdayItem.setTotal(workdayItem.getTotal() + i.getChildren());
                    //workdayItem.setSummary(workdayItem.getSummary() + i.getPrice() * i.getChildren());
                }
                //  Иначе - обновляем данные за субботы
                else {
                    weekendItem.setChildren(weekendItem.getChildren() + i.getChildren());
                    weekendItem.setTotal(weekendItem.getTotal() + i.getChildren());
                    //weekendItem.setSummary(weekendItem.getSummary() + i.getPrice() * i.getChildren());
                }
            }
            for(Double p : prices) {
                workdayItem.setSummary(workdayItem.getSummary() + workdayItem.getTotal() * p);
                weekendItem.setSummary(weekendItem.getSummary() + weekendItem.getTotal() * p);
            }
            workdayItem.setChildren((long) Math.round((double) workdayItem.getChildren() / workDaysCount));
            weekendItem.setChildren((long) Math.round((double) weekendItem.getChildren() / weekendsCount));
            periodResult.setPeriodChildren(periodResult.getPeriodChildren() +
                                           workdayItem.getChildren() + weekendItem.getChildren());
            periodResult.setPeriodTotal(periodResult.getPeriodTotal() +
                    workdayItem.getTotal() + weekendItem.getTotal());
            periodResult.setPeriodSummary(periodResult.getPeriodSummary() +
                                        workdayItem.getSummary() + weekendItem.getSummary());
            id++;
        }
        //  Калькуляция итого по будням+субб

        //  Подсчет пробы
        ReferReportItem workdaysTestItem = new ReferReportItem();
        workdaysTestItem.setName("СУТОЧНАЯ ПРОБА");
        workdaysTestItem.setLineId(id);
        workdaysTestItem.setTotal(workdaysSample.getTotal());
        workdaysTestItem.setSummary(workdaysSample.getTotal() * workdaysSample.getPrice());
        ReferReportItem weekendsTestItem = new ReferReportItem();
        weekendsTestItem.setName("СУТОЧНАЯ ПРОБА");
        weekendsTestItem.setLineId(id++);
        weekendsTestItem.setTotal(weekendsSample.getTotal());
        weekendsTestItem.setSummary(weekendsSample.getTotal() * weekendsSample.getPrice());
        workdays.add(workdaysTestItem);
        weekends.add(weekendsTestItem);

        periodResult.setPeriodChildren(periodResult.getPeriodChildren() +
                workdaysTestItem.getChildren() + weekendsTestItem.getChildren());
        periodResult.setPeriodTotal(periodResult.getPeriodTotal() +
                workdaysTestItem.getTotal() + weekendsTestItem.getTotal());
        periodResult.setPeriodSummary(periodResult.getPeriodSummary() +
                workdaysTestItem.getSummary() + weekendsTestItem.getSummary());


        /*ReferReportItem workdaysTotalItem = new ReferReportItem();
        workdaysTotalItem.setName("ИТОГО");
        workdaysTotalItem.setLineId(id);
        //  Считаем итог по будням
        for (ReferReportItem i : workdays) {
            i.setChildren(Math.round(i.getTotal() / workDaysCount));
            workdaysTotalItem.setChildren(workdaysTotalItem.getChildren() + i.getChildren());
            workdaysTotalItem.setTotal(workdaysTotalItem.getTotal() + i.getTotal());
            workdaysTotalItem.setSummary(workdaysTotalItem.getSummary() + i.getSummary());
        }
        workdays.add(workdaysTotalItem);
        //  Считаем итог по выходным
        ReferReportItem weekendsTotalItem = new ReferReportItem();
        weekendsTotalItem.setName("ИТОГО");
        weekendsTotalItem.setLineId(id++);
        for (ReferReportItem i : weekends) {
            i.setTotal(Math.round(i.getChildren() / workDaysCount));
            weekendsTotalItem.setChildren(weekendsTotalItem.getChildren() + i.getChildren());
            weekendsTotalItem.setTotal(weekendsTotalItem.getTotal() + i.getTotal());
            weekendsTotalItem.setSummary(weekendsTotalItem.getSummary() + i.getSummary());
        }
        weekends.add(weekendsTotalItem);*/


        List<List<ReferReportItem>> result = new ArrayList<List<ReferReportItem>>();
        result.add(workdays);
        result.add(weekends);
        return result;
    }

    private static final int [] getDaysCount(Session session, long idoforg, Date startTime, Date endTime) {
        int count [] = new int[] {0, 0};    //  0 - будние дние; 1 - выхоные
        Calendar day = getClearCalendar(startTime.getTime());
        Query query = session.createSQLQuery(
                "select int8(EXTRACT(EPOCH FROM d) * 1000) "
                + "from (select distinct(date_trunc('day', to_timestamp(cf_orders.createddate / 1000))) as d "
                + "      from cf_orders "
                + "      where cf_orders.idoforg=:idoforg and "
                + "            cf_orders.createddate between :start and :end) as dates "
                + "order by 1");
        query.setLong("idoforg", idoforg);
        query.setLong("start", startTime.getTime());
        query.setLong("end", endTime.getTime());
        List<BigInteger> dates = (List<BigInteger>) query.list();
        for (BigInteger ts : dates) {
            day.setTimeInMillis(ts.longValue());
            if (day.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                count[1]++;
            } else if (day.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                count[0]++;
            }
        }
        return count;
    }

    public static final Calendar getClearCalendar(long ts) {
        Calendar day = new GregorianCalendar();
        day.setTimeInMillis(ts);
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);
        return day;
    }


    public ReferReport() {
    }


    public ReferReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, List<List<ReferReportItem>> items) {
        super(generateTime, generateDuration, print, startTime, endTime);
        this.items = items;
    }

    public ReferReport(Date generateTime, long generateDuration, Date startTime, Date endTime,
            List<List<ReferReportItem>> items) {
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
        private long ts;
        private String name;
        private String goodName;
        
        public DailyReferReportItem() {

        }

        public DailyReferReportItem(String name) {
            this.name = name;
        }

        public DailyReferReportItem(long ts, String name, String goodname, long children, double price, double summary) {
            this.ts = ts;
            this.name = name;
            day = dailyItemsFormat.format(new Date(ts));
            this.group1 = name.substring(0, name.indexOf("("));
            if (goodname.toLowerCase().indexOf(BREAKFAST.toLowerCase()) > -1) {
                this.group2 = BREAKFAST;
            } else if (goodname.toLowerCase().indexOf(LUNCH.toLowerCase()) > -1) {
                this.group2 = LUNCH;
            } else if (goodname.toLowerCase().indexOf(SNACK.toLowerCase()) > -1) {
                this.group2 = SNACK;
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

        public long getTs() {
            return ts;
        }

        public String getName() {
            return name;
        }

        public String getGoodName() {
            return goodName;
        }

        public void setGoodName(String goodName) {
            this.goodName = goodName;
        }
    }


    public static class ReferReportItem {
        protected int lineId;             //  id для категории
        protected String name;            //  наименование правила
        protected long children;          //  количество детей
        protected long total;             //  дето/дни
        protected double summary;         //  сумма
        protected int value;              //  поле для группировки, всегда = 1

        public ReferReportItem() {
            lineId = -1;
            name = "";
            children = 0L;
            total = 0L;
            summary = 0D;
            value = 1;

        }

        public ReferReportItem(int lineId, String name, long children, long total, double summary) {
            value = 1;
            this.lineId = lineId;
            this.name = name;
            this.children = children;
            this.total = total;
            this.summary = summary;
        }

        public int getLineId() {
            return lineId;
        }

        public void setLineId(int lineId) {
            this.lineId = lineId;
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

    public static class PeriodResult {
        protected Long periodChildren = 0L;
        protected Long periodTotal = 0L;
        protected Double periodSummary = 0D;


        public PeriodResult() {
            periodChildren = 0L;
            periodTotal = 0L;
            periodSummary = 0D;
        }

        public Long getPeriodChildren() {
            return periodChildren;
        }

        public void setPeriodChildren(Long periodChildren) {
            this.periodChildren = periodChildren;
        }

        public Long getPeriodTotal() {
            return periodTotal;
        }

        public void setPeriodTotal(Long periodTotal) {
            this.periodTotal = periodTotal;
        }

        public Double getPeriodSummary() {
            return periodSummary;
        }

        public void setPeriodSummary(Double periodSummary) {
            this.periodSummary = periodSummary;
        }
    }
}
