/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.EnterEvent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.mapping.Array;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 11.03.12
 * Time: 13:46
 * To change this template use File | Settings | File Templates.
 */
public class AutoEnterEventByDaysReport extends BasicReportForOrgJob {

    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
    public static SimpleDateFormat dayInWeekFormat = new SimpleDateFormat("EE", new Locale("ru"));



    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    public static class Builder implements BasicReportJob.Builder {

        public class Event implements Comparable<Event>{
            private Long time;
            private int passdirection;

            public Event(Long time, int passdirection) {
                this.time = time;
                this.passdirection = passdirection;
            }

            public Long getTime() {
                return time;
            }

            public void setTime(Long time) {
                this.time = time;
            }

            public int getPassdirection() {
                return passdirection;
            }

            public void setPassdirection(int passdirection) {
                this.passdirection = passdirection;
            }

            @Override
            public int compareTo(Event event) {
                return this.getTime().compareTo(event.getTime());
            }
        }


        public static class ReportItem {

            private Integer id = null;
            private String fio = null;
            private List<String> timeList;
            // все события храним в списках по дням (1 список на 1 день)
            private Map<Integer, ArrayList<Event>> eventMap = new HashMap<Integer, ArrayList<Event>>(31);
            private String groupName;
            // флаги для того, чтобы не сортировать списки повторно
            private Set<Integer> setOfSortFlags = new HashSet<Integer>();


            public ReportItem(Integer id, String fio, String groupName) {
                this.id = id;
                this.fio = fio;
                this.groupName = groupName;
            }

            public Map<Integer, ArrayList<Event>> getEventMap() {
                return eventMap;
            }

            public String getGroupName() {
                return groupName;
            }

            public ReportItem() {
            }

            public Integer getId() {
                return id;
            }

            public String getFio() {
                return fio;
            }

            public ArrayList<Event> getSortedEventList(ArrayList<Event> eventList) {
                if (!setOfSortFlags.contains(eventList.hashCode()) && !eventList.isEmpty()) {
                    Collections.sort(eventList);
                    setOfSortFlags.add(eventList.hashCode());
                }
                return eventList;
            }

            public String getTimeEnter(ArrayList<Event> eventList) {
                if (eventList == null || eventList.isEmpty())
                    return null;
                /*
                for (int i = 0; i<getSortedEventList(eventList).size(); i++) {
                    Event evt = eventList.get(i);
                    if (evt.getPassdirection() == EnterEvent.ENTRY ||
                            (getSortedEventList(eventList).size()>=2 && i!=getSortedEventList(eventList).size()-1
                                    && eventList.get(i).getPassdirection() == EnterEvent.PASSAGE_RUFUSAL &&
                                    eventList.get(i+1).getPassdirection() == EnterEvent.PASSAGE_RUFUSAL))
                        return timeFormat.format(new Date(eventList.get(i).getTime()));
                }*/
                //return null;
                return timeFormat.format(new Date(eventList.get(0).getTime()));
            }

            public String getTimeExit(ArrayList<Event> eventList) {
                if (eventList == null || eventList.isEmpty())
                    return "";
                /*for (int i = getSortedEventList(eventList).size()-1; i>=0; i--)
                    if (eventList.get(i).getPassdirection() == EnterEvent.EXIT ||
                            (getSortedEventList(eventList).size()>=2 && i!=0 &&
                                    eventList.get(i).getPassdirection() == EnterEvent.PASSAGE_RUFUSAL &&
                                    eventList.get(i-1).getPassdirection() == EnterEvent.PASSAGE_RUFUSAL))
                        return timeFormat.format(new Date(eventList.get(i).getTime()));
                return null;*/
                return timeFormat.format(new Date(eventList.get(eventList.size()-1).getTime()));
            }

            public List<String> getTimeList() {
                if (timeList == null) {
                    timeList = new ArrayList<String>(31);
                    for (int day = 0; day < 31; day++) {
                        String enter = getTimeEnter(this.getEventMap().get(day));
                        String exit = getTimeExit(this.getEventMap().get(day));
                        if (enter != null && exit != null)
                            timeList.add(day, String.format("%s - %s", enter, exit));
                        else
                            timeList.add(day, null);
                    }
                }
                return timeList;
            }
        }

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public BasicReportJob build(Session session, Org org, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Date generateTime = new Date();
            Map<Object, Object> parameterMap = new HashMap<Object, Object>();
            List<String> daysOfMonth = new ArrayList<String>(31); // 1 Вс	2 Пн	3 Вт	4 Ср ...
            parameterMap.put("orgName", org.getOfficialName());
            calendar.setTime(startTime);
            parameterMap.put("days", daysOfMonth);
            parameterMap.put("monthName", calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("ru")));
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap, daysOfMonth));
            Date generateEndTime = new Date();
            return new AutoEnterEventByDaysReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, Org org, Date startTime, Date endTime,
                Calendar calendar, Map<Object, Object> parameterMap, List<String> daysOfMonth) throws Exception {
            // хешмап для хранения записей отчета по айди клиенту
            HashMap<Long, ReportItem> mapItems = new HashMap<Long, ReportItem>();
            // лист для хранения результата
            List<ReportItem> resultRows = new LinkedList<ReportItem>();
            Calendar c = Calendar.getInstance();
            Long startDate = CalendarUtils.getTimeFirstDayOfMonth(startTime.getTime());
            for (int day = 1; day <= 31; day++)
                daysOfMonth.add(day-1, String.format("%d %s", day, dayInWeekFormat.format(startDate+(day-1)*1000*60*60*24)));

            // При создании правила для данного типа отчета можно задать параметр enterEventType, который может принимать значения
            // "все", "учащиеся", "все_без_учащихся". typeCondition содержит соответствующее улосвие для sql-запроса
            String typeCondition = "";
            // извлекаем значение, которое было указано при создании правила
            String typeConditionsValue = (String)RuleProcessor.getReportProperties().get("enterEventType");
            if (typeConditionsValue != null) {
                // значения могут перечисляться через запятую, однако данный параметр может принимать только 1 из "все", "учащиеся", "все_без_учащихся"
                String typeConditionsValues[] = typeConditionsValue.split(RuleProcessor.DELIMETER);
                if (typeConditionsValues.length > 1)
                    throw new Exception(String.format("%s: Параметр enterEventType не может принимать несколько значений.", AutoEnterEventByDaysReport.class.getSimpleName()));
                if ((typeConditionsValues[0].trim().equals(RuleCondition.ENTEREVENT_TYPE_TEXT[RuleCondition.ENTEREVENT_TYPE_STUDS])))
                    typeCondition = String.format("AND c.idOfClientGroup<%d ", ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES);
                else if ((typeConditionsValues[0].trim().equals(RuleCondition.ENTEREVENT_TYPE_TEXT[RuleCondition.ENTEREVENT_TYPE_WITHOUTSTUDS])))
                    typeCondition = String.format("AND c.idOfClientGroup>=%d ", ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES);
            }
            Query query = session.createSQLQuery(
                    "SELECT p.firstname, p.surname, p.secondname, e.evtdatetime, e.passdirection, c.idOfClient, g.groupname "
                    + "FROM cf_enterevents e, cf_clients c, cf_persons p, cf_clientgroups g "
                    + "WHERE e.idofclient=c.idofclient and p.idofperson=c.idofperson AND e.idoforg=:idOfOrg AND g.idofclientgroup=c.idofclientgroup AND g.idoforg=c.idoforg "
                    + "AND e.evtdatetime>=:startTime AND e.evtdatetime<=:endTime "
                    + typeCondition+ " AND c.idOfClientGroup!=1100000060 "
                    + "order by g.groupname, p.surname, p.firstname, p.secondname;");

            query.setParameter("startTime", CalendarUtils.getTimeFirstDayOfMonth(startTime.getTime()));
            query.setParameter("endTime", CalendarUtils.getTimeLastDayOfMonth(startTime.getTime()));
            query.setParameter("idOfOrg", org.getIdOfOrg());

            List resultList = query.list();
            int i = 1; // порядковый номер записи в отчете
            for (Object o : resultList) {
                Object vals[]=(Object[])o;
                String firstname = (String)vals[0];
                String surname = (String)vals[1];
                String secondname = (String)vals[2];
                Long time = Long.parseLong(vals[3].toString());
                Integer passdirection = Integer.parseInt(vals[4].toString());
                Long idOfClient = Long.parseLong(vals[5].toString());
                String groupName = (String)vals[6];
                c.setTimeInMillis(time);
                int day = c.get(Calendar.DAY_OF_MONTH);

                ReportItem reportItem = mapItems.get(idOfClient);
                if (reportItem == null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (surname != null && !surname.trim().isEmpty())
                    stringBuilder.append(surname).append(' ');
                    if (firstname != null && !firstname.trim().isEmpty())
                        stringBuilder.append(firstname.trim().charAt(0)).append(". ");
                    if (secondname != null && !secondname.trim().isEmpty())
                        stringBuilder.append(secondname.trim().charAt(0)).append('.');
                    reportItem = new ReportItem(i++, stringBuilder.toString().trim(), groupName);
                    mapItems.put(idOfClient, reportItem);
                    resultRows.add(reportItem);
                }
                // ищем список событий по дню
                ArrayList<Event> events = reportItem.getEventMap().get(day-1);
                if (events == null) {
                    events = new ArrayList<Event>(31);
                    reportItem.getEventMap().put(day-1, events);
                }
                // добавляем событие входа или выхода
                events.add(new Event(time, passdirection));
            }
            return new JRBeanCollectionDataSource(resultRows);
        }

    }


    public AutoEnterEventByDaysReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);    //To change body of overridden methods use File | Settings | File Templates.
    }
    private static final Logger logger = LoggerFactory.getLogger(AutoEnterEventByDaysReport.class);

    public AutoEnterEventByDaysReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new AutoEnterEventByDaysReport();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_PREV_PREV_DAY;
    }
}


