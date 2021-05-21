/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
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
import ru.axetta.ecafe.processor.core.persistence.GroupNamesToOrgs;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SubscriberFeedingSettingSettingValue;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 17.04.16
 * Time: 11:17
 */
public class SpecialDatesReportBuilder extends BasicReportForAllOrgJob.Builder {
    private final String templateFilename;

    public SpecialDatesReportBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();

        String templateFilename =
                autoReportGenerator.getReportsTemplateFilePath() + "SpecialDatesReport.jasper";

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

        return new SpecialDatesReport(generateBeginTime, generateDuration, jasperPrint, startTime, endTime).setHtmlReport(os.toString("UTF-8"));
    }

    private JRDataSource buildDataSource(Session session, Date startDate, Date endDate) throws Exception {
        List<SpecialDatesReportItem> list = new ArrayList<SpecialDatesReportItem>();

        Boolean showComments = Boolean.valueOf(getReportProperties().getProperty("showComments"));
        String clientGroupName = getReportProperties().getProperty("clientGroupName");
        String id = getReportProperties().getProperty("idOfClientGroup");
        Long idOfClientGroup = id == null ? null : Long.parseLong(id);

        String idOfOrgs = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
        List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
        List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
        for (String idOfOrg : stringOrgList) {
            idOfOrgList.add(Long.parseLong(idOfOrg));
        }
        HashMap<Long, BasicReportJob.OrgShortItem> orgMap = getDefinedOrgs(session, idOfOrgList, new ArrayList<Long>());

        TimeZone timeZone = RuntimeContext.getInstance().getLocalTimeZone(null);
        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeZone);

        List<Long> idOfOrgsList = new ArrayList<Long>(orgMap.keySet());
        Collections.sort(idOfOrgsList);

        for(Long orgId : idOfOrgsList){
            Org org = (Org) session.load(Org.class, orgId);
            DAOService daoService = DAOService.getInstance();
            List<ECafeSettings> settings = daoService
                    .geteCafeSettingses(orgId, SettingsIds.SubscriberFeeding, false);
            boolean isSixWorkWeek = false;
            if(!settings.isEmpty()){
                ECafeSettings cafeSettings = settings.get(0);
                SubscriberFeedingSettingSettingValue parser =
                        (SubscriberFeedingSettingSettingValue) cafeSettings.getSplitSettingValue();
            }
            c.setTime(startDate);
            while (c.getTimeInMillis() < endDate.getTime() ){
                Date currentDate = CalendarUtils.parseDate(CalendarUtils.dateShortToStringFullYear(c.getTime()));
                Date bDate = CalendarUtils.startOfDay(currentDate);
                Date eDate = CalendarUtils.endOfDay(currentDate);
                StringBuilder comment = new StringBuilder();
                String groupCondition = idOfClientGroup == null ? "" : " and sd.idofclientgroup = :idOfClientGroup";

                String getGroup = "select sd.idofclientgroup, gto.groupname, sd.comment, gto.issixdaysworkweek "
                        + "from cf_specialdates sd "
                        + "join cf_clientgroups cg on sd.idoforg = cg.idoforg and sd.idofclientgroup = cg.idofclientgroup "
                        + "join cf_groupnames_to_orgs gto on sd.idoforg = gto.idoforg and gto.groupname = cg.groupname "
                        + "where sd.deleted = 0 and sd.date BETWEEN :startDate and :endDate  and sd.idoforg = :orgId and sd.isweekend = 1 "
                        + groupCondition;
                Query query = session.createSQLQuery(getGroup);
                query.setParameter("startDate", bDate.getTime())
                        .setParameter("endDate", eDate.getTime());
                query.setParameter("orgId", Integer.parseInt(orgId.toString()));
                if (idOfClientGroup != null)
                    query.setParameter("idOfClientGroup", idOfClientGroup);
                List<Object[]> group = query.list();

                Boolean isWeekend = !CalendarUtils.isWorkDateWithoutParser(isSixWorkWeek, currentDate);

                for (Object[] gr : group) {
                    isWeekend = true;
                    if (idOfClientGroup != null && showComments)
                        comment.append(gr[1].toString()).append(" - ").append(gr[2].toString());
                    else {
                        comment.append(gr[1].toString());
                    }
                    comment.append(", ");
                }

                if (comment.length() > 2) comment = new StringBuilder(comment.substring(0, comment.length() - 2));

                int day = CalendarUtils.getDayOfWeek(currentDate);
                if (day == Calendar.SATURDAY && !isSixWorkWeek) {
                    //проверяем нет ли привязки отдельных групп к 6-ти дневной неделе
                    List<String> groupName = new ArrayList<>();
                    for(Object[] gr: group)
                        groupName.add(gr[1].toString());
                    Object[] weekendByGroup = isWeekendByGroups(session, orgId, clientGroupName, groupName);
                    isWeekend = (Boolean) weekendByGroup[0];
                    comment = new StringBuilder((String) weekendByGroup[1]);
                }
                String dateStr = getDayOfWeekString(CalendarUtils.getDayOfWeek(currentDate) - 1) + " " + CalendarUtils.dateShortToString(currentDate);

                list.add(new SpecialDatesReportItem(orgId, dateStr, org.getShortName(), isWeekend, comment.toString()));

                c.add(Calendar.DATE, 1);
            }
        }
        return new JRBeanCollectionDataSource(list);
    }

    private Object[] isWeekendByGroups(Session session, Long idOfOrg, String clientGroupName, List<String> groupName) {
        Boolean isWeekend = true;
        StringBuilder comment = new StringBuilder();
        Criteria criteria = session.createCriteria(GroupNamesToOrgs.class);
        criteria.add(Restrictions.eq("idOfOrg", idOfOrg));
        criteria.add(Restrictions.eq("isSixDaysWorkWeek", true));
        if(clientGroupName != null)
            criteria.add(Restrictions.eq("groupName", clientGroupName));
        List<GroupNamesToOrgs> list = criteria.list();

        for (GroupNamesToOrgs group : list) {
            if (groupName.size() == 0)
                comment.append(group.getGroupName()).append(", ");
            else
                if(!listContains(group.getGroupName(), groupName))
                    comment.append(group.getGroupName()).append(", ");
        }
        if (comment.length() > 2) {
            comment = new StringBuilder(comment.substring(0, comment.length() - 2));
            isWeekend = false;
        }
        return new Object[] {isWeekend, comment.toString()};
    }

    private boolean listContains(String group, List<String> groupName){
        for (String gr: groupName)
            if(group.equals(gr))
                return true;
            return false;
    }

    private HashMap<Long, BasicReportJob.OrgShortItem> getDefinedOrgs(Session session, List<Long> idOfOrgList,
            List<Long> idOfMenuSourceOrgList) {
        Criteria orgCriteria = session.createCriteria(Org.class);
        if (!CollectionUtils.isEmpty(idOfOrgList)) {
            orgCriteria.add(Restrictions.in("idOfOrg", idOfOrgList));
        }
        orgCriteria.createAlias("sourceMenuOrgs", "sm", JoinType.LEFT_OUTER_JOIN);
        if (!CollectionUtils.isEmpty(idOfMenuSourceOrgList)) {
            orgCriteria.add(Restrictions.in("sm.idOfOrg", idOfMenuSourceOrgList));
        }
        orgCriteria.setProjection(
                Projections.projectionList().add(Projections.property("idOfOrg")).add(Projections.property("shortName"))
                        .add(Projections.property("officialName")).add(Projections.property("sm.idOfOrg")));
        List orgList = orgCriteria.list();
        HashMap<Long, BasicReportJob.OrgShortItem> orgMap = new HashMap<Long, BasicReportJob.OrgShortItem>(
                orgList.size());
        for (Object obj : orgList) {
            Object[] row = (Object[]) obj;
            long idOfOrg = Long.parseLong(row[0].toString());
            BasicReportJob.OrgShortItem educationItem;
            educationItem = new BasicReportJob.OrgShortItem(idOfOrg, row[1].toString(), row[2].toString());
            if (row[3] != null) {
                Long sourceMenuOrg = Long.parseLong(row[3].toString());
                educationItem.setSourceMenuOrg(sourceMenuOrg);
                idOfMenuSourceOrgList.add(sourceMenuOrg);
            }
            orgMap.put(idOfOrg, educationItem);
        }
        return orgMap;
    }

    private String getDayOfWeekString(int dayOfWeek){
        if(dayOfWeek == 0) {return "Вс";}
        if(dayOfWeek == 1) {return "Пн";}
        if(dayOfWeek == 2) {return "Вт";}
        if(dayOfWeek == 3) {return "Ср";}
        if(dayOfWeek == 4) {return "Чт";}
        if(dayOfWeek == 5) {return "Пт";}
        if(dayOfWeek == 6) {return "Сб";}
        if(dayOfWeek == 7) {return "Вс";}
        return "";
    }
}
