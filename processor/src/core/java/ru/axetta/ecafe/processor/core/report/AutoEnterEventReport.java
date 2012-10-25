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
public class AutoEnterEventReport extends BasicReportForOrgJob {

    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportJob.Builder {

        public class Event implements Comparable<Event>{
            private Long time; // время события
            private int passdirection; // направление (вошел, вышел)

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
            // события прохода через турникет
            private ArrayList<Event> eventList = new ArrayList<Event>();
            private String date;
            private String groupName; // группа клиента (класс, сотрудники и т.д.)
            private boolean isSorted = false; // флаг упорядовенности eventList'a

            public ReportItem(Integer id, String fio, String date, String groupName) {
                this.id = id;
                this.fio = fio;
                this.date = date;
                this.groupName = groupName;
            }

            public boolean isSorted() {
                return isSorted;
            }

            public void setSorted(boolean sorted) {
                isSorted = sorted;
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

            public ArrayList<Event> getEventList() {
                return eventList;
            }

            /**
             * Метод доступа к eventList. Отличается от getEventList() тем, что проверяет флаг упорядоченности.
             * Если флан установлен в значение fasle, выполняет сортировку.
             * @return
             */
            public ArrayList<Event> getSortedEventList() {
                if (!isSorted() && !this.eventList.isEmpty()) {
                    Collections.sort(this.eventList);
                    setSorted(true);
                }
                return eventList;
            }

            public String getDate() {
                return date;
            }

            // время прихода
            public String getTimeEnter() {
                if (this.getEventList().isEmpty())
                    return "";
                for (Event e : getSortedEventList()) {
                    if (e.getPassdirection() == EnterEvent.ENTRY)
                        return timeFormat.format(new Date(e.getTime()));
                }
                return "-";
            }

            // время ухода
            public String getTimeExit() {
                if (this.getEventList().isEmpty())
                    return "";
                for (int i = getSortedEventList().size()-1; i>=0; i--)
                    if (getEventList().get(i).getPassdirection() == EnterEvent.EXIT)
                        return timeFormat.format(new Date(getEventList().get(i).getTime()));
                return "-";
            }

            // время отсутствия
            public Integer getAbsenceOfDay() {
                if (this.getEventList().isEmpty())
                    return null;
                Integer result = 0;
                Long exitTime = null;
                for (Event e : this.getSortedEventList()) {
                    if (e.getPassdirection() == EnterEvent.ENTRY && exitTime!=null) {
                        result += (int)((e.getTime() - exitTime) / (1000 * 60));
                    }
                    if (e.getPassdirection() == EnterEvent.EXIT)
                        exitTime = e.getTime();
                }
                return result;
            }

            // первый вход - последний выход
            public String getTimeEnterExit() {
                if (this.getEventList().isEmpty())
                    return "";
                StringBuilder sb = new StringBuilder();
                for (Event e : this.getSortedEventList()) {
                    if (e.getPassdirection() == EnterEvent.ENTRY || e.getPassdirection() == EnterEvent.EXIT) {
                        sb.append(timeFormat.format(new Date(e.getTime()))).append((e.getPassdirection() == EnterEvent.ENTRY?" (+)":" (-)")).append(", ");
                    }
                }
                if (sb.length()>2)
                    return sb.toString().substring(0, sb.length() - 2);
                else
                    return "-";

            }
        }

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("orgName", org.getOfficialName());
            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new AutoEnterEventReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, Org org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            HashMap<Integer, HashMap<Integer, ReportItem>> mapItems = new HashMap<Integer, HashMap<Integer, ReportItem>>();
            List<ReportItem> resultRows = new LinkedList<ReportItem>();
            Calendar c = Calendar.getInstance();

            String typeCondition = "";
            String typeConditionsValue = (String)getReportProperties().get("enterEventType");
            if (typeConditionsValue != null) {
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
                    + "order by g.groupname, e.evtdatetime/(1000*60*60*24), p.surname, p.firstname, p.secondname;");

            query.setParameter("startTime", CalendarUtils.getTimeFirstDayOfMonth(startTime.getTime()));
            query.setParameter("endTime", CalendarUtils.getTimeLastDayOfMonth(startTime.getTime()));
            query.setParameter("idOfOrg", org.getIdOfOrg());

            List resultList = query.list();
            HashMap<Integer, ReportItem> tmpMap;
            int i = 1;
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

                tmpMap = mapItems.get(idOfClient.hashCode());
                if (tmpMap == null) {
                    tmpMap = new HashMap<Integer, ReportItem>(31);
                    mapItems.put(idOfClient.hashCode(), tmpMap);
                }
                ReportItem reportItem = tmpMap.get(day);
                if (reportItem == null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (surname != null && !surname.trim().isEmpty())
                    stringBuilder.append(surname).append(' ');
                    if (firstname != null && !firstname.trim().isEmpty())
                        stringBuilder.append(firstname.trim().charAt(0)).append(". ");
                    if (secondname != null && !secondname.trim().isEmpty())
                        stringBuilder.append(secondname.trim().charAt(0)).append('.');
                    reportItem = new ReportItem(i++, stringBuilder.toString().trim(), dateFormat.format(new Date(time)), groupName);
                    tmpMap.put(day, reportItem);
                    resultRows.add(reportItem);
                }
                reportItem.getEventList().add(new Event(time, passdirection));
            }
            return new JRBeanCollectionDataSource(resultRows);
        }

    }


    public AutoEnterEventReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);    //To change body of overridden methods use File | Settings | File Templates.
    }
    private static final Logger logger = LoggerFactory.getLogger(AutoEnterEventReport.class);

    public AutoEnterEventReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new AutoEnterEventReport();
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


