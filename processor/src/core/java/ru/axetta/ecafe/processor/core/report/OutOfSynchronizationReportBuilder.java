/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

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
                "SELECT CASE WHEN (lastsynctime IS NOT null) AND (current_timestamp - lastsynctime > INTERVAL '10 minutes')AND (current_timestamp - lastsynctime <= INTERVAL '30 minutes') AND (to_timestamp(syncendtime / 1000) IS NOT null) THEN 'more10Minutes' "
                        + "WHEN (lastsynctime IS NOT null) AND (current_timestamp - lastsynctime > INTERVAL '30 minutes') AND (current_timestamp - lastsynctime <= INTERVAL '1 hour') AND (to_timestamp(syncendtime / 1000) IS NOT null) THEN 'more30Minutes' "
                        + "WHEN ((lastsynctime IS NOT null) AND (current_timestamp - lastsynctime > INTERVAL '1 hour') AND (current_timestamp - lastsynctime <= INTERVAL '3 hours') AND (to_timestamp(syncendtime / 1000) IS NOT null)) THEN 'more60Minutes' "
                        + "WHEN ((lastsynctime IS NOT null) AND (current_timestamp - lastsynctime > INTERVAL '3 hours') AND (to_timestamp(syncendtime / 1000) IS NOT null) OR (lastsynctime IS null AND to_timestamp(syncendtime / 1000) IS null)) THEN 'more3Hours' "
                        + "ELSE 'other' END AS condition, nosynch.idoforg, cfor.shortname, cfor.address, cfos.lastsucbalancesync, cfos.clientversion, cfos.remoteaddress, cfor.statusdetailing, cfor.introductionqueue, "
                        + "to_timestamp(syncendtime / 1000) AS fullsyncendtime, lastsynctime "
                        + "FROM cf_synchistory cfsh INNER JOIN (SELECT idoforg, max(syncstarttime) AS lastfullsynctime "
                        + "FROM cf_synchistory cfs   WHERE to_timestamp(syncstarttime / 1000) > DATE_TRUNC('hour', current_date) "
                        + "GROUP BY idoforg) AS lastsyncbyorg  ON cfsh.idoforg = lastsyncbyorg.idoforg AND lastfullsynctime = cfsh.syncstarttime RIGHT JOIN (SELECT  idoforg, "
                        + "max(to_timestamp(syncdate / 1000)) AS lastsynctime FROM cf_synchistory_daily WHERE to_timestamp(syncdate / 1000) > DATE_TRUNC('hour', current_date) "
                        + "AND idoforg in (:idOfOrgList) "
                        + "GROUP BY idoforg UNION SELECT idoforg, null AS lastsynctime FROM (SELECT idoforg FROM cf_orgs WHERE state = 1 "
                        + "AND idoforg in (:idOfOrgList)  "
                        + "EXCEPT (SELECT idoforg FROM cf_synchistory_daily WHERE to_timestamp(syncdate / 1000) > DATE_TRUNC('hour', current_date) "
                        + "AND idoforg in (:idOfOrgList))) "
                        + "AS noSynchOrgs)AS nosynch ON nosynch.idoforg = cfsh.idoforg INNER JOIN cf_orgs_sync cfos ON cfos.idoforg = nosynch.idoforg "
                        + "INNER JOIN cf_orgs cfor ON cfor.idoforg = nosynch.idoforg where cfor.state = 1 AND cfor.idoforg in (:idOfOrgList) "
                        + "ORDER BY cfos.lastsucbalancesync DESC");
        query.setParameterList("idOfOrgList", idOfOrgList);

        List result = query.list();

        for (Object resultItem : result) {
            Object[] object = (Object[]) resultItem;



                if (parseCondition((String) object[0]) != null) {

                    OutOfSynchronizationItem outOfSynchronizationItem = new OutOfSynchronizationItem(
                            parseCondition((String) object[0]),
                            ((BigInteger) object[1]).longValue(),
                            (String) object[2],
                            (String) object[3],
                            object[4] == null ? "" : CalendarUtils.dateTimeToString(new Date(((BigInteger) object[4]).longValue())),
                            object[5] == null ? "" : (String) object[5],
                            object[6] == null ? "" : (String) object[6],
                            rowName((String) object[0]),
                            (String) object[7],
                            (String) object[8]);
                    outOfSynchronizationReportList.add(outOfSynchronizationItem);
                }
            }
//        Collections.sort(outOfSynchronizationReportList);
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
        if (condition.equals("more3Hours")) {
            return "Синхронизация отсутствует 3 часа и более";
        }
        if (condition.equals("other")) {
            return null;
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
            return 0L;
        }
        return 0L;
    }


    public String parseTags(String orgTagsStr) {
        if (StringUtils.isEmpty(orgTagsStr)) {
            return "";
        }
        String allowedTagsStr = RuntimeContext.getInstance()
                .getOptionValueString(Option.OPTION_MSK_MONITORING_ALLOWED_TAGS);
        if (StringUtils.isEmpty(allowedTagsStr)) {
            return "";
        }
        String allowedTags[] = allowedTagsStr.split(";");
        String orgTags[] = orgTagsStr.split(";");
        String result = "";
        for (String allowedTag : allowedTags) {
            for (String orgTag : orgTags) {
                if (allowedTag.trim().equals(orgTag.trim())) {
                    if (result.length() > 0) {
                        result = result + "<br/>";
                    }
                    result = result + allowedTag;
                }
            }
        }
        return result;
    }
}
