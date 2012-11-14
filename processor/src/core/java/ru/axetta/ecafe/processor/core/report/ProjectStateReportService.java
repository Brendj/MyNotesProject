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

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
public class ProjectStateReportService
    {
    private static final int ACTIVE_CHART_DATA       = 100;
    private static final int ACTIVE_CHART_1_DATA     = 101;
    private static final int ACTIVE_CHART_2_DATA     = 102;
    private static final int ACTIVE_CHART_3_DATA     = 103;
    private static final int ACTIVE_CHART_4_DATA     = 104;
    private static final int UNIQUE_CHART_DATA       = 200;
    private static final int UNIQUE_CHART_1_DATA     = 201;
    private static final int UNIQUE_CHART_2_DATA     = 202;
    private static final int UNIQUE_CHART_3_DATA     = 203;
    private static final int UNIQUE_CHART_4_DATA     = 204;
    private static final int CONTENTS_CHART_DATA     = 300;
    private static final int REFILL_CHART_DATA       = 400;
    private static final int INFORMING_CHART_DATA    = 500;
    private static final int BENEFIT_PART_CHART_DATA = 600;
    private static final int BENEFITS_CHART_DATA     = 700;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat ("dd.MM.yyyy");
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger (ProjectStateReportService.class);
    private ServletConfig config;
    public static final Map<String, Type> TYPES;
    static
        {
        TYPES = new HashMap<String, Type>();
        TYPES.put ("ActiveChart",
                new ComplexType (new Type [] { new SimpleType ("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                                                               "from (select distinct cf_enterevents.idoforg as v, date_trunc('day', to_timestamp(cf_enterevents.evtdatetime / 1000)) as d " +
                                                               "from cf_enterevents " +
                                                               "where cf_enterevents.evtdatetime between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND " +
                                                               "                                         EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 " +
                                                               "union " +
                                                               "select distinct cf_orders.idoforg as v, date_trunc('day', to_timestamp(cf_orders.createddate / 1000)) as d " +
                                                               "from cf_orders " +
                                                               "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND " +
                                                               "                                    EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000) as oo " +
                                                               "group by d " +
                                                               "order by 1", ACTIVE_CHART_1_DATA),

                                               new SimpleType ("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                                                               "from (select distinct cf_enterevents.idoforg as v, date_trunc('day', to_timestamp(cf_enterevents.evtdatetime / 1000)) as d " +
                                                               "from cf_enterevents " +
                                                               "where cf_enterevents.evtdatetime between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND " +
                                                               "                                         EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 ) as oo " +
                                                               "group by d " +
                                                               "order by 1", ACTIVE_CHART_2_DATA),

                                               new SimpleType ("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                                                               "from (select distinct cf_orders.idoforg as v, date_trunc('day', to_timestamp(cf_orders.createddate / 1000)) as d " +
                                                               "from cf_orders " +
                                                               "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND " +
                                                               "                                    EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 AND " +
                                                               "      cf_orders.socdiscount=0) as oo " +
                                                               "group by d " +
                                                               "order by 1", ACTIVE_CHART_3_DATA),

                                               new SimpleType ("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                                                               "from (select distinct cf_orders.idoforg as v, date_trunc('day', to_timestamp(cf_orders.createddate / 1000)) as d " +
                                                               "from cf_orders " +
                                                               "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND " +
                                                               "                                    EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 AND " +
                                                               "      cf_orders.socdiscount<>0) as oo " +
                                                               "group by d " +
                                                               "order by 1", ACTIVE_CHART_4_DATA)},
                        new Object [][] { { ValueType.DATE, "Дата" },
                                          { ValueType.NUMBER, "Общее количество ОУ в проекте" },
                                          { ValueType.NUMBER, "ОУ, оказывающие услугу ПРОХОД" },
                                          { ValueType.NUMBER, "ОУ, оказывающие услугу Платного питания по безналичному расчету" },
                                          { ValueType.NUMBER, "ОУ, отражающие в системе услугу Льготного питания" } },
                        ACTIVE_CHART_DATA));
        TYPES.put ("UniqueChart",
                new ComplexType (new Type [] {new SimpleType ("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                                                               "from (select distinct cf_enterevents.idofclient as v, date_trunc('day', to_timestamp(cf_enterevents.evtdatetime / 1000)) as d " +
                                                               "from cf_enterevents " +
                                                               "where cf_enterevents.evtdatetime between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND " +
                                                               "                                         EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 " +
                                                               "union " +
                                                               "select distinct cf_orders.idofclient as v, date_trunc('day', to_timestamp(cf_orders.createddate / 1000)) as d " +
                                                               "from cf_orders " +
                                                               "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND " +
                                                               "                                    EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000) as oo " +
                                                               "group by d " +
                                                               "order by 1", UNIQUE_CHART_1_DATA),

                                              new SimpleType ("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                                                           "from (select distinct cf_enterevents.idofclient as v, date_trunc('day', to_timestamp(cf_enterevents.evtdatetime / 1000)) as d " +
                                                           "from cf_enterevents " +
                                                           "where cf_enterevents.evtdatetime between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND " +
                                                           "                                         EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000) as oo " +
                                                           "group by d " +
                                                           "order by 1", UNIQUE_CHART_2_DATA),

                                              new SimpleType ("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                                                           "from (select distinct cf_orders.idofclient as v, date_trunc('day', to_timestamp(cf_orders.createddate / 1000)) as d " +
                                                           "from cf_orders " +
                                                           "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND " +
                                                           "                                    EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 AND " +
                                                           "      cf_orders.socdiscount=0) as oo " +
                                                           "group by d " +
                                                           "order by 1", UNIQUE_CHART_3_DATA),

                                              new SimpleType ("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                                                           "from (select distinct cf_orders.idofclient as v, date_trunc('day', to_timestamp(cf_orders.createddate / 1000)) as d " +
                                                           "from cf_orders " +
                                                           "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND " +
                                                           "                                    EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 AND " +
                                                           "      cf_orders.socdiscount<>0) as oo " +
                                                           "group by d " +
                                                           "order by 1", UNIQUE_CHART_4_DATA)},
                        new Object [][] { { ValueType.DATE, "Дата" },
                                          { ValueType.NUMBER, "Число уникальных пользователей в день" },
                                          { ValueType.NUMBER, "Число уникальных пользователей услуги ПРОХОД" },
                                          { ValueType.NUMBER, "Число уникальных пользователей, получивших платное питание" },
                                          { ValueType.NUMBER, "Число уникальных пользователей, получивших льготное питание" } },
                                    UNIQUE_CHART_DATA ));
        TYPES.put ("ContentsChart",
                new SimpleType ("select cf_orderdetails.menugroup as g, count(cf_orderdetails.idoforder) as c " +
                        "from cf_orders  " +
                        "left join cf_orderdetails on cf_orders.idoforg=cf_orderdetails.idoforg and cf_orders.idoforder=cf_orderdetails.idoforder " +
                        "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND " +
                        "                                    EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 AND " +
                        "      cf_orderdetails.menugroup<>'' " +
                        "group by cf_orderdetails.menugroup",
                        new Object [][] { { ValueType.TEXT, "Группа меню" },
                                          { ValueType.NUMBER, "Покупок" } },
                        CONTENTS_CHART_DATA));
        TYPES.put ("RefillChart",
                new SimpleType ("select cf_contragents.contragentname, count(cf_clientpayments.idofclientpayment) " +
                        "from cf_contragents " +
                        "left join cf_clientpayments on cf_contragents.idofcontragent=cf_clientpayments.idofcontragent " +
                        "where cf_clientpayments.paysum<>0 " +
                        "group by cf_contragents.contragentname " +
                        "order by 1",
                        new Object [][] { { ValueType.TEXT, "Способ пополнения" },
                                          { ValueType.NUMBER, "Количество пополнений" } },
                        REFILL_CHART_DATA));
        TYPES.put ("InformingChart",
                new SimpleType ("select 'Без всего', count(cf_clients.idofclient) " +
                        "from cf_clients " +
                        "left join cf_cards on cf_clients.idofclient=cf_cards.idofclient " +
                        "where cf_clients.email='' and cf_clients.mobile='' and cf_cards.state=0 " +

                        "union " +
                        "select 'E-mail', count(cf_clients.idofclient) " +
                        "from cf_clients " +
                        "left join cf_cards on cf_clients.idofclient=cf_cards.idofclient " +
                        "where cf_clients.email<>'' and cf_cards.state=0 " +

                        "union " +

                        "select 'SMS', count(cf_clients.idofclient) " +
                        "from cf_clients " +
                        "left join cf_cards on cf_clients.idofclient=cf_cards.idofclient " +
                        "where cf_clients.mobile<>'' and cf_cards.state=0",
                        new Object [][] { { ValueType.TEXT, "Способ информирования" },
                                          { ValueType.NUMBER, "Количество клиентов" } },
                        INFORMING_CHART_DATA));
        TYPES.put ("BenefitPartChart",
                new SimpleType ("select \'1-4 класс\', count(cf_clients.idofclient) " +
                        "from cf_clients " +
                        "left join cf_cards on cf_clients.idOfClient=cf_cards.idOfClient " +
                        "left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg " +
                        "where cf_cards.state=0 AND CAST(substring(groupname FROM \'[0-9]+\') AS INTEGER)<>0 and CAST(substring(groupname FROM \'[0-9]+\') AS INTEGER)<4 " +

                        "union " +

                        "select \'Льготники\', count(cf_clients.idofclient) " +
                        "from cf_clients " +
                        "where cf_clients.discountmode<>0 " +

                        "union " +

                        "select \'Всего\', count(distinct cf_clients.idofclient) " +
                        "from cf_clients " +
                        "left join cf_cards on cf_clients.idOfClient=cf_cards.idOfClient " +
                        "left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg " +
                        "where cf_cards.state=0 AND CAST(substring(groupname FROM \'[0-9]+\') AS INTEGER)<>0",
                        new Object [][] { { ValueType.TEXT, "Льготные категории" },
                                          { ValueType.NUMBER, "Количество клиентов" } },
                        BENEFIT_PART_CHART_DATA));
        TYPES.put ("BenefitsChart",
                new SimpleType ("select cf_categorydiscounts.categoryname, count (cat) " +
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
                        "group by cf_categorydiscounts.categoryname",
                        new Object [][] { { ValueType.TEXT, "Льготные категории" },
                                          { ValueType.NUMBER, "Количество клиентов" } },
                        BENEFITS_CHART_DATA));
        }
    private static final String INSERT_SQL = "INSERT INTO cf_projectstate_data (GenerationDate, Period, Type, StringKey, StringValue) VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_SQL = "DELETE FROM cf_projectstate_data WHERE Period=? AND Type=?";
    private static final String SELECT_SQL = "SELECT StringKey, StringValue FROM cf_projectstate_data WHERE Period=? AND Type=? order by StringKey";




    public void run ()
        {
        logger.info ("Execute all reports");
        RuntimeContext runtimeContext = null;
        try
            {
            runtimeContext = RuntimeContext.getInstance ();
            Session session = runtimeContext.createPersistenceSession();
            for (String t : TYPES.keySet ())
                {
                parseType(session, TYPES.get(t));
                }
            }
        catch (Exception e)
            {
            }
        }


    public void parseType (Session session, Type t)
        {
        try
            {
            if (t instanceof SimpleType)
                {
                Map <String, String> data = loadData (session, (SimpleType) t);
                if (data == null)
                    {
                    return;
                    }
                saveData (session, data, t);
                }
            else if (t instanceof ComplexType)
                {
                ComplexType ct = (ComplexType) t;
                Type types [] = ct.getTypes();
                for (Type t2 : types)
                    {
                    parseType(session, t2);
                    }
                }
            }
        catch (Exception e)
            {
            logger.error ("Failed to process type " + t.getReportType (), e);
            }
        }


    public void saveData (Session session, Map <String, String> data, Type t)
        {
        try
            {
            long ms = System.currentTimeMillis ();
            Calendar cal = Calendar.getInstance ();
            cal.setTimeInMillis (ms);
            cal.set (Calendar.HOUR, 0);
            cal.set (Calendar.MINUTE, 0);
            cal.set (Calendar.SECOND, 0);
            cal.set (Calendar.MILLISECOND, 0);


            org.hibernate.Query q = session.createSQLQuery (DELETE_SQL);
            q.setLong (0, cal.getTimeInMillis ());
            q.setInteger(1, t.getReportType());
            q.executeUpdate ();


            q = session.createSQLQuery (INSERT_SQL);
            q.setLong (0, ms);
            q.setLong (1, cal.getTimeInMillis ());
            q.setInteger (2, t.getReportType ());
                for (String k : data.keySet ())
                {
                q.setString (3, k);
                q.setString (4, data.get (k));
                q.executeUpdate ();
                }
            }
        catch (Exception e)
            {
            logger.error ("Failed to save report data into database for " + t.getReportType (), e);
            }
        }


    public Map <String, String> loadData (Session session, SimpleType t)
        {
        try
            {
            Map <String, String> result = new TreeMap <String, String> ();
            org.hibernate.Query q = session.createSQLQuery (applyMacroReplace (t.getSQL ()));
            List resultList = q.list ();
            for (Object entry : resultList)
                {
                Object e []  = (Object []) entry;
                String key   = (String) e [0];
                String value = null;
                if (t.getValueType () == Integer.class)
                    {
                    value = "" + ((BigInteger) e [1]).intValue ();
                    }
                if (t.getValueType () == Double.class)
                    {
                    value = "" + ((BigDecimal) e [1]).doubleValue ();
                    }
                if (t.getValueType () == String.class)
                    {
                    value = ((String) e [1]).trim ();
                    }
                result.put (key, value);
                }
            return result;
            }
        catch (Exception e)
            {
            logger.error ("Failed to load report data from database for " + t.getReportType (), e);
            return null;
            }
        }


    public String applyMacroReplace (String sql)
        {
        if (sql.indexOf ("%MINIMUM_DATE%") > -1)
            {
            sql = sql.replaceAll ("%MINIMUM_DATE%", "2012-09-01");
            }
        if (sql.indexOf ("%MAXIMUM_DATE%") > -1)
            {
            sql = sql.replaceAll ("%MAXIMUM_DATE%", "2012-11-01");
            }
        return sql;
        }








    public static DataTable generateReport (RuntimeContext runtimeContext,
                                            Calendar cal, Type t) throws IllegalArgumentException
        {
        if (runtimeContext == null || cal == null || t == null)
            {
            throw new IllegalArgumentException ("RuntimeContext, Calendar and Type cannot be null(s)");
            }

        cal.set (Calendar.HOUR, 0);
        cal.set (Calendar.MINUTE, 0);
        cal.set (Calendar.SECOND, 0);
        cal.set (Calendar.MILLISECOND, 0);

        try
            {
            Session session = runtimeContext.createPersistenceSession();
            Map <String, List <String>> data = loadReportData (session, cal, t);
            session.close ();
            DataTable dataTable = buildDataTable (data, t);
            return dataTable;
            }
        catch (Exception e)
            {
            logger.error ("Failed to load data from database for report " + t.getReportType () + " generation", e);
            }
        return null;
        }


    private static Map <String, List <String>> loadReportData (Session session, Calendar cal, Type t)
        {
        return loadReportData (session, cal, t, new TreeMap <String, List <String>> ());
        }


    private static Map <String, List <String>> loadReportData (Session session, Calendar cal,
                                                               Type t, Map <String, List <String>> result)
        {
        try
            {
            if (t instanceof SimpleType)
                {
                Map <String, String> res = loadReportData (session, cal, (SimpleType) t);
                if (res == null)
                    {
                    return result;
                    }
                for (String k : res.keySet ())
                    {
                    List <String> vals = result.get (k);
                    if (vals == null)
                        {
                        vals = new ArrayList<String> ();
                        result.put (k, vals);
                        }
                    vals.add (res.get (k));
                    }
                }
            else if (t instanceof ComplexType)
                {
                Type types [] = ((ComplexType) t).getTypes ();
                for (Type t2 : types)
                    {
                    loadReportData (session, cal, (SimpleType) t2, result);
                    }
                }
            }
        catch (Exception e)
            {
            logger.error ("Failed to parse report using type: " + t.getReportType () + " and date: " + cal, e);
            }
        return result;
        }


    private static Map <String, String> loadReportData (Session session, Calendar cal, SimpleType t)
        {
        try
            {
            Map <String, String> result = new TreeMap <String, String> ();
            org.hibernate.Query q = session.createSQLQuery (SELECT_SQL);
            q.setLong (0, cal.getTimeInMillis ());
            q.setInteger (1, t.getReportType ());
            List resultList = q.list ();

            for (Object entry : resultList)
                {
                Object e [] = (Object []) entry;
                result.put (((String) e [0]).trim (), ((String) e [1]).trim ());
                }
            return result;
            }
        catch (Exception e)
            {
            logger.error ("Failed to load report using type: " + t.getReportType () + " and date: " + cal, e);
            return null;
            }
        }


    private static DataTable buildDataTable (Map <String, List <String>> data, Type t) throws TypeMismatchException
        {
        DataTable dt = new DataTable ();
        ArrayList cd = new ArrayList ();
        for (int i=0; i<t.getColumns ().length; i++)
            {
            Object col [] = t.getColumns() [i];
            ValueType vt = (ValueType) col[0];
            cd.add(new ColumnDescription("col" + (i + 1), vt == ValueType.DATE ? ValueType.TEXT : vt, (String) col[1]));
            }
        dt.addColumns (cd);

        for (String k : data.keySet ())
            {
            TableRow r = new TableRow ();
            List <String> vals = data.get (k);


            if (t.getColumns () [0][0] == ValueType.TEXT)
                {
                r.addCell (k);
                }
            else if (t.getColumns () [0][0] == ValueType.DATE)
                {
                Date d = new Date (Long.parseLong (k));
                if (d.getDay () == 0 || d.getDay () == 6)
                    {
                    continue;
                    }
                r.addCell (DATE_FORMAT.format (d));
                }
            else if (t.getColumns () [0][0] == ValueType.NUMBER)
                {
                r.addCell (Integer.parseInt (k));
                }

            for (int i=1; i<t.getColumns ().length; i++)
                {
                Object col [] = t.getColumns() [i];
                if ((ValueType) col [0] == ValueType.TEXT)
                    {
                    if (i - 1 >= vals.size ())
                        r.addCell ("");
                    else
                        r.addCell (vals.get (i - 1));
                    }
                else if ((ValueType) col [0] == ValueType.NUMBER)
                    {
                    if (i - 1 >= vals.size ())
                        r.addCell (0);
                    else
                        r.addCell (Integer.parseInt (vals.get (i - 1)));
                    }
                }
            dt.addRow (r);
            }

        return dt;
        }







    protected static class ComplexType implements Type
        {
        private Type types [];
        protected Object [][] columns;
        private int type;


        public ComplexType (Type types [], Object [][] columns, int type)
            {
            this.types = types;
            this.columns = columns;
            this.type = type;
            }

        public Object [][] getColumns ()
            {
            return columns;
            }

        public Type [] getTypes ()
            {
            return types;
            }

        public int getReportType ()
            {
            return type;
            }
        }


    protected static class SimpleType implements Type
        {
        private String sql;
        protected Object [][] columns;
        private int type;
        private Class valueType;


        public SimpleType (String sql, int type)
            {
            this.sql = sql;
            this.columns = null;
            this.type = type;
            valueType = Integer.class;
            }

        public SimpleType (String sql, Object [][] columns, int type)
            {
            this.sql = sql;
            this.columns = columns;
            this.type = type;
            valueType = Integer.class;
            }

        public SimpleType (String sql, Object [][] columns, int type, Class valueType)
            {
            this.sql = sql;
            this.columns = columns;
            this.type = type;
            this.valueType = valueType;
            }

        public SimpleType stValueType (Class valueType)
            {
            this.valueType = valueType;
            return this;
            }

        public Object [][] getColumns ()
            {
            return columns;
            }

        public Class getValueType ()
            {
            return valueType;
            }

        public String getSQL()
            {
            return sql;
            }

        public int getReportType ()
            {
            return type;
            }
        }


    public interface Type
        {
        public Object [][] getColumns ();

        public int getReportType ();
        }
    }