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

        Boolean allOrg = Boolean.valueOf(getReportProperties().getProperty("allOrg"));
        String selectGroupName = getReportProperties().getProperty("selectGroupName");
        String selectGroupId = getReportProperties().getProperty("selectGroupId");
        List<String> stringGroupId = Arrays.asList(StringUtils.split(selectGroupId, ','));
        List<Integer> groupId = new ArrayList<>();
        for(String group: stringGroupId)
            groupId.add(Integer.parseInt(group));
        String idOfOrgs = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
        List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
        List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
        for (String idOfOrg : stringOrgList)
            idOfOrgList.add(Long.parseLong(idOfOrg));
        String idOfOrgsCondition = CollectionUtils.isEmpty(idOfOrgList) ? "" : " and sd.idoforg in (:idOfOrgList) " ;
        String idOfGroupCondition = CollectionUtils.isEmpty(groupId) ? "" : " and sd.idofclientgroup in (:groupId) " ;
        String idOfOrg = CollectionUtils.isEmpty(idOfOrgList) ? "" : " where sd.idoforg in (:idOfOrgList) " ;
        if(allOrg) {
            idOfOrg = "";
            idOfOrgsCondition = "";
        }

        String getOrg = "select sd.shortnameinfoservice "
                + "from cf_orgs sd "
                + idOfOrg;

        String getSpecialDates =  "select sd.date, cg.groupname, sd.idOfOrg, o.shortaddress, sd.isweekend, sd.comment, sd.deleted, sdh.armlastupdate, sd.idoforgowner, s.firstname as name, p.surname, p.firstname, p.secondname, oe.shortaddress as address "
                + "from cf_specialdates sd "
                + "join cf_orgs o on sd.idoforg = o.idoforg "
                + "join cf_orgs oe on sd.idoforgowner = oe.idoforg "
                + "join cf_specialdates_history sdh on sd.idoforg = sdh.idoforg and sd.idofclientgroup = sdh.idofclientgroup "
                + "join cf_staffs s on sdh.staffguid = s.guid "
                + "join cf_persons p on s.idofclient = p.idofperson "
                + "join cf_clientgroups cg on cg.idoforg = sd.idoforg and cg.idofclientgroup = sd.idofclientgroup "
                + "where sd.date BETWEEN :startDate and :endDate "
                + idOfOrgsCondition
                + idOfGroupCondition
                + " order by 1";

        Query query = session.createSQLQuery(getSpecialDates);
        query.setParameter("startDate", startDate.getTime())
                .setParameter("endDate", endDate.getTime());
        if(!CollectionUtils.isEmpty(idOfOrgList) && !allOrg){
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
        if (!CollectionUtils.isEmpty(idOfOrgList) && !allOrg)
            query.setParameterList("idOfOrgList", idOfOrgList);
        StringBuilder org = new StringBuilder();
        if (!CollectionUtils.isEmpty(idOfOrgList) || allOrg) {
            List<String> dataOrg = query.list();
            for(String orgs: dataOrg)
                org.append(orgs).append("; ");
        }
        if (org.length() > 2) org = new StringBuilder(org.substring(0, org.length() - 2));

        for (Object[] data: dataSpecialDates){
            String isWeekend = Integer.parseInt(data[4].toString()) == 0 ? "Да" : "Нет";
            String deleted = Integer.parseInt(data[6].toString()) == 1 ? "Да" : "Нет";
            Date lastEditDate = DataBaseSafeConverterUtils.getDateFromBigIntegerOrNull(data[7]);
            String name = data[9].toString() + getName(data[10].toString(), data[11].toString(), data[12].toString());
            String pattern = "dd.MM.yyyy";
            DateFormat df = new SimpleDateFormat(pattern);
            String periodDate = df.format(startDate) + " - " + df.format(endDate);
            String date = df.format(DataBaseSafeConverterUtils.getDateFromBigIntegerOrNull(data[0]));
            list.add(new FoodDaysCalendarReportItem(allOrg ? "Все организации" : org.toString(), selectGroupName, periodDate, date, data[1].toString(), data[2].toString(), data[3].toString(),
                    isWeekend, data[5].toString(), deleted, lastEditDate, data[8].toString(), name, data[13].toString()));
        }
        return new JRBeanCollectionDataSource(list);
    }

    private String getName(String surName, String firstName, String secondName){
        String name = " (";
        if (surName.length() > 0)
            name += surName.toString() + " ";
        if (firstName.length() > 0)
            name += firstName.charAt(0) + ".";
        if (secondName.length() > 0)
            name += secondName.charAt(0) + ".";
        name += ")";
        return name.length() > 3 ? name : "";
    }

}