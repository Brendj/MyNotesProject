/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.TransactionsReport;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Anvarov
 * Date: 25.03.16
 * Time: 18:30
 * To change this template use File | Settings | File Templates.
 */
public class InteractiveCardDataReport extends BasicReportForAllOrgJob {

    private final static Logger logger = LoggerFactory.getLogger(InteractiveCardDataReport.class);
    public static DateFormat dailyItemsFormat = new SimpleDateFormat("dd.MM.yyyy");

    protected List<InteractiveCardDataReportItem> items;
    private String htmlReport;

    public InteractiveCardDataReport(Date generateTime, long l, JasperPrint jasperPrint,
            List<InteractiveCardDataReportItem> items) {
        super(generateTime, l, jasperPrint, generateTime, generateTime);
        this.items = items;
    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;
        private boolean exportToHTML = false;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
            exportToHTML = true;
        }

        public Builder() {
            String templateName = InteractiveCardDataReport.class.getSimpleName();
            templateFilename =
                    RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + templateName
                            + ".jasper";
        }

        @Override
        public InteractiveCardDataReport build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            return doBuild(session, calendar);
        }

        public InteractiveCardDataReport doBuild(Session session, Calendar calendar) throws Exception {
            String idOfOrg = StringUtils
                    .trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG));

            Org org = (Org) session.load(Org.class, Long.valueOf(idOfOrg));

            Date generateTime = new Date();

            /* Строим параметры для передачи в jasper */
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(generateTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("reportDate", dailyItemsFormat.format(generateTime));
            parameterMap.put("orgName", org.getShortName());

            List<InteractiveCardDataReportItem> items = findItems(session, idOfOrg);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, createDataSource(items));
            Date generateEndTime = new Date();

            if (!exportToHTML) {
                return new InteractiveCardDataReport(generateTime, generateEndTime.getTime() - generateTime.getTime(), jasperPrint, items);
            } else {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                JRHtmlExporter exporter = new JRHtmlExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
                exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
                exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();
                return new InteractiveCardDataReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        jasperPrint, items).setHtmlReport(os.toString("UTF-8"));

            }
        }

        private JRDataSource createDataSource(List<InteractiveCardDataReportItem> items) {
            return new JRBeanCollectionDataSource(items);
        }

        private List<InteractiveCardDataReportItem> findItems(Session session, String idOfOrg) {

            List<InteractiveCardDataReportItem> items = new ArrayList<InteractiveCardDataReportItem>();

            getInteractiveReportDataEntity(session, idOfOrg, items);

            return items;
        }

        private List<InteractiveCardDataReportItem>  getInteractiveReportDataEntity(Session session, String idOfOrg,
               List<InteractiveCardDataReportItem> items) {

            Long idOfOrgL = Long.valueOf(idOfOrg);

            String sql = "select idofrecord, value from cf_interactive_report_data where idoforg = :idoforg ";
            Query query = session.createSQLQuery(sql);
            query.setParameter("idoforg", idOfOrgL);
            List res = query.list();

            if (res == null) {
                return Collections.EMPTY_LIST;
            }

            long fondGk = 0;

            for (Object entry : res) {
                Object e[] = (Object[]) entry;
                long idOfRecord = ((BigInteger) e[0]).longValue();
                String value = (String) e[1];

                if (idOfRecord == 0L) {
                    fondGk += Long.valueOf(value);
                    InteractiveCardDataReportItem item = new InteractiveCardDataReportItem(5L, "2.1", Long.valueOf(value), "в рамках ГК на внедрение", "Количество сервисных карт, поступивших на этапе внедрения ИС ПП (Форма учета, показатель 1).");
                    items.add(item);
                }

                if (idOfRecord == 1L) {
                    fondGk += Long.valueOf(value);
                    InteractiveCardDataReportItem item = new InteractiveCardDataReportItem(6L, "2.2", Long.valueOf(value), "в рамках ГК на сервис", "Количество сервисных карт, поступивших на этапе эксплуатации ИС ПП (Форма учета, показатель 2).");
                    items.add(item);
                }

                if (idOfRecord == 2L) {
                    fondGk += Long.valueOf(value);
                    InteractiveCardDataReportItem item = new InteractiveCardDataReportItem(7L, "2.3", Long.valueOf(value), "Закуплено ОО", "Количество сервисных карт, поступивших в результате их закупки (Форма учета, показатель 3).");
                    items.add(item);
                }

                if (idOfRecord == 3L) {
                    InteractiveCardDataReportItem item = new InteractiveCardDataReportItem(15L, "6", Long.valueOf(value), "Фонд резервных карт, доступных к использованию", "Количество карт, физически находящихся в резервном фонде (Форма учета, показатель 5).");
                    items.add(item);
                }

                if (idOfRecord == 4L) {
                    InteractiveCardDataReportItem item = new InteractiveCardDataReportItem(14L, "5", Long.valueOf(value), "Потери сервисных  карт", "Количество карт, физически находящихся в резервном фонде (Форма учета, показатель 5).");
                    items.add(item);
                }
            }

            InteractiveCardDataReportItem item = new InteractiveCardDataReportItem(4L, "2", fondGk, "Фонд поступивших сервисных карт", "Количество сервисных карт, зарегистрированных в ИС ПП.");
            items.add(item);

            Collections.sort(items);

            return items;
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

    public static final class InteractiveCardDataReportItem implements Comparable<InteractiveCardDataReportItem> {

        protected Long id;
        protected String rowNum;
        protected long value;
        protected String ruleName;
        protected String description;

        public InteractiveCardDataReportItem(Long id, String rowNum, long value, String ruleName, String description) {
            this.id = id;
            this.rowNum = rowNum;
            this.value = value;
            this.ruleName = ruleName;
            this.description = description;
        }

        public InteractiveCardDataReportItem(String rowNum, long value, String ruleName, String description) {
            this.rowNum = rowNum;
            this.value = value;
            this.ruleName = ruleName;
            this.description = description;
        }

        public InteractiveCardDataReportItem() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getRowNum() {
            return rowNum;
        }

        public void setRowNum(String rowNum) {
            this.rowNum = rowNum;
        }

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public String getRuleName() {
            return ruleName;
        }

        public void setRuleName(String ruleName) {
            this.ruleName = ruleName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public int compareTo(InteractiveCardDataReportItem o) {
            int retCode = this.id.compareTo(o.getId());
            return retCode;
        }
    }
}

