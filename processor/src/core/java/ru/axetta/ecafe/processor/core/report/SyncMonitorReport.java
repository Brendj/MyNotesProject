/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.dashboard.data.DashboardResponse;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class SyncMonitorReport extends BasicReportForAllOrgJob {

    final private static Logger logger = LoggerFactory.getLogger(SyncMonitorReport.class);

    public SyncMonitorReport(Date generateTime, Long generateDuration, JasperPrint jasperPrint) {
        super(generateTime, generateDuration, jasperPrint, null, null);
    }

    public SyncMonitorReport() {
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private String templateFilename;
        private List<DashboardResponse.OrgSyncStatItem> items;
        private List<String> versionsList;

        public Builder() {
        }

        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("currentTimeMillis", System.currentTimeMillis());
            JRDataSource dataSource = createDataSource(session);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

            Date generateEndTime = new Date();
            return new SyncMonitorReport(generateTime,generateEndTime.getTime() - generateTime.getTime(), jasperPrint);
        }

        private JRDataSource createDataSource(Session session) throws Exception {

            List<DashboardResponse.OrgSyncStatItem> items = getSyncReportData(session, this.versionsList);

            return new JRBeanCollectionDataSource(items);
        }

        public static List<DashboardResponse.OrgSyncStatItem> getSyncReportData(Session session, List<String> versionsList) {
            List<DashboardResponse.OrgSyncStatItem> items = new ArrayList<DashboardResponse.OrgSyncStatItem>();
            String sqlQuery =
                    "SELECT o.idoforg, o.shortnameinfoservice, o.district, o.shortaddress, o.organizationtype, "
                  + "   o.introductionqueue, os.lastsucbalancesync, os.remoteaddress, os.clientversion, "
                  + "   count(se.idoforg) AS exceptions, os.sqlServerVersion, os.databaseSize "
                  + "FROM cf_orgs o "
                  + "INNER JOIN cf_orgs_sync os ON os.idoforg=o.idoforg "
                  + "LEFT JOIN cf_synchistory_exceptions se ON se.idoforg=o.idoforg "
                  + "WHERE state<>0 " + ((null != versionsList && !versionsList.isEmpty()) ? "AND os.clientversion IN (:versions) " : "")
                  + "GROUP BY o.idoforg, o.tag, o.shortname, o.district, os.lastsucbalancesync, os.lastunsucbalancesync, "
                  + "   os.remoteaddress, os.clientversion, os.sqlServerVersion, os.databaseSize "
                  + "ORDER BY os.lastSucBalanceSync";

            Query query = session.createSQLQuery(sqlQuery);
            if (null != versionsList && !versionsList.isEmpty())
                query.setParameterList("versions", versionsList);
            List result = query.list();

            for (Object o : result) {
                Object vals[] = (Object[])o;

                Long idOfOrg = ((BigInteger)vals[0]).longValue();
                String shortName = (String)vals[1];
                String district = (String)vals[2];
                String address = (String)vals[3];
                String organizationTypeName = "";
                if (null != vals[4])
                    organizationTypeName = OrganizationType.fromInteger((Integer)vals[4]).toString();
                String introductionQueue = (String)vals[5];
                Date successfulBalanceSync = null;
                if (null != vals[6])
                    successfulBalanceSync = new Date(((BigInteger)vals[6]).longValue());
                String remoteAddress = (String)vals[7];
                String clientVersion = (String)vals[8];
                Long exceptionsCount = ((BigInteger)vals[9]).longValue();
                String sqlServerVersion = (String) vals[10];
                Double databaseSize = vals[11] == null ? null : ((BigDecimal) vals[11]).doubleValue();

                items.add(new DashboardResponse.OrgSyncStatItem(idOfOrg, shortName, address, organizationTypeName,
                        introductionQueue, successfulBalanceSync, remoteAddress, clientVersion, exceptionsCount,
                        district, sqlServerVersion, databaseSize));
            }

            return items;
        }

        public String getTemplateFilename() {
            return templateFilename;
        }

        public void setTemplateFilename(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public List<String> getVersionsList() {
            return versionsList;
        }

        public void setVersionsList(List<String> versionsList) {
            this.versionsList = versionsList;
        }
    }

    public static List<String> getAvailableVersions(Session session) {
        List<String> versionsList = new ArrayList<String>();
        String sqlQuery =
                "SELECT DISTINCT os.clientversion "
              + "FROM cf_orgs o "
              + "INNER JOIN cf_orgs_sync os ON os.idoforg=o.idoforg "
              + "WHERE os.clientversion IS NOT NULL AND o.state<>0 "
              + "ORDER BY os.clientversion";

        Query query = session.createSQLQuery(sqlQuery);
        List result = query.list();

        for (Object o : result) {
            versionsList.add((String) o);
        }

        return versionsList;
    }


    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new SyncMonitorReport();
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        SyncMonitorReport.Builder builder = new SyncMonitorReport.Builder();
        builder.setTemplateFilename(templateFilename);
        return builder;
    }
}
