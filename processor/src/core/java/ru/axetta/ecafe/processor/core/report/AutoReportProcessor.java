/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.02.2010
 * Time: 14:26:33
 * To change this template use File | Settings | File Templates.
 */
public interface AutoReportProcessor {

    /**
     * Warning: has to be threadsafe
     *
     * @param reports
     * @param reportDocumentBuilders
     * @throws Exception
     */
    void processAutoReports(List<AutoReport> reports, Map<Integer, ReportDocumentBuilder> reportDocumentBuilders, List<Long> reportHandleRuleIdsList)
            throws Exception;

    /**
     * Warning: has to be threadsafe
     *
     * @throws Exception
     */
    void loadAutoReportRules() throws Exception;

    /**
     * Created by IntelliJ IDEA.
     * User: Developer
     * Date: 23.12.2009
     * Time: 11:35:52
     * To change this template use File | Settings | File Templates.
     */
    public static class ProcessTask implements Runnable {

        private static final Logger logger = LoggerFactory.getLogger(ProcessTask.class);
        private final AutoReportProcessor autoReportProcessor;
        private final List<AutoReport> reports;
        private final Map<Integer, ReportDocumentBuilder> reportDocumentBuilders;
        private List<Long> reportHandleRuleIdsList;

        public ProcessTask(AutoReportProcessor autoReportProcessor, List<AutoReport> reports,
                Map<Integer, ReportDocumentBuilder> reportDocumentBuilders) {
            this.autoReportProcessor = autoReportProcessor;
            this.reports = reports;
            this.reportDocumentBuilders = reportDocumentBuilders;
        }

        public ProcessTask(AutoReportProcessor autoReportProcessor, List<AutoReport> reports,
                Map<Integer, ReportDocumentBuilder> reportDocumentBuilders, List<Long> reportHandleRuleIdsList) {
            this.autoReportProcessor = autoReportProcessor;
            this.reports = reports;
            this.reportDocumentBuilders = reportDocumentBuilders;
            this.reportHandleRuleIdsList = reportHandleRuleIdsList;
        }

        public void run() {
            try {
                autoReportProcessor.processAutoReports(reports, reportDocumentBuilders, reportHandleRuleIdsList);
            } catch (Exception e) {
                logger.error("Failed to handle reports", e);
            }
        }
    }
}
