/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ExecutorServiceWrappedJob;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: kolpakov
 * Date: 13.03.11
 * Time: 23:22
 * To change this template use File | Settings | File Templates.
 */
public abstract class BasicReportJob extends BasicJasperReport {
    public final static int REPORT_PERIOD_PREV_MONTH=0, REPORT_PERIOD_PREV_DAY=1, REPORT_PERIOD_TODAY=2, REPORT_PERIOD_PREV_PREV_DAY=3, REPORT_PERIOD_PREV_PREV_PREV_DAY=4;
    private String BASE_DOCUMENT_FILENAME;

    {
        String fullName = this.getClass().getCanonicalName();
        int i = fullName.lastIndexOf('.');
        BASE_DOCUMENT_FILENAME = fullName.substring(i + 1);
    }

    public String getBaseDocumentFilename() { return BASE_DOCUMENT_FILENAME; }
    public int getDefaultReportPeriod() { return REPORT_PERIOD_PREV_MONTH; }

    public BasicReportJob(Date generateTime, long generateDuration, JasperPrint print) {
        super(generateTime, generateDuration,
                print);
    }

    // call initialize after this constructor
    public BasicReportJob() {
    }

    public interface AutoReportRunner {
        public void run(AutoReportBuildTask autoReportBuildTask);
    }


    public interface Builder {
        public BasicReportJob build(Session session, Org org, Date startTime, Date endTime, Calendar calendar)
                        throws Exception;
    }

    public abstract AutoReportRunner getAutoReportRunner();

    public static class AutoReportBuildJob extends ExecutorServiceWrappedJob {

        public static class ExecuteEnvironment {

            private final ExecutorService executorService;
            private final SessionFactory sessionFactory;
            private final AutoReportProcessor autoReportProcessor;
            private final String reportPath;
            private final String templateFileName;
            private final Calendar calendar;
            private final DateFormat dateFormat;
            private final DateFormat timeFormat;
            private final BasicReportJob reportJob;
            private Date startDate, endDate;

            public ExecuteEnvironment(BasicReportJob reportJob, ExecutorService executorService, SessionFactory sessionFactory,
                    AutoReportProcessor autoReportProcessor, String reportPath, String templateFileName,
                    Calendar calendar, DateFormat dateFormat, DateFormat timeFormat) {
                this.executorService = executorService;
                this.sessionFactory = sessionFactory;
                this.autoReportProcessor = autoReportProcessor;
                this.reportPath = reportPath;
                this.templateFileName = templateFileName;
                this.calendar = calendar;
                this.dateFormat = dateFormat;
                this.timeFormat = timeFormat;
                this.reportJob = reportJob;
            }

            public ExecutorService getExecutorService() {
                return executorService;
            }

            public SessionFactory getSessionFactory() {
                return sessionFactory;
            }

            public AutoReportProcessor getAutoReportProcessor() {
                return autoReportProcessor;
            }

            public String getReportPath() {
                return reportPath;
            }

            public String getTemplateFileName() {
                return templateFileName;
            }

            public Calendar getCalendar() {
                synchronized (calendar) {
                    return (Calendar) calendar.clone();
                }
            }

            public DateFormat getDateFormat() {
                synchronized (dateFormat) {
                    return (DateFormat) dateFormat.clone();
                }
            }

            public DateFormat getTimeFormat() {
                synchronized (timeFormat) {
                    return (DateFormat) timeFormat.clone();
                }
            }

            public Date getStartDate() {
                return startDate;
            }

            public void setStartDate(Date startDate) {
                this.startDate = startDate;
            }

            public Date getEndDate() {
                return endDate;
            }

            public void setEndDate(Date endDate) {
                this.endDate = endDate;
            }
        }

        public static final String ENVIRONMENT_JOB_PARAM = ExecuteEnvironment.class.getCanonicalName();

        protected ExecutorService getExecutorService(JobExecutionContext context) throws Exception {
            final ExecuteEnvironment executeEnvironment = (ExecuteEnvironment) context.getJobDetail().getJobDataMap()
                    .get(ENVIRONMENT_JOB_PARAM);
            return executeEnvironment.getExecutorService();
        }

        protected Runnable getRunnable(JobExecutionContext context) {
            final ExecuteEnvironment executeEnvironment = (ExecuteEnvironment) context.getJobDetail().getJobDataMap()
                    .get(ENVIRONMENT_JOB_PARAM);
            Calendar calendar = executeEnvironment.getCalendar();
            Date endTime, startTime;
            startTime = executeEnvironment.getStartDate();
            executeEnvironment.setStartDate(null);
            endTime = executeEnvironment.getEndDate();
            executeEnvironment.setEndDate(null);

            if (endTime==null) {
                if (executeEnvironment.reportJob.getDefaultReportPeriod()==REPORT_PERIOD_PREV_MONTH) {
                    if (startTime==null) startTime = calculateLastMonthFirstDay(calendar, context.getScheduledFireTime());
                    endTime = calculatePlusOneMonth(calendar, startTime);
                } else if (executeEnvironment.reportJob.getDefaultReportPeriod()==REPORT_PERIOD_PREV_DAY) {
                    if (startTime==null) startTime = calculateYesterdayStart(calendar, context.getScheduledFireTime());
                    endTime = calculatePlusOneDay(calendar, startTime);
                } else if (executeEnvironment.reportJob.getDefaultReportPeriod()==REPORT_PERIOD_PREV_PREV_DAY) {
                    if (startTime==null) {
                        startTime = calculateYesterdayStart(calendar, context.getScheduledFireTime());
                        startTime = calculateMinusOneDay(calendar, startTime);
                    }
                    endTime = calculatePlusOneDay(calendar, startTime);
                } else if (executeEnvironment.reportJob.getDefaultReportPeriod()==REPORT_PERIOD_PREV_PREV_PREV_DAY) {
                    if (startTime==null) {
                        startTime = calculateYesterdayStart(calendar, context.getScheduledFireTime());
                        startTime = calculateMinusOneDay(calendar, startTime);
                        startTime = calculateMinusOneDay(calendar, startTime);
                    }
                    endTime = calculatePlusOneDay(calendar, startTime);
                } else if (executeEnvironment.reportJob.getDefaultReportPeriod()==REPORT_PERIOD_TODAY) {
                    if (startTime==null) startTime = calculateTodayStart(calendar, context.getScheduledFireTime());
                    endTime = calculatePlusOneDay(calendar, startTime);
                }
            }
            return new AutoReportBuildTask(executeEnvironment.reportJob.getAutoReportRunner(), executeEnvironment.getExecutorService(),
                    executeEnvironment.getAutoReportProcessor(), executeEnvironment.getSessionFactory(),
                    executeEnvironment.getTemplateFileName(), calendar, startTime, endTime, executeEnvironment.reportJob
                    .createDocumentBuilders(executeEnvironment.getReportPath(), executeEnvironment.getDateFormat(),
                            executeEnvironment.getTimeFormat()));
        }

        private static Date calculateYesterdayStart(Calendar calendar, Date scheduledFireTime) {
            calendar.setTime(scheduledFireTime);
            CalendarUtils.truncateToDayOfMonth(calendar);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            return calendar.getTime();
        }

        private static Date calculateTodayStart(Calendar calendar, Date scheduledFireTime) {
            calendar.setTime(scheduledFireTime);
            CalendarUtils.truncateToDayOfMonth(calendar);
            return calendar.getTime();
        }

        private static Date calculateMinusOneDay(Calendar calendar, Date endTime) {
            calendar.setTime(endTime);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            return calendar.getTime();
        }

        private static Date calculatePlusOneDay(Calendar calendar, Date endTime) {
            calendar.setTime(endTime);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            return calendar.getTime();
        }

        private static Date calculateLastMonthFirstDay(Calendar calendar, Date scheduledFireTime) {
            calendar.setTime(scheduledFireTime);
            calendar.add(Calendar.MONTH, -1);
            CalendarUtils.truncateToMonth(calendar);
            return calendar.getTime();
        }

        private static Date calculatePlusOneMonth(Calendar calendar, Date endTime) {
            calendar.setTime(endTime);
            calendar.add(Calendar.MONTH, 1);
            return calendar.getTime();
        }
    }
    public static class AutoReportBuildTask implements Runnable {

        private static final Logger logger = LoggerFactory.getLogger(AutoReportBuildTask.class);
        public final ExecutorService executorService;
        public final AutoReportProcessor autoReportProcessor;
        public final SessionFactory sessionFactory;
        public final String templateFileName;
        public final Calendar startCalendar;
        public final Date startTime;
        public final Date endTime;
        public final Map<Integer, ReportDocumentBuilder> documentBuilders;
        private final AutoReportRunner reportRunner;

        public AutoReportBuildTask(AutoReportRunner reportRunner, ExecutorService executorService,
                AutoReportProcessor autoReportProcessor, SessionFactory sessionFactory, String templateFileName,
                Calendar startCalendar, Date startTime, Date endTime,
                Map<Integer, ReportDocumentBuilder> documentBuilders) {
            this.executorService = executorService;
            this.autoReportProcessor = autoReportProcessor;
            this.sessionFactory = sessionFactory;
            this.templateFileName = templateFileName;
            this.startCalendar = startCalendar;
            this.startTime = startTime;
            this.endTime = endTime;
            this.documentBuilders = documentBuilders;
            this.reportRunner = reportRunner;
        }

        public void run() {
            reportRunner.run(this);
        }


    }

    public abstract String getReportDistinctText();

    public static class DocumentBuilderCallback implements BasicReport.DocumentBuilderCallback {
        public String getReportDistinctText(BasicReport report) {
            BasicReportJob basicReportJob = (BasicReportJob) report;
            return basicReportJob.getReportDistinctText();
        }
    }

    Map<Integer, ReportDocumentBuilder> createDocumentBuilders(String reportPath, DateFormat dateFormat,
            DateFormat timeFormat) {
        DocumentBuilderCallback documentBuilderCallback = new DocumentBuilderCallback();
        Map<Integer, ReportDocumentBuilder> documentBuilders = new HashMap<Integer, ReportDocumentBuilder>();
        documentBuilders.put(ReportHandleRule.PDF_FORMAT,
                new PdfBuilder(reportPath, BASE_DOCUMENT_FILENAME, documentBuilderCallback,
                        (DateFormat) dateFormat.clone(), (DateFormat) timeFormat.clone()));
        documentBuilders.put(ReportHandleRule.XLS_FORMAT,
                new XlsBuilder(reportPath, BASE_DOCUMENT_FILENAME, documentBuilderCallback,
                        (DateFormat) dateFormat.clone(), (DateFormat) timeFormat.clone()));
        documentBuilders.put(ReportHandleRule.HTML_FORMAT,
                new HtmlBuilder(reportPath, BASE_DOCUMENT_FILENAME, documentBuilderCallback,
                        (DateFormat) dateFormat.clone(), (DateFormat) timeFormat.clone()));
        documentBuilders.put(ReportHandleRule.CSV_FORMAT,
                new CsvBuilder(reportPath, BASE_DOCUMENT_FILENAME, documentBuilderCallback,
                        (DateFormat) dateFormat.clone(), (DateFormat) timeFormat.clone()));
        return documentBuilders;
    }

    public Date startTime;
    public Date endTime;
    public Calendar calendar;
    public String templateFilename;
    public SessionFactory sessionFactory;

    public BasicReportJob(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print);
        this.startTime = startTime;
        this.endTime = endTime;
        this.templateFilename = null;
        this.sessionFactory = null;
        this.calendar = null;
    }

    public void initialize(Date startTime, Date endTime, String templateFilename,
            SessionFactory sessionFactory, Calendar calendar) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.templateFilename = templateFilename;
        this.sessionFactory = sessionFactory;
        this.calendar = calendar;
    }

    public void BasicReportJob(Date startTime, Date endTime, String templateFilename,
            SessionFactory sessionFactory, Calendar calendar) {
        initialize(startTime, endTime, templateFilename, sessionFactory, calendar);
    }
    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

}
