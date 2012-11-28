/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import com.google.visualization.datasource.DataSourceRequest;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.ResponseStatus;
import com.google.visualization.datasource.base.StatusType;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;
import com.google.visualization.datasource.render.JsonRenderer;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 09.11.12
 * Time: 18:57
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class ProjectStateReportService {

    private static final int ACTIVE_CHART_DATA = 100;
    private static final int ACTIVE_CHART_1_DATA = 101;
    private static final int ACTIVE_CHART_2_DATA = 102;
    private static final int ACTIVE_CHART_3_DATA = 103;
    private static final int ACTIVE_CHART_4_DATA = 104;
    private static final int UNIQUE_CHART_DATA = 200;
    private static final int UNIQUE_CHART_1_DATA = 201;
    private static final int UNIQUE_CHART_2_DATA = 202;
    private static final int UNIQUE_CHART_3_DATA = 203;
    private static final int UNIQUE_CHART_4_DATA = 204;
    private static final int CONTENTS_CHART_DATA = 300;
    private static final int REFILL_CHART_DATA = 400;
    private static final int INFORMING_CHART_DATA = 500;
    private static final int BENEFIT_PART_CHART_DATA = 600;
    private static final int BENEFITS_CHART_DATA = 700;
    private static final int VISITORS_CHART_DATA = 800;
    private static final int VISITORS_CHART_1_DATA = 801;
    private static final int VISITORS_CHART_2_DATA = 802;


    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM");
    private static final DateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProjectStateReportService.class);
    public static final Map<String, Type> TYPES;

    static {
        TYPES = new HashMap<String, Type>();
        TYPES.put("ActiveChart", new ComplexType(new Type[]{
                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                        "from (select distinct cf_enterevents.idoforg as v, date_trunc('day', to_timestamp(cf_enterevents.evtdatetime / 1000)) as d "
                        +
                        "from cf_enterevents " +
                        "where cf_enterevents.evtdatetime between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "                                         EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 "
                        +
                        "union " +
                        "select distinct cf_orders.idoforg as v, date_trunc('day', to_timestamp(cf_orders.createddate / 1000)) as d "
                        +
                        "from cf_orders " +
                        "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "                                    EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000) as oo "
                        +
                        "group by d " +
                        "order by 1", ACTIVE_CHART_1_DATA).setIncremental(true),

                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                        "from (select distinct cf_enterevents.idoforg as v, date_trunc('day', to_timestamp(cf_enterevents.evtdatetime / 1000)) as d "
                        +
                        "from cf_enterevents " +
                        "where cf_enterevents.evtdatetime between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "                                         EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 ) as oo "
                        +
                        "group by d " +
                        "order by 1", ACTIVE_CHART_2_DATA).setIncremental(true),

                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                        "from (select distinct cf_orders.idoforg as v, date_trunc('day', to_timestamp(cf_orders.createddate / 1000)) as d "
                        +
                        "from cf_orders " +
                        "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "                                    EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 AND "
                        +
                        "      cf_orders.socdiscount=0) as oo " +
                        "group by d " +
                        "order by 1", ACTIVE_CHART_3_DATA).setIncremental(true),

                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                        "from (select distinct cf_orders.idoforg as v, date_trunc('day', to_timestamp(cf_orders.createddate / 1000)) as d "
                        +
                        "from cf_orders " +
                        "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "                                    EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 AND "
                        +
                        "      cf_orders.socdiscount<>0) as oo " +
                        "group by d " +
                        "order by 1", ACTIVE_CHART_4_DATA).setIncremental(true)}, new Object[][]{
                {ValueType.DATE, "Год"}, {ValueType.NUMBER, "Общее количество ОУ в проекте"},
                {ValueType.NUMBER, "ОУ, оказывающие услугу ПРОХОД"},
                {ValueType.NUMBER, "ОУ, оказывающие услугу Платного питания по безналичному расчету"},
                {ValueType.NUMBER, "ОУ, отражающие в системе услугу Льготного питания"}}, ACTIVE_CHART_DATA));
        TYPES.put("UniqueChart", new ComplexType(new Type[]{
                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                        "from (select distinct cf_enterevents.idofclient as v, date_trunc('day', to_timestamp(cf_enterevents.evtdatetime / 1000)) as d "
                        +
                        "from cf_enterevents " +
                        "where cf_enterevents.evtdatetime between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "                                         EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 "
                        +
                        "union " +
                        "select distinct cf_orders.idofclient as v, date_trunc('day', to_timestamp(cf_orders.createddate / 1000)) as d "
                        +
                        "from cf_orders " +
                        "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "                                    EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000) as oo "
                        +
                        "group by d " +
                        "order by 1", UNIQUE_CHART_1_DATA).setIncremental(true),

                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                        "from (select distinct cf_enterevents.idofclient as v, date_trunc('day', to_timestamp(cf_enterevents.evtdatetime / 1000)) as d "
                        +
                        "from cf_enterevents " +
                        "where cf_enterevents.evtdatetime between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "                                         EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000) as oo "
                        +
                        "group by d " +
                        "order by 1", UNIQUE_CHART_2_DATA).setIncremental(true),

                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                        "from (select distinct cf_orders.idofclient as v, date_trunc('day', to_timestamp(cf_orders.createddate / 1000)) as d "
                        +
                        "from cf_orders " +
                        "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "                                    EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 AND "
                        +
                        "      cf_orders.socdiscount=0) as oo " +
                        "group by d " +
                        "order by 1", UNIQUE_CHART_3_DATA).setIncremental(true),

                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                        "from (select distinct cf_orders.idofclient as v, date_trunc('day', to_timestamp(cf_orders.createddate / 1000)) as d "
                        +
                        "from cf_orders " +
                        "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "                                    EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 AND "
                        +
                        "      cf_orders.socdiscount<>0) as oo " +
                        "group by d " +
                        "order by 1", UNIQUE_CHART_4_DATA).setIncremental(true)}, new Object[][]{
                {ValueType.DATE, "Год"}, {ValueType.NUMBER, "Число уникальных пользователей в день"},
                {ValueType.NUMBER, "Число уникальных пользователей услуги ПРОХОД"},
                {ValueType.NUMBER, "Число уникальных пользователей, получивших платное питание"},
                {ValueType.NUMBER, "Число уникальных пользователей, получивших льготное питание"}}, UNIQUE_CHART_DATA));
        TYPES.put("ContentsChart",
                new SimpleType("select cf_orderdetails.menugroup as g, count(cf_orderdetails.idoforder) as c " +
                        "from cf_orders  " +
                        "left join cf_orderdetails on cf_orders.idoforg=cf_orderdetails.idoforg and cf_orders.idoforder=cf_orderdetails.idoforder "
                        +
                        "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "                                    EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 AND "
                        +
                        "      cf_orderdetails.menugroup<>'' " +
                        "group by cf_orderdetails.menugroup", new Object[][]{
                        {ValueType.TEXT, "Группа меню"}, {ValueType.NUMBER, "Покупок"}}, CONTENTS_CHART_DATA)
                        .setPostReportMethod("parseContentsChart"));
        TYPES.put("RefillChart",
                new SimpleType("select cf_contragents.contragentname, count(cf_clientpayments.idofclientpayment) " +
                        "from cf_contragents " +
                        "left join cf_clientpayments on cf_contragents.idofcontragent=cf_clientpayments.idofcontragent "
                        +
                        "where cf_clientpayments.paysum<>0 " +
                        "group by cf_contragents.contragentname " +
                        "order by 1", new Object[][]{
                        {ValueType.TEXT, "Способ пополнения"}, {ValueType.NUMBER, "Количество пополнений"}},
                        REFILL_CHART_DATA).setPostReportMethod("parseRefillChart"));
        TYPES.put("InformingChart",
                new SimpleType("select 'Не предоставлены данные для информирования', count(cf_clients.idofclient) " +
                        "from cf_clients " +
                        "left join cf_cards on cf_clients.idofclient=cf_cards.idofclient " +
                        "where cf_clients.email='' and cf_clients.mobile='' and cf_cards.state=0 " +

                        "union " +
                        "select 'В систему внесен электронный почтовый адрес', count(cf_clients.idofclient) " +
                        "from cf_clients " +
                        "left join cf_cards on cf_clients.idofclient=cf_cards.idofclient " +
                        "where cf_clients.email<>'' and cf_cards.state=0 " +

                        "union " +

                        "select 'В систему внесен номер мобильного телефона', count(cf_clients.idofclient) " +
                        "from cf_clients " +
                        "left join cf_cards on cf_clients.idofclient=cf_cards.idofclient " +
                        "where cf_clients.mobile<>'' and cf_cards.state=0", new Object[][]{
                        {ValueType.TEXT, "Способ информирования"}, {ValueType.NUMBER, "Количество клиентов"}},
                        INFORMING_CHART_DATA));
        TYPES.put("BenefitPartChart",
                new SimpleType("select \'Льготные категории 1-4 класс\', count(cf_clients.idofclient) " +
                        "from cf_clients " +
                        "left join cf_cards on cf_clients.idOfClient=cf_cards.idOfClient " +
                        "left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg "
                        +
                        "where cf_cards.state=0 AND CAST(substring(groupname FROM \'[0-9]+\') AS INTEGER)<>0 and CAST(substring(groupname FROM \'[0-9]+\') AS INTEGER)<=4 "
                        +

                        "union " +

                        "select \'Прочите льготные категории\', count(cf_clients.idofclient) " +
                        "from cf_clients " +
                        "where cf_clients.discountmode<>0 " +

                        "union " +

                        "select \'Не имеющие льгот\', count(distinct cf_clients.idofclient) " +
                        "from cf_clients " +
                        "left join cf_cards on cf_clients.idOfClient=cf_cards.idOfClient " +
                        "left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg "
                        +
                        "where cf_cards.state=0 AND CAST(substring(groupname FROM \'[0-9]+\') AS INTEGER)<>0",
                        new Object[][]{
                                {ValueType.TEXT, "Льготные категории по питанию в общем составе учащихся"},
                                {ValueType.NUMBER, "Количество учащихся"}}, BENEFIT_PART_CHART_DATA));
        TYPES.put("BenefitsChart", new SimpleType("select cf_categorydiscounts.categoryname, count (cat) " +
                "from ( " +
                "select trim(split_part(categoriesdiscounts, ',', 1)) as cat " +
                "from cf_clients " +

                "union all " +
                "select trim(split_part(categoriesdiscounts, ',', 2)) as cat " +
                "from cf_clients " +

                "union all " +
                "select split_part(categoriesdiscounts, ',', 3) as cat " +
                "from cf_clients " +

                "union all " +
                "select split_part(categoriesdiscounts, ',', 4) as cat " +
                "from cf_clients " +

                "union all " +
                "select split_part(categoriesdiscounts, ',', 5) as cat " +
                "from cf_clients) as tbl " +
                "left join cf_categorydiscounts on int8(cat)=cf_categorydiscounts.idofcategorydiscount " +
                "where cat <> '' and int8(cat)>0 " +
                "group by cf_categorydiscounts.categoryname", new Object[][]{
                {ValueType.TEXT, "Детализация льготных категорий кроме 1-4 класса"},
                {ValueType.NUMBER, "Количество учащихся"}}, BENEFITS_CHART_DATA));
        TYPES.put("VisitorsChart", new ComplexType(new Type[]{
                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000 as date, " +
                        "       int8(sum(evnt_count) / sum(all_count) * 100) as visited " +
                        "from (select events.d as d, overall.o, overall.c as all_count, count(events.c) as evnt_count, cast(count(events.c) as float8)/cast(overall.c as float8) "
                        +
                        "from (select cf_clients.idoforg as o, count(cf_clients.idofclient) as c " +
                        "      from cf_clients " +
                        "      left join cf_cards on cf_clients.idOfClient=cf_cards.idOfClient " +
                        "      left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg "
                        +
                        "      where cf_cards.state=0 AND CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<>0 and CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<=4 "
                        +
                        "      group by cf_clients.idoforg) as overall " +
                        "join " +
                        "(select DISTINCT cf_enterevents.idofclient as c, cf_enterevents.idoforg as o, date_trunc('day', to_timestamp(cf_enterevents.evtdatetime / 1000)) as d "
                        +
                        "from cf_enterevents " +
                        "left join cf_clients on cf_clients.idOfClient=cf_enterevents.idOfClient " +
                        "left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg "
                        +
                        "where CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<>0 and CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<=4 "
                        +
                        "      and cf_enterevents.evtdatetime BETWEEN EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 "
                        +
                        "group by cf_enterevents.idofclient, cf_enterevents.idoforg, cf_enterevents.evtdatetime) events on events.o = overall.o "
                        +
                        "group by events.d, overall.o, overall.c " +
                        "having cast(count(events.c) as float8)/cast(overall.c as float8) > 0.2) as res " +
                        "group by d", VISITORS_CHART_1_DATA).setIncremental(true),
                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000 as date, " +
                        "       int8(sum(evnt_count) / sum(all_count) * 100) as visited " +
                        "from (select events.d as d, overall.o, overall.c as all_count, count(events.c) as evnt_count, cast(count(events.c) as float8)/cast(overall.c as float8) "
                        +
                        "from (select cf_clients.idoforg as o, count(cf_clients.idofclient) as c " +
                        "      from cf_clients " +
                        "      left join cf_cards on cf_clients.idOfClient=cf_cards.idOfClient " +
                        "      left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg "
                        +
                        "      where cf_cards.state=0 AND CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<>0 and CAST(substring(groupname FROM '[0-9]+') AS INTEGER)>=5 "
                        +
                        "      group by cf_clients.idoforg) as overall " +
                        "join " +
                        "(select DISTINCT cf_enterevents.idofclient as c, cf_enterevents.idoforg as o, date_trunc('day', to_timestamp(cf_enterevents.evtdatetime / 1000)) as d "
                        +
                        "from cf_enterevents " +
                        "left join cf_clients on cf_clients.idOfClient=cf_enterevents.idOfClient " +
                        "left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg "
                        +
                        "where CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<>0 and CAST(substring(groupname FROM '[0-9]+') AS INTEGER)>=5 "
                        +
                        "      and cf_enterevents.evtdatetime BETWEEN EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 "
                        +
                        "group by cf_enterevents.idofclient, cf_enterevents.idoforg, cf_enterevents.evtdatetime) events on events.o = overall.o "
                        +
                        "group by events.d, overall.o, overall.c " +
                        "having cast(count(events.c) as float8)/cast(overall.c as float8) > 0.2) as res " +
                        "group by d", VISITORS_CHART_2_DATA).setIncremental(true)}, new Object[][]{
                {ValueType.DATE, "Дата"}, {ValueType.NUMBER, "1-4 класс"}, {ValueType.NUMBER, "5-11 класс"}},
                VISITORS_CHART_DATA));
    }

    private static final String INSERT_SQL = "INSERT INTO cf_projectstate_data (GenerationDate, Period, Type, StringKey, StringValue) VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_SQL = "DELETE FROM cf_projectstate_data WHERE Period=? AND Type=?";
    private static final String SELECT_SQL = "SELECT StringKey, StringValue FROM cf_projectstate_data WHERE Type=? and Period<=? order by Period DESC, StringKey";
    private static final String PERIODIC_SELECT_SQL = "SELECT distinct StringKey, StringValue FROM cf_projectstate_data WHERE INT8(StringKey) <= EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 AND Type=? order by StringKey";
    private static final String CHECK_SQL = "SELECT Period FROM cf_projectstate_data WHERE Type=? order by Period DESC";


    public static boolean isOn() {
        return RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_PROJECT_STATE_REPORT_ON);
    }


    public static void setOn(boolean on) {
        RuntimeContext.getInstance()
                .setOptionValueWithSave(Option.OPTION_PROJECT_STATE_REPORT_ON, "" + (on ? "1" : "0"));
    }


    public void run() {
        setOn(false);
        if (!RuntimeContext.getInstance().isMainNode() || !isOn()) {
            //logger.info ("Project State is turned off. You have to activate this tool using common Settings");
            return;
        }


        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            Session session = runtimeContext.createPersistenceSession();
            for (String t : TYPES.keySet()) {
                parseType(session, TYPES.get(t));
            }
        } catch (Exception e) {
        }
    }


    public void parseType(Session session, Type t) {
        try {
            if (t instanceof SimpleType) {
                Map<String, String> data = loadData(session, (SimpleType) t);
                if (data == null) {
                    return;
                }
                executePostMethod((SimpleType) t, data, ((SimpleType) t).getPostSelectSQLMethod());
                saveData(session, data, t);
            } else if (t instanceof ComplexType) {
                ComplexType ct = (ComplexType) t;
                Type types[] = ct.getTypes();
                for (Type t2 : types) {
                    parseType(session, t2);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to process type " + t.getReportType(), e);
        }
    }


    @Transactional
    public void saveData(Session session, Map<String, String> data, Type t) {
        try {
            if (data.isEmpty()) {
                return;
            }

            long ms = System.currentTimeMillis();
            Calendar cal = getToday();


            org.hibernate.Query q = session.createSQLQuery(DELETE_SQL);
            q.setLong(0, cal.getTimeInMillis());
            q.setInteger(1, t.getReportType());
            q.executeUpdate();


            q = session.createSQLQuery(INSERT_SQL);
            q.setLong(0, ms);
            q.setLong(1, cal.getTimeInMillis());
            q.setInteger(2, t.getReportType());
            for (String k : data.keySet()) {
                q.setString(3, k);
                q.setString(4, data.get(k));
                q.executeUpdate();
            }
        } catch (Exception e) {
            logger.error("Failed to save report data into database for " + t.getReportType(), e);
        }
    }


    public Calendar getLastUploadData(Session session, SimpleType t) {
        try {
            org.hibernate.Query q = session.createSQLQuery(applyMacroReplace(CHECK_SQL));
            q.setInteger(0, t.getReportType());
            List resultList = q.list();
            for (Object entry : resultList) {
                BigInteger e = (BigInteger) entry;
                Calendar res = Calendar.getInstance();
                res.setTimeInMillis(e.longValue());
                return res;
            }
        } catch (Exception e) {
            logger.error("Failed to check existance of data for report " + t.getReportType(), e);
        }

        return getStartDate();
    }


    public Map<String, String> loadData(Session session, SimpleType t) {
        try {
            Map<String, String> result = new TreeMap<String, String>();
            Calendar lastUpload = getLastUploadData(session, t);
            Calendar today = getToday();
            if (t.isIncremental() && today.getTimeInMillis() <= lastUpload.getTimeInMillis()) {
                return result;
            }
            org.hibernate.Query q = session.createSQLQuery(applyMacroReplace(t.getSQL(), lastUpload, today));
            List resultList = q.list();
            for (Object entry : resultList) {
                Object e[] = (Object[]) entry;
                String key = (String) e[0];
                String value = null;

                if (t.getValueType() == Integer.class) {
                    value = "" + ((BigInteger) e[1]).intValue();
                }
                if (t.getValueType() == Double.class) {
                    value = "" + ((BigDecimal) e[1]).doubleValue();
                }
                if (t.getValueType() == String.class) {
                    value = ((String) e[1]).trim();
                }
                result.put(key, value);
            }
            return result;
        } catch (Exception e) {
            logger.error("Failed to load report data from database for " + t.getReportType(), e);
            return null;
        }
    }


    public String applyMacroReplace(String sql) {
        Calendar min = getStartDate();
        Calendar max = getToday();
        return applyMacroReplace(sql, min, max);
    }


    public static Calendar getStartDate() {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.YEAR, 2012);
        cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal;
    }


    public static Calendar getToday() {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }


    public static String applyMacroReplace(String sql, Calendar min, Calendar max) {
        if (sql.indexOf("%MINIMUM_DATE%") > -1) {
            sql = sql.replaceAll("%MINIMUM_DATE%", DB_DATE_FORMAT.format(min.getTime()));
        }
        if (sql.indexOf("%MAXIMUM_DATE%") > -1) {
            sql = sql.replaceAll("%MAXIMUM_DATE%", DB_DATE_FORMAT.format(max.getTime()));
        }
        return sql;
    }


    public static void parseContentsChart(Object dataSource) {
        Map<String, List<String>> data = (Map<String, List<String>>) dataSource;

        List<String> list = data.get("Соб. Произв.");
        data.put("Собственное производство", list);
        data.remove("Соб. Произв.");

        list = data.get("Напитки уп.");
        data.put("Напитки упакованные", list);
        data.remove("Напитки уп.");

        list = data.get("Молочная прод.");
        data.put("Молочная продукция", list);
        data.remove("Молочная прод.");

        list = data.get("Гор. блюда");
        data.put("Горячие блюда", list);
        data.remove("Гор. блюда");
    }


    public static void parseRefillChart(Object dataSource) {
        Map<String, List<String>> data = (Map<String, List<String>>) dataSource;
        List<String> list = data.get("Банк Москвы");
        data.put("Через Банк Москвы", list);
        data.remove("Банк Москвы");

        list = data.get("Конкорд-ПА");
        data.put("Через терминалы поставщика питания", list);
        data.remove("Конкорд-ПА");

        list = data.get("Сбербанк-Москва");
        data.put("Через Сбербанк-Москва", list);
        data.remove("Сбербанк-Москва");
    }


    public static void parse1_4Visitors(Object dataSource) {
        Map<String, String> data = (Map<String, String>) dataSource;
    }


    public static DataTable generateReport(RuntimeContext runtimeContext, Calendar dateAt, Calendar dateTo, Type t)
            throws IllegalArgumentException {
        if (runtimeContext == null || dateTo == null || dateAt == null || t == null) {
            throw new IllegalArgumentException("RuntimeContext, Calendar and Type cannot be null(s)");
        }

        dateAt.set(Calendar.HOUR, 0);
        dateAt.set(Calendar.MINUTE, 0);
        dateAt.set(Calendar.SECOND, 0);
        dateAt.set(Calendar.MILLISECOND, 0);
        dateTo.set(Calendar.HOUR, 0);
        dateTo.set(Calendar.MINUTE, 0);
        dateTo.set(Calendar.SECOND, 0);
        dateTo.set(Calendar.MILLISECOND, 0);

        try {
            Session session = runtimeContext.createPersistenceSession();
            Map<String, List<String>> data = loadReportData(session, dateAt, dateTo, t);
            session.close();

            if (t instanceof SimpleType) {
                executePostMethod((SimpleType) t, data, ((SimpleType) t).getPostReportSQLMethod());
            }
            DataTable dataTable = buildDataTable(data, t);
            return dataTable;
        } catch (Exception e) {
            logger.error("Failed to load data from database for report " + t.getReportType() + " generation", e);
        }
        return null;
    }


    public static void executePostMethod(SimpleType t, Map data, String method) {
        if (method == null || method.length() < 1) {
            return;
        }

        java.lang.reflect.Method meth;
        try {
            meth = ProjectStateReportService.class.getDeclaredMethod(method, Object.class);
            meth.invoke(null, data);
        } catch (Exception e) {
            logger.error("Failed to execute support method " + t.getPostReportSQLMethod());
        }
    }


    private static Map<String, List<String>> loadReportData(Session session, Calendar dateAt, Calendar dateTo, Type t) {
        return loadReportData(session, dateAt, dateTo, t, new TreeMap<String, List<String>>());
    }


    private static Map<String, List<String>> loadReportData(Session session, Calendar dateAt, Calendar dateTo, Type t,
            Map<String, List<String>> result) {
        try {
            if (t instanceof SimpleType) {
                Map<String, String> res = loadReportData(session, dateAt, dateTo, (SimpleType) t);
                if (res == null) {
                    return result;
                }
                for (String k : res.keySet()) {
                    List<String> vals = result.get(k);
                    if (vals == null) {
                        vals = new ArrayList<String>();
                        result.put(k, vals);
                    }
                    vals.add(res.get(k));
                }
            } else if (t instanceof ComplexType) {
                Type types[] = ((ComplexType) t).getTypes();
                for (Type t2 : types) {
                    loadReportData(session, dateAt, dateTo, (SimpleType) t2, result);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to parse report using type: " + t.getReportType() + " and date: " + dateTo, e);
        }
        return result;
    }


    private static Map<String, String> loadReportData(Session session, Calendar dateAt, Calendar dateTo, SimpleType t) {
        try {
            Map<String, String> result = new TreeMap<String, String>();
            org.hibernate.Query q = null;
            if (t.isIncremental()) {
                q = session.createSQLQuery(applyMacroReplace(PERIODIC_SELECT_SQL, dateAt, dateTo));
            } else {
                q = session.createSQLQuery(SELECT_SQL);
                q.setLong(1, dateTo.getTimeInMillis());
            }
            q.setInteger(0, t.getReportType());
            List resultList = q.list();

            for (Object entry : resultList) {
                Object e[] = (Object[]) entry;
                result.put(((String) e[0]).trim(), ((String) e[1]).trim());
            }
            return result;
        } catch (Exception e) {
            logger.error("Failed to load report using type: " + t.getReportType() + " and date: " + dateTo, e);
            return null;
        }
    }


    private static DataTable buildDataTable(Map<String, List<String>> data, Type t) throws TypeMismatchException {
        DataTable dt = new DataTable();
        ArrayList cd = new ArrayList();
        for (int i = 0; i < t.getColumns().length; i++) {
            Object col[] = t.getColumns()[i];
            ValueType vt = (ValueType) col[0];
            cd.add(new ColumnDescription("col" + (i + 1), vt == ValueType.DATE ? ValueType.TEXT : vt, (String) col[1]));
        }
        dt.addColumns(cd);

        for (String k : data.keySet()) {
            TableRow r = new TableRow();
            List<String> vals = data.get(k);


            if (t.getColumns()[0][0] == ValueType.TEXT) {
                r.addCell(k);
            } else if (t.getColumns()[0][0] == ValueType.DATE) {
                Date d = new Date(Long.parseLong(k));
                if (d.getDay() == 0 || d.getDay() == 6) {
                    continue;
                }
                r.addCell(DATE_FORMAT.format(d));
            } else if (t.getColumns()[0][0] == ValueType.NUMBER) {
                r.addCell(Integer.parseInt(k));
            }

            for (int i = 1; i < t.getColumns().length; i++) {
                Object col[] = t.getColumns()[i];
                if ((ValueType) col[0] == ValueType.TEXT) {
                    if (i - 1 >= vals.size()) {
                        r.addCell("");
                    } else {
                        r.addCell(vals.get(i - 1));
                    }
                } else if ((ValueType) col[0] == ValueType.NUMBER) {
                    if (i - 1 >= vals.size()) {
                        r.addCell(0);
                    } else {
                        r.addCell(Integer.parseInt(vals.get(i - 1)));
                    }
                }
            }
            dt.addRow(r);
        }

        return dt;
    }


    protected static class ComplexType implements Type {

        private Type types[];
        protected Object[][] columns;
        private int type;
        private boolean incremental;


        public ComplexType(Type types[], Object[][] columns, int type) {
            this.types = types;
            this.columns = columns;
            this.type = type;
        }

        public Object[][] getColumns() {
            return columns;
        }

        public Type[] getTypes() {
            return types;
        }

        public int getReportType() {
            return type;
        }

        public ComplexType setIncremental(boolean incremental) {
            this.incremental = incremental;
            return this;
        }

        public boolean isIncremental() {
            return incremental;
        }
    }


    protected static class SimpleType implements Type {

        private String postSelectSQL;
        private String postReport;
        private String sql;
        protected Object[][] columns;
        private int type;
        private Class valueType;
        private boolean incremental = false;


        public SimpleType(String sql, int type) {
            this.sql = sql;
            this.columns = null;
            this.type = type;
            valueType = Integer.class;
        }

        public SimpleType(String sql, Object[][] columns, int type) {
            this.sql = sql;
            this.columns = columns;
            this.type = type;
            valueType = Integer.class;
        }

        public SimpleType(String sql, Object[][] columns, int type, Class valueType) {
            this.sql = sql;
            this.columns = columns;
            this.type = type;
            this.valueType = valueType;
        }

        public SimpleType stValueType(Class valueType) {
            this.valueType = valueType;
            return this;
        }

        public Object[][] getColumns() {
            return columns;
        }

        public Class getValueType() {
            return valueType;
        }

        public String getSQL() {
            return sql;
        }

        public int getReportType() {
            return type;
        }

        public SimpleType setPostSelectSQLMethod(String postSelectSQL) {
            this.postSelectSQL = postSelectSQL;
            return this;
        }

        public String getPostSelectSQLMethod() {
            return postSelectSQL;
        }

        public SimpleType setPostReportMethod(String postReport) {
            this.postReport = postReport;
            return this;
        }

        public String getPostReportSQLMethod() {
            return postReport;
        }

        public SimpleType setIncremental(boolean incremental) {
            this.incremental = incremental;
            return this;
        }

        public boolean isIncremental() {
            return incremental;
        }
    }


    public interface Type {

        public Object[][] getColumns();

        public int getReportType();

        public boolean isIncremental();
    }
}