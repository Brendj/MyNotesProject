/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.MigrantsUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormatSymbols;
import java.util.*;

import static org.hibernate.criterion.Restrictions.*;

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
            Map<Client, String> clientSections = new HashMap<Client, String>();
            List<Long> idOfOrgList = new LinkedList<Long>();
            List<Migrant> expectedMigrants = null;

            try {
                eventFilter = Integer.parseInt(reportProperties.getProperty("eventFilter"));
            } catch (Exception e) {
                eventFilter = 0;
            }

            //группа фильтр
            String groupNameFilter = reportProperties.getProperty("groupName");

            ArrayList<String> groupList = new ArrayList<String>();

            if (groupNameFilter != null && !groupNameFilter.equals("")) {
                String[] groups = StringUtils.split(groupNameFilter, ",");
                for (String str: groups) {
                    groupList.add(str);
                }
            }

            List<EnterEventItem> enterEventItems = new ArrayList<EnterEventItem>();
            Criteria criteria = session.createCriteria(EnterEvent.class);

            if (!groupList.isEmpty()) {
                criteria.createAlias("clientGroup", "cg", JoinType.LEFT_OUTER_JOIN);
                criteria.add(Restrictions.in("cg.groupName", groupList));
            }

            if (allFriendlyOrgs) {
                Org org = (Org) session.load(Org.class, idOfOrg);
                for (Org orgItem : org.getFriendlyOrg()) {
                    idOfOrgList.add(orgItem.getIdOfOrg());
                }

                criteria.createAlias("org", "o").add(in("o.idOfOrg", idOfOrgList));
            } else {
                criteria.createAlias("org", "o").add(eq("o.idOfOrg", idOfOrg));
            }

            criteria.add((between("evtDateTime", startTime, endTime)));
            criteria.addOrder(Order.asc("evtDateTime"));
            List<EnterEvent> enterEventList = criteria.list();

            String passdirection, personFullName;

            if(idOfOrgList.isEmpty()){
                Org org = (Org) session.load(Org.class, idOfOrg);
                for (Org orgItem : org.getFriendlyOrg()) {
                    idOfOrgList.add(orgItem.getIdOfOrg());
                }
            }

            if(outputMigrants){
                if(allFriendlyOrgs) {
                    expectedMigrants = MigrantsUtils
                            .getMigrationForListOfOrgVisit(session, idOfOrgList, startTime, endTime);
                } else {
                    expectedMigrants = MigrantsUtils
                            .getMigrationForListOfOrgVisit(session, Arrays.asList(idOfOrg), startTime, endTime);
                }
            }

            for (EnterEvent enterEvent : enterEventList) {
                if (!matchItem(enterEvent, eventFilter)) {
                    continue;
                }
                if (enterEvent.getClient() != null) {
                    personFullName = enterEvent.getClient().getPerson().getFullName();
                } else if (enterEvent.getVisitorFullName() != null) {
                    personFullName = enterEvent.getVisitorFullName();
                } else if (enterEvent.getIdOfVisitor() != null) {
                    Criteria visitorCriteria = session.createCriteria(Visitor.class);
                    visitorCriteria.add(Restrictions.eq("idOfVisitor", enterEvent.getIdOfVisitor()));
                    Visitor visitor = (Visitor) visitorCriteria.uniqueResult();
                    if (visitor != null) {
                        if (visitor.getPerson() != null) {
                            personFullName = visitor.getPerson().getFullName();
                        } else {
                            personFullName = "";
                        }
                    } else {
                        personFullName = "";
                    }
                } else {
                    personFullName = "";
                }

                switch (enterEvent.getPassDirection()) {
                    case EnterEvent.ENTRY:
                        passdirection = "вход";
                        break;
                    case EnterEvent.EXIT:
                        passdirection = "выход";
                        break;
                    case EnterEvent.PASSAGE_IS_FORBIDDEN:
                        passdirection = "проход запрещен";
                        break;
                    case EnterEvent.TURNSTILE_IS_BROKEN:
                        passdirection = "взлом турникета";
                        break;
                    case EnterEvent.EVENT_WITHOUT_PASSAGE:
                        passdirection = "событие без прохода";
                        break;
                    case EnterEvent.PASSAGE_RUFUSAL:
                        passdirection = "отказ от прохода";
                        break;
                    case EnterEvent.RE_ENTRY:
                        passdirection = "повторный вход";
                        break;
                    case EnterEvent.RE_EXIT:
                        passdirection = "повторный выход";
                        break;
                    case EnterEvent.DETECTED_INSIDE:
                        passdirection = "обнаружен на подносе карты внутри здания";
                        break;
                    case EnterEvent.CHECKED_BY_TEACHER_EXT:
                        passdirection = "отмечен в классном журнале через внешнюю систему";
                        break;
                    case EnterEvent.CHECKED_BY_TEACHER_INT:
                        passdirection = "отмечен учителем внутри здания";
                        break;
                    case EnterEvent.QUERY_FOR_ENTER:
                        passdirection = "запрос на вход";
                        break;
                    case EnterEvent.QUERY_FOR_EXIT:
                        passdirection = "запрос на выход";
                        break;
                    default:
                        passdirection = "неизвестный тип";
                }

                String groupName = "";

                ClientGroup clientGroup = getClientGroupByID(enterEvent.getIdOfClientGroup(), enterEvent.getOrg().getIdOfOrg(), session);

                if (clientGroup != null) {
                    if (clientGroup.getGroupName() != null) {
                        groupName = clientGroup.getGroupName();
                    }
                }

                EnterEventItem enterEventItem = new EnterEventItem(
                        CalendarUtils.dateShortToStringFullYear(enterEvent.getEvtDateTime()),
                        CalendarUtils.formatTimeClassicToString(enterEvent.getEvtDateTime().getTime()), personFullName,
                        groupName, passdirection, enterEvent.getOrg().getShortNameInfoService());


                if (outputMigrants && !CollectionUtils.isEmpty(expectedMigrants) && enterEvent.getClient() != null) { // Если надо вывести кружки и в ОО вообще они есть...
                    Client client = (Client) session.load(Client.class, enterEvent.getClient().getIdOfClient()); // То достаем клиента...
                    if (client != null && !clientSections.containsKey(client)) { // Проверям находили ли мы на него, иначе уходим
                        if (!idOfOrgList.contains(client.getOrg().getIdOfOrg())) { // Проверяем подает ли он признаки мигранта, иначе добавляем в мапу с NULL в названии кружка
                            boolean findMigrant = false;
                            for (Migrant migrant : expectedMigrants) { // Через линейный поиск ищем клиента среди мигрантов...
                                if (migrant.getClientMigrate().equals(client)) {
                                    findMigrant = true;
                                    clientSections.put(client, migrant.getSection()); //...и добавляем в мапу чтоб больше с ним не возиться
                                    break;
                                }
                            }
                            if (!findMigrant) {
                                clientSections.put(client, null);
                            }
                        } else {
                            clientSections.put(client, null);
                        }
                    }
                    enterEventItem.setSectionName(clientSections.get(client)); // Добавляем название кружка в item
                }
                enterEventItems.add(enterEventItem);
            }

            if(sortedBySections){
                Collections.sort(enterEventItems, new Comparator<EnterEventItem>() {
                    @Override
                    public int compare(EnterEventItem o1, EnterEventItem o2) {
                        if(o1.getSectionName() == null && o2.getSectionName() == null) {
                            return 0;
                        } else if(o1.getSectionName() == null){
                            return -1;
                        } else if (o2.getSectionName() == null){
                            return 1;
                        } else {
                            return o1.getSectionName().compareTo(o2.getSectionName());
                        }
                    }
                });
            }

            return new JRBeanCollectionDataSource(enterEventItems);
        }

        private boolean matchItem(EnterEvent enterEvent, Integer eventFilter) {
            if (eventFilter.equals(0)) return true;      //все
            if (eventFilter.equals(1)) {                //входы-выходы
                return (enterEvent.getPassDirection() == EnterEvent.ENTRY || enterEvent.getPassDirection() == EnterEvent.EXIT
                || enterEvent.getPassDirection() == EnterEvent.RE_ENTRY || enterEvent.getPassDirection() == EnterEvent.RE_EXIT);
            }
            if (eventFilter.equals(2)) {                //с клиентом
                return enterEvent.getClient() != null;
            } else {
                return enterEvent.getClient() == null;
            }
        }

        public ClientGroup getClientGroupByID(Long idOfClientGroup, Long idOfOrg, Session session) throws Exception {
            Criteria criteria = session.createCriteria(ClientGroup.class);
            criteria.add(Restrictions.eq("compositeIdOfClientGroup.idOfOrg", idOfOrg));
            criteria.add(Restrictions.eq("compositeIdOfClientGroup.idOfClientGroup", idOfClientGroup));
            return (ClientGroup) criteria.uniqueResult();
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
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
                String shortNameOrg) {
            this.eventDate = eventDate;
            this.time = time;
            this.fullName = fullName;
            this.group = group;
            this.eventName = eventName;
            this.shortNameOrg = shortNameOrg;
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
