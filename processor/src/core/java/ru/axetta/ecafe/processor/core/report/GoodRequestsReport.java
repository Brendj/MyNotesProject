/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DocumentState;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
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
    public static final long REQUESTS_MONITORING_TIMEOUT = 172800000;          //  2 дня
    public static final String OVERALL_TITLE = "ИТОГО";
    public static final String OVERALL_ALL_TITLE = "ВСЕГО";
    private static final String STR_YEAR_DATE_FORMAT = "dd.MM.yyyy EE";
    //private static final String STR_MONTHLY_DATE_FORMAT = "dd.MM EE";
    //private static final String STR_DAILY_DATE_FORMAT = "dd EE";
    private static final DateFormat YEAR_DATE_FORMAT = new SimpleDateFormat(STR_YEAR_DATE_FORMAT, new Locale("ru"));
    //private static final DateFormat MONTHLY_DATE_FORMAT = new SimpleDateFormat(STR_MONTHLY_DATE_FORMAT, new Locale("ru"));
    //private static final DateFormat DAILY_DATE_FORMAT = new SimpleDateFormat(STR_DAILY_DATE_FORMAT, new Locale("ru"));
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

        public GoodRequestsReport build(Session session, Boolean hideMissedColumns,
                Date startDate, Date endDate, Long idOfOrg) throws Exception{
            return build(session, hideMissedColumns,startDate,endDate, Arrays.asList(idOfOrg),null,-1, "", false);
        }

        public GoodRequestsReport build(Session session,
                Date startDate, Date endDate, List<Long> idOfSupplierList) throws Exception{
             return build(session, false,startDate,endDate, new ArrayList<Long>(0),idOfSupplierList,-1, "", true);
        }

        public GoodRequestsReport build(Session session, Boolean hideMissedColumns,
                Date startDate, Date endDate, List<Long> idOfOrgList, List<Long> idOfSupplierList,
                int requestsFilter, String goodName)
                throws Exception {
            return build(session, hideMissedColumns,startDate,endDate, idOfOrgList ,idOfSupplierList, requestsFilter, goodName, true);
        }

        private GoodRequestsReport build(Session session, Boolean hideMissedColumns,
                                        Date startDate, Date endDate, List<Long> idOfOrgList, List <Long> idOfSupplierList,
                                        int requestsFilter, String goodName, Boolean isWriteTotalRow)
                throws Exception {
            Date generateTime = new Date();
            List<RequestItem> items = new LinkedList<RequestItem>();
            GoodRequestsReport report = new GoodRequestsReport(generateTime, new Date().getTime() - generateTime.getTime(),
                    hideMissedColumns, startDate, endDate, items);

            long startDateLong = startDate.getTime();
            long endDateLong = endDate.getTime();

            String goodCondition = "";
            if (!goodName.equals("")) {
                goodCondition = "and (cf_goods.nameofgood like '%" + goodName + "%' or cf_goods.fullname like '%"+goodName+"%' )";
            }

            String productCondition = "";
            if (!goodName.equals("")) {
                productCondition = "and (cf_products.productname like '%" + goodName + "%' or cf_products.fullname like '%"+goodName+"%' )";
            }

            String stateCondition = "";
            stateCondition = " cf_goods_requests.deletedstate<>true and ";
            switch (requestsFilter) {
                case 0:
                    stateCondition = " cf_goods_requests.state=" + DocumentState.CREATED.ordinal() + " AND ";
                    break;
                case 1:
                    stateCondition = " cf_goods_requests.state=" + DocumentState.FOLLOW.ordinal() + " AND ";
                    break;
                case 2:
                    stateCondition = " cf_goods_requests.state=" + DocumentState.COMPLETED.ordinal() + " AND ";
                    break;
                default:
                    break;
            }
            //if(requestsFilter>0){
            //}
            String orgCondition = "";
            if (!(idOfOrgList==null || idOfOrgList.isEmpty())) {
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
            if (!(idOfSupplierList==null || idOfSupplierList.isEmpty())) {
                // Обработать лист с организациями
                suppliersCondition = " and (cf_menuexchangerules.idofsourceorg in (";
                for (Long idOfOrg : idOfSupplierList) {
                    if (!suppliersCondition.endsWith("(")) {
                        suppliersCondition = suppliersCondition.concat(", ");
                    }
                    suppliersCondition = suppliersCondition.concat("" +idOfOrg);
                }
                suppliersCondition = suppliersCondition + ")) ";
            }
            String notCreatedAtConfition = "";
            if (requestsFilter == -1) {
                //  Если выбрано отображение тех школ, у которых были
                // заявки указанный период, но не было заявок последнии дни
                long limit = System.currentTimeMillis() - REQUESTS_MONITORING_TIMEOUT;
                notCreatedAtConfition = "and (cf_goods_requests.createddate < " + (limit) + ") ";
            }

            String sqlGood = "select requests.idorg, requests.org, requests.orgFull, requests.shortGood, requests.good, requests.idofgood, requests.d, int8(sum(requests.cnt)) as sumcnt, sum(coalesce(requests.ds_cnt, 0)) as sumdscnt, "+
                         " sum(coalesce(requests.lcnt, 0)) as lastsumcnt, sum(coalesce(requests.lds_cnt, 0)) as lastsumdscnt"  +
                         " from (select cf_orgs.idoforg as idorg, substring(cf_orgs.officialname from '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)') as org, cf_orgs.officialname as orgFull, "+
                         "             cf_goods.fullname as good, cf_goods.nameofgood as shortGood, cf_goods.idofgood as idofgood , date_trunc('day', to_timestamp(cf_goods_requests.donedate / 1000)) as d, "+
                         "             cf_goods_requests_positions.TotalCount / 1000 as cnt, cf_goods_requests_positions.DailySampleCount / 1000 as ds_cnt," +
                         "             cf_goods_requests_positions.LastTotalCount / 1000 as lcnt, cf_goods_requests_positions.lastDailySampleCount / 1000 as lds_cnt "+
                         "       from cf_goods_requests "+
                         "      left join cf_orgs on cf_orgs.idoforg=cf_goods_requests.orgowner "+
                         "      left join cf_goods_requests_positions on cf_goods_requests.idofgoodsrequest=cf_goods_requests_positions.idofgoodsrequest "+
                         "      join cf_goods on cf_goods.idofgood=cf_goods_requests_positions.idofgood and cf_goods_requests_positions.idofgood is not null "+
                         "      " + (suppliersCondition.length() < 1 ? "" : "join cf_menuexchangerules on idofdestorg=cf_orgs.idoforg ") +
                         "      where cf_orgs.officialname<> '' and " +
                         "            " + stateCondition +
                         "            (cf_goods_requests.donedate>=" + startDateLong + " and cf_goods_requests.donedate<" + endDateLong + ") "+
                         "            " + notCreatedAtConfition +
                         "            " + goodCondition +
                         "            " + orgCondition +
                         "            " + suppliersCondition + ") as requests "+
                         "group by requests.idorg, requests.org, requests.orgFull, requests.idofgood, requests.shortGood, requests.good, requests.d "+
                         "order by requests.org, requests.idofgood, requests.d";

            String sqlProduct = "select requests.idorg, requests.org, requests.orgFull, requests.shortGood, requests.good, requests.idofgood, requests.d, int8(sum(requests.cnt)) as sumcnt, sum(coalesce(requests.ds_cnt, 0)) as sumdscnt, "+
                    "  sum(coalesce(requests.lcnt, 0)) as lastsumcnt, sum(coalesce(requests.lds_cnt, 0)) as lastsumdscnt"  +
                    "  from (select cf_orgs.idoforg as idorg, substring(cf_orgs.officialname from '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)') as org, cf_orgs.officialname as orgFull, "+
                    "             cf_products.fullname as good, cf_products.productname as shortGood, cf_products.idofproducts as idofgood , date_trunc('day', to_timestamp(cf_goods_requests.donedate / 1000)) as d, "+
                    "             cf_goods_requests_positions.totalcount / 1000 as cnt, cf_goods_requests_positions.DailySampleCount / 1000 as ds_cnt, "+
                    "             cf_goods_requests_positions.LastTotalCount / 1000 as lcnt, cf_goods_requests_positions.lastDailySampleCount / 1000 as lds_cnt "+
                    "       from cf_goods_requests "+
                    "      left join cf_orgs on cf_orgs.idoforg=cf_goods_requests.orgowner "+
                    "      left join cf_goods_requests_positions on cf_goods_requests.idofgoodsrequest=cf_goods_requests_positions.idofgoodsrequest "+
                    "      join cf_products on cf_products.idofproducts=cf_goods_requests_positions.idofproducts and cf_goods_requests_positions.idofproducts is not null"+
                    "      " + (suppliersCondition.length() < 1 ? "" : "join cf_menuexchangerules on idofdestorg=cf_orgs.idoforg ") +
                    "      where cf_orgs.officialname<> '' and " +
                    "            " + stateCondition +
                    "            (cf_goods_requests.donedate>=" + startDateLong + " and cf_goods_requests.donedate<" + endDateLong + ") "+
                    "            " + notCreatedAtConfition +
                    "            " + productCondition +
                    "            " + orgCondition +
                    "            " + suppliersCondition + ") as requests "+
                    "group by requests.idorg, requests.org, requests.orgFull, requests.idofgood, requests.shortGood, requests.good, requests.d "+
                    "order by requests.org, requests.idofgood, requests.d";

            //Map <String, RequestItem> totalItems = new TreeMap <String, RequestItem>();
            Map <Long, RequestItem> totalItems = new TreeMap <Long, RequestItem>();
            RequestItem overallItem = new TotalItem(-1L,OVERALL_TITLE, "", -1L,OVERALL_ALL_TITLE, report);

            List res = new ArrayList();
            Query queryGood = session.createSQLQuery(sqlGood);
            Query queryProduct = session.createSQLQuery(sqlProduct);
            //List res = queryGood.list();
            res.addAll(queryGood.list());
            res.addAll(queryProduct.list());
            for (Object o : res) {
                Object entry [] = (Object []) o;
                long idOfOrg   = Long.valueOf(entry[0].toString());
                String org      = ((String) entry [1]).trim ();
                String orgFull  = ((String) entry [2]).trim ();
                String shortGood= ((String) entry [3]).trim ();
                String good     = ((String) entry [4]).trim ();
                long idOfGood   = Long.valueOf(entry[5].toString());
                long date       = ((Timestamp) entry [6]).getTime();
                int value       = ((BigInteger) entry [7]).intValue();
                int dailySample = ((BigDecimal) entry[8]).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                int lastValue       = ((BigDecimal) entry[9]).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                int lastDailySample = ((BigDecimal) entry[10]).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();


                //RequestItem item = findItemByOrgAndGood(items, org, good);
                RequestItem item = findItemByOrgAndGood(items, org, idOfGood);
                final String name = (StringUtils.isEmpty(StringUtils.trimToNull(good))? shortGood: good);
                if (item == null) {
                    item = new RequestItem(idOfOrg, org, orgFull, idOfGood, name, report);
                    items.add(item);
                }
                item.addValue(date, new RequestValue(value));
                item.addDailySample(date, new RequestValue(dailySample));
                if(lastValue==0 || lastValue==value){
                    item.addLastValue(date, new RequestValue(value));
                } else {
                    item.addLastValue(date, new RequestValue(lastValue));
                }
                if(lastDailySample==0 || lastDailySample==dailySample){
                    item.addLastDailySample(date, new RequestValue(dailySample));
                } else {
                    item.addLastDailySample(date, new RequestValue(lastDailySample));
                }

                //  Получаем итоговый элемент по данному товару, чтобы добавить в него количество от текущей записи
                //RequestItem totalItem = totalItems.get(good);
                if(isWriteTotalRow){
                    RequestItem totalItem = totalItems.get(idOfGood);
                    if (totalItem == null) {
                        totalItem = new TotalItem(-1L,OVERALL_TITLE, "", idOfGood, name, report);
                        //totalItems.put(good, totalItem);
                        totalItems.put(idOfGood, totalItem);
                    }
                    totalItem.addValue(date, new RequestValue(value));      //  Добавляем в итог по товару
                    totalItem.addDailySample(date, new RequestValue(dailySample));
                    //totalItem.addLastValue(date, new RequestValue(lastValue));      //  Добавляем в итог по товару
                    //totalItem.addLastDailySample(date, new RequestValue(lastDailySample));
                }
                overallItem.addValue(date, new RequestValue(value));    //  Добавляем в общий итог
                overallItem.addDailySample(date, new RequestValue(dailySample));
                //overallItem.addLastValue(date, new RequestValue(lastValue));    //  Добавляем в общий итог
                //overallItem.addLastDailySample(date, new RequestValue(lastDailySample));
            }

            //  Добавляем строки с общими значениями в список товаров
            if(isWriteTotalRow){
                for (Long key : totalItems.keySet()) {
                    items.add(totalItems.get(key));
                }
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

        public RequestItem findItemByOrgAndGood(List<RequestItem>  list, String org, String good) {
            for (RequestItem i : list) {
                if (i.getOrg().equals(org) && i.getGood().equals(good)) {
                    return i;
                }
            }
            return null;
        }

        public RequestItem findItemByOrgAndGood(List<RequestItem>  list, String org, Long good) {
            for (RequestItem i : list) {
                if (i.getOrg().equals(org) && i.getIdOfGood().equals(good)) {
                    return i;
                }
            }
            return null;
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
        } else {
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
            for (long ts=startDate.getTime(); ts<endDate.getTime(); ts+=86400000){
                Date tmp = new Date(ts);
                dates.add (tmp);
            }
        }
        //  Анализируем месяц, если у первой и последней даты он разный, значит надо будет выводить даты с месяцами
        //boolean showMonths = ((Date) dates.toArray()[0]).getMonth() != ((Date) dates.toArray()[dates.size() - 1]).getMonth();
        //boolean showMonths = startDate.getMonth() != endDate.getMonth();
        //boolean showYears = startDate.getYear() != endDate.getYear();
        for (Date d : dates) {
            //DateFormat format = null;
            //if (showYears) {
            //    format = YEAR_DATE_FORMAT;
            //} else if (showMonths) {
            //    format = MONTHLY_DATE_FORMAT;
            //} else {
            //    format = DAILY_DATE_FORMAT;
            //}


            cols.add(YEAR_DATE_FORMAT.format(d));
        }
        return cols.toArray();
    }

    public boolean isHideMissedColumns() {
        return hideMissedColumns;
    }


    public static class TotalItem extends RequestItem {

        public TotalItem (Long idOfOrg, String org, String orgFull,Long idOfGood, String item, GoodRequestsReport report) {
            super(idOfOrg, org, orgFull, idOfGood, item, report);
        }

        @Override
        public void addValue(Long ts, RequestValue value) {
            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(ts);
            CalendarUtils.truncateToDayOfMonth(cal);

            //  Необходимо переписать подсчет количества товара для итоговых строк - необходимо не
            //  переписывать значения, а складывать с предыдущими
            RequestValue nowVal = values.get(cal.getTimeInMillis());
            if (nowVal != null) {
                nowVal.setValue(nowVal.getValue() + value.getValue());
            } else {
                values.put(cal.getTimeInMillis(), value);
            }
        }

        @Override
        public void addDailySample(Long ts, RequestValue value) {
            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(ts);
            CalendarUtils.truncateToDayOfMonth(cal);
            RequestValue nowVal = dailySamples.get(cal.getTimeInMillis());
            if (nowVal != null) {
                nowVal.setValue(nowVal.getValue() + value.getValue());
            } else {
                dailySamples.put(cal.getTimeInMillis(), value);
            }

        }


        @Override
        public void addLastValue(Long ts, RequestValue value) {
            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(ts);
            CalendarUtils.truncateToDayOfMonth(cal);

            //  Необходимо переписать подсчет количества товара для итоговых строк - необходимо не
            //  переписывать значения, а складывать с предыдущими
            RequestValue nowVal = lastValues.get(cal.getTimeInMillis());
            if (nowVal != null) {
                nowVal.setValue(nowVal.getValue() + value.getValue());
            } else {
                lastValues.put(cal.getTimeInMillis(), value);
            }
        }

        @Override
        public void addLastDailySample(Long ts, RequestValue value) {
            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(ts);
            CalendarUtils.truncateToDayOfMonth(cal);
            RequestValue nowVal = lastDailySamples.get(cal.getTimeInMillis());
            if (nowVal != null) {
                nowVal.setValue(nowVal.getValue() + value.getValue());
            } else {
                lastDailySamples.put(cal.getTimeInMillis(), value);
            }

        }
    }


    public static class RequestItem {
        protected List <String> result;
        protected final Long idOfOrg; // Идетификатор организации
        protected final String org; // Наименование организации
        protected final String orgFull; // Полное наименование организации
        protected final Long idOfGood; // Идентификатор  товара
        protected final String good; // Наименование товара
        protected Map<Long, RequestValue> values = new TreeMap<Long, RequestValue>();
        protected GoodRequestsReport report;
        protected Map<Long, RequestValue> dailySamples = new TreeMap<Long, RequestValue>();
        protected Map<Long, RequestValue> lastValues = new TreeMap<Long, RequestValue>();
        protected Map<Long, RequestValue> lastDailySamples = new TreeMap<Long, RequestValue>();


        public RequestItem(Long idOfOrg, String org, String orgFull, long idOfGood, String good, GoodRequestsReport report) {
            this.idOfOrg = idOfOrg;
            this.org = org;
            this.orgFull = orgFull;
            this.good = good;
            this.idOfGood = idOfGood;
            this.report = report;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
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

        public Long getIdOfGood() {
            return idOfGood;
        }

        public Set<Date> getDates () {
            Set <Date> res = new TreeSet<Date>();
            for (Long ts : values.keySet()) {
                res.add(new Date(ts.longValue()));
            }
            return res;
        }


        public String getStyle (String colName) {
            String style = "";

            try
            {
                if (org.equals(OVERALL_TITLE)) {
                    style = style + "font-weight: bold; ";
                }
                if (good.equals(OVERALL_ALL_TITLE)) {
                    style = style + "color: #10185C; ";
                }

                return style;
            } catch ( Exception e) {
                return "";
            }
        }


        public String getBackgoundColor (String colName) {
            String style = "";

            try
            {
                Calendar now = new GregorianCalendar();
                now.setTimeInMillis(System.currentTimeMillis());
                CalendarUtils.truncateToDayOfMonth(now);
                Calendar cal = getColumnDate (colName);
                //  Проверяем, является ли текущий столбец сегодняшней датой, и если да, то добавляем задний фон
                if (now.getTimeInMillis() == cal.getTimeInMillis()) {
                    style = "background-color: lightgreen; ";
                }

                return style;
            } catch ( Exception ignore) {
                return "";
            }
        }

        public String getRowValue(String colName, int dailySamplesMode) {
           String dailySample = getDailySample(colName);
           return getValue(colName) + (dailySamplesMode == 1 && !dailySample.equals("0") ? ("/" + dailySample) : "");
        }

        public String getRowLastValue(String colName, int dailySamplesMode) {
            String lastDailySample = getDailySample(colName);
            return getLastValue(colName) +(dailySamplesMode == 1 && !lastDailySample.equals("0") ? ("/" + lastDailySample) : "");
        }

        public String getValue (String colName) {
            //  Если это значение по умолчанию, то не делаем проверку по месяцам
            String val = getDefaultValue (colName, report);
            if (val != null) {
                return val;
            }

            try
                {
                Calendar cal = getColumnDate (colName);

                //return "" + new BigDecimal(values.get(cal.getTimeInMillis()).getValue()).setScale(1, BigDecimal.ROUND_HALF_DOWN);
                return "" + new BigDecimal(values.get(cal.getTimeInMillis()).getValue()).setScale(0, BigDecimal.ROUND_HALF_DOWN);
            } catch ( Exception e) {
                return "0";
            }
        }

        public String getDailySample(String colName) {
            try {
                Calendar cal = getColumnDate(colName);
                RequestValue rv = dailySamples.get(cal.getTimeInMillis());
                return rv != null ? String.valueOf(rv.getValue()) : "0";
            } catch (Exception e) {
                return "0";
            }
        }

        public String getLastValue(String colName) {
            try {
                Calendar cal = getColumnDate(colName);
                RequestValue rv = lastValues.get(cal.getTimeInMillis());
                return rv != null ? String.valueOf(rv.getValue()) : "0";
            } catch (Exception e) {
                return "0";
            }
        }

        public String getLastDailySample(String colName) {
            try {
                Calendar cal = getColumnDate(colName);
                RequestValue rv = lastDailySamples.get(cal.getTimeInMillis());
                return rv != null ? String.valueOf(rv.getValue()) : "0";
            } catch (Exception e) {
                return "0";
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


        public Calendar getColumnDate (String colName) throws ParseException {
            //  Если это не столбец по умолчанию, значит это дата - берем значение из массива, используя дату
            //  Используем дату от первого значений - нам понадоббятся его месяц и год
            //Calendar firstDate = new GregorianCalendar();
            //firstDate.setTimeInMillis(values.keySet().iterator().next());
            //Calendar cal = new GregorianCalendar();
            //int day = -1;
            //int month = firstDate.get(Calendar.MONTH);
            //int year = firstDate.get(Calendar.YEAR);
            //String parts [] = colName.split("\\.");
            //if (parts.length == 3) {
            //    day = Integer.parseInt(parts[0]);
            //    month = Integer.parseInt(parts[1]) - 1;
            //    year = Integer.parseInt(parts[2]);
            //} else if (parts.length == 2) {
            //    day = Integer.parseInt(parts[0]);
            //    month = Integer.parseInt(parts[1]) - 1;
            //} else {
            //    day = Integer.parseInt(parts[0]);
            //}
            /*if (colName.indexOf(".") > 0) {
                //  определяем есть ли месяц - если есть, значит будем использовать месяц + день
                day = Integer.parseInt(colName.substring(0, colName.indexOf(".")));
                month = Integer.parseInt(colName.substring(colName.indexOf(".") + 1)) - 1;
            } else {
                //  если месяца нет, то получаем его у первого значения
                day = Integer.parseInt(colName);
                month = firstDate.get(Calendar.MONTH);
            }*/
            //cal.set(Calendar.DAY_OF_MONTH, day);
            //cal.set(Calendar.MONTH, month);
            //cal.set(Calendar.YEAR, year);
            //CalendarUtils.truncateToDayOfMonth(cal);
            //DateFormat format = null;
            //final int length = colName.length();
            //if(length==STR_YEAR_DATE_FORMAT.length()){
            //    format = YEAR_DATE_FORMAT;
            //}
            //if(length==STR_MONTHLY_DATE_FORMAT.length()){
            //    format = MONTHLY_DATE_FORMAT;
            //}
            //if(length==STR_DAILY_DATE_FORMAT.length()){
            //    format = DAILY_DATE_FORMAT;
            //}
            Date date = YEAR_DATE_FORMAT.parse(colName);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            CalendarUtils.truncateToDayOfMonth(calendar);
            return calendar;
        }

        public void addValue(Long ts, RequestValue value) {
            Date date = CalendarUtils.truncateToDayOfMonth(new Date(ts));
            values.put(date.getTime(), value);
        }

        public void addDailySample(Long ts, RequestValue value) {
            Date date = CalendarUtils.truncateToDayOfMonth(new Date(ts));
            dailySamples.put(date.getTime(), value);
        }

        public void addLastValue(Long ts, RequestValue value) {
            Date date = CalendarUtils.truncateToDayOfMonth(new Date(ts));
            lastValues.put(date.getTime(), value);
        }

        public void addLastDailySample(Long ts, RequestValue value) {
            Date date = CalendarUtils.truncateToDayOfMonth(new Date(ts));
            lastDailySamples.put(date.getTime(), value);
        }
    }


    public static class RequestValue {
        private int value;

        public RequestValue (int value) {
            this.value = value;
        }

        public int getValue () {
            return value;
        }

        public void setValue (int value) {
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