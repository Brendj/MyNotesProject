/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ExecutorServiceWrappedJob;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;

public abstract class BasicReportJob extends BasicJasperReport {
    public abstract Builder createBuilder(String templateFilename);
    public final static int
            REPORT_PERIOD_PREV_MONTH = 0,
            REPORT_PERIOD_PREV_DAY = 1,
            REPORT_PERIOD_TODAY = 2,
            REPORT_PERIOD_PREV_PREV_DAY = 3,
            REPORT_PERIOD_PREV_PREV_PREV_DAY = 4,
            REPORT_PERIOD_LAST_WEEK = 5,
            REPORT_PERIOD_CURRENT_MONTH = 6,
            REPORT_PERIOD_PREV_WEEK = 7;
    private String BASE_DOCUMENT_FILENAME;
    protected Date startTime;
    protected Date endTime;
    protected Calendar calendar;
    protected String templateFilename;
    protected SessionFactory sessionFactory;

    {
        String fullName = this.getClass().getCanonicalName();
        int i = fullName.lastIndexOf('.');
        BASE_DOCUMENT_FILENAME = fullName.substring(i + 1);
    }

    public String getBaseDocumentFilename() { return BASE_DOCUMENT_FILENAME; }
    public int getDefaultReportPeriod() { return REPORT_PERIOD_PREV_MONTH; }

    public BasicReportJob(Date generateTime, long generateDuration, JasperPrint print) {
        super(generateTime, generateDuration, print);
    }

    // call initialize after this constructor
    public BasicReportJob() {
    }

    public Class getMyClass() { return getClass(); }

    protected List<RuleProcessor.Rule> getThisReportRulesList(Session session, Long idOfSchedulerJob) throws Exception {
        List<JobRules> loadJobRules;
        SchedulerJob schedulerJob = (SchedulerJob) session.load(SchedulerJob.class, idOfSchedulerJob);
        Criteria reportJobRules = JobRules.createReportJobRulesCriteria(session, schedulerJob);
        loadJobRules = reportJobRules.list();

        List<RuleProcessor.Rule> newRules = new LinkedList<RuleProcessor.Rule>();

        if (!loadJobRules.isEmpty()) {
            for (JobRules jobRule: loadJobRules) {
                newRules.add(new RuleProcessor.Rule(jobRule.getReportHandleRule()));
            }
        } else {
            Criteria reportRulesCriteria = ReportHandleRule.createEnabledReportRulesCriteria(session);
            List rules = reportRulesCriteria.list();
            for (Object currObject : rules) {
                ReportHandleRule currRule = (ReportHandleRule) currObject;
                if (currRule.isEnabled()) {
                    for (RuleCondition ruleCondition : currRule.getRuleConditions()) {
                        if (ruleCondition.getConditionConstant().equals(getMyClass().getCanonicalName())) {
                            newRules.add(new RuleProcessor.Rule(currRule));
                            break;
                        }
                    }
                }
            }
        }
        return newRules;
    }

    //Метод который возвращает правила по таблице cf_jobrules
    public List<Long> getRulesIdsByJobRules(Session session, Long idOfSchedulerJob) throws Exception {
        List<Long> reportHandleRules = new ArrayList<Long>();

        SchedulerJob schedulerJob = (SchedulerJob) session.load(SchedulerJob.class, idOfSchedulerJob);
        Criteria reportJobRules = JobRules.createReportJobRulesCriteria(session, schedulerJob);
        List<JobRules> loadJobRules = reportJobRules.list();

        if (!loadJobRules.isEmpty()) {
            for (JobRules jobRule : loadJobRules) {
                reportHandleRules.add(jobRule.getReportHandleRule().getIdOfReportHandleRule());
            }
        }

        return reportHandleRules;
    }

    public interface AutoReportRunner {
        public void run(AutoReportBuildTask autoReportBuildTask);
    }

    public static abstract class Builder {
        protected Contragent contragent;
        protected OrgShortItem org;
        protected List<OrgShortItem> orgShortItemList;
        protected long userId;
        
        public Properties reportProperties = new Properties();

        public void setUserId(long value) {
            userId = value;
        }

        public long getUserId() {
            return userId;
        }

        public Properties getReportProperties() {
            return reportProperties;
        }

        public void setReportProperties(Properties reportProperties) {
            this.reportProperties = reportProperties;
        }

        public Contragent getContragent() {
            return contragent;
        }

        public void setContragent(Contragent contragent) {
            this.contragent = contragent;
        }

        public OrgShortItem getOrg() {
            return org;
        }

        public void setOrg(OrgShortItem org) {
            this.org = org;
        }

        public List<OrgShortItem> getOrgShortItemList() {
            return orgShortItemList;
        }

        public void setOrgShortItemList(List<OrgShortItem> orgShortItemList) {
            this.orgShortItemList = orgShortItemList;
        }

        public abstract BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar)
                        throws Exception;
    }

    public static class OrgShortItem {

        private Long idOfOrg;
        private String shortName;
        private String officialName;
        private String address;
        private Long sourceMenuOrg;
        private Boolean selected = false;
        private String orgDistrict;
        public OrgShortItem() {
            selected = false;
        }

        public OrgShortItem(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public OrgShortItem(Long idOfOrg, String shortName, String officialName, String address) {
            this.idOfOrg = idOfOrg;
            this.shortName = shortName;
            this.officialName = officialName;
            this.address = address;
        }

        public OrgShortItem(Long idOfOrg, String shortName, String officialName) {
            this.idOfOrg = idOfOrg;
            this.shortName = shortName;
            this.officialName = officialName;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public String getOfficialName() {
            return officialName;
        }

        public void setOfficialName(String officialName) {
            this.officialName = officialName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        public Long getSourceMenuOrg() {
            return sourceMenuOrg;
        }

        public void setSourceMenuOrg(Long sourceMenuOrg) {
            this.sourceMenuOrg = sourceMenuOrg;
        }

        public String getOrgDistrict() {
            return orgDistrict;
        }

        public void setOrgDistrict(String orgDistrict) {
            this.orgDistrict = orgDistrict;
        }
    }

    public abstract AutoReportRunner getAutoReportRunner();

    public static class AutoReportBuildJob extends ExecutorServiceWrappedJob {

        private ExecuteEnvironment executeEnvironment;

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
            private final String jobName;
            private final String jobId;

            public ExecuteEnvironment(String jobId, String jobName, BasicReportJob reportJob, ExecutorService executorService, SessionFactory sessionFactory,
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
                this.jobName = jobName;
                this.jobId = jobId;
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
            this.executeEnvironment = (ExecuteEnvironment) context.getJobDetail().getJobDataMap()
                    .get(ENVIRONMENT_JOB_PARAM);
            Calendar calendar = executeEnvironment.getCalendar();
            Date endTime, startTime;
            startTime = executeEnvironment.getStartDate();
            executeEnvironment.setStartDate(null);
            endTime = executeEnvironment.getEndDate();
            executeEnvironment.setEndDate(null);
            
            boolean datesSpecifiedByUser = true;

            if (endTime==null) {
                datesSpecifiedByUser = false;

                Date[] dates = calculateDatesForPeriodType(calendar, startTime, context.getScheduledFireTime(), executeEnvironment.reportJob.getDefaultReportPeriod());
                startTime = dates[0];
                endTime = dates[1];
            }
            return new AutoReportBuildTask(executeEnvironment.jobId, executeEnvironment.jobName, datesSpecifiedByUser,
                    executeEnvironment.reportJob.getAutoReportRunner(), executeEnvironment.getExecutorService(),
                    executeEnvironment.getAutoReportProcessor(), executeEnvironment.getSessionFactory(),
                    executeEnvironment.getTemplateFileName(), calendar, startTime, endTime, executeEnvironment.reportJob
                    .createDocumentBuilders(executeEnvironment.getReportPath(), executeEnvironment.getDateFormat(),
                            executeEnvironment.getTimeFormat()));
        }
        
        

    }
    public static class AutoReportBuildTask implements Runnable {

        private static final Logger logger = LoggerFactory.getLogger(AutoReportBuildTask.class);
        public final ExecutorService executorService;
        public final AutoReportProcessor autoReportProcessor;
        public final SessionFactory sessionFactory;
        public final String templateFileName;
        public final String jobName;
        public final String jobId;
        public final boolean datesSpecifiedByUser;
        public final Calendar startCalendar;
        public final Date startTime;
        public final Date endTime;
        public final Map<Integer, ReportDocumentBuilder> documentBuilders;
        private final AutoReportRunner reportRunner;

        public AutoReportBuildTask(String jobId, String jobName, boolean datesSpecifiedByUser, AutoReportRunner reportRunner, ExecutorService executorService,
                AutoReportProcessor autoReportProcessor, SessionFactory sessionFactory, String templateFileName,
                Calendar startCalendar, Date startTime, Date endTime,
                Map<Integer, ReportDocumentBuilder> documentBuilders) {
            this.jobId = jobId;
            this.jobName = jobName;
            this.datesSpecifiedByUser = datesSpecifiedByUser;
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

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getTemplateFilename() {
        return templateFilename;
    }

    public void setTemplateFilename(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    public void applyDataQueryPeriod(int period) {
        startTime = CalendarUtils.addDays(endTime, -period);
    }

    public static Date[] calculateDatesForPeriodType(Calendar calendar, Date startTime, Date generateTime, int type) {
        Date endTime = null;
        if (type == REPORT_PERIOD_PREV_MONTH) {
            if (startTime == null) {
                startTime = CalendarUtils.calculateLastMonthFirstDay(calendar, generateTime);
            }
            endTime = minusMillisecond(calendar, CalendarUtils.calculatePlusOneMonth(calendar, startTime));
        } else if (type == REPORT_PERIOD_PREV_DAY) {
            if (startTime == null) {
                startTime = CalendarUtils.calculateYesterdayStart(calendar, generateTime);
            }
            endTime = minusMillisecond(calendar, CalendarUtils.calculatePlusOneDay(calendar, startTime));
        } else if (type == REPORT_PERIOD_TODAY) {
            if (startTime == null) {
                startTime = CalendarUtils.calculateTodayStart(calendar, generateTime);
            }
            endTime = minusMillisecond(calendar, CalendarUtils.calculatePlusOneDay(calendar, startTime));
        } else if (type == REPORT_PERIOD_PREV_PREV_DAY) {
            if (startTime == null) {
                startTime = CalendarUtils.calculateYesterdayStart(calendar, generateTime);
                startTime = CalendarUtils.calculateMinusOneDay(calendar, startTime);
            }
            endTime = minusMillisecond(calendar, CalendarUtils.calculatePlusOneDay(calendar, startTime));
        } else if (type == REPORT_PERIOD_PREV_PREV_PREV_DAY) {
            if (startTime == null) {
                startTime = CalendarUtils.calculateYesterdayStart(calendar, generateTime);
                startTime = CalendarUtils.calculateMinusOneDay(calendar, startTime);
                startTime = CalendarUtils.calculateMinusOneDay(calendar, startTime);
            }
            endTime = minusMillisecond(calendar, CalendarUtils.calculatePlusOneDay(calendar, startTime));
        } else if (type == REPORT_PERIOD_LAST_WEEK) {
            if (startTime == null) {
                calendar.setTime(generateTime);
            } else {
                calendar.setTime(startTime);
            }
            // устанавливаем понедельник в качестве стартовой даты выборки
            calendar.set(Calendar.DAY_OF_WEEK, 2);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            startTime = calendar.getTime();
            // устанавливаем субботу в качестве конечной даты выборки
            calendar.set(Calendar.DAY_OF_WEEK, 7);
            endTime = CalendarUtils.endOfDay(calendar.getTime());
        } else if (type == REPORT_PERIOD_CURRENT_MONTH) {
            // startTime - первый день текущего месяца
            // endTime - поледний день текущего месяца
            if (startTime == null) {
                startTime = CalendarUtils.calculateCurrentMonthFirstDay(calendar, generateTime);
            }
            endTime = minusMillisecond(calendar, CalendarUtils.calculatePlusOneMonth(calendar, startTime));
        } else if (type == REPORT_PERIOD_PREV_WEEK) {
            if (startTime == null) {
                calendar.setTime(generateTime);
            } else {
                calendar.setTime(startTime);
            }
            // устанавливаем понедельник в качестве стартовой даты выборки
            calendar.set(Calendar.DAY_OF_WEEK, 2);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.add(Calendar.DATE, -7);
            startTime = calendar.getTime();
            calendar.set(Calendar.DAY_OF_WEEK, 7);
            endTime = CalendarUtils.endOfDay(calendar.getTime());
        }
        return new Date[]{startTime, endTime};
    }

    private static Date minusMillisecond(Calendar calendar, Date endTime) {
        calendar.setTime(endTime);
        calendar.add(Calendar.MILLISECOND, -1);
        endTime = calendar.getTime();
        return endTime;
    }

}
