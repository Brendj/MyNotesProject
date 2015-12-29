/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.msc;


import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.report.*;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ExecutorServiceWrappedJob;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
 * Date: 22.10.12
 * Time: 11:52
 * Реестр талонов льготного питания
 */
public class BeneficiarySummaryReport extends BasicReportJob
{
    public static final java.text.Format SQL_DATE_FORMATTER = new SimpleDateFormat ("yyyy-MM-dd");
    //private static java.text.NumberFormat JASPER_CURRENCY_FORMATTER;
    private static Map <String, JasperField> JASPER_FIELDS;
    static{
        //JASPER_CURRENCY_FORMATTER = java.text.NumberFormat.getCurrencyInstance();
        JASPER_FIELDS = new HashMap <String, JasperField> ();
        JASPER_FIELDS.put ("no", new JasperField (Integer.class));
        JASPER_FIELDS.put ("name", new JasperField (String.class));
        JASPER_FIELDS.put ("type", new JasperField (String.class));
        JASPER_FIELDS.put ("3-7_breakfast1Count", new JasperField (Integer.class));
        JASPER_FIELDS.put ("3-7_breakfast1Avg", new JasperField (Currency.class));
        JASPER_FIELDS.put ("3-7_breakfast1Summ", new JasperField (Currency.class));
        JASPER_FIELDS.put ("3-7_breakfast2Count", new JasperField (Integer.class));
        JASPER_FIELDS.put ("3-7_breakfast2Avg", new JasperField (Currency.class));
        JASPER_FIELDS.put ("3-7_breakfast2Summ", new JasperField (Currency.class));
        JASPER_FIELDS.put ("3-7_lunchCount", new JasperField (Integer.class));
        JASPER_FIELDS.put ("3-7_lunchAvg", new JasperField (Currency.class));
        JASPER_FIELDS.put ("3-7_lunchSumm", new JasperField (Currency.class));
        JASPER_FIELDS.put ("3-7_snackCount", new JasperField (Integer.class));
        JASPER_FIELDS.put ("3-7_snackAvg", new JasperField (Currency.class));
        JASPER_FIELDS.put ("3-7_snackSumm", new JasperField (Currency.class));

        JASPER_FIELDS.put ("1-4_breakfastCount", new JasperField (Integer.class));
        JASPER_FIELDS.put ("1-4_breakfastAvg", new JasperField ("", Currency.class));
        JASPER_FIELDS.put ("1-4_breakfastSumm", new JasperField (Currency.class));
        JASPER_FIELDS.put ("1-4_lunchCount", new JasperField (Integer.class));
        JASPER_FIELDS.put ("1-4_lunchAvg", new JasperField (Currency.class));
        JASPER_FIELDS.put ("1-4_lunchSumm", new JasperField (Currency.class));
        JASPER_FIELDS.put ("1-4_snackCount", new JasperField (Integer.class));
        JASPER_FIELDS.put ("1-4_snackAvg", new JasperField (Currency.class));
        JASPER_FIELDS.put ("1-4_snackSumm", new JasperField (Currency.class));


        JASPER_FIELDS.put ("5-11_breakfastCount", new JasperField (Integer.class));
        JASPER_FIELDS.put ("5-11_breakfastAvg", new JasperField (Currency.class));
        JASPER_FIELDS.put ("5-11_breakfastSumm", new JasperField (Currency.class));
        JASPER_FIELDS.put ("5-11_lunchCount", new JasperField (Integer.class));
        JASPER_FIELDS.put ("5-11_lunchAvg", new JasperField ("", Currency.class));
        JASPER_FIELDS.put ("5-11_lunchSumm", new JasperField (Currency.class));
        JASPER_FIELDS.put ("5-11_snackCount", new JasperField (Integer.class));
        JASPER_FIELDS.put ("5-11_snackAvg", new JasperField ("", Currency.class));
        JASPER_FIELDS.put ("5-11_snackSumm", new JasperField (Currency.class));
    }
    private static final Logger logger = LoggerFactory.getLogger (BeneficiarySummaryReport.class);

    public BeneficiarySummaryReport (){}

    public BeneficiarySummaryReport (Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime){
        super (generateTime, generateDuration, print, startTime, endTime);
    }

    public void initialize (Date startTime, Date endTime, String templateFilename,
            SessionFactory sessionFactory, Calendar calendar){
        super.initialize (startTime, endTime, templateFilename, sessionFactory, calendar);
    }

    public BeneficiarySummaryReport (Date startTime, Date endTime, String templateFilename,
            SessionFactory sessionFactory, Calendar calendar){
        initialize (startTime, endTime, templateFilename, sessionFactory, calendar);
    }

    @Override
    public String getReportDistinctText (){
        return "";
    }

    public BasicReportJob createInstance (Date startTime, Date endTime, String templateFilename,
            SessionFactory sessionFactory, Calendar calendar){
        return new BeneficiarySummaryReport (startTime, endTime, templateFilename,
                sessionFactory, calendar);
    }

    public Builder createCustomBuilder (String templateFilename){
        return new Builder (templateFilename);
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return null;
    }

    public Logger getLogger (){
        return logger;
    }

    @Override
    public int getDefaultReportPeriod (){
        return REPORT_PERIOD_PREV_PREV_PREV_DAY;
    }

    public Class getMyClass (){
        return BeneficiarySummaryReport.class;
    }

    protected void prepare (){
        if (!hasPrint () && templateFilename != null && sessionFactory != null){
            String templatesPath = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
            templateFilename = AutoReportGenerator.restoreFilename(templatesPath, templateFilename);
            Builder builder = createCustomBuilder(templateFilename);
            Session session = null;
            org.hibernate.Transaction transaction = null;
            try{
                session = sessionFactory.openSession ();
                BasicReportJob report = builder.build (session, startTime, endTime, calendar);
                setGenerateTime (report.getGenerateTime ());
                setGenerateDuration (report.getGenerateDuration ());
                setPrint (report.getPrint ());
            }
            catch (Exception e){
                String message = String.format("Failed at report lazy-build \"%s\"", BeneficiarySummaryReport.class);
                getLogger().error(message, e);
            }
            finally{
                HibernateUtils.rollback (transaction, getLogger ());
                HibernateUtils.close (session, getLogger ());
            }
        }
    }

    @Override
    public BasicReportJob.AutoReportRunner getAutoReportRunner (){
        return new BasicReportJob.AutoReportRunner (){
            @Override
            public void run (BasicReportJob.AutoReportBuildTask autoReportBuildTask){

                String jobId = autoReportBuildTask.jobId;
                Long idOfSchedulerJob = Long.valueOf(jobId);

                if (logger.isDebugEnabled ()){
                    logger.debug (String.format ("Building auto reports \"%s\"", getMyClass ().getCanonicalName ()));
                }
                String classPropertyValue = getMyClass ().getCanonicalName ();
                List <AutoReport> autoReports = new LinkedList <AutoReport> ();
                Session session = null;
                Transaction transaction = null;
                try{
                    session = autoReportBuildTask.sessionFactory.openSession ();
                    transaction = BasicReport.createTransaction(session);
                    transaction.begin ();

                    Properties properties = new Properties ();
                    ReportPropertiesUtils.addProperties (properties, getMyClass (), null);
                    BasicReportJob report = new BeneficiarySummaryReport (autoReportBuildTask.startTime, autoReportBuildTask.endTime,
                            autoReportBuildTask.templateFileName, autoReportBuildTask.sessionFactory,
                            autoReportBuildTask.startCalendar);

                    /*createInstance (autoReportBuildTask.startTime, autoReportBuildTask.endTime,
                    autoReportBuildTask.templateFileName, autoReportBuildTask.sessionFactory,
                    autoReportBuildTask.startCalendar);*/
                    autoReports.add (new AutoReport (report, properties));

                    List<Long> reportHandleRuleList = getRulesIdsByJobRules(session, idOfSchedulerJob);

                    transaction.commit ();
                    transaction = null;
                    autoReportBuildTask.executorService.execute(
                            new AutoReportProcessor.ProcessTask (autoReportBuildTask.autoReportProcessor, autoReports,
                                    autoReportBuildTask.documentBuilders, reportHandleRuleList));
                }
                catch (Exception e){
                    logger.error (String.format ("Failed at building auto reports \"%s\"", classPropertyValue), e);
                }
                finally{
                    HibernateUtils.rollback (transaction, logger);
                    HibernateUtils.close (session, logger);
                }
            }
        };
    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob{}

    public static class Builder extends ExecutorServiceWrappedJob{
        private final String templateFilename;

        public Builder (String templateFilename){
            this.templateFilename = templateFilename;
        }

        public BeneficiarySummaryReport build (Session session, Date startTime, Date endTime, Calendar calendar) throws Exception{
            try{
                Date generateDate = new Date ();
                Calendar cal = CalendarUtils.truncateToMonth(generateDate);
                //Calendar cal = Calendar.getInstance ();
                //cal.setTime (generateDate);
                //cal.set(Calendar.HOUR, 0);
                //cal.set(Calendar.MINUTE, 0);
                //cal.set(Calendar.SECOND, 0);
                //cal.set (Calendar.MONTH, 0);
                //cal.set (Calendar.DAY_OF_MONTH, 1);

                Map <String, Object> parameterMap = new HashMap <String, Object> ();
                parameterMap.put ("startDate", new Date (cal.getTimeInMillis ()));
                cal.set (Calendar.MONTH, 5);
                parameterMap.put ("endDate", new Date (cal.getTimeInMillis ()));
                parameterMap.put ("generateDate", String.format("%1$te.%1$tm.%1$tY", generateDate));

                JasperPrint jasperPrint = JasperFillManager.fillReport (templateFilename, parameterMap,
                        createDataSource (session, parameterMap, cal));
                Date generateEndTime = new Date();
                final long duration = generateEndTime.getTime() - generateDate.getTime();
                return new BeneficiarySummaryReport(generateDate, duration,jasperPrint, startTime, endTime);
            }
            catch (Exception e){
                logger.error("BeneficiarySummaryReport.Builder error", e);
                throw e;
            }
        }

        private JRDataSource createDataSource (Session session, Map <String, Object> params, Calendar cal) throws Exception{
            Map<Long, MyRow> rows = initList(session);

            List <MyRow> export = new ArrayList <MyRow> (rows.values ());
            int i=1;
            for (MyRow r : export){
                r.put ("no", i);
                i++;
            }
            return new JRBeanCollectionDataSource (export);
        }


        /* В данном методе заполняются все поля, которые могут быть получены одним запросом */
        private Map <Long, MyRow> initList (Session session) throws Exception{
            Map <Long, MyRow> result = new HashMap <Long, MyRow> ();
            Query q = session.createSQLQuery
                    ("select cf_orders.idoforg, cf_orgs.shortname, count(qty), avg(cf_orderdetails.socdiscount*qty), sum(cf_orderdetails.socdiscount*qty), menudetailname " +
                            "from cf_orders " +
                            "left join cf_orgs on cf_orgs.idoforg=cf_orders.idoforg " +
                            "left join cf_orderdetails on cf_orderdetails.idoforg=cf_orders.idoforg and cf_orderdetails.idoforder=cf_orders.idoforder " +
                            "where cf_orderdetails.menutype BETWEEN " + OrderDetail.TYPE_COMPLEX_MIN + " AND " + OrderDetail.TYPE_COMPLEX_MAX + " and " +
                            "cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '2012-09-01') * 1000 AND " +
                            "EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '2012-11-01') * 1000 and " +
                            "cf_orderdetails.socdiscount > 0 and cf_orders.state=0 and cf_orderdetails.state=0 " +
                            "group by cf_orders.idoforg, cf_orgs.shortname, menudetailname " +
                            "order by cf_orders.idoforg, menudetailname");
            MyRow row = null;
            long prevID = -1L;
            List list = q.list ();
            String keyPrefix = null;
            String mealPrefix = null;
            for (Object o : list){
                try{
                    Object entry [] = (Object []) o;
                    long id = ((BigInteger) entry [0]).longValue ();
                    String name = ((String) entry [1]).trim ();
                    int count = ((BigInteger) entry [2]).intValue ();
                    double avg = ((BigDecimal) entry [3]).doubleValue ();
                    long sum = ((BigDecimal) entry [4]).longValue ();
                    String mealType = ((String) entry [5]).trim ();

                    if (id != prevID){
                        row = new MyRow (id);
                        row.put ("name", name);
                        row.put ("type", "");
                        result.put (id, row);
                        prevID = id;
                    }

                    keyPrefix = null;
                    mealPrefix = null;
                    if (mealType.contains ("1-4")){
                        keyPrefix = "1-4";
                    }
                    if (mealType.contains ("5-11")){
                        keyPrefix = "5-11";
                    }
                    if (mealType.toLowerCase ().indexOf ("завтрак") > -1){
                        mealPrefix = "breakfast";
                    }
                    if (mealType.toLowerCase ().indexOf ("обед") > -1){
                        mealPrefix = "lunch";
                    }
                    if (mealType.toLowerCase ().indexOf ("полдник") > -1){
                        mealPrefix = "snack";
                    }
                    if (keyPrefix != null && mealPrefix != null){
                        row.put (keyPrefix + "_" + mealPrefix + "Count", count);
                        row.put (keyPrefix + "_" + mealPrefix + "Avg", avg);
                        row.put (keyPrefix + "_" + mealPrefix + "Summ", sum);
                    }
                }
                catch (Exception e){
                    throw e;
                }
            }
            return result;
        }

        public String useMacroReplace (String sql, Map <Object, Object> params){
            if (sql.indexOf ("%REPORT_PERIOD_START%") > -1){
                sql = sql.replaceAll ("%REPORT_PERIOD_START%", SQL_DATE_FORMATTER.format ((Date) params.get ("startDate")));
            }
            if (sql.indexOf ("%REPORT_PERIOD_END%") > -1){
                sql = sql.replaceAll ("%REPORT_PERIOD_END%", SQL_DATE_FORMATTER.format ((Date) params.get ("endDate")));
            }
            if (sql.indexOf ("%MONTH_START%") > -1){
                sql = sql.replaceAll ("%MONTH_START%", SQL_DATE_FORMATTER.format ((Date) params.get ("monthStart")));
            }
            if (sql.indexOf ("%MONTH_END%") > -1){
                sql = sql.replaceAll ("%MONTH_END%", SQL_DATE_FORMATTER.format ((Date) params.get ("monthEnd")));
            }
            return sql;
        }

        @Override
        protected ExecutorService getExecutorService(JobExecutionContext context) throws Exception {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected Runnable getRunnable(JobExecutionContext context) throws Exception {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public static class MyRow{
            private long idOfOrg;
            public Map <String, Object> props;

            public MyRow (long idOfOrg){
                this.idOfOrg = idOfOrg;
                initProps();
            }

            public MyRow (Long idOfOrg){
                this.idOfOrg = idOfOrg.longValue();
                initProps();
            }

            public Map <String, Object> getProps (){
                return props;
            }

            private void initProps (){
                props = new HashMap <String, Object> ();
                for (String k : JASPER_FIELDS.keySet ()){
                    Class cl = JASPER_FIELDS.get (k).getClazz ();
                    if (cl == String.class){
                        props.put (k, "");
                    }
                    if (cl == Integer.class || cl == BigInteger.class){
                        props.put (k, "0");
                    }
                    if (cl == Currency.class){
                        props.put (k, "0");
                    }
                }
            }

            public void put (String key, Object val){
                String putVal = "";
                Class cl = JASPER_FIELDS.get (key).getClazz ();
                if (cl == String.class){
                    putVal = val + "";
                }
                if (cl == Integer.class || cl == BigInteger.class){
                    int v = 0;
                    try{
                        v = Integer.parseInt (val + "");
                    } catch (Exception ignore) { }
                    putVal = v + "";
                }
                if (cl == Currency.class){
                    double v = 0D;
                    try{
                        v = Double.parseDouble (val + "");
                    } catch (Exception ignore) { }
                    putVal = "" + (v == 0L ? 0D : v / 100); //JASPER_CURRENCY_FORMATTER.format (v == 0L ? 0 : v / 100);
                }
                props.put (key, putVal);
            }
        }
    }

    public static class JasperField{
        private String sql;
        private Class clazz;

        public JasperField (Class clazz){
            this.clazz = clazz;
        }

        public JasperField (String sql, Class clazz){
            this.sql = sql;
            this.clazz = clazz;
        }

        public String getSQL (){
            return sql;
        }

        public Class getClazz (){
            return clazz;
        }
    }
}
