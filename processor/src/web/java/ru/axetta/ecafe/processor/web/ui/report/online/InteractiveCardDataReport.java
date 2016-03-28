/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.TransactionsReport;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: T800
 * Date: 25.03.16
 * Time: 18:30
 * To change this template use File | Settings | File Templates.
 */
public class InteractiveCardDataReport extends BasicReportForAllOrgJob {

    private final static Logger logger = LoggerFactory.getLogger(InteractiveCardDataReport.class);
    public static DateFormat dailyItemsFormat = new SimpleDateFormat("dd.MM.yyyy");

    //protected List<InteractiveCardDataReportItem> items;
    private String htmlReport;

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {
        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            String templateName = InteractiveCardDataReport.class.getSimpleName();
            templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + templateName + ".jasper";
        }

        @Override
        public InteractiveCardDataReport build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            return doBuild(session, calendar);
        }

        public InteractiveCardDataReport doBuild(Session session, Calendar calendar) throws Exception {
                        Date generateTime = new Date();

            /* Строим параметры для передачи в jasper */
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(generateTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("reportDate", dailyItemsFormat.format(generateTime));

            return null;
        }


    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new TransactionsReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_MONTH;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public InteractiveCardDataReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }

    public static final class InteracticeCardDataReportItem {
        protected long idOfRule;
    }
}

