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

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportJob.Builder {

        private final String templateFilename;

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

            private String fio = null;
            private List<String> timeList;
            // все события храним в списках по дням (1 список на 1 день)
            private Map<Integer, ArrayList<Event>> eventMap = new HashMap<Integer, ArrayList<Event>>(31);
            private String groupName;


            public ReportItem(String fio, String groupName) {
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

            public String getFio() {
                return fio;
            }

            boolean sorted;
            public String getTimeEnter(ArrayList<Event> eventList) {
                if (eventList == null || eventList.isEmpty())
                    return null;
                if (!sorted) sort();
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
                return CalendarUtils.timeToString(new Date(eventList.get(0).getTime()));
            }

            public String getTimeExit(ArrayList<Event> eventList) {
                if (eventList == null || eventList.isEmpty())
                    return "";
                if (!sorted) sort();
                /*for (int i = getSortedEventList(eventList).size()-1; i>=0; i--)
                    if (eventList.get(i).getPassdirection() == EnterEvent.EXIT ||
                            (getSortedEventList(eventList).size()>=2 && i!=0 &&
                                    eventList.get(i).getPassdirection() == EnterEvent.PASSAGE_RUFUSAL &&
                                    eventList.get(i-1).getPassdirection() == EnterEvent.PASSAGE_RUFUSAL))
                        return timeFormat.format(new Date(eventList.get(i).getTime()));
                return null;*/
                return CalendarUtils.timeToString(new Date(eventList.get(eventList.size() - 1).getTime()));
            }

            private void sort() {
                for (Map.Entry<Integer, ArrayList<Event>> me : getEventMap().entrySet()) {
                    Collections.sort(me.getValue());
                }
                sorted=true;
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

            // время присутствия в течении дня
            public String getPresenceOfDay() {
                if (getEventMap().isEmpty()) return ""; // событий прохода небыло
                long fullResult = 0;
                int countDay =0;
                for (Integer val:getEventMap().keySet()){
                    boolean lastExit = false;
                    long result = 0;
                    long entryTime = 0L;
                    List<Event> events = getEventMap().get(val);
                    Collections.sort(events);
                    for (Event e: events){
                        if(EnterEvent.isEntryOrReEntryEvent(e.getPassdirection()) && entryTime<=0){
                            entryTime = e.getTime();
                            lastExit = false;
                        }
                        if(EnterEvent.isExitOrReExitEvent(e.getPassdirection()) && entryTime>0){
                            double value = ((e.getTime() * 1.0 - entryTime * 1.0) / (1000.0 * 60.0));
                            result += Math.round(value);
                            //result += (int)((e.getTime() - entryTime) / (1000 * 60));
                            lastExit = true;
                            entryTime = 0L;
                        }
                    }
                    if (!lastExit) continue;
                    fullResult +=result;
                    countDay++;
                }

                //if (lastEntry == null)  return ""; // клиент только выходил
                //if (!lastExit)  return ""; // клиент последний раз вошел но не вышел
                //Calendar calendar = Calendar.getInstance();
                //calendar.setTimeZone(RuntimeContext.getInstance().getLocalTimeZone(null));
                //calendar.setTimeInMillis(presenceOfDay);
                //SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
                //ft.setTimeZone(RuntimeContext.getInstance().getLocalTimeZone(null));
                //return ft.format(calendar.getTime());
                //long h = (presenceOfDay / 60000L) / 60;
                //long m = (presenceOfDay / 60000L) % 60;
                if(countDay==0) return "";
                if(fullResult == 0L) return "00:00";
                long result = fullResult / countDay;
                long h = result / 60;
                long m = result % 60;
                return String.format("%02d:%02d", h,m);
            }
        }

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            List<String> daysOfMonth = new ArrayList<String>(31); // 1 Вс	2 Пн	3 Вт	4 Ср ...
            parameterMap.put("orgName", org.getOfficialName());
            calendar.setTime(startTime);
            parameterMap.put("days", daysOfMonth);
            parameterMap.put("monthName", calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("ru")));
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, daysOfMonth));
            Date generateEndTime = new Date();
            return new AutoEnterEventByDaysReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, List<String> daysOfMonth) throws Exception {
            // хешмап для хранения записей отчета по айди клиенту
            HashMap<Long, ReportItem> mapItems = new HashMap<Long, ReportItem>();
            // лист для хранения результата
            List<ReportItem> resultRows = new ArrayList<ReportItem>();
            Calendar c = Calendar.getInstance();
            Long startDate = CalendarUtils.getTimeFirstDayOfMonth(startTime.getTime());
            for (int day = 1; day <= 31; day++)
                daysOfMonth.add(day-1, String.format("%d %s", day, CalendarUtils.dayInWeekToString(startDate+(day-1)*1000*60*60*24)));

            // При создании правила для данного типа отчета можно задать параметр enterEventType, который может принимать значения
            // "все", "учащиеся", "все_без_учащихся". typeCondition содержит соответствующее улосвие для sql-запроса
            String typeCondition = "";
            // извлекаем значение, которое было указано при создании правила
            String typeConditionsValue = (String)getReportProperties().get("enterEventType");
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

            String clientSQL = "select c.idOfClient, p.firstname, p.surname, p.secondname, g.groupname "
                    + " from cf_clients c"
                    + " join cf_persons p on c.idofperson=p.idofperson "
                    + " left join cf_clientgroups g on "
                    + " g.idofclientgroup=c.idofclientgroup and g.idoforg=c.idoforg "+ typeCondition+ "  AND c.idOfClientGroup!=1100000060"
                    + " where c.idoforg=:idOfOrg"
                    + " order by g.groupname, p.surname, p.firstname, p.secondname";

            Query clientQuery = session.createSQLQuery(clientSQL);
            clientQuery.setParameter("idOfOrg",org.getIdOfOrg());
            List<Object> clients = clientQuery.list();
            for (Object client: clients){
                //Query query = session.createSQLQuery(
                //        "SELECT p.firstname, p.surname, p.secondname, e.evtdatetime, e.passdirection, c.idOfClient, g.groupname "
                //                + "FROM cf_enterevents e, cf_clients c, cf_persons p, cf_clientgroups g "
                //                + "WHERE e.idofclient=c.idofclient and p.idofperson=c.idofperson AND e.idoforg=:idOfOrg AND g.idofclientgroup=c.idofclientgroup "
                //                + " AND g.idoforg=c.idoforg AND e.evtdatetime>=:startTime AND e.evtdatetime<=:endTime "
                //                + typeCondition+ " AND c.idOfClientGroup!=1100000060 "
                //                + "order by g.groupname, p.surname, p.firstname, p.secondname;");

                Object[] clientParams = (Object[]) client;

                Long idOfClient = Long.valueOf(clientParams[0].toString());
                ReportItem reportItem = mapItems.get(idOfClient);
                if (reportItem == null) {
                    String firstName = String.valueOf(clientParams[1]) ;
                    String surName = String.valueOf(clientParams[2]);
                    String secondName = String.valueOf(clientParams[3]);
                    String groupName = String.valueOf(clientParams[4]);
                    if(groupName==null || groupName.isEmpty() || groupName.equalsIgnoreCase("null")){
                        groupName ="";
                    }
                    StringBuilder shortFullName = new StringBuilder();
                    if (surName != null && !surName.trim().isEmpty())
                        shortFullName.append(surName.trim()).append(' ');
                    if (firstName != null && !firstName.trim().isEmpty())
                        shortFullName.append(firstName.trim().charAt(0)).append(". ");
                    if (secondName != null && !secondName.trim().isEmpty())
                        shortFullName.append(secondName.trim().charAt(0)).append('.');
                    reportItem = new ReportItem(shortFullName.toString().trim(), groupName);
                    mapItems.put(idOfClient, reportItem);
                    resultRows.add(reportItem);
                }


                Query query = session.createSQLQuery(
                        "SELECT e.evtdatetime, e.passdirection "
                                + " FROM cf_enterevents e "
                                + " WHERE e.idofclient=:idOfClient AND e.idoforg=:idOfOrg "
                                + " AND e.evtdatetime>=:startTime AND e.evtdatetime<=:endTime ");


                query.setParameter("startTime", CalendarUtils.getTimeFirstDayOfMonth(startTime.getTime()));
                query.setParameter("endTime", CalendarUtils.getTimeLastDayOfMonth(startTime.getTime()));
                query.setParameter("idOfOrg", org.getIdOfOrg());
                query.setParameter("idOfClient", idOfClient);

                List resultList = query.list();
                for (Object o : resultList) {
                    Object vals[]=(Object[])o;
                    Long time = Long.valueOf(vals[0].toString());
                    Integer passDirection = Integer.valueOf(vals[1].toString());
                    c.setTimeInMillis(time);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    // ищем список событий по дню
                    ArrayList<Event> events = reportItem.getEventMap().get(day-1);
                    if (events == null) {
                        events = new ArrayList<Event>(31);
                        reportItem.getEventMap().put(day-1, events);
                    }
                    // добавляем событие входа или выхода
                    events.add(new Event(time, passDirection));
                }
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


