/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 21.01.16
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
public class OutOfSynchronizationReportBuilder extends BasicReportForAllOrgJob.Builder {

    private final String templateFilename;
    private final static Logger logger = LoggerFactory.getLogger(OutOfSynchronizationReportBuilder.class);

    public OutOfSynchronizationReportBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();

        String templateFilename =
                autoReportGenerator.getReportsTemplateFilePath() + "OutOfSynchronizationReport.jasper";

        if (!(new File(templateFilename)).exists()) {
            throw new Exception(String.format("Не найден файл шаблона '%s'", templateFilename));
        }

        Date generateBeginTime = new Date();

        /* Параметры для передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("startDate", CalendarUtils.dateTimeToString(new Date()));

        JRDataSource dataSource = buildDataSource(session);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

        Date generateEndTime = new Date();
        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();

        return new OutOfSynchronizationReport(generateBeginTime, generateDuration, jasperPrint, startTime, endTime);
    }

    private JRDataSource buildDataSource(Session session) throws Exception {
        //Результирующий лист по которому строиться отчет
        List<OutOfSynchronizationItem> outOfSynchronizationReportList = new ArrayList<OutOfSynchronizationItem>();

        String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG));

        List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
        List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
        for (String idOfOrg : stringOrgList) {
            idOfOrgList.add(Long.parseLong(idOfOrg));
        }

        String str = "";
        if (idOfOrgList.isEmpty()) {
            throw new Exception(String.format("Не указана организация '%s'", str));
        }

        Query query = session.createSQLQuery(
                "SELECT CASE WHEN (current_timestamp - lastfastsynctime < INTERVAL '10 minutes') "
                        + "    THEN 'less10Minutes' WHEN (current_timestamp - lastfastsynctime > INTERVAL '10 minutes') AND (current_timestamp - lastfastsynctime <= INTERVAL '30 minutes')"
                        + "    THEN 'more10Minutes' WHEN (current_timestamp - lastfastsynctime > INTERVAL '30 minutes') AND (current_timestamp - lastfastsynctime <= INTERVAL '1 hour')"
                        + "    THEN 'more30Minutes' WHEN ((current_timestamp - lastfastsynctime > INTERVAL '1 hour') AND"
                        + "        (current_timestamp - lastfastsynctime <= INTERVAL '3 hours'))"
                        + "    THEN 'more60Minutes' WHEN ((lastfastsynctime - current_timestamp > INTERVAL '3 hours'))"
                        + "    THEN 'more3Hours'  ELSE 'other' END AS condition, cfor.idoforg,"
                        + "  cfor.shortname, cfor.address, cfor.isworkinsummertime,"
                        + "  cfos.lastAccRegistrySync,  cfos.clientversion,  cfos.remoteaddress, "
                        + "  cfor.statusdetailing, cfor.introductionqueue,  cfor.district, lastFastSynctime "
                        + " FROM cf_orgs cfor LEFT JOIN cf_synchistory cfsh ON cfor.idoforg = cfsh.idoforg "
                        + "                  LEFT JOIN (SELECT idoforg, max(to_timestamp(syncstarttime / 1000)) AS lastfastsynctime"
                        + "                                     FROM cf_synchistory cfs WHERE to_timestamp(syncstarttime / 1000) > DATE_TRUNC('hour', CURRENT_DATE) AND"
                        + "                                           cfs.synctype = 1 AND idoforg IN (:idOfOrgList)"
                        + "                                     GROUP BY idoforg) AS lastsyncbyorg ON cfsh.idoforg = lastsyncbyorg.idoforg"
                        + " LEFT JOIN cf_orgs_sync cfos ON cfos.idoforg = cfor.idoforg"
                        + " WHERE cfor.state = 1 AND cfor.idoforg IN (:idOfOrgList)"
                        + " GROUP BY cfor.idoforg, lastFastSynctime, cfor.shortname, cfor.address, cfor.isworkinsummertime, cfos.lastAccRegistrySync,"
                        + " cfos.clientversion, cfos.remoteaddress, cfor.statusdetailing, cfor.introductionqueue, cfor.district");
        query.setParameterList("idOfOrgList", idOfOrgList);

        logger.info("OutOfSynchronizationReport start query");

        List result = query.list();

        if (result != null) {
            logger.info(String.format("OutOfSynchronizationReport query result = %s records", result.size()));
        } else {
            logger.info("OutOfSynchronizationReport query result is null");
        }

        for (Object resultItem : result) {
            Object[] object = (Object[]) resultItem;

            if (parseCondition((String) object[0]) != null) {

                OutOfSynchronizationItem outOfSynchronizationItem = new OutOfSynchronizationItem(
                        parseCondition((String) object[0]),
                        ((BigInteger) object[1]).longValue(),
                        (String) object[2],
                        (String) object[3],
                        (object[4]).equals(1),
                        object[5] == null ? "" : CalendarUtils.dateTimeToString(new Date(((BigInteger) object[5]).longValue())),
                        object[6] == null ? "" : (String) object[6],
                        object[7] == null ? "" : (String) object[7],
                        rowName((String) object[0]),
                        (String) object[8],
                        (String) object[9],
                        (String) object[10]);
                outOfSynchronizationReportList.add(outOfSynchronizationItem);
            }
        }
        logger.info("OutOfSynchronizationReport OK");
        Collections.sort(outOfSynchronizationReportList);
        return new JRBeanCollectionDataSource(outOfSynchronizationReportList);
    }

    public String parseCondition(String condition) {
        if (condition.equals("more10Minutes")) {
            return "Синхронизация отсутствует более 10 минут";
        }
        if (condition.equals("more30Minutes")) {
            return "Синхронизация отсутствует более 30 минут";
        }
        if (condition.equals("more60Minutes")) {
            return "Синхронизация отсутствует более 60 минут";
        }
        if (condition.equals("more3Hours") || condition.equals("other")) {
            return "Синхронизация отсутствует 3 часа и более";
        }

        return null;
    }

    public Long rowName(String condition) {
        if (condition.equals("more10Minutes")) {
            return 1L;
        }
        if (condition.equals("more30Minutes")) {
            return 2L;
        }
        if (condition.equals("more60Minutes")) {
            return 3L;
        }
        if (condition.equals("more3Hours")) {
            return 4L;
        }
        if (condition.equals("other")) {
            return 4L;
        }
        return 0L;
    }

    public String getTemplateFilename() {
        return templateFilename;
    }
}
