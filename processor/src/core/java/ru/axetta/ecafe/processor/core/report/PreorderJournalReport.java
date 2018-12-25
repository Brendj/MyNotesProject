/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.PreorderComplex;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by nuc on 24.12.2018.
 */
public class PreorderJournalReport extends BasicReportForListOrgsJob {
    private static final Logger logger = LoggerFactory.getLogger(PreorderJournalReport.class);
    final public static String P_ID_OF_CLIENTS="idOfClients";
    final public static String P_LINE_SEPARATOR="line_separator";

    public PreorderJournalReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public PreorderJournalReport() {

    }

    public static class Builder extends BasicReportForListOrgsJob.Builder {
        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            String idOfClients = StringUtils.trimToEmpty(reportProperties.getProperty(P_ID_OF_CLIENTS));
            List<String> stringClientsList = Arrays.asList(StringUtils.split(idOfClients, ','));
            List<Long> idOfClientList = new ArrayList<Long>();
            for (String idOfClient : stringClientsList) {
                idOfClientList.add(Long.parseLong(idOfClient));
            }

            String idOfOrgs = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            List<String> stringOrgsList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> idOfOrgList = new ArrayList<Long>();
            for (String idOfOrg : stringOrgsList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }

            String lineSeparator = reportProperties.getProperty(P_LINE_SEPARATOR);

            JRDataSource dataSource = createDataSource(session, startTime, endTime, idOfOrgList, idOfClientList, parameterMap, lineSeparator);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            long generateDuration = generateEndTime.getTime() - generateTime.getTime();
            return new PreorderJournalReport(generateTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, List<Long> idOfOrgList,
                List<Long> idOfClientList, Map<String, Object> parameterMap, String lineSeparator) {
            List<PreorderJournalReportItem> items = new ArrayList<PreorderJournalReportItem>();

            String orgCondition = idOfOrgList.size() == 0 ? "" : " and pc.idOfOrgOnCreate in :orgList";
            String clientCondition = idOfClientList.size() == 0 ? "" : " and pc.client.idOfClient in :clientList";
            Query query = session.createQuery("select pc from PreorderComplex pc where pc.preorderDate between :startDate and :endDate "
                    + orgCondition + clientCondition + " order by pc.preorderDate");
            query.setParameter("startDate", startTime);
            query.setParameter("endDate", endTime);
            if (idOfOrgList.size() > 0) query.setParameterList("orgList", idOfOrgList);
            if (idOfClientList.size() > 0) query.setParameterList("clientList", idOfClientList);
            List<PreorderComplex> list = query.list();
            int num = 1;
            for (PreorderComplex pc : list) {
                PreorderJournalReportItem item = new PreorderJournalReportItem(num, pc, lineSeparator);
                items.add(item);
                num++;
            }
            return new JRBeanCollectionDataSource(items);
        }
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new PreorderJournalReport.Builder(templateFilename);
    }

    @Override
    public PreorderJournalReport createInstance() {
        return new PreorderJournalReport();
    }
}
