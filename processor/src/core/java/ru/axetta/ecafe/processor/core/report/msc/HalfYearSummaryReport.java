/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.msc;


import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.*;
import ru.axetta.ecafe.processor.core.utils.ExecutorServiceWrappedJob;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 05.10.12
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
public class HalfYearSummaryReport extends BasicReportJob
{
    public static final java.text.Format SQL_DATE_FORMATTER = new SimpleDateFormat ("yyyy-MM-dd");
    private static java.text.NumberFormat JASPER_CURRENCY_FORMATTER;
    private static Map <String, JasperField> JASPER_FIELDS;
    static
    {
        JASPER_CURRENCY_FORMATTER = java.text.NumberFormat.getCurrencyInstance();
        JASPER_FIELDS = new HashMap <String, JasperField> ();
        JASPER_FIELDS.put ("name", new JasperField (String.class));
        JASPER_FIELDS.put ("no", new JasperField (String.class));
        JASPER_FIELDS.put ("type", new JasperField (String.class));
        JASPER_FIELDS.put ("studentsCount", new JasperField ("Select cf_clients.idoforg, int8(count(*)) " +
                "from cf_clients " +
                "group by cf_clients.idoforg", Integer.class));
        JASPER_FIELDS.put ("studyDays", new JasperField (Integer.class));
        JASPER_FIELDS.put("benefitBreakfastsCount",
                new JasperField("select cf_orderdetails.idoforg, int8(count(qty)) " +
                        "from cf_orderdetails " +
                        "left join cf_orders on cf_orders.idoforder=cf_orderdetails.idoforder and cf_orders.idoforg=cf_orderdetails.idoforg " +
                        "where (cf_orderdetails.menutype BETWEEN 50 AND 60) and " +
                        "cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%REPORT_PERIOD_START%') * 1000 AND " +
                        "EXTRACT(EPOCH FROM TIMESTAMP '%REPORT_PERIOD_END%') * 1000 and " +
                        "cf_orders.socdiscount <> 0 and " +
                        "menudetailname LIKE 'Завтрак%' " +
                        "group by cf_orderdetails.idoforg ", Integer.class));
        JASPER_FIELDS.put ("benefitPaymentsSum",
                new JasperField ("select cf_orderdetails.idoforg, int8(sum(cf_orderdetails.socdiscount*qty)) "+
                        "from cf_orderdetails "+
                        "left join cf_orders on cf_orders.idoforder=cf_orderdetails.idoforder and cf_orders.idoforg=cf_orderdetails.idoforg "+
                        "where (cf_orderdetails.menutype BETWEEN 50 AND 60) and "+
                        "cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%REPORT_PERIOD_START%') * 1000 AND "+
                        "EXTRACT(EPOCH FROM TIMESTAMP '%REPORT_PERIOD_END%') * 1000 and "+
                        "cf_orders.socdiscount <> 0 "+
                        //"and menudetailname LIKE 'Завтрак%' "+
                        "group by cf_orderdetails.idoforg ", Currency.class));
        JASPER_FIELDS.put ("benefitDailyCount",
                new JasperField ("select o2.idoforg, int8(avg(o2.cnt)) " +
                        "from " +
                        "(select o1.idoforg, int8(sum(o1.cnt)) AS cnt, mon " +
                        "from (" +
                        "select cf_orderdetails.idoforg, date_part('month', to_timestamp(cf_orders.createddate / 1000)) as mon, count (qty) AS cnt " +
                        "from cf_orderdetails " +
                        "left join cf_orders on cf_orders.idoforder=cf_orderdetails.idoforder and cf_orders.idoforg=cf_orderdetails.idoforg " +
                        "where (cf_orderdetails.menutype BETWEEN 50 AND 60) and " +
                        "cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%REPORT_PERIOD_START%') * 1000 AND " +
                        "EXTRACT(EPOCH FROM TIMESTAMP '%REPORT_PERIOD_END%') * 1000 and " +
                        "cf_orders.socdiscount <> 0 " +
                        "group by cf_orderdetails.idoforg, cf_orders.createddate) AS o1 " +
                        "group by o1.idoforg, mon) AS o2 " +
                        "group by o2.idoforg", Integer.class));
        JASPER_FIELDS.put ("payerTotalCount", new JasperField ("Select cf_clients.idoforg, int8(count(*)) " +
                "from cf_clients " +
                "where discountmode = 0 " +
                "group by cf_clients.idoforg", Integer.class));
        JASPER_FIELDS.put ("payerDailyLunchCount",
                new JasperField ("select o2.idoforg, int8(avg(o2.cnt)) " +
                        "from " +
                        "(select o1.idoforg, int8(sum(o1.cnt)) AS cnt, mon " +
                        "from (" +
                        "select cf_orderdetails.idoforg, date_part('month', to_timestamp(cf_orders.createddate / 1000)) as mon, count (qty) AS cnt " +
                        "from cf_orderdetails " +
                        "left join cf_orders on cf_orders.idoforder=cf_orderdetails.idoforder and cf_orders.idoforg=cf_orderdetails.idoforg " +
                        "where (cf_orderdetails.menutype BETWEEN 50 AND 60) and " +
                        "cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%REPORT_PERIOD_START%') * 1000 AND " +
                        "EXTRACT(EPOCH FROM TIMESTAMP '%REPORT_PERIOD_END%') * 1000 and " +
                        "cf_orders.socdiscount = 0 and menudetailname LIKE 'Обед%' " +
                        "group by cf_orderdetails.idoforg, cf_orders.createddate) AS o1 " +
                        "group by o1.idoforg, mon) AS o2 " +
                        "group by o2.idoforg", Integer.class));
        JASPER_FIELDS.put ("canteenDailySum",
                new JasperField ("select o2.idoforg, int8(avg(o2.cnt)) " +
                        "from " +
                        "(select o1.idoforg, int8(sum(o1.cnt)) AS cnt, mon " +
                        "from (" +
                        "select cf_orderdetails.idoforg, date_part('month', to_timestamp(cf_orders.createddate / 1000)) as mon, sum(rprice*qty) AS cnt " +
                        "from cf_orderdetails " +
                        "left join cf_orders on cf_orders.idoforder=cf_orderdetails.idoforder and cf_orders.idoforg=cf_orderdetails.idoforg " +
                        "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%REPORT_PERIOD_START%') * 1000 AND " +
                        "EXTRACT(EPOCH FROM TIMESTAMP '%REPORT_PERIOD_END%') * 1000 and " +
                        "cf_orderdetails.rootmenu <> '' and rprice <> 0 " +
                        "group by cf_orderdetails.idoforg, cf_orders.createddate) AS o1 " +
                        "group by o1.idoforg, mon) AS o2 " +
                        "group by o2.idoforg", Currency.class));
        JASPER_FIELDS.put ("doNotEatCount",
                new JasperField ("select CF_Clients.IdOfOrg, int8(count(distinct(CF_Clients.IdOfClient))) " +
                        "from CF_Clients " +
                        "right outer join CF_Orders on CF_Clients.IdOfClient=CF_Orders.IdOfClient " +
                        "group by CF_Clients.IdOfOrg", Integer.class));
        // Для каждого месяца, используем необходимые SQL
        for (int i=0; i<6; i++)
        {
            JASPER_FIELDS.put ("m" + i + "_daysCount", new JasperField (Integer.class));
            JASPER_FIELDS.put ("m" + i + "_1-4CountMonthly",
                    new JasperField ("select cf_orders.idoforg, int8(count(qty)) " +
                            "from cf_orders " +
                            "left join cf_orderdetails on cf_orderdetails.idoforg=cf_orders.idoforg and cf_orderdetails.idoforder=cf_orders.idoforder " +
                            "where cf_orderdetails.menutype BETWEEN 50 AND 60 and " +
                            "cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_START%') * 1000 AND " +
                            "EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_END%') * 1000 and " +
                            "cf_orderdetails.socdiscount <> 0 and menudetailname LIKE '%1-4%' and qty <> 0 " +
                            "group by cf_orders.idoforg " +
                            "order by cf_orders.idoforg", Integer.class));
            JASPER_FIELDS.put ("m" + i + "_1-4AvgMonthly",
                    new JasperField ("select cf_orders.idoforg, int8(avg(cf_orderdetails.socdiscount*qty)) " +
                            "from cf_orders " +
                            "left join cf_orderdetails on cf_orderdetails.idoforg=cf_orders.idoforg and cf_orderdetails.idoforder=cf_orders.idoforder " +
                            "where cf_orderdetails.menutype BETWEEN 50 AND 60 and " +
                            "cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_START%') * 1000 AND " +
                            "EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_END%') * 1000 and " +
                            "cf_orderdetails.socdiscount <> 0 and menudetailname LIKE '%1-4%' and qty <> 0 " +
                            "group by cf_orders.idoforg " +
                            "order by cf_orders.idoforg", Currency.class));
            JASPER_FIELDS.put ("m" + i + "_1-4SummMonthly",
                    new JasperField ("select cf_orders.idoforg, int8(sum(cf_orderdetails.socdiscount*qty)) " +
                            "from cf_orders " +
                            "left join cf_orderdetails on cf_orderdetails.idoforg=cf_orders.idoforg and cf_orderdetails.idoforder=cf_orders.idoforder " +
                            "where cf_orderdetails.menutype BETWEEN 50 AND 60 and " +
                            "cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_START%') * 1000 AND " +
                            "EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_END%') * 1000 and " +
                            "cf_orderdetails.socdiscount <> 0 and menudetailname LIKE '%1-4%' and qty <> 0 " +
                            "group by cf_orders.idoforg " +
                            "order by cf_orders.idoforg", Currency.class));

            JASPER_FIELDS.put ("m" + i + "_5-11CountMonthly",
                    new JasperField ("select cf_orders.idoforg, int8(count(qty)) " +
                            "from cf_orders " +
                            "left join cf_orderdetails on cf_orderdetails.idoforg=cf_orders.idoforg and cf_orderdetails.idoforder=cf_orders.idoforder " +
                            "where cf_orderdetails.menutype BETWEEN 50 AND 60 and " +
                            "cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_START%') * 1000 AND " +
                            "EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_END%') * 1000 and " +
                            "cf_orderdetails.socdiscount <> 0 and menudetailname LIKE '5-11%' and qty <> 0 " +
                            "group by cf_orders.idoforg " +
                            "order by cf_orders.idoforg", Integer.class));
            JASPER_FIELDS.put ("m" + i + "_5-11AvgMonthly",
                    new JasperField ("select cf_orders.idoforg, int8(avg(cf_orderdetails.socdiscount*qty)) " +
                            "from cf_orders " +
                            "left join cf_orderdetails on cf_orderdetails.idoforg=cf_orders.idoforg and cf_orderdetails.idoforder=cf_orders.idoforder " +
                            "where cf_orderdetails.menutype BETWEEN 50 AND 60 and " +
                            "cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_START%') * 1000 AND " +
                            "EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_END%') * 1000 and " +
                            "cf_orderdetails.socdiscount <> 0 and menudetailname LIKE '%5-11%' and qty <> 0 " +
                            "group by cf_orders.idoforg " +
                            "order by cf_orders.idoforg", Currency.class));
            JASPER_FIELDS.put ("m" + i + "_5-11SummMonthly",
                    new JasperField ("select cf_orders.idoforg, int8(sum(cf_orderdetails.socdiscount*qty)) " +
                            "from cf_orders " +
                            "left join cf_orderdetails on cf_orderdetails.idoforg=cf_orders.idoforg and cf_orderdetails.idoforder=cf_orders.idoforder " +
                            "where cf_orderdetails.menutype BETWEEN 50 AND 60 and " +
                            "cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_START%') * 1000 AND " +
                            "EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_END%') * 1000 and " +
                            "cf_orderdetails.socdiscount <> 0 and menudetailname LIKE '%5-11%' and qty <> 0 " +
                            "group by cf_orders.idoforg " +
                            "order by cf_orders.idoforg", Currency.class));
            JASPER_FIELDS.put ("m" + i + "_payerCountMonthly",
                    new JasperField ("select cf_orders.idoforg, int8(count(distinct cf_orders.idofclient)) " +
                            "from cf_orders " +
                            "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_START%') * 1000 AND " +
                            "EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_END%') * 1000 and " +
                            "socdiscount=0 " +
                            "group by cf_orders.idoforg", Integer.class));
            JASPER_FIELDS.put ("m" + i + "_payerSummMonthly",
                    new JasperField ("select cf_orders.idoforg, int8(sum(SumByCard+SumByCash)), count(distinct cf_orders.idofclient) " +
                            "from cf_orders " +
                            "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_START%') * 1000 AND " +
                            "EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_END%') * 1000 and " +
                            "socdiscount=0 " +
                            "group by cf_orders.idoforg", Currency.class));
            JASPER_FIELDS.put ("m" + i + "_canteenSummMonthly",  ////////////////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    new JasperField ("", Currency.class));
            JASPER_FIELDS.put ("m" + i + "_benefitCountDaily",
                    new JasperField ("select idoforg, int8(avg (cnt)) " +
                            "from (select o1.idoforg, int8(sum(o1.cnt)) as cnt, d " +
                            "from (select cf_orders.idoforg, count(distinct cf_orders.idofclient) as cnt, date_part('day', to_timestamp(cf_orders.createddate / 1000)) as d " +
                            "from cf_orders " +
                            "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_START%') * 1000 AND " +
                            "EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_END%') * 1000 and " +
                            "socdiscount<>0 " +
                            "group by cf_orders.idoforg, cf_orders.createddate) AS o1 " +
                            "group by o1.idoforg, d) AS o2 " +
                            "group by idoforg", Integer.class));
            JASPER_FIELDS.put ("m" + i + "_benefitSummDaily",
                    new JasperField ("select idoforg, int8(avg (cnt)) " +
                            "from (select o1.idoforg, int8(sum(o1.cnt)) as cnt, d " +
                            "from (select cf_orders.idoforg, sum(cf_orderdetails.socdiscount*qty) as cnt, date_part('day', to_timestamp(cf_orders.createddate / 1000)) as d " +
                            "from cf_orders " +
                            "left join cf_orderdetails on cf_orders.idoforg=cf_orderdetails.idoforg and cf_orders.idoforder=cf_orderdetails.idoforder " +
                            "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_START%') * 1000 AND " +
                            "EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_END%') * 1000 and " +
                            "cf_orderdetails.socdiscount<>0 " +
                            "group by cf_orders.idoforg, cf_orders.createddate) AS o1 " +
                            "group by o1.idoforg, d) AS o2 " +
                            "group by idoforg", Currency.class));
            JASPER_FIELDS.put ("m" + i + "_noBenefitCountDaily",
                    new JasperField ("select idoforg, int8(avg (cnt)) " +
                            "from (select o1.idoforg, int8(sum(o1.cnt)) as cnt, d " +
                            "from (select cf_orders.idoforg, count(distinct cf_orders.idofclient) as cnt, date_part('day', to_timestamp(cf_orders.createddate / 1000)) as d " +
                            "from cf_orders " +
                            "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_START%') * 1000 AND " +
                            "EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_END%') * 1000 and " +
                            "socdiscount=0 "+
                            "group by cf_orders.idoforg, cf_orders.createddate) AS o1 " +
                            "group by o1.idoforg, d) AS o2 " +
                            "group by idoforg", Integer.class));
            JASPER_FIELDS.put ("m" + i + "_payersCountDaily",
                    new JasperField ("select idoforg, int8(avg (cnt)) " +
                            "from (select o1.idoforg, int8(sum(o1.cnt)) as cnt, d " +
                            "from (select cf_orders.idoforg, count(distinct cf_orders.idofclient) as cnt, date_part('day', to_timestamp(cf_orders.createddate / 1000)) as d " +
                            "from cf_orders " +
                            "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_START%') * 1000 AND " +
                            "EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_END%') * 1000 and " +
                            "socdiscount=0 " +
                            "group by cf_orders.idoforg, cf_orders.createddate) AS o1 " +
                            "group by o1.idoforg, d) AS o2 " +
                            "group by idoforg", Integer.class));
            JASPER_FIELDS.put ("m" + i + "_payersSummDaily",
                    new JasperField ("select idoforg, int8(avg (cnt)) " +
                            "from (select o1.idoforg, int8(sum(o1.cnt)) as cnt, d " +
                            "from (select cf_orders.idoforg, sum(cf_orderdetails.socdiscount*qty) as cnt, date_part('day', to_timestamp(cf_orders.createddate / 1000)) as d " +
                            "from cf_orders " +
                            "left join cf_orderdetails on cf_orders.idoforg=cf_orderdetails.idoforg and cf_orders.idoforder=cf_orderdetails.idoforder " +
                            "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_START%') * 1000 AND " +
                            "EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_END%') * 1000 and " +
                            "cf_orderdetails.socdiscount=0 " +
                            "group by cf_orders.idoforg, cf_orders.createddate) AS o1 " +
                            "group by o1.idoforg, d) AS o2 " +
                            "group by idoforg", Currency.class));
            JASPER_FIELDS.put ("m" + i + "_canteenAvgDaily",
                    new JasperField ("", Currency.class));
            JASPER_FIELDS.put ("m" + i + "_doNotEatDaily",
                    new JasperField ("select idoforg, int8(avg (cnt)) " +
                            "from (select o1.idoforg, int8(sum(o1.cnt)) as cnt, d " +
                            "from (select CF_Orders.IdOfOrg, count(distinct(CF_Orders.idofclient)) AS cnt, date_part('day', to_timestamp(cf_orders.createddate / 1000)) as d " +
                            "from CF_Orders " +
                            "left outer join CF_Clients  on CF_Clients.IdOfClient=CF_Orders.IdOfClient " +
                            "where cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_START%') * 1000 AND " +
                            "EXTRACT(EPOCH FROM TIMESTAMP '%MONTH_END%') * 1000 " +
                            "group by CF_Orders.IdOfOrg, cf_orders.createddate) AS o1 " +
                            "group by o1.idoforg, d) AS o2 " + "group by idoforg", Integer.class));
        }
    }
    private static final Logger logger = LoggerFactory.getLogger (HalfYearSummaryReport.class);





    public HalfYearSummaryReport()
    {
    }


    public HalfYearSummaryReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime)
    {
        super (generateTime, generateDuration, print, startTime, endTime);
    }

    public void initialize (Date startTime, Date endTime, String templateFilename,
            SessionFactory sessionFactory, Calendar calendar)
    {
        super.initialize (startTime, endTime, templateFilename, sessionFactory, calendar);
    }


    public HalfYearSummaryReport(Date startTime, Date endTime, String templateFilename, SessionFactory sessionFactory,
            Calendar calendar)
    {
        initialize(startTime, endTime, templateFilename, sessionFactory, calendar);
    }


    @Override
    public String getReportDistinctText ()
    {
        return "";
    }


    public BasicReportJob createInstance (Date startTime, Date endTime, String templateFilename,
            SessionFactory sessionFactory, Calendar calendar)
    {
        return new HalfYearSummaryReport(startTime, endTime, templateFilename,
                sessionFactory, calendar);
    }


    public Builder createBuilder (String templateFilename)
    {
        return new Builder (templateFilename);
    }


    public Logger getLogger ()
    {
        return logger;
    }

    @Override
    public int getDefaultReportPeriod ()
    {
        return REPORT_PERIOD_PREV_PREV_PREV_DAY;
    }


    public Class getMyClass ()
    {
        return HalfYearSummaryReport.class;
    }


    protected void prepare ()
    {
        if (!hasPrint() && templateFilename != null && sessionFactory != null)
        {
            templateFilename = AutoReportGenerator
                    .restoreFilename(RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath(),
                            templateFilename);
            Builder builder = createBuilder (templateFilename);
            Session session = null;
            org.hibernate.Transaction transaction = null;
            try
            {
                session = sessionFactory.openSession ();
                /*transaction = BasicReport.createTransaction (session);
                transaction.begin ();*/
                BasicReportJob report = builder.build (session, startTime, endTime, calendar);
                setGenerateTime (report.getGenerateTime ());
                setGenerateDuration (report.getGenerateDuration ());
                setPrint (report.getPrint ());
                /*transaction.commit ();
                transaction = null;*/
            }
            catch (Exception e)
            {
                getLogger ().error (String.format ("Failed at report lazy-build \"%s\"",
                        BasicReportForOrgJob.class), e);
            }
            finally
            {
                HibernateUtils.rollback (transaction, getLogger ());
                HibernateUtils.close (session, getLogger ());
            }
        }
    }


    @Override
    public AutoReportRunner getAutoReportRunner()
    {
        return new AutoReportRunner ()
        {
            @Override
            public void run (AutoReportBuildTask autoReportBuildTask)
            {
                if (logger.isDebugEnabled ())
                {
                    logger.debug (String.format ("Building auto reports \"%s\"", getMyClass ().getCanonicalName ()));
                }
                String classPropertyValue = getMyClass ().getCanonicalName ();
                List <AutoReport> autoReports = new LinkedList <AutoReport> ();
                Session session = null;
                org.hibernate.Transaction transaction = null;
                try
                {
                    session = autoReportBuildTask.sessionFactory.openSession ();
                    transaction = BasicReport.createTransaction(session);
                    transaction.begin ();

                    Properties properties = new Properties ();
                    ReportPropertiesUtils.addProperties (properties, getMyClass (), null);
                    BasicReportJob report = new HalfYearSummaryReport(autoReportBuildTask.startTime, autoReportBuildTask.endTime,
                            autoReportBuildTask.templateFileName, autoReportBuildTask.sessionFactory,
                            autoReportBuildTask.startCalendar);

                    /*createInstance (autoReportBuildTask.startTime, autoReportBuildTask.endTime,
                                                            autoReportBuildTask.templateFileName, autoReportBuildTask.sessionFactory,
                                                            autoReportBuildTask.startCalendar);*/
                    autoReports.add (new AutoReport (report, properties));
                    transaction.commit ();
                    transaction = null;
                    autoReportBuildTask.executorService.execute(
                            new AutoReportProcessor.ProcessTask (autoReportBuildTask.autoReportProcessor, autoReports,
                                    autoReportBuildTask.documentBuilders));
                }
                catch (Exception e)
                {
                    logger.error (String.format ("Failed at building auto reports \"%s\"", classPropertyValue), e);
                }
                finally
                {
                    HibernateUtils.rollback (transaction, logger);
                    HibernateUtils.close (session, logger);
                }
            }
        };
    }


    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob
    {
    }


    public static class Builder extends ExecutorServiceWrappedJob
    {
        private final String templateFilename;


        public Builder (String templateFilename)
        {
            this.templateFilename = templateFilename;
        }

        @Override
        protected ExecutorService getExecutorService(JobExecutionContext context) throws Exception {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected Runnable getRunnable(JobExecutionContext context) throws Exception {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public HalfYearSummaryReport build (Session session, Date startTime, Date endTime, Calendar calendar) throws Exception
        {
            try
            {
                Date generateDate = new Date ();
                Calendar cal = Calendar.getInstance ();
                cal.setTime(generateDate);
                cal.set(Calendar.HOUR, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set (Calendar.DAY_OF_MONTH, 1);


                Map <String, Object> parameterMap = new HashMap <String, Object> ();
                cal.set (Calendar.MONTH, 8);
                parameterMap.put ("startDate", new Date (cal.getTimeInMillis ()));
                cal.set (Calendar.MONTH, 11);
                parameterMap.put ("endDate", new Date (cal.getTimeInMillis ()));
                parameterMap.put ("generateDate", String.format("%1$te.%1$tm.%1$tY", generateDate));


                if (cal.get (Calendar.MONTH) < 6)
                {
                    parameterMap.put ("Month0", "Январь");
                    parameterMap.put ("Month1", "Февраль");
                    parameterMap.put ("Month2", "Март");
                    parameterMap.put ("Month3", "Апрель");
                    parameterMap.put ("Month4", "Май");
                    parameterMap.put ("Month5", "Июнь");
                }
                else
                {
                    parameterMap.put ("Month0", "Июль");
                    parameterMap.put ("Month1", "Август");
                    parameterMap.put ("Month2", "Сентябрь");
                    parameterMap.put ("Month3", "Октябрь");
                    parameterMap.put ("Month4", "Ноябрь");
                    parameterMap.put ("Month5", "Декабрь");
                }



                JasperPrint jasperPrint = JasperFillManager.fillReport (templateFilename, parameterMap,
                        createDataSource (session, parameterMap, cal));
                Date generateEndTime = new Date();
                return new HalfYearSummaryReport(generateDate, generateEndTime.getTime() - generateDate.getTime(),
                        jasperPrint, startTime, endTime);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw e;
            }
        }


        private JRDataSource createDataSource (Session session, Map <String, Object> params, Calendar cal) throws Exception
        {
            Map <Long, MyRow> rows = initList (session);
            fillValues (session, rows, "studentsCount", params);
            fillValues (session, rows, "benefitBreakfastsCount", params);
            fillValues (session, rows, "benefitPaymentsSum", params);
            fillValues (session, rows, "benefitDailyCount", params);
            fillValues (session, rows, "payerTotalCount", params);
            fillValues (session, rows, "canteenDailySum", params);
            fillValues (session, rows, "doNotEatCount", params);
            // Для каждого месяца, выполняем SQL для последующего их заполнения
            for (int i=0; i<6; i++)
            {
                cal.set (Calendar.MONTH, i + 0);
                params.put ("monthStart", new Date (cal.getTimeInMillis ()));
                cal.set (Calendar.MONTH, i + 1 + 0);
                params.put ("monthEnd", new Date (cal.getTimeInMillis ()));

                fillValues (session, rows, "m" + i + "_1-4CountMonthly", params);
                fillValues (session, rows, "m" + i + "_1-4AvgMonthly", params);
                fillValues (session, rows, "m" + i + "_1-4SummMonthly", params);
                fillValues (session, rows, "m" + i + "_5-11CountMonthly", params);
                fillValues (session, rows, "m" + i + "_5-11AvgMonthly", params);
                fillValues (session, rows, "m" + i + "_5-11SummMonthly", params);
                fillValues (session, rows, "m" + i + "_payerCountMonthly", params);
                fillValues (session, rows, "m" + i + "_payerSummMonthly", params);
                fillValues (session, rows, "m" + i + "_canteenSummMonthly", params);
                fillValues (session, rows, "m" + i + "_benefitCountDaily", params);
                fillValues (session, rows, "m" + i + "_benefitSummDaily", params);
                fillValues (session, rows, "m" + i + "_payersCountDaily", params);
                fillValues (session, rows, "m" + i + "_payersSummDaily", params);
                fillValues (session, rows, "m" + i + "_canteenAvgDaily", params);
                fillValues (session, rows, "m" + i + "_doNotEatDaily", params);
            }

            List <MyRow> export = new ArrayList <MyRow> (rows.values ());
            return new JRBeanCollectionDataSource (export);
        }


        /* В данном методе заполняются все поля, которые могут быть получены одним запросом */
        private Map <Long, MyRow> initList (Session session)
        {
            Map <Long, MyRow> result = new HashMap <Long, MyRow> ();
            Query q = session.createSQLQuery ("SELECT cf_orgs.idoforg, cf_orgs.shortname " +
                    "FROM cf_orders " +
                    "left join cf_orgs on cf_orgs.idoforg=cf_orders.idoforg " +
                    "group by cf_orgs.idoforg " +
                    "ORDER BY cf_orgs.shortname");
            List list = q.list ();
            for (Object o : list)
            {
                Object entry [] = (Object []) o;
                long id = ((BigInteger) entry [0]).longValue ();
                MyRow row = new MyRow (id);
                String name = (String) entry [1];
                row.put ("name", name);
                if (name.lastIndexOf ("№") > 1)
                {
                    row.put ("no", name.substring (name.lastIndexOf ("№") + 1).trim ());
                }
                result.put (id, row);
            }
            return result;
        }


        /* Метод загружает поля для всех объектах (org), используя sql. В качестве 2ой колонки ВСЕГДА
            выступает значение, которое будет использовано, 1ая - ID объекта Org

           Params: sql -
                   session -
                   rows - список объектов Org
                   fieldName - имя поля, к которому будет привязано значение
         */
        private void fillValues (Session session, Map <Long, MyRow> rows,
                String fieldName, Map <String, Object> params)
        {
            JasperField field = JASPER_FIELDS.get (fieldName);
            if (field.getSQL () == null || field.getSQL ().length () < 1)
            {
                return;
            }
            Query q = session.createSQLQuery (useMacroReplace (field.getSQL (), params));
            List list = q.list ();
            for (Object o : list)
            {
                Object entry [] = (Object []) o;
                try
                {
                    long id = ((BigInteger) entry [0]).longValue ();
                    MyRow row = rows.get (id);
                    if (row == null)
                    {
                        continue;
                    }
                    if (field.getClazz () == BigInteger.class || field.getClazz () == Integer.class)
                    {
                        row.put (fieldName, ((BigInteger) entry [1]).intValue());
                    }

                    if (field.getClazz () == Currency.class)
                    {
                        try
                        {
                            row.put(fieldName, ((BigDecimal) entry[1]).intValue ());
                        }
                        catch (Exception e)
                        {
                            row.put(fieldName, ((BigInteger) entry[1]).intValue ());
                        }
                    }
                    continue;
                }
                catch (Exception e)
                {
                    logger.info ("Failed to put entry for [" + fieldName + "] " + entry [0] + " the value is " + entry [1]);
                }
            }
        }


        public String useMacroReplace (String sql, Map <String, Object> params)
        {
            if (sql.indexOf ("%REPORT_PERIOD_START%") > -1)
            {
                sql = sql.replaceAll ("%REPORT_PERIOD_START%", SQL_DATE_FORMATTER.format ((Date) params.get ("startDate")));
            }
            if (sql.indexOf ("%REPORT_PERIOD_END%") > -1)
            {
                sql = sql.replaceAll ("%REPORT_PERIOD_END%", SQL_DATE_FORMATTER.format ((Date) params.get ("endDate")));
            }
            if (sql.indexOf ("%MONTH_START%") > -1)
            {
                sql = sql.replaceAll ("%MONTH_START%", SQL_DATE_FORMATTER.format ((Date) params.get ("monthStart")));
            }
            if (sql.indexOf ("%MONTH_END%") > -1)
            {
                sql = sql.replaceAll ("%MONTH_END%", SQL_DATE_FORMATTER.format ((Date) params.get ("monthEnd")));
            }
            return sql;
        }


        public static class MyRow
        {
            private long idOfOrg;
            public Map <String, Object> props;


            public MyRow (long idOfOrg)
            {
                this.idOfOrg = idOfOrg;
                initProps ();
            }


            public MyRow (Long idOfOrg)
            {
                this.idOfOrg = idOfOrg.longValue ();
                initProps ();
            }


            public Map <String, Object> getProps ()
            {
                return props;
            }


            private void initProps ()
            {
                props = new HashMap <String, Object> ();
                for (String k : JASPER_FIELDS.keySet ())
                {
                    props.put (k, "");
                }
            }


            public void put (String key, Object val)
            {
                String putVal = "";
                Class cl = JASPER_FIELDS.get (key).getClazz ();
                if (cl == String.class)
                {
                    putVal = val + "";
                }
                if (cl == Integer.class || cl == BigInteger.class)
                {
                    int v = 0;
                    try
                    {
                        v = Integer.parseInt (val + "");
                    }
                    catch (Exception e) { }
                    putVal = v + "";
                }
                if (cl == Currency.class)
                {
                    long v = 0L;
                    try
                    {
                        v = Long.parseLong (val + "");
                    }
                    catch (Exception e) { }
                    putVal = JASPER_CURRENCY_FORMATTER.format (v == 0L ? 0 : v / 100);
                }
                props.put (key, putVal);
            }
        }
    }


    public static class JasperField
    {
        private String sql;
        private Class clazz;

        public JasperField (Class clazz)
        {
            this.clazz = clazz;
        }

        public JasperField (String sql, Class clazz)
        {
            this.sql = sql;
            this.clazz = clazz;
        }

        public String getSQL ()
        {
            return sql;
        }

        public Class getClazz ()
        {
            return clazz;
        }
    }
}