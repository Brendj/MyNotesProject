/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
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
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.DataBaseSafeConverterUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class FoodDaysCalendarReportBuilder extends BasicReportForAllOrgJob.Builder {
    private final String templateFilename;

    public FoodDaysCalendarReportBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();

        String templateFilename =
                autoReportGenerator.getReportsTemplateFilePath() + "FoodDaysCalendarReport.jasper";

        if (!(new File(templateFilename)).exists()) {
            throw new Exception(String.format("Не найден файл шаблона '%s'", templateFilename));
        }

        Date generateBeginTime = new Date();

        /* Параметры для передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("startDate", CalendarUtils.dateShortToStringFullYear(startTime));
        parameterMap.put("endDate", CalendarUtils.dateShortToStringFullYear(endTime));

        JRDataSource dataSource = buildDataSource(session, startTime, endTime);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
        Date generateEndTime = new Date();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JRHtmlExporter exporter = new JRHtmlExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
        exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
        exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
        exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
        exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
        exporter.setParameter(JRHtmlExporterParameter.IS_WRAP_BREAK_WORD, Boolean.TRUE);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
        exporter.exportReport();
        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();

        return new FoodDaysCalendarReport(generateBeginTime, generateDuration, jasperPrint, startTime, endTime).setHtmlReport(os.toString("UTF-8"));
    }

    private JRDataSource buildDataSource(Session session, Date startDate, Date endDate) throws Exception {
        List<FoodDaysCalendarReportItem> list = new ArrayList<FoodDaysCalendarReportItem>();

        Boolean friendlyOrg = Boolean.valueOf(getReportProperties().getProperty("allOrg"));
        String selectGroupName = getReportProperties().getProperty("selectGroupName");
        String selectGroupId = getReportProperties().getProperty("selectGroupId");
        List<String> stringGroupId = Arrays.asList(StringUtils.split(selectGroupId, ','));
        List<Integer> groupId = new ArrayList<>();
        for(String group: stringGroupId)
            groupId.add(Integer.parseInt(group));
        String idOfOrgString = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
        List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgString, ','));
        Set<Long> idOfOrgList = new TreeSet<>();
        for (String idOfOrg : stringOrgList)
            idOfOrgList.add(Long.parseLong(idOfOrg));
        String idOfOrgs = CollectionUtils.isEmpty(idOfOrgList) ? "" : " and sdh.idoforg in (:idOfOrgList) " ;
        String idOfGroup = CollectionUtils.isEmpty(groupId) ? "" : " and sdh.idofclientgroup in (:groupId) " ;
        String idOfOrg = CollectionUtils.isEmpty(idOfOrgList) ? "" : " where sd.idoforg in (:idOfOrgList) " ;

        String getFriendlyOrg = "select fo.friendlyOrg "
                + "from cf_friendly_organization fo "
                + "where fo.currentorg in (:idOfOrgList)";

        Query query = session.createSQLQuery(getFriendlyOrg);

        if(!CollectionUtils.isEmpty(idOfOrgList)){
            query.setParameterList("idOfOrgList", idOfOrgList);
        }
        if (friendlyOrg) {
            List<BigInteger> friendlyOrgList = query.list();
            for(BigInteger org: friendlyOrgList)
                idOfOrgList.add(Long.valueOf(String.valueOf(org)));
        }

        String getOrg = "select sd.shortnameinfoservice "
                + "from cf_orgs sd "
                + idOfOrg;

        String getName = " select s.guid, s.firstname, p.surname, p.firstname as name, p.secondname "
                + "from cf_staffs s "
                + "join cf_clients c on s.idofclient = c.idofclient "
                + "join cf_persons p on c.idofperson = p.idofperson "
                + "where s.guid in (:guid)";

        String getSpecialDates =  "select sdh.date, cg.groupname, sdh.idOfOrg, o.shortaddress, sdh.isweekend, sdh.comment, sdh.deleted, "
                + "sdh.armlastupdate, sdh.idoforgowner, sdh.staffguid, oe.shortaddress as address, sd.deleted as del "
                + "from cf_specialdates_history sdh "
                + "join cf_orgs o on sdh.idoforg = o.idoforg "
                + "join cf_specialdates sd on sd.idoforg = sdh.idoforg and sd.idofclientgroup = sdh.idofclientgroup and sd.date = sdh.date "
                + "join cf_orgs oe on sdh.idoforgowner = oe.idoforg "
                + "join cf_clientgroups cg on cg.idoforg = sdh.idoforg and cg.idofclientgroup = sdh.idofclientgroup "
                + "where sdh.date BETWEEN :startDate and :endDate "
                + idOfOrgs
                + idOfGroup
                + " order by 1, 2";

        query = session.createSQLQuery(getSpecialDates);
        query.setParameter("startDate", startDate.getTime())
                .setParameter("endDate", endDate.getTime());
        if(!CollectionUtils.isEmpty(idOfOrgList)){
            query.setParameterList("idOfOrgList", idOfOrgList);
        }
        if(!CollectionUtils.isEmpty(groupId)){
            query.setParameterList("groupId", groupId);
        }
        List<Object[]> dataSpecialDates = query.list();
        if (CollectionUtils.isEmpty(dataSpecialDates)) {
            throw new Exception("Нет данных для построения отчета");
        }

        query = session.createSQLQuery(getOrg);
        StringBuilder org = new StringBuilder();
        if (!CollectionUtils.isEmpty(idOfOrgList)){
            query.setParameterList("idOfOrgList", idOfOrgList);
            List<String> dataOrg = query.list();
            for(String orgs: dataOrg)
                org.append(orgs).append("; ");
        }

        if (org.length() > 2) org = new StringBuilder(org.substring(0, org.length() - 2));
        Set<String> guid = new TreeSet<>();
        for(Object[] staffGuid: dataSpecialDates)
            if(staffGuid[9] != null)
                guid.add(staffGuid[9].toString());
        query = session.createSQLQuery(getName);
        List<Object[]> dataGuid = null;
        if (!CollectionUtils.isEmpty(guid)) {
            query.setParameterList("guid", guid);
            dataGuid = query.list();
        }

        for (Object[] data: dataSpecialDates){
            String isWeekend = data[4].toString().equals("1") ? "Да" : "Нет";
            String deleted = data[6].toString().equals("1") ? "Да" : "Нет";
            Date lastEditDate = DataBaseSafeConverterUtils.getDateFromBigIntegerOrNull(data[7]);
            String name = "";
            if(dataGuid != null)
                for(Object[] guidItem: dataGuid)
                    if (guidItem[0] != null && data[9] != null)
                        if (guidItem[0].toString().equals(data[9].toString()))
                            name = guidItem[1].toString() + getName(guidItem[2].toString(), guidItem[3].toString(), guidItem[4].toString());
            String pattern = "dd.MM.yyyy";
            DateFormat df = new SimpleDateFormat(pattern);
            String periodDate = df.format(startDate) + " - " + df.format(endDate);
            String date = df.format(DataBaseSafeConverterUtils.getDateFromBigIntegerOrNull(data[0]));
            Integer deleteHistory = Integer.parseInt(data[11].toString());
            list.add(new FoodDaysCalendarReportItem(org.toString(), selectGroupName, periodDate, date, data[1].toString(), data[2].toString(), data[3].toString(),
                    isWeekend, data[5].toString(), deleted, lastEditDate, data[8].toString(), name, data[10].toString(), deleteHistory));
        }
        return new JRBeanCollectionDataSource(list);
    }

    private String getName(String surName, String firstName, String secondName){
        String name = " (";
        if (surName.length() > 0)
            name += surName + " ";
        if (firstName.length() > 0)
            name += firstName.charAt(0) + ".";
        if (secondName.length() > 0)
            name += secondName.charAt(0) + ".";
        name += ")";
        return name.length() > 3 ? name : "";
    }

}