/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.DailyFileCreator;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.hibernate.Transaction;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 15.12.2009
 * Time: 12:10:58
 * To change this template use File | Settings | File Templates.
 */
public class BasicReport {




    public static interface DocumentBuilderCallback {

        String getReportDistinctText(BasicReport report) throws Exception;
    }

    public static abstract class BasicDocumentBuilder extends DailyFileCreator implements ReportDocumentBuilder {

        private static final Logger logger = LoggerFactory.getLogger(BasicDocumentBuilder.class);
        private final String baseFileName;
        private final String fileNameSuffix;
        private final DocumentBuilderCallback documentBuilderCallback;
        private final DateFormat timeFormat;

        public BasicDocumentBuilder(String basePath, String baseFileName, String fileNameSuffix,
                DocumentBuilderCallback documentBuilderCallback, DateFormat dateFormat, DateFormat timeFormat) {
            super(basePath, dateFormat);
            this.baseFileName = baseFileName;
            this.fileNameSuffix = fileNameSuffix;
            this.documentBuilderCallback = documentBuilderCallback;
            this.timeFormat = timeFormat;
        }

        public DateFormat getTimeFormat() {
            synchronized (this.timeFormat) {
                return (DateFormat) timeFormat.clone();
            }
        }

        public ReportDocument buildDocument(String ruleId, BasicReport report) throws Exception {
            DateFormat timeFormat = getTimeFormat();
            String filename = String
                    .format("%s-%s-%s-%s", baseFileName, ruleId, documentBuilderCallback.getReportDistinctText(report),
                            timeFormat.format(report.getGenerateTime()));
            try {
                File reportDocumentFile = createFile(filename, fileNameSuffix);
                writeReportDocumentTo(report, reportDocumentFile);
                return new ReportDocument(reportDocumentFile);
            } catch (Exception e) {
                logger.error("Failed to create jasperReport document file", e);
                throw e;
            }
        }

        protected abstract void writeReportDocumentTo(BasicReport report, File file) throws Exception;

    }

    private Properties reportProperties;
    private Date generateTime;
    private long generateDuration;

    public BasicReport() {
        this.generateTime = new Date();
        this.generateDuration = 0L;
    }

    public BasicReport(Date generateTime, long generateDuration) {
        this.generateTime = generateTime;
        this.generateDuration = generateDuration;
    }

    public Date getGenerateTime() {
        prepare();
        return generateTime;
    }

    public Long getGenerateDuration() {
        prepare();
        return generateDuration;
    }

    @Override
    public String toString() {
        return "BasicReport{" + "generateTime=" + generateTime + ", generateDuration=" + generateDuration + '}';
    }

    protected void setGenerateTime(Date generateTime) {
        this.generateTime = generateTime;
    }

    protected void setGenerateDuration(long generateDuration) {
        this.generateDuration = generateDuration;
    }

    protected void prepare() {

    }

    protected static Transaction createTransaction(Session session) {
        Transaction transaction = session.getTransaction();
        //transaction.setTimeout(60*60);
        return transaction;
    }

    public static String longToMoney(Long money) {
        return String.format("%d.%02d", money/100, Math.abs(money%100));
    }

    public Properties getReportProperties() {
        return reportProperties;
    }

    public void setReportProperties(Properties reportProperties) {
        this.reportProperties = reportProperties;
    }
}
