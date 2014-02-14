/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 13.02.14
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
public class OrgSmsStatsReport extends BasicReportForAllOrgJob {

    private final static Logger logger = LoggerFactory.getLogger(OrgSmsStatsReport.class);
    protected List<OrgSmsStatsItem> items;
    private String htmlReport;

    public static class Builder extends BasicReportForAllOrgJob.Builder {
        private final String templateFilename;
        private boolean exportToHTML = false;
        private boolean exportToObjects = false;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + OrgSmsStatsReport.class.getSimpleName() + ".jasper";
            exportToHTML = true;
        }

        public Builder setExportToObjects(boolean exportToObjects) {
            this.exportToObjects = exportToObjects;
            return this;
        }

        @Override
        public OrgSmsStatsReport build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Date generateTime = new Date();


            /* Строим параметры для передачи в jasper */
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);


            Date generateEndTime = new Date();
            List<OrgSmsStatsItem> items = findItems(session, startTime, endTime);
            //  Если имя шаблона присутствует, значит строится для джаспера
            JasperPrint jasperPrint = JasperFillManager
                    .fillReport(templateFilename, parameterMap, createDataSource(items));
            if (!exportToHTML) {
                return new OrgSmsStatsReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        jasperPrint, startTime, endTime, items);
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
                return new OrgSmsStatsReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        startTime, endTime, items).setHtmlReport(os.toString("UTF-8"));
            }
        }


        public List<OrgSmsStatsItem> findItems(Session session, Date start, Date end) {
            List<OrgSmsStatsItem> items = new ArrayList<OrgSmsStatsItem>();
            String sql =
                        "select o.idoforg, o.shortname, c.idofclient, count(g.idofclientguardian) cnt "
                        + "from cf_clients c "
                        + "left join cf_client_guardian g on g.idofchildren=c.idofclient "
                        + "join cf_orgs o on c.idoforg=o.idoforg "
                        + "where c.idOfClientGroup>=:groupStart AND c.idOfClientGroup<:groupEnd "
                        + "group by o.idoforg, o.shortname, c.idofclient "
                        + "order by o.shortname, o.idoforg";
            Query query = session.createSQLQuery(sql);
            query.setParameter("groupStart", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("groupEnd", ClientGroup.Predefined.CLIENT_LEAVING.getValue());
            List res = query.list();
            long prevIdoOfOrg = -1L;
            long prevIdOfClient = -1L;
            OrgSmsStatsItem item = null;
            for (Object entry : res) {
                Object e[]         = (Object[]) entry;
                long idoforg       = ((BigInteger) e[0]).longValue();
                String org         = (String) e[1];
                long idofclient    = ((BigInteger) e[2]).longValue();
                int guardiansCount = ((BigInteger) e[3]).intValue();

                if(prevIdoOfOrg != idoforg) {
                    item = new OrgSmsStatsItem(idoforg, org);
                    items.add(item);
                }
                int count = count = guardiansCount > 0 ? guardiansCount : 1;
                parseCount(count, item);

                prevIdoOfOrg = idoforg;
                prevIdOfClient = idofclient;
            }


            OrgSmsStatsSummaryItem summary = new OrgSmsStatsSummaryItem(Long.MAX_VALUE, "ИТОГО");
            int oneSum  = 0;
            int twoSum  = 0;
            int moreSum = 0;
            for(OrgSmsStatsItem i : items) {
                oneSum  += i.getOneClientsCount();
                twoSum  += i.getTwoClientsCount();
                moreSum += i.getMoreClientsCount();
            }
            summary.setOneClientsCount(oneSum);
            summary.setTwoClientsCount(twoSum);
            summary.setMoreClientsCount(moreSum);
            items.add(summary);

            return items;
        }

        protected void parseCount(int count, OrgSmsStatsItem item) {
            if(count == 1) {
                item.addOneClientsCount();
            } else if(count == 2) {
                item.addTwoClientsCount();
            } else if(count > 2) {
                item.addMoreClientsCount();
            }
        }

        private JRDataSource createDataSource(List<OrgSmsStatsItem> items) throws Exception {
            return new JRBeanCollectionDataSource(items);
        }

    }


    public OrgSmsStatsReport() {
        items = Collections.emptyList();
    }

    public OrgSmsStatsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, List<OrgSmsStatsItem> items) {
        super(generateTime, generateDuration, print, startTime, endTime);
        this.items = items;
    }

    public OrgSmsStatsReport(Date startTime,
            Date endTime, List<OrgSmsStatsItem> items) {
        this.items = items;
    }

    public OrgSmsStatsReport(Date generateTime, long generateDuration, Date startTime,
            Date endTime, List<OrgSmsStatsItem> items) {
        this.items = items;
    }

    public static class OrgSmsStatsSummaryItem extends OrgSmsStatsItem {
        public OrgSmsStatsSummaryItem() {
            throw new UnsupportedOperationException();
        }

        public OrgSmsStatsSummaryItem(long idoforg, String org) {
            super(idoforg, org);
        }

        public void setOneClientsCount(int oneClientsCount) {
            this.oneClientsCount = oneClientsCount;
        }

        public void setTwoClientsCount(int twoClientsCount) {
            this.twoClientsCount = twoClientsCount;
        }

        public void setMoreClientsCount(int moreClientsCount) {
            this.moreClientsCount = moreClientsCount;
        }
    }

    public static class OrgSmsStatsItem {
        protected long uniqueId;
        protected int columnId;
        protected long idOfOrg;
        protected String org;
        protected int oneClientsCount;
        protected int twoClientsCount;
        protected int moreClientsCount;
        protected int totalCount;

        public OrgSmsStatsItem() {
            throw new UnsupportedOperationException();
        }

        public OrgSmsStatsItem(long idoforg, String org) {
            columnId = 1;
            this.idOfOrg = idoforg;
            this.uniqueId = idoforg;
            this.org = org;
            oneClientsCount = 0;
            twoClientsCount = 0;
            moreClientsCount = 0;
        }

        public long getUniqueId() {
            return uniqueId;
        }

        public int getColumnId() {
            return columnId;
        }

        public long getIdOfOrg() {
            return idOfOrg;
        }

        public String getOrg() {
            return org;
        }

        public int getTotalCount() {
            return oneClientsCount + twoClientsCount + moreClientsCount;
        }

        public int getOneClientsCount() {
            return oneClientsCount;
        }

        public int getTwoClientsCount() {
            return twoClientsCount;
        }

        public int getMoreClientsCount() {
            return moreClientsCount;
        }

        public void addOneClientsCount() {
            oneClientsCount++;
        }

        public void addTwoClientsCount() {
            twoClientsCount++;
        }

        public void addMoreClientsCount() {
            moreClientsCount++;
        }
    }


    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new ActiveDiscountClientsReport();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_MONTH;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public OrgSmsStatsReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }
}
