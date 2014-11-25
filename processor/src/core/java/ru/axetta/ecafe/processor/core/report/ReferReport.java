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
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
    public static final String BREAKFAST = "Завтрак";
    public static final String LUNCH = "Обед";
    public static final String SNACK = "Полдник";


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
            parameterMap.put("SUBREPORT_DIR", RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath());
            Object o = reportProperties.getProperty("region");
            if(org == null && o == null) {
                throw new IllegalArgumentException("Не указана организация или регион");
            }
            parameterMap.put("orgName", org != null ? org.getShortName() : (String) o);


            //  Получение рабочих и выходных дней
            PeriodResult periodResult = new PeriodResult();
            Date generateEndTime = new Date();
            int counts [] = getDaysCount(session, org == null ? null : org.getIdOfOrg(),
                    o == null ? null : (String) o, startTime, endTime);
            int workDaysCount = counts [0];
            int weekendsCount = counts [1];
            //  Загрузка данных из БД
            /*
            ! Бывший алгоритм начало !
            List<DailyReferReportItem> items = findReferItems(session, startTime, endTime, o != null ? (String) o : null);
            List<String> categories = DAOUtils.getDiscountRuleSubcategories(session);                   //  Данные по дням
            DailyReferReportItem samples [] = Builder.getSampleItems(session, org, startTime, endTime, o != null ? (String) o : null);    //  Хранится 2 объекта с данными по пробе
            //  Соединение всего вместе
            List<List<ReferReportItem>> total = getTotalItems(workDaysCount, weekendsCount, items, categories,
                    samples[0], samples[1], periodResult);
            ! Бывший алгоритм конец !
            */

            //List<String> categories = DAOUtils.getDiscountRuleSubcategories(session);                   //  Данные по дням
            DailyReferReportItem samples [] = Builder.getSampleItems(session, org, startTime, endTime, o != null ? (String) o : null);    //  Хранится 2 объекта с данными по пробе
            workDaysCount = workDaysCount + weekendsCount;
            weekendsCount = 0;
            List<List<ReferReportItem>> total = getTotalItems(workDaysCount, weekendsCount, session, samples[0], samples[1],
                    startTime, endTime, org, o != null ? (String) o : null);
            //solveCategories(total, categories);

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
                Set<String> groups, boolean isOverallReport, String region) {
            Map<String, DailyReferReportItem> items = new HashMap<String, DailyReferReportItem>();
            for(String g : groups) {
                DailyReferReportItem i = new DailyReferReportItem(g);
                i.setGoodName(g);
                items.put(g, i);
            }
            List res = executeSampleItemsQuery(session, org, startTime, endTime, region);
            Calendar cal = new GregorianCalendar();
            for (Object entry : res) {
                Object e[] = (Object[]) entry;
                BigDecimal priceObj   = e[0] == null ? new BigDecimal(0D) : (BigDecimal) e[0];
                priceObj              = priceObj.setScale(2, BigDecimal.ROUND_HALF_DOWN);
                long ts               = ((BigInteger) e[1]).longValue();
                String good           = ((String) e[2]).trim();
                cal.setTimeInMillis(ts);
                String groupName = "";
                if(isOverallReport) {
                    groupName = good.toUpperCase() + " кл.";
                } else {
                    if(good.toLowerCase().indexOf(BREAKFAST.toLowerCase()) >= 0) {
                        groupName = BREAKFAST;
                    }
                    if(good.toLowerCase().indexOf(SNACK.toLowerCase()) >= 0) {
                        groupName = SNACK;
                    }
                    if(good.toLowerCase().indexOf(LUNCH.toLowerCase()) >= 0) {
                        groupName = LUNCH;
                    }
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
                Date startTime, Date endTime, String region) {
            List<SampleItem> sampleItems = new ArrayList<SampleItem>();
            List res = executeSampleItemsQuery(session, org, startTime, endTime, region);
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
                sampleItems.add(new SampleItem(priceObj.doubleValue(), ts, good));
                /*result[index].setTotal(result[index].getTotal() + 1);
                result[index].setPrice(priceObj.doubleValue());
                result[index].setGoodName(good);*/
            }


            Set<String> weekendCategories = new HashSet<String>();
            Set<String> workdayCategories = new HashSet<String>();
            Map<Integer, List<SampleItem>> categories = new HashMap<Integer, List<SampleItem>> ();
            for(SampleItem i : sampleItems) {
                cal.setTimeInMillis(i.getCreatedate());
                int index = cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                        cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY ? 0 : 1;
                if(index == 0) {
                    workdayCategories.add(i.getNameOfGood());
                } else {
                    weekendCategories.add(i.getNameOfGood());
                }
                List<SampleItem> samples = categories.get(index);
                if(samples == null) {
                    samples = new ArrayList<SampleItem>();
                    categories.put(index, samples);
                }
                samples.add(i);
            }


            DailyReferReportItem result [] = new DailyReferReportItem [] { new DailyReferReportItem("БУДНИЕ"),
                                                                           new DailyReferReportItem("ВЫХОДНЫЕ") };
            result[0].setTotal(workdayCategories.size() == 0 ? 0 :
                    new BigDecimal((double) categories.get(0).size() / workdayCategories.size()).setScale(0, RoundingMode.HALF_UP).longValue());
            result[1].setTotal(weekendCategories.size() == 0 ? 0 :
                    new BigDecimal((double) categories.get(1).size() / weekendCategories.size()).setScale(0, RoundingMode.HALF_UP).longValue());
            result[0].setSummary(getSampleSummary(categories.get(0)));
            result[1].setSummary(getSampleSummary(categories.get(1)));
            return result;
        }

        protected static double getSampleSummary(List<SampleItem> samples) {
            if(samples == null || samples.size() < 1) {
                return 0D;
            }
            double res = 0D;
            for(SampleItem si : samples) {
                res += si.getPrice();
            }
            return res;
        }

        protected static List executeSampleItemsQuery(Session session, OrgShortItem org,
                Date startTime, Date endTime, String region) {
            String orgJoin = "";
            String regionClause = "";
            String orgClause = "";
            if(org == null) {
                orgJoin = " join cf_orgs on cf_orgs.idoforg=cf_orders.idoforg ";
                regionClause = " cf_orgs.district='" + region + "' and ";
            } else {
                orgClause = " cf_orders.idoforg=" + org.getIdOfOrg() + " and ";
            }
            Query query = session.createSQLQuery(
                    "select cast (cf_orderdetails.socdiscount as decimal) / 100 as price, cf_orders.createddate, cf_goods.nameofgood "
                            + "from cf_orders "
                            + "join cf_orderdetails on cf_orders.idoforder=cf_orderdetails.idoforder and cf_orders.idoforg=cf_orderdetails.idoforg "
                            + "join cf_goods on cf_orderdetails.idofgood=cf_goods.idofgood "
                            + orgJoin
                            + "where cf_orders.socdiscount<>0 and " + orgClause + regionClause
                            + "  cf_orders.createddate between :start and :end "
                            + "  and cf_orders.ordertype=:ordertype and"
                            + "  cf_orders.state=0 and cf_orderdetails.state=0"
                            + "order by cf_goods.nameofgood, cf_orders.createddate");
            //query.setLong("idoforg", org.getIdOfOrg());
            query.setLong("start", startTime.getTime());
            query.setLong("end", endTime.getTime());
            query.setInteger("ordertype", OrderTypeEnumType.DAILY_SAMPLE.ordinal());
            List res = query.list();
            return res;
        }

        private List<DailyReferReportItem> findReferItems(Session session, Date startTime, Date endTime, String region) {
            List<DailyReferReportItem> result = new ArrayList<DailyReferReportItem>();
            List res = DailyReferReport.getReportData(session, org == null ? null : org.getIdOfOrg(), startTime.getTime(), endTime.getTime(),
                    " and cf_discountrules.subcategory <> ''", region);
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

    protected static void solveCategories(List<List<ReferReportItem>> items, List<String> categories) {
        for(String cat : categories) {
            for(List<ReferReportItem> items2: items) {
                boolean found = false;

                int i=0;
                for(int c=0; c<items2.size(); c++) {
                    ReferReportItem it = items2.get(c);
                    i = Math.max(it.getLineId(), i);
                    if(it.getName() != null && it.getName().equals(cat)) {
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    ReferReportItem item = new ReferReportItem();
                    item.setLineId(i + 1);
                    item.setChildren(0L);
                    item.setTotal(0L);
                    item.setName(cat);
                    item.setSummary(0D);
                    items2.add(item);
                }
            }
        }
    }

    protected static final List<List<ReferReportItem>> getTotalItems(int workDaysCount, int weekendsCount,
            Session session, DailyReferReportItem workdaysSample,
            DailyReferReportItem weekendsSample,
            Date startTime, Date endTime,
            OrgShortItem org, String region) {
        List<List<ReferReportItem>> result = new ArrayList<List<ReferReportItem>>();
        String regionClause = "";
        if(region != null && region.trim().length() > 0) {
            regionClause = " and o.district='" + region + "' ";
        }
        String orgClause = "";
        if(org != null) {
            orgClause = " and o.idoforg=" + org.getIdOfOrg() + " ";
        }

        Query q = session.createSQLQuery(
                  "select clients.idoforg, clients.subcategory, clients.clientsCount, events.entersCount, orders.ordersCount "
                + "from "
                //Количество учеников
                + "     (select idoforg, subcategory, count(idofclient) as clientsCount "
                + "      from (select distinct cl.idoforg, cl.idofclient, dr.subcategory "
                + "            from cf_orgs o "
                + "            left join cf_clients cl on o.idoforg=cl.idoforg "
                + "            join cf_clientscomplexdiscounts ccd on ccd.idofclientcomplexdiscount= "
                + "                 (select ccd2.idofclientcomplexdiscount from cf_clientscomplexdiscounts ccd2 where cl.idofclient=ccd2.idofclient order by createdate desc limit 1) "
                + "            join cf_discountrules dr on ccd.idofrule=dr.idofrule "
                + "            where cl.idofclientgroup<1100000000 " + regionClause + orgClause + ") as data "
                + "      where subcategory<>'' "
                + "      group by idoforg, subcategory) as clients, "

                //Уникальные проходы
                + "      (select idoforg, subcategory, cast(avg(cnt) as bigint) as entersCount "
                + "       from ("
                + "             select idoforg, subcategory, count(distinct idofclient) as cnt, evtday "
                + "             from ("
                + "                   select cl.idoforg, dr.subcategory, evt.idofclient, date_trunc('day', to_timestamp(evt.evtdatetime / 1000)) as evtday "
                + "                   from cf_orgs o "
                + "                   left join cf_enterevents evt on o.idoforg=evt.idoforg "
                + "                   join cf_clients cl on evt.idofclient=cl.idofclient and cl.idoforg=o.idoforg "
                + "                   join cf_clientscomplexdiscounts ccd on ccd.idofclientcomplexdiscount= "
                + "                        (select ccd2.idofclientcomplexdiscount from cf_clientscomplexdiscounts ccd2 where cl.idofclient=ccd2.idofclient order by createdate desc limit 1) "
                + "                   join cf_discountrules dr on ccd.idofrule=dr.idofrule "
                + "                   where cl.idofclientgroup<1100000000 and subcategory<>'' "
                +                           regionClause + orgClause
                + "                         and evt.evtdatetime>=:startDate and evt.evtdatetime<:endDate "
                + "                   ) as aa "
                + "             group by idoforg, subcategory, evtday "
                + "             ) as bb "
                + "       group by idoforg, subcategory "
                + "       order by subcategory desc) as events, "

                //Сумма покупок
                + "       (SELECT ord.idoforg, dr.subcategory, SUM(od.Qty*(od.RPrice+od.socdiscount)) / 100 as ordersCount "
                + "        FROM CF_ORDERS ord, CF_ORGS o,CF_ORDERDETAILS od "
                + "        left join cf_discountrules dr on dr.idofrule=od.idofrule "
                + "        WHERE (ord.IdOfOrder=od.IdOfOrder) " + orgClause
                + "              AND (ord.idOfOrg=o.idoforg AND od.idOfOrg=o.idoforg) and "
                + "              (od.MenuType>=" + OrderDetail.TYPE_COMPLEX_MIN + " OR od.MenuType<=" + OrderDetail.TYPE_COMPLEX_MAX + ") AND "
                + "              (od.RPrice=0 AND od.Discount>0) "
                + "              and (ord.CreatedDate>=:startDate AND ord.CreatedDate<=:endDate) and ord.state=0 and "
                + "              od.state=0 and dr.subcategory<>'' "
                + "        GROUP BY ord.idoforg, dr.subcategory) as  orders "
                + "where clients.idoforg=events.idoforg and clients.idoforg=orders.idoforg and  "
                + "      clients.subcategory=events.subcategory and clients.subcategory=orders.subcategory "
                + "order by 1, 2");
        q.setParameter("startDate", startTime.getTime());
        q.setParameter("endDate", endTime.getTime());
        List sqlRes = q.list();

        int i=1;
        List<ReferReportItem> items = new ArrayList<ReferReportItem>();
        if(sqlRes != null && sqlRes.size() > 0) {
            for (Object entry : sqlRes) {
                Object e[] = (Object[]) entry;
                long idoforg = ((BigInteger) e[0]).longValue();
                String category = (String) e[1];
                long clientsCount = ((BigInteger) e[2]).longValue();
                double eventsCount = ((BigInteger) e[3]).longValue();
                double ordersSummary = ((BigDecimal) e[4]).doubleValue();

                ReferReportItem item = new ReferReportItem();
                item.setLineId(i);
                item.setChildren(clientsCount);
                item.setTotal((long) eventsCount);
                item.setName(category);
                item.setSummary(ordersSummary);
                items.add(item);
                i++;
            }
        }

        ReferReportItem workdaysTestItem = new ReferReportItem();
        workdaysTestItem.setName("СУТОЧНАЯ ПРОБА");
        workdaysTestItem.setLineId(i);
        workdaysTestItem.setChildren(null);
        workdaysTestItem.setTotal(null);//workdaysSample.getTotal() + weekendsSample.getTotal());
        workdaysTestItem.setSummary(workdaysSample.getSummary() + weekendsSample.getSummary());
        items.add(workdaysTestItem);

        result.add(items);
        result.add(new ArrayList<ReferReportItem>());
        return result;
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
        Map<String, Integer> feedTypes = getFeedTypesByCategory(items);
        for (String cat : categories) {
            prices.clear();
            boolean exists = false;
            //  Поиск итогового объекта
            ReferReportItem workdayItem = new ReferReportItem();
            workdayItem.setLineId(id);
            workdayItem.setName(cat);
            workdayItem.setValue(1);
            ReferReportItem weekendItem = new ReferReportItem();
            weekendItem.setLineId(id);
            weekendItem.setName(cat);
            weekendItem.setValue(1);

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
                        (!i.getGroup2().equals(LUNCH) && !i.getGroup2().equals(SNACK) && !i.getGroup2().equals(BREAKFAST)/* && cat.indexOf("(завтрак)") < 1)*/)) {
                    continue;
                }
                /*if (i.getGroup2() != null && !i.getGroup2().equals(LUNCH)) {
                    continue;
                }*/

                exists = true;
                tmp.setTimeInMillis(i.getTs());
                //  Если запись относится к рабочему дню, то обновляем итог по рабочим
                if (tmp.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                        tmp.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                    workdayItem.setChildren(workdayItem.getChildren() + i.getChildren());
                    workdayItem.setTotal(workdayItem.getTotal() + i.getChildren());
                    workdayItem.setSummary(workdayItem.getSummary() + i.getSummary());
                }
                //  Иначе - обновляем данные за субботы
                else {
                    weekendItem.setChildren(weekendItem.getChildren() + i.getChildren());
                    weekendItem.setTotal(weekendItem.getTotal() + i.getChildren());
                    weekendItem.setSummary(weekendItem.getSummary() + i.getSummary());
                }
            }
            /*for(Double p : prices) {
                workdayItem.setSummary(workdayItem.getSummary() + workdayItem.getTotal() * p);
                weekendItem.setSummary(weekendItem.getSummary() + weekendItem.getTotal() * p);
            }*/
            Integer feedTypesCount = feedTypes.get(cat);
            if(feedTypesCount == null) {
                feedTypesCount = 1;
            }
            if(feedTypesCount > 1) {
                workdayItem.setChildren(new BigDecimal((double) workdayItem.getChildren() / feedTypesCount).setScale(0, RoundingMode.HALF_UP).longValue());
                workdayItem.setTotal(new BigDecimal((double) workdayItem.getTotal() / feedTypesCount).setScale(0, RoundingMode.HALF_UP).longValue());
                weekendItem.setChildren(new BigDecimal((double) weekendItem.getChildren() / feedTypesCount).setScale(0, RoundingMode.HALF_UP).longValue());
                weekendItem.setTotal(new BigDecimal((double) weekendItem.getTotal() / feedTypesCount).setScale(0, RoundingMode.HALF_UP).longValue());
                //workdayItem.setSummary(new BigDecimal(workdayItem.getSummary() / feedTypesCount).setScale(2, RoundingMode.FLOOR).doubleValue());
            }
            if(exists) {
                workdays.add(workdayItem);
                weekends.add(weekendItem);
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
        workdaysTestItem.setSummary(workdaysSample.getSummary());
        ReferReportItem weekendsTestItem = new ReferReportItem();
        weekendsTestItem.setName("СУТОЧНАЯ ПРОБА");
        weekendsTestItem.setLineId(id++);
        weekendsTestItem.setTotal(weekendsSample.getTotal());
        weekendsTestItem.setSummary(weekendsSample.getSummary());
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

    protected static Map<String, Integer> getFeedTypesByCategory(List<DailyReferReportItem> items) {
        Map<String, Set<String>> list = new HashMap<String, Set<String>>();
        for(DailyReferReportItem i : items) {
            Set<String> feeds = list.get(i.getName());
            if(feeds == null) {
                feeds = new HashSet<String>();
                list.put(i.getName(), feeds);
            }
            feeds.add(i.getGroup2());
        }

        Map<String, Integer> result = new HashMap<String, Integer>();
        for(String cat : list.keySet()) {
            Set<String> catFeeds = list.get(cat);
            result.put(cat, catFeeds.size());
        }
        return result;
    }

    private static final int [] getDaysCount(Session session, Long idoforg, String region, Date startTime, Date endTime) {
        int count [] = new int[] {0, 0};    //  0 - будние дние; 1 - выхоные
        Calendar day = getClearCalendar(startTime.getTime());
        String orgRestrict = "";
        if(idoforg != null) {
            orgRestrict = " cf_orgs.idoforg=" + idoforg + " and ";
        }
        if(region != null && StringUtils.isBlank(region)) {
            orgRestrict = " cf_orgs.district='" + region + "' ";
        }
        Query query = session.createSQLQuery(
                "select int8(EXTRACT(EPOCH FROM d) * 1000) "
                        + "from (select distinct(date_trunc('day', to_timestamp(cf_orders.createddate / 1000))) as d "
                        + "      from cf_orders "
                        + "      join cf_orderdetails on cf_orders.idoforder=cf_orderdetails.idoforder and cf_orders.idoforg=cf_orderdetails.idoforg "
                        + "      join cf_orgs on cf_orders.idoforg=cf_orgs.idoforg "
                        + "      where cf_orderdetails.socdiscount<>0 and cf_orders.state=0 and "+ orgRestrict
                        + "            cf_orders.createddate between :start and :end) as dates "
                        + "order by 1");
        //query.setLong("idoforg", idoforg);
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
        protected Long children;          //  количество детей
        protected Long total;             //  дето/дни
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

        public Long getChildren() {
            return children;
        }

        public void setChildren(Long children) {
            this.children = children;
        }

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
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

    public static class SampleItem {
        protected double price;
        protected long createdate;
        protected String nameOfGood;

        public SampleItem(double price, long createdate, String nameOfGood) {
            this.price = price;
            this.createdate = createdate;
            this.nameOfGood = nameOfGood;
        }

        public double getPrice() {
            return price;
        }

        public long getCreatedate() {
            return createdate;
        }

        public String getNameOfGood() {
            return nameOfGood;
        }
    }
}
