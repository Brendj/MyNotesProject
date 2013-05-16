/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import org.hibernate.Query;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 25.04.13
 * Time: 16:45
 * To change this template use File | Settings | File Templates.
 */
public class GoodRequestsReport extends BasicReport {
    private static final DateFormat MONTHLY_DATE_FORMAT = new SimpleDateFormat("dd.MM");
    private static final DateFormat DAILY_DATE_FORMAT = new SimpleDateFormat("dd");
    private final List<RequestItem> items;
    private boolean hideMissedColumns;
    private List <String> cols;
    private Date startDate;
    private Date endDate;
    private String prevOrg = "";     //  Все 3 переменных хранят в себе значения от предыдущих строк, используется для
    private String prevOrgFull = ""; //  определения надо ли отображать название Орга или Товара в отчете
    private String pregGood = "";    //

    private static final String ORG_NUM = "Номер ОУ";
    private static final String ORG_NAME = "Наименование ОУ";
    private static final String GOOD_NAME = "Товар";
    private static final List <ReportColumn> DEFAULT_COLUMNS = new ArrayList <ReportColumn> ();
    static
        {
        DEFAULT_COLUMNS.add(new ReportColumn (ORG_NUM));
        DEFAULT_COLUMNS.add(new ReportColumn (ORG_NAME));
        DEFAULT_COLUMNS.add(new ReportColumn (GOOD_NAME));
        }


    public static class Builder {

        public GoodRequestsReport build(Session session, Boolean hideMissedColumns, String goodName,
                                        Date startDate, Date endDate, List<Long> idOfOrgList, List <Long> idOfSupplierList)
                throws Exception {
            Date generateTime = new Date();
            List<RequestItem> items = new LinkedList<RequestItem>();
            GoodRequestsReport report = new GoodRequestsReport(generateTime, new Date().getTime() - generateTime.getTime(),
                    hideMissedColumns, startDate, endDate, items);

            String goodCondition = "";
            if (goodName != null && goodName.length() > 0) {
                goodCondition = " and (cf_goods.fullname like '%" + goodName + "%')";
            }

            String orgCondition = "";
            if (!idOfOrgList.isEmpty()) {
                // Обработать лист с организациями
                orgCondition = " and (cf_goods_requests.orgowner in (";
                for (Long idOfOrg : idOfOrgList) {
                    if (!orgCondition.endsWith("(")) {
                        orgCondition = orgCondition.concat(", ");
                    }
                    orgCondition = orgCondition.concat("" +idOfOrg);
                }
                orgCondition = orgCondition + ")) ";
            }
            String suppliersCondition = "";
            if (!idOfSupplierList.isEmpty()) {
                // Обработать лист с организациями
                suppliersCondition = " and (cf_orgs.defaultsupplier in (";
                for (Long idOfOrg : idOfSupplierList) {
                    if (!suppliersCondition.endsWith("(")) {
                        suppliersCondition = suppliersCondition.concat(", ");
                    }
                    suppliersCondition = suppliersCondition.concat("" +idOfOrg);
                }
                suppliersCondition = suppliersCondition + ")) ";
            }

            long startDateLong = startDate.getTime();
            long endDateLong = endDate.getTime();

            String sql = "select requests.org, requests.orgFull, requests.good, requests.d, int8(sum(requests.cnt)) "+
                         "from (select substring(cf_orgs.officialname from '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)') as org, cf_orgs.officialname as orgFull, "+
                         "             cf_goods.fullname as good , date_trunc('day', to_timestamp(cf_goods_requests.createddate / 1000)) as d, "+
                         "             cf_goods_requests_positions.totalcount / 100 as cnt "+
                         "      from cf_goods_requests "+
                         "      left join cf_orgs on cf_orgs.idoforg=cf_goods_requests.orgowner "+
                         "      left join cf_goods_requests_positions on cf_goods_requests.idofgoodsrequest=cf_goods_requests_positions.idofgoodsrequest "+
                         "      join cf_goods on cf_goods.idofgood=cf_goods_requests_positions.idofgood "+
                         "      where cf_orgs.officialname<> '' and (cf_goods_requests.createddate between " + startDateLong + " and " + endDateLong + ")"+
                         "            " + goodCondition +
                         "            " + orgCondition +
                         "            " + suppliersCondition + ") as requests "+
                         "group by requests.org, requests.orgFull, requests.good, requests.d "+
                         "order by requests.org, requests.good, requests.d";

            String prevOrg = "";
            String prevGood = "";
            RequestItem item = null;

            Map <String, RequestItem> totalItems = new TreeMap <String, RequestItem>();
            RequestItem overallItem = new TotalItem("ИТОГО", "", "ВСЕГО", report);

            Query query = session.createSQLQuery(sql);
            List res = query.list();
            for (Object o : res) {
                Object entry [] = (Object []) o;
                String org      = ((String) entry [0]).trim ();
                String orgFull  = ((String) entry [1]).trim ();
                String good     = ((String) entry [2]).trim ();
                long date       = ((Timestamp) entry [3]).getTime();
                long value      = ((BigInteger) entry [4]).longValue();

                if (!prevOrg.equals(org) || !prevGood.equals(good)) {
                    item = new RequestItem(org, orgFull, good, report);
                    items.add(item);
                    prevOrg = org;
                    prevGood = good;
                }
                item.addValue(date, new RequestValue(value));

                //  Получаем итоговый элемент по данному товару, чтобы добавить в него количество от текущей записи
                RequestItem totalItem = totalItems.get(good);
                if (totalItem == null) {
                    totalItem = new TotalItem("ИТОГО", "", good, report);
                    totalItems.put(good, totalItem);
                }
                totalItem.addValue(date, new RequestValue(value));      //  Добавляем в итог по товару
                overallItem.addValue(date, new RequestValue(value));    //  Добавляем в общий итог
            }

            //  Добавляем строки с общими значениями в список товаров
            for (String key : totalItems.keySet()) {
                items.add(totalItems.get(key));
            }
            items.add(overallItem);

            /*items.add(new RequestItem("1477", "ГБОУ СОШ 1477", "Школа / СД / 1-4 / Завтрак 2", report).
                    addValue(1356998400000L, new RequestValue(Math.random())).
                    addValue(1357084800000L, new RequestValue(Math.random())).
                    addValue(1357257600000L, new RequestValue(Math.random())).
                    addValue(1357430400000L, new RequestValue(Math.random())));
            items.add(new RequestItem("1477", "ГБОУ СОШ 1477", "Школа / СД / 1-4 / Завтрак 2", report).
                    addValue(1356998400000L, new RequestValue(Math.random())).
                    addValue(1357430400000L, new RequestValue(Math.random())));*/
            return report;
        }
    }

    public GoodRequestsReport() {
        super();
        this.items = Collections.emptyList();
    }

    public GoodRequestsReport(Date generateTime, long generateDuration, boolean hideMissedColumns,
                              Date startDate, Date endDate, List<RequestItem> items) {
        super(generateTime, generateDuration);
        this.items = items;
        this.hideMissedColumns = hideMissedColumns;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public List<RequestItem> getGoodRequestItems() {
        return items;
    }

    public Object [] getColumnNames () {
        if (cols != null) {
            return cols.toArray();
        }
        if (cols == null) {
            cols = new ArrayList<String>();
        }
        for (ReportColumn c : DEFAULT_COLUMNS) {
            cols.add(c.getName());
        }

        //  Если надо исключать те даты, в которых отсутствуют значения, исключаем их из списка столбцов
        if (items == null || items.size() < 1) {
            return cols.toArray();
        }
        Set <Date> dates = new TreeSet <Date> ();
        if (hideMissedColumns) {
            for (RequestItem it : items) {
                for (Date d : it.getDates()) {
                    dates.add(d);
                }
            }
        } else {
            for (long ts=startDate.getTime(); ts<=endDate.getTime(); ts+=86400000){
                Date tmp = new Date(ts);
                dates.add (tmp);
            }
        }
        //  Анализируем месяц, если у первой и последней даты он разный, значит надо будет выводить даты с месяцами
        //boolean showMonths = ((Date) dates.toArray()[0]).getMonth() != ((Date) dates.toArray()[dates.size() - 1]).getMonth();
        boolean showMonths = startDate.getMonth() != endDate.getMonth();
        for (Date d : dates) {
            cols.add(showMonths ? MONTHLY_DATE_FORMAT.format(d) : DAILY_DATE_FORMAT.format(d));
        }
        return cols.toArray();
    }

    public boolean isHideMissedColumns() {
        return hideMissedColumns;
    }


    public static class TotalItem extends RequestItem {

        public TotalItem (String org, String orgFull, String item, GoodRequestsReport report) {
            super(org, orgFull, item, report);
        }


        public TotalItem (String org, String orgFull, String item,
                Map<Long, RequestValue> values, GoodRequestsReport report) {
            super(org, orgFull, item, values, report);
        }

        public RequestItem addValue (Long ts, RequestValue value) {
            if (values == null) {
                values = new TreeMap<Long, RequestValue>();
            }

            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(ts);
            clearCalendarTime(cal);

            //  Необходимо переписать подсчет количества товара для итоговых строк - необходимо не
            //  переписывать значения, а складывать с предыдущими
            RequestValue nowVal = values.get(cal.getTimeInMillis());
            if (nowVal != null) {
                nowVal.setValue (nowVal.getValue() + value.getValue());
            } else {
                values.put(cal.getTimeInMillis(), value);
            }
            return this;
        }
    }


    public static class RequestItem {
        protected List <String> result;
        protected final String org; // Наименование организации
        protected final String orgFull; // Полное наименование организации
        protected final String good; // Наименование товара
        protected Map <Long, RequestValue> values;
        protected GoodRequestsReport report;


        public RequestItem (String org, String orgFull, String good, GoodRequestsReport report) {
            this.org = org;
            this.orgFull = orgFull;
            this.good = good;
            this.values = new TreeMap<Long, RequestValue>();
            this.report = report;
        }


        public RequestItem (String org, String orgFull, String good,
                            Map<Long, RequestValue> values, GoodRequestsReport report) {
            this.org = org;
            this.orgFull = orgFull;
            this.good = good;
            this.values = values;
            this.report = report;
        }

        public String getOrg () {
            return org;
        }

        public String getOrgFull () {
            return orgFull;
        }

        public String getGood() {
            return good;
        }

        public Set<Date> getDates () {
            Set <Date> res = new TreeSet<Date>();
            for (Long ts : values.keySet()) {
                res.add(new Date(ts.longValue()));
            }
            return res;
        }


        public String getValue (String colName) {
            //  Если это значение по умолчанию, то не делаем проверку по месяцам
            String val = getDefaultValue (colName, report);
            if (val != null) {
                return val;
            }

            try
                {
                //  Если это не столбец по умолчанию, значит это дата - берем значение из массива, используя дату
                //  Используем дату от первого значений - нам понадоббятся его месяц и год
                Calendar firstDate = new GregorianCalendar();
                firstDate.setTimeInMillis(values.keySet().iterator().next());
                Calendar cal = new GregorianCalendar();
                int day = -1;
                int month = -1;
                if (colName.indexOf(".") > 0) {
                    //  определяем есть ли месяц - если есть, значит будем использовать месяц + день
                    day = Integer.parseInt(colName.substring(0, colName.indexOf(".")));
                    month = Integer.parseInt(colName.substring(colName.indexOf(".") + 1)) - 1;
                } else {
                    //  если месяца нет, то получаем его у первого значения
                    day = Integer.parseInt(colName);
                    month = firstDate.get(Calendar.MONTH);
                }
                cal.set(Calendar.DAY_OF_MONTH, day);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.YEAR, firstDate.get(Calendar.YEAR));
                clearCalendarTime(cal);

                return "" + new BigDecimal(values.get(cal.getTimeInMillis()).getValue()).setScale(1, BigDecimal.ROUND_HALF_DOWN);
            } catch ( Exception e) {
                return "0.0";
            }
        }


        public String getDefaultValue (String colName, GoodRequestsReport report) {
            if (colName.equals(ORG_NUM)) {
                if (report.prevOrg.equals(org)) {
                    return "";
                }
                report.prevOrg = org;
                return org;
            }
            if (colName.equals(ORG_NAME)) {
                if (report.prevOrgFull.equals(orgFull)) {
                    return "";
                }
                report.prevOrgFull = orgFull;
                return orgFull;
            }
            if (colName.equals(GOOD_NAME)) {
                /*if (report.pregGood.equals(good)) {
                    return "";
                }
                report.pregGood= good;*/
                return good;
            }
            return null;
        }

        public static void clearCalendarTime (Calendar cal) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }


        public RequestItem addValue (Long ts, RequestValue value) {
            if (values == null) {
                values = new TreeMap<Long, RequestValue>();
            }
            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(ts);
            clearCalendarTime(cal);
            values.put(cal.getTimeInMillis(), value);
            return this;
        }
    }


    public static class RequestValue {
        private double value;

        public RequestValue (double value) {
            this.value = value;
        }

        public double getValue () {
            return value;
        }

        public void setValue (double value) {
            this.value = value;
        }
    }

    public static class ReportColumn {
        private String name;

        public ReportColumn (String name) {
            this.name = name;
        }

        public String getName () {
            return name;
        }
    }
}
