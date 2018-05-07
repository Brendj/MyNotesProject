/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.EnterEvent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormatSymbols;
import java.util.*;

import static org.hibernate.criterion.Restrictions.between;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.in;

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

            startTime = CalendarUtils.endOfDay(startTime); //на конец дня, чтобы отображать зареганные в этот день карты
            JasperPrint jasperPrint = JasperFillManager
                    .fillReport(templateFilename, parameterMap, createDataSource(session, startTime, endTime, idOfOrg));
            long generateDuration = generateTime.getTime();
            return new EnterEventJournalReport(generateTime, generateDuration, jasperPrint, startTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Long idOfOrg)
                throws Exception {


            String eventState = reportProperties.getProperty("eventNums");

            List<Integer> eventsNumList = new ArrayList<Integer>();

            if (!eventState.equals("")) {
                String [] eventArr = StringUtils.split(eventState, ",");

                for (String str: eventArr) {
                    eventsNumList.add(Integer.valueOf(str));
                }
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
                List<Long> idOfOrgList = new ArrayList<Long>();

                for (Org orgItem : org.getFriendlyOrg()) {
                    idOfOrgList.add(orgItem.getIdOfOrg());
                }

                criteria.createAlias("org", "o").add(in("o.idOfOrg", idOfOrgList));
            } else {
                criteria.createAlias("org", "o").add(eq("o.idOfOrg", idOfOrg));
            }

            if (!eventsNumList.isEmpty()){
                criteria.add(Restrictions.in("passDirection", eventsNumList));
            }

            //criteria.createAlias("clientGroup", "cg");
            criteria.add((between("evtDateTime", startTime, endTime)));
            List<EnterEvent> enterEventList = criteria.list();

            for (EnterEvent enterEvent : enterEventList) {

                String passdirection, personFullName;

                if (enterEvent.getClient() != null) {
                    personFullName = enterEvent.getClient().getPerson().getFullName();
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
                        passdirection = "Ошибка обратитесь администратору";
                        /*
                        TwicePassEnter = 6,     //повторный вход
                        TwicePassExit = 7,       //повторный выход
                        DetectedInside=100 // обнаружен на подносе карты внутри здания
                        * */
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

                enterEventItems.add(enterEventItem);
            }
            Collections.sort(enterEventItems);
            Collections.reverse(enterEventItems);

            return new JRBeanCollectionDataSource(enterEventItems);
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
    }
}