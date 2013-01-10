/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.Org;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormatSymbols;
import java.util.*;

public class ComplaintIterationsReport extends BasicReportForOrgJob {

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportJob.Builder {

        public static class ComplaintIterationReportItem {

            // Статус жалобы
            private String iterationState;
            // Количество итераций в указанном статусе
            private Long stateCount;

            public ComplaintIterationReportItem(String iterationState, Long stateCount) {
                this.iterationState = iterationState;
                this.stateCount = stateCount;
            }

            public String getIterationState() {
                return iterationState;
            }

            public void setIterationState(String iterationState) {
                this.iterationState = iterationState;
            }

            public Long getStateCount() {
                return stateCount;
            }

            public void setStateCount(Long stateCount) {
                this.stateCount = stateCount;
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
            return new ComplaintIterationsReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, Org org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            List<ComplaintIterationReportItem> resultRows = new LinkedList<ComplaintIterationReportItem>();
            Query query = session.createSQLQuery("SELECT d.description, count(i.iterationstatus) AS state_count"
                    + " FROM cf_possible_complaint_iteration_states d"
                    + " LEFT OUTER JOIN cf_goods_complaint_iterations i"
                    + " ON d.statenumber = i.iterationstatus"
                    + " WHERE i.orgowner = :idoforg AND i.createddate >= :startDate AND i.createddate <= :endDate"
                    + " GROUP BY d.description"
                    + " ORDER BY d.description, state_count;");
            query.setParameter("startTime", startTime.getTime());
            query.setParameter("endTime", endTime.getTime());
            query.setParameter("idoforg", org.getIdOfOrg());
            List list = query.list();
            for (Object result : list) {
                Object[] state = (Object[]) result;
                String iterationState = state[0].toString();
                Long stateCount = Long.parseLong(state[1].toString());
                resultRows.add(new ComplaintIterationReportItem(iterationState, stateCount));
            }
            return new JRBeanCollectionDataSource(resultRows);
        }

    }

    public ComplaintIterationsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);
    }
    private static final Logger logger = LoggerFactory.getLogger(ComplaintIterationsReport.class);

    public ComplaintIterationsReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new ComplaintIterationsReport();
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
        return REPORT_PERIOD_TODAY;
    }
}
