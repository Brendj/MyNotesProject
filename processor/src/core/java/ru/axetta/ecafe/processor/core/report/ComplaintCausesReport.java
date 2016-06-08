/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormatSymbols;
import java.util.*;

public class ComplaintCausesReport extends BasicReportForOrgJob {
    /*
    * Параметры отчета для добавления в правила и шаблоны
    *
    * При создании любого отчета необходимо добавить параметры:
    * REPORT_NAME - название отчета на русском
    * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
    * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
    * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
    * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
    *
    * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
    */
    public static final String REPORT_NAME = "Отчет по частоте указания той или иной причины подачи жалобы на товары";
    public static final String[] TEMPLATE_FILE_NAMES = {"ComplaintCausesReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{3, 4, 5};


    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportJob.Builder {

        public static class ComplaintCauseReportItem {

            // Причина подачи жалобы
            private String complaintcause;
            // Количество жалоб, поданных с указанием данной причины
            private Long causecount;

            public ComplaintCauseReportItem(String complaintcause, Long causecount) {
                this.complaintcause = complaintcause;
                this.causecount = causecount;
            }

            public String getComplaintcause() {
                return complaintcause;
            }

            public void setComplaintcause(String complaintcause) {
                this.complaintcause = complaintcause;
            }

            public Long getCausecount() {
                return causecount;
            }

            public void setCausecount(Long causecount) {
                this.causecount = causecount;
            }

        }

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("orgName", org.getOfficialName());
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new ComplaintCausesReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            List<ComplaintCauseReportItem> resultRows = new LinkedList<ComplaintCauseReportItem>();
            Query query = session.createSQLQuery("SELECT d.description, count(c.cause) AS cause_count"
                    + " FROM cf_possible_complaint_causes d"
                    + " LEFT OUTER JOIN cf_goods_complaint_causes c"
                    + " ON d.causenumber = c.cause"
                    + " AND c.orgowner = :idoforg AND c.createddate >= :startTime AND c.createddate <= :endTime"
                    + " GROUP BY d.description"
                    + " ORDER BY cause_count DESC, d.description;");
            query.setParameter("startTime", startTime.getTime());
            query.setParameter("endTime", endTime.getTime());
            query.setParameter("idoforg", org.getIdOfOrg());
            List list = query.list();
            for (Object result : list) {
                Object[] cause = (Object[]) result;
                String complaintCause = cause[0].toString();
                Long causeCount = Long.parseLong(cause[1].toString());
                resultRows.add(new ComplaintCauseReportItem(complaintCause, causeCount));
            }
            return new JRBeanCollectionDataSource(resultRows);
        }

    }

    public ComplaintCausesReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);
    }
    private static final Logger logger = LoggerFactory.getLogger(ComplaintCausesReport.class);

    public ComplaintCausesReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new ComplaintCausesReport();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
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
}
