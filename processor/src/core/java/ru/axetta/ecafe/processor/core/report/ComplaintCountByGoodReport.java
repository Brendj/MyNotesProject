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

public class ComplaintCountByGoodReport extends BasicReportForOrgJob {

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportJob.Builder {

        public static class ComplaintReportItem {

            // Имя проблемного товара
            private String nameofgood;
            // Количество поданных жалоб на данный товар
            private Long goodcount;

            public ComplaintReportItem(String nameofgood, Long goodcount) {
                this.nameofgood = nameofgood;
                this.goodcount = goodcount;
            }

            public String getNameofgood() {
                return nameofgood;
            }

            public void setNameofgood(String nameofgood) {
                this.nameofgood = nameofgood;
            }

            public Long getGoodcount() {
                return goodcount;
            }

            public void setGoodcount(Long goodcount) {
                this.goodcount = goodcount;
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
            return new ComplaintCountByGoodReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, Org org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            List<ComplaintReportItem> resultRows = new LinkedList<ComplaintReportItem>();
            Query query = session.createSQLQuery("SELECT g.nameofgood, count(c.idofgood) as good_count"
                    + " FROM cf_goods_complaint_iterations i, cf_goods_complaintbook c, cf_goods g"
                    + " WHERE c.orgowner = :idoforg AND c.createddate >= :startTime AND c.createddate <= :endTime"
                    + " AND c.idofcomplaint = i.idofcomplaint AND c.idofgood = g.idofgood"
                    + " GROUP BY g.nameofgood"
                    + " ORDER BY good_count DESC, g.nameofgood;");
            query.setParameter("startTime", startTime.getTime());
            query.setParameter("endTime", endTime.getTime());
            query.setParameter("idoforg", org.getIdOfOrg());
            List list = query.list();
            for (Object result : list) {
                Object[] complaint = (Object[]) result;
                String nameOfGood = complaint[0].toString();
                Long goodCount = Long.parseLong(complaint[1].toString());
                resultRows.add(new ComplaintReportItem(nameOfGood, goodCount));
            }
            return new JRBeanCollectionDataSource(resultRows);
        }

    }

    public ComplaintCountByGoodReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);
    }
    private static final Logger logger = LoggerFactory.getLogger(ComplaintCountByGoodReport.class);

    public ComplaintCountByGoodReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new ComplaintCountByGoodReport();
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
