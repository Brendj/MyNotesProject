/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GoodRequestsNewReport extends BasicReportForAllOrgJob {

    private final static Logger logger = LoggerFactory.getLogger(GoodRequestsNewReport.class);

    public static class Item {
        private Long orgNum;
        private String officialName;
        private String goodName;
        private Date doneDate;
        private Long totalCount;
        private Long dailySample;
        private Long lastTotalCount;
        private Long lastDailySample;

        public Item(Long orgNum, String officialName, String goodName, Date doneDate, Long totalCount, Long dailySample,
                Long lastTotalCount, Long lastDailySample) {
            this.orgNum = orgNum;
            this.officialName = officialName;
            this.goodName = goodName;
            this.doneDate = doneDate;
            this.totalCount = totalCount;
            this.dailySample = dailySample;
            this.lastTotalCount = lastTotalCount;
            this.lastDailySample = lastDailySample;
        }

        public Long getOrgNum() {
            return orgNum;
        }

        public void setOrgNum(Long orgNum) {
            this.orgNum = orgNum;
        }

        public String getOfficialName() {
            return officialName;
        }

        public void setOfficialName(String officialName) {
            this.officialName = officialName;
        }

        public String getGoodName() {
            return goodName;
        }

        public void setGoodName(String goodName) {
            this.goodName = goodName;
        }

        public Date getDoneDate() {
            return doneDate;
        }

        public void setDoneDate(Date doneDate) {
            this.doneDate = doneDate;
        }

        public Long getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Long totalCount) {
            this.totalCount = totalCount;
        }

        public Long getDailySample() {
            return dailySample;
        }

        public void setDailySample(Long dailySample) {
            this.dailySample = dailySample;
        }

        public Long getLastTotalCount() {
            return lastTotalCount;
        }

        public void setLastTotalCount(Long lastTotalCount) {
            this.lastTotalCount = lastTotalCount;
        }

        public Long getLastDailySample() {
            return lastDailySample;
        }

        public void setLastDailySample(Long lastDailySample) {
            this.lastDailySample = lastDailySample;
        }
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            String reportsTemplateFilePath = RuntimeContext.getInstance().getAutoReportGenerator()
                    .getReportsTemplateFilePath();
            templateFilename = reportsTemplateFilePath + GoodRequestsNewReport.class.getSimpleName() + ".jasper";
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            calendar.setTime(startTime);
            JRDataSource dataSource = createDataSource(session, startTime, endTime, parameterMap);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            long generateDuration = generateEndTime.getTime() - generateTime.getTime();
            return new GoodRequestsNewReport(generateTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Map<String, Object> parameterMap) {
            boolean hideMissedColumns = Boolean.parseBoolean(reportProperties.getProperty("hideMissedColumns", "false"));
            String goodName = reportProperties.getProperty("goodName", "");
            int orgFilter = Integer.parseInt(reportProperties.getProperty("goodsFilter", "1"));
            if(orgFilter == 3) {
                orgFilter = -1;
            }
            boolean dailySample = Boolean.parseBoolean(reportProperties.getProperty("dailySample", "true"));

            String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
            for (String idOfOrg : stringOrgList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }

            String idOfContragents = StringUtils.trimToEmpty(
                    getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            List<String> idOfContragentStrList = Arrays.asList(StringUtils.split(idOfContragents, ','));
            List<Long> idOfContragentList = new ArrayList<Long>(idOfContragentStrList.size());
            for (String idOfContragent : idOfContragentStrList) {
                idOfOrgList.add(Long.parseLong(idOfContragent));
            }

            List<Item> itemList = new LinkedList<Item>();


            Date doneDate = new Date();
            itemList.add(new Item(0L, "test №0", "asd/asd/asd/asd1", doneDate, 1L, 1L, 0L, 0L));
            itemList.add(new Item(0L, "test №0", "asd/asd/asd/asd1", doneDate, 2L, 1L, 0L, 0L));
            itemList.add(new Item(0L, "test №0", "asd/asd/asd/asd2", doneDate, 11L, 1L, 0L, 0L));
            itemList.add(new Item(0L, "test №0", "asd/asd/asd/asd2", doneDate, 12L, 1L, 0L, 0L));
            itemList.add(new Item(0L, "test №0", "asd/asd/asd/asd3", doneDate, 21L, 1L, 0L, 0L));
            itemList.add(new Item(0L, "test №0", "asd/asd/asd/asd3", doneDate, 22L, 1L, 0L, 0L));
            itemList.add(new Item(0L, "test №0-1", "asd/asd/asd/asd2", doneDate, 2L, 0L, 0L, 0L));
            itemList.add(new Item(0L, "test №0-1", "asd/asd/asd/asd2", doneDate, 4L, 0L, 0L, 0L));
            itemList.add(new Item(2L, "test №2", "asd/asd/asd/asd3", doneDate, 3L, 0L, 0L, 0L));
            itemList.add(new Item(2L, "test №2", "asd/asd/asd/asd3", doneDate, 3L, 0L, 0L, 0L));
            itemList.add(new Item(2L, "test №2", "asd/asd/asd/asd3", doneDate, 3L, 0L, 0L, 0L));
            itemList.add(new Item(2L, "test №2-1", "asd/asd/asd/asd", doneDate, 4L, 0L, 0L, 0L));
            itemList.add(new Item(2L, "test №2-1", "asd/asd/asd/asd", doneDate, 4L, 0L, 0L, 0L));
            itemList.add(new Item(2L, "test №2-1", "asd/asd/asd/asd", doneDate, 4L, 0L, 0L, 0L));
            itemList.add(new Item(2L, "test №2-1", "asd/asd/asd/asd", doneDate, 4L, 0L, 0L, 0L));
            itemList.add(new Item(Long.MAX_VALUE, "итого", "asd/asd/asd/asd", doneDate, 5L, 0L, 0L, 0L));
            itemList.add(new Item(Long.MAX_VALUE, "итого", "asd/asd/asd/asd1", doneDate, 5L, 0L, 0L, 0L));
            itemList.add(new Item(Long.MAX_VALUE, "итого", "asd/asd/asd/asd2", doneDate, 5L, 0L, 0L, 0L));
            itemList.add(new Item(Long.MAX_VALUE, "итого", "asd/asd/asd/asd3", doneDate, 5L, 0L, 0L, 0L));

            return new JRBeanCollectionDataSource(itemList);
        }

    }

    public GoodRequestsNewReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public GoodRequestsNewReport() {}

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new GoodRequestsNewReport();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;  //To change body of implemented methods use File | Settings | File Templates.
    }
}