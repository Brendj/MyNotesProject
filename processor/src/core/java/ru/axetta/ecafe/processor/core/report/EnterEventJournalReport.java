/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.util.ObjectUtils;
import ru.axetta.ecafe.processor.core.persistence.EnterEvent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Created by anvarov on 04.04.18.
 */
public class EnterEventJournalReport extends BasicReportForAllOrgJob {

    /*
   * Параметры отчета для добавления в правила и шаблоны
   *
   * При создании любого отчета необходимо добавить параметры:
   * REPORT_NAME - название отчета на русском
   * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
   * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
   * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
   * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
   *
   * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
   */
    public static final String REPORT_NAME = "Журнал посещений";
    public static final String[] TEMPLATE_FILE_NAMES = {"EnterEventJournalReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{-46, -47, -48};

    public static final String P_ID_CLIENT = "idOfClients";

    private final static Logger logger = LoggerFactory.getLogger(EnterEventJournalReport.class);

    public EnterEventJournalReport(Date generateTime, long generateDuration, JasperPrint jasperPrint, Date startTime) {
        super(generateTime, generateDuration, jasperPrint, startTime, null);
    }

    public EnterEventJournalReport() {
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new EnterEventJournalReport();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;
        private Long idOfOrg;
        private Boolean allFriendlyOrgs;
        private List<String> clientGroupNames = new ArrayList<>();

        public List<String> getClientGroupNames() {
            return clientGroupNames;
        }

        public void setClientGroupNames(List<String> clientGroupNames) {
            this.clientGroupNames = clientGroupNames;
        }
        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Boolean outputMigrants = Boolean.parseBoolean(reportProperties.getProperty("outputMigrants"));
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
            parameterMap.put("endDate", CalendarUtils.dateToString(endTime));
            parameterMap.put("outputMigrants", outputMigrants);

            JasperPrint jasperPrint = JasperFillManager
                    .fillReport(templateFilename, parameterMap, createDataSource(session, startTime, endTime, idOfOrg));
            long generateDuration = generateTime.getTime();
            return new EnterEventJournalReport(generateTime, generateDuration, jasperPrint, startTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Long idOfOrg)
                throws Exception {
            Integer eventFilter;
            Boolean outputMigrants = Boolean.parseBoolean(reportProperties.getProperty("outputMigrants", "false"));
            Boolean sortedBySections = Boolean.parseBoolean(reportProperties.getProperty("sortedBySections", "false"));
            List<EnterEventItem> enterEventItems = new LinkedList<EnterEventItem>();

            String joinMigrants = outputMigrants ? " left join cf_migrants m on m.IdOfClientMigrate = c.idofclient"
                    + " and m.IdOfOrgVisit = ee.idOfOrg and ee.evtdatetime between m.VisitStartDate and m.VisitEndDate" : "";
            String selectPartSectionName = outputMigrants ? " ,array_to_string(array_agg(m.section ),', ') as section " : ", cast('' as text) as section "; // No Dialect mapping for JDBC type: 1111 exception
            String partOfOrderByMigrantSection = sortedBySections && outputMigrants ? " 8 desc, 1 "
                    : outputMigrants ? " 1, 8" : " 1 ";
            String groupWhenSelectMigrants = outputMigrants ? " group by 1, 2, 3, 4, 5, 6, 7 " : "";
            String eventsCondition = "";
            String clientGroupCondition = "";

            String orgCondition = allFriendlyOrgs ? " and ee.idoforg in (select friendlyorg from cf_friendly_organization where currentorg = :idOfOrg)" : " and ee.idoforg = :idOfOrg ";

            try {
                eventFilter = Integer.parseInt(reportProperties.getProperty("eventFilter"));
            } catch (Exception e) {
                eventFilter = 0;
            }

            eventsCondition = getEventsCondition(eventFilter);

            if(!ObjectUtils.isEmpty(clientGroupNames)){
                clientGroupCondition = " and gr.groupname in (:clientGroupNames) ";
            }

            List<String> stringClientsList;
            try
            {
                String idOfClients = StringUtils.trimToEmpty(reportProperties.getProperty(P_ID_CLIENT));
                stringClientsList = Arrays.asList(StringUtils.split(idOfClients, ','));
            } catch (Exception e)
            {
                stringClientsList = new ArrayList<>();
            }


            String clientIdWhere = "";
            if (!stringClientsList.isEmpty()) {
                int i = 0;
                String clientIdQuery = "";
                for (String client : stringClientsList) {
                    clientIdQuery = clientIdQuery + "'" + client + "'";
                    if (i < stringClientsList.size() - 1) {
                        clientIdQuery = clientIdQuery + ", ";
                    }
                    i++;
                }
                clientIdWhere = " AND c.idofclient in (" + clientIdQuery + ") ";
            }

            Query query = session.createSQLQuery(
                           " select ee.evtdatetime, p.surname, p.firstname, p.secondname,"
                            + " case when gr.idoforg not in (select friendlyorg from cf_friendly_organization where currentorg = :idOfOrg) then 'Обучающиеся других ОО'"
                            + " else gr.groupname end as groupname, "
                            + " ee.passdirection, o.shortname " + selectPartSectionName
                            + " from cf_enterevents ee "
                            + " join cf_orgs o on ee.idoforg = o.idoforg "
                            + " left join cf_cards crd on ee.idofcard = crd.cardno "
                            + " left join cf_clients c on c.idofclient = ee.idofclient or c.idofclient = crd.idofclient "
                            + " left join cf_visitors v on v.idOfVisitor = ee.idofvisitor "
                            + " left join cf_persons p on p.idofperson = c.idofperson or p.idofperson = v.idofperson "
                            + joinMigrants
                            + " left join cf_clientgroups gr on gr.idoforg = c.idoforg "
                            + " and gr.idofclientgroup = c.idofclientgroup "
                            + " where ee.evtdatetime between :startTime and :endTime "
                            + orgCondition
                            + clientIdWhere
                            + eventsCondition
                            + clientGroupCondition
                            + groupWhenSelectMigrants
                            + " order by " + partOfOrderByMigrantSection
            );
            query.setParameter("startTime", startTime.getTime())
                 .setParameter("endTime", endTime.getTime())
                 .setParameter("idOfOrg", idOfOrg);

            if(!clientGroupCondition.isEmpty()){
                query.setParameterList("clientGroupNames", clientGroupNames);
            }

            List<Object[]> data = query.list();
            for(Object[] row : data){
                Date evtDateTime = new Date(((BigInteger) row[0]).longValue());
                String eventDate = CalendarUtils.dateShortToStringFullYear(evtDateTime);
                String time = CalendarUtils.formatTimeClassicToString(evtDateTime.getTime());
                String fullName = StringUtils.defaultString((String) row[1])  + " " +StringUtils.defaultString((String) row[2]) + " " + StringUtils.defaultString((String) row[3]);
                String group = StringUtils.defaultString((String) row[4]);
                String eventName = getEventsName((Integer) row[5]);
                String shortNameOrg = StringUtils.defaultString((String) row[6]);
                String sectionName = StringUtils.defaultString((String) row[7]);

                EnterEventItem item = new EnterEventItem(eventDate, time, fullName, group, eventName, shortNameOrg, sectionName);
                enterEventItems.add(item);
            }
            return new JRBeanCollectionDataSource(enterEventItems);
        }

        private String getEventsName(Integer passdirection) {
            switch (passdirection) {
                case EnterEvent.ENTRY:
                    return "вход";
                case EnterEvent.EXIT:
                    return "выход";
                case EnterEvent.PASSAGE_IS_FORBIDDEN:
                    return "проход запрещен";
                case EnterEvent.TURNSTILE_IS_BROKEN:
                    return "взлом турникета";
                case EnterEvent.EVENT_WITHOUT_PASSAGE:
                    return "событие без прохода";
                case EnterEvent.PASSAGE_RUFUSAL:
                    return "отказ от прохода";
                case EnterEvent.RE_ENTRY:
                    return "повторный вход";
                case EnterEvent.RE_EXIT:
                    return "повторный выход";
                case EnterEvent.DETECTED_INSIDE:
                    return "обнаружен на подносе карты внутри здания";
                case EnterEvent.CHECKED_BY_TEACHER_EXT:
                    return "отмечен в классном журнале через внешнюю систему";
                case EnterEvent.CHECKED_BY_TEACHER_INT:
                    return "отмечен учителем внутри здания";
                case EnterEvent.QUERY_FOR_ENTER:
                    return "запрос на вход";
                case EnterEvent.QUERY_FOR_EXIT:
                    return "запрос на выход";
                case EnterEvent.BLACK_LIST:
                    return "чёрный список";
                default:
                    return "неизвестный тип";
            }
        }

        private String getEventsCondition(Integer eventFilter) {
            switch (eventFilter) {
                case 1:
                    return " and ee.passdirection in ( " + EnterEvent.ENTRY + ", " + EnterEvent.EXIT + ", " + EnterEvent.RE_ENTRY + ", " + EnterEvent.RE_EXIT + " ) ";
                case 2:
                    return " and not (c.idofclient is null and crd.cardno is null) ";
                case 3:
                    return " and c.idofclient is null and crd.cardno is null ";
                default:
                    return "";
            }
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public Boolean getAllFriendlyOrgs() {
            return allFriendlyOrgs;
        }

        public void setAllFriendlyOrgs(Boolean allFriendlyOrgs) {
            this.allFriendlyOrgs = allFriendlyOrgs;
        }
    }

    public static class EnterEventItem implements Comparable<EnterEventItem> {

        private String eventDate;// Дата события
        private String time;// Время события
        private String fullName;// Фамилия и Имя учащегося
        private String group;// Группа
        private String eventName;// Наименование события
        private String shortNameOrg; // Название учреждения
        private String sectionName;

        public EnterEventItem(String eventDate, String time, String fullName, String group, String eventName,
                String shortNameOrg, String sectionName) {
            this.eventDate = eventDate;
            this.time = time;
            this.fullName = fullName;
            this.group = group;
            this.eventName = eventName;
            this.shortNameOrg = shortNameOrg;
            this.sectionName = sectionName;
        }

        public String getEventDate() {
            return eventDate;
        }

        public void setEventDate(String eventDate) {
            this.eventDate = eventDate;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getShortNameOrg() {
            return shortNameOrg;
        }

        public void setShortNameOrg(String shortNameOrg) {
            this.shortNameOrg = shortNameOrg;
        }

        @Override
        public int compareTo(EnterEventItem o) {
            return o.getEventDate().compareTo(this.eventDate);
        }

        public String getSectionName() {
            return sectionName;
        }

        public void setSectionName(String sectionName) {
            this.sectionName = sectionName;
        }
    }
}
